package common.cn.nsimu.simukraft.material;

import common.cn.nsimu.simukraft.SimuKraft;
import common.cn.nsimu.simukraft.config.ServerConfig;
import common.cn.nsimu.simukraft.config.material.MaterialConfig;
import common.cn.nsimu.simukraft.config.material.ReplaceableMaterialHolder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


public final class WorkMaterialPolicy {
    private static final ConcurrentMap<String, WorkMaterialRequest> REQUEST_CACHE = new ConcurrentHashMap<>();
    private static volatile ConfigSnapshot cachedSnapshot = null;
    private static volatile ParsedRules cachedRules = ParsedRules.EMPTY;

    private WorkMaterialPolicy() {
    }

    public static boolean requiresMaterial(BlockState state) {
        if (state == null || isAirBlock(state.getBlock()) || ServerConfig.materialsCreativeMode()) {
            return false;
        }
        String blockId = blockId(state.getBlock());
        if (blockId.isBlank()) {
            return false;
        }
        ParsedRules rules = rules();
        if (ServerConfig.materialsExpertMode()) {
            return !rules.expertSkipList().contains(blockId);
        }
        if (rules.basicMaterials().contains(blockId)) {
            return true;
        }
        return ServerConfig.materialCategoryMatchingEnabled() && rules.covers(blockKey(blockId));
    }

    public static WorkMaterialRequest requestForBlock(BlockState state) {
        if (!requiresMaterial(state)) {
            return WorkMaterialRequest.EMPTY;
        }
        String blockId = blockId(state.getBlock());
        if (blockId.isBlank()) {
            return WorkMaterialRequest.EMPTY;
        }
        ConfigSnapshot snapshot = snapshot();
        return REQUEST_CACHE.computeIfAbsent(snapshot.cacheKey() + "|" + blockId, ignored -> buildRequest(state, blockId, snapshot));
    }

    public static void clearCache() {
        synchronized (WorkMaterialPolicy.class) {
            cachedSnapshot = null;
            cachedRules = ParsedRules.EMPTY;
            REQUEST_CACHE.clear();
        }
    }

    private static WorkMaterialRequest buildRequest(BlockState state, String blockId, ConfigSnapshot snapshot) {
        ParsedRules rules = rules(snapshot);
        LinkedHashSet<Item> acceptedItems = new LinkedHashSet<>();
        if (snapshot.expertMode()) {
            addAcceptedMaterialId(acceptedItems, blockId);
        } else if (snapshot.categoryMatching()) {
            rules.acceptedIds(blockKey(blockId)).forEach(materialId -> addAcceptedMaterialId(acceptedItems, materialId));
        }
        if (acceptedItems.isEmpty()) {
            addAcceptedMaterialId(acceptedItems, blockId);
        }

        List<Item> acceptedList = List.copyOf(acceptedItems);
        Item displayItem = state.getBlock().asItem();
        ItemStack displayStack = displayItem == Items.AIR && !acceptedList.isEmpty()
                ? new ItemStack(acceptedList.getFirst())
                : new ItemStack(displayItem);
        return WorkMaterialRequest.matching(displayStack, acceptedList, stack -> acceptedItems.contains(stack.getItem()));
    }

    private static ParsedRules rules() {
        return rules(snapshot());
    }

    private static ParsedRules rules(ConfigSnapshot snapshot) {
        ParsedRules rules = cachedRules;
        if (snapshot.equals(cachedSnapshot)) {
            return rules;
        }
        synchronized (WorkMaterialPolicy.class) {
            if (!snapshot.equals(cachedSnapshot)) {
                cachedRules = parseRules(snapshot);
                cachedSnapshot = snapshot;
                REQUEST_CACHE.clear();
            }
            return cachedRules;
        }
    }

    private static ParsedRules parseRules(ConfigSnapshot snapshot) {
        return new ParsedRules(blockIdSet(MaterialConfig.BASIC_MATERIALS), blockIdSet(MaterialConfig.EXPERT_MODE_SKIP_LIST),
                MaterialConfig.MATERIAL_CATEGORY_GROUPS == null ? Set.of() : Set.copyOf(MaterialConfig.MATERIAL_CATEGORY_GROUPS));
    }

    private static Set<String> blockIdSet(Set<ResourceKey<Block>> blocks) {
        if (blocks == null || blocks.isEmpty()) {
            return Set.of();
        }
        LinkedHashSet<String> ids = new LinkedHashSet<>();
        for (ResourceKey<Block> block : blocks) {
            if (block != null) {
                ids.add(block.identifier().toString().toLowerCase(Locale.ROOT));
            }
        }
        return Set.copyOf(ids);
    }

    private static ResourceKey<Block> blockKey(String blockId) {
        return ResourceKey.create(Registries.BLOCK, Identifier.parse(blockId));
    }

    private static void addAcceptedMaterialId(Set<Item> acceptedItems, String materialId) {
        resolveItem(materialId).ifPresent(acceptedItems::add);
        for (String variantId : variantItemIds(materialId)) {
            resolveItem(variantId).ifPresent(acceptedItems::add);
        }
    }

    private static java.util.Optional<Item> resolveItem(String materialId) {
        Identifier id = Identifier.tryParse(materialId);
        if (id == null) {
            return java.util.Optional.empty();
        }
        Item item = BuiltInRegistries.ITEM.getOptional(id).orElse(Items.AIR);
        if (item != Items.AIR) {
            return java.util.Optional.of(item);
        }
        Block block = BuiltInRegistries.BLOCK.getOptional(id).orElse(Blocks.AIR);
        Item blockItem = block.asItem();
        if (blockItem != Items.AIR) {
            return java.util.Optional.of(blockItem);
        }
        SimuKraft.LOGGER.debug("Simukraft: Unknown material id in config: {}", materialId);
        return java.util.Optional.empty();
    }

    private static List<String> variantItemIds(String blockId) {
        String namespace = "minecraft";
        String path = blockId;
        int separatorIndex = blockId.indexOf(':');
        if (separatorIndex >= 0) {
            namespace = blockId.substring(0, separatorIndex);
            path = blockId.substring(separatorIndex + 1);
        }
        return switch (path) {
            case "wall_torch" -> List.of(namespace + ":torch");
            case "soul_wall_torch" -> List.of(namespace + ":soul_torch");
            case "redstone_wall_torch" -> List.of(namespace + ":redstone_torch");
            case "wall_lantern" -> List.of(namespace + ":lantern");
            case "soul_wall_lantern" -> List.of(namespace + ":soul_lantern");
            default -> List.of();
        };
    }

    private static ConfigSnapshot snapshot() {
        return new ConfigSnapshot(
                ServerConfig.materialsCreativeMode(),
                ServerConfig.materialsExpertMode(),
                ServerConfig.materialCategoryMatchingEnabled(),
                MaterialConfig.revision()
        );
    }

    private static String blockId(Block block) {
        Identifier id = BuiltInRegistries.BLOCK.getKey(block);
        return id == null ? "" : id.toString().toLowerCase(Locale.ROOT);
    }

    private static boolean isAirBlock(Block block) {
        return block == Blocks.AIR || block == Blocks.CAVE_AIR || block == Blocks.VOID_AIR;
    }

    private record ConfigSnapshot(
            boolean creativeMode,
            boolean expertMode,
            boolean categoryMatching,
            int materialRevision
    ) {
        private String cacheKey() {
            return creativeMode + "|" + expertMode + "|" + categoryMatching + "|" + materialRevision;
        }
    }

    private record ParsedRules(Set<String> basicMaterials, Set<String> expertSkipList,
                               Set<ReplaceableMaterialHolder> holders) {
        private static final ParsedRules EMPTY = new ParsedRules(Set.of(), Set.of(), Set.of());

        private boolean covers(ResourceKey<Block> block) {
            for (ReplaceableMaterialHolder holder : holders) {
                if (containsBlock(holder, block)) {
                    return true;
                }
            }
            return false;
        }

        private Set<String> acceptedIds(ResourceKey<Block> block) {
            LinkedHashSet<String> ids = new LinkedHashSet<>();
            ids.add(block.identifier().toString().toLowerCase(Locale.ROOT));
            for (ReplaceableMaterialHolder holder : holders) {
                if (!containsBlock(holder, block)) {
                    continue;
                }
                for (ResourceKey<Block> replacement : holder.getAllReplaceableBlocks(block)) {
                    if (replacement != null) {
                        ids.add(replacement.identifier().toString().toLowerCase(Locale.ROOT));
                    }
                }
            }
            return ids;
        }

        private static boolean containsBlock(ReplaceableMaterialHolder holder, ResourceKey<Block> block) {
            for (Set<ResourceKey<Block>> blocks : holder.replaceableBlocks.values()) {
                if (blocks.contains(block)) {
                    return true;
                }
            }
            return false;
        }
    }
}
