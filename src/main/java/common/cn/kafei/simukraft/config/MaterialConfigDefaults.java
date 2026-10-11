package common.cn.kafei.simukraft.config;

import common.cn.kafei.simukraft.registry.ModBlocks;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.references.BlockItemIds;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;

import java.util.*;
import java.util.function.Supplier;

// TODO
// schedule to move this to datapacks or otherwise instead config-control
// sometimes, we will update this list, but the original saves will not implement it well
// when we update the mod version, we still need to reset config file
// 计划移动到数据包或者其他地方内
// 有时我们会更新这个列表，但是原始的存档却未正确应用这些修改
// 当我们更新模组的时候，我们仍然需要重置配置文件
public final class MaterialConfigDefaults {
    public static final Supplier<Set<ResourceKey<Block>>> ALL_MODE_BLOCK_BLACKLIST_BLOCKS = () -> {
        Set<ResourceKey<Block>> set = new HashSet<>();

        set.add(BlockItemIds.BEDROCK.block());
        set.add(BlockItemIds.COMMAND_BLOCK.block());
        set.add(BlockItemIds.CHAIN_COMMAND_BLOCK.block());
        set.add(BlockItemIds.REPEATING_COMMAND_BLOCK.block());
        set.add(BlockItemIds.BARRIER.block());
        set.add(BlockItemIds.STRUCTURE_BLOCK.block());
        set.add(BlockItemIds.STRUCTURE_VOID.block());
        set.add(BlockItemIds.JIGSAW.block());
        set.add(ModBlocks.BUILD_BOX.key());
        set.add(ModBlocks.CITY_CORE.key());
        set.add(ModBlocks.RESIDENTIAL_CONTROL_BOX.key());
        set.add(ModBlocks.COMMERCIAL_CONTROL_BOX.key());
        set.add(ModBlocks.INDUSTRIAL_CONTROL_BOX.key());
        set.add(ModBlocks.OTHER_CONTROL_BOX.key());
        set.add(ModBlocks.MEDICAL_CONTROL_BOX.key());
        set.add(ModBlocks.BANK_CONTROL_BOX.key());
        set.add(ModBlocks.EXCHANGE_CONTROL_BOX.key());
        set.add(ModBlocks.LOGISTICS_CLIENT_BOX.key());
        set.add(ModBlocks.LOGISTICS_SERVER_BOX.key());
        set.add(ModBlocks.NSUK_FARMLAND_BOX.key());

        return Set.copyOf(set);
    };

    public static final Supplier<Set<ResourceKey<Block>>> BASIC_MATERIALS_BLOCKS = () -> {
        Set<ResourceKey<Block>> set = new HashSet<>();
        set.add(BlockItemIds.IRON_BLOCK.block());
        set.add(BlockItemIds.GOLD_BLOCK.block());
        set.add(BlockItemIds.DIAMOND_BLOCK.block());
        set.add(BlockItemIds.EMERALD_BLOCK.block());
        BlockItemIds.COPPER_BLOCK.forEach(block -> set.add(block.block()));
        set.add(BlockItemIds.NETHERITE_BLOCK.block());
        set.add(BlockItemIds.LAPIS_BLOCK.block());
        set.add(BlockItemIds.REDSTONE_BLOCK.block());
        set.add(BlockItemIds.COAL_BLOCK.block());
        set.add(BlockItemIds.BAMBOO_BLOCK.block());
        return Set.copyOf(set);
    };

    public static final Supplier<Map<String, Set<Pair<ResourceKey<Block>, Boolean>>>> MATERIAL_CATEGORY_GROUPS_BLOCKS = () -> {
        Map<String, Set<Pair<ResourceKey<Block>, Boolean>>> map = new HashMap<>();
        {
            String toAdd = "bamboo_block";
            Set<Pair<ResourceKey<Block>, Boolean>> set = new HashSet<>();
            addCommons(set, BlockItemIds.BAMBOO_BLOCK.block());
            addNotCommons(set, BlockItemIds.BAMBOO_SLAB.block(), BlockItemIds.BAMBOO_STAIRS.block());
            map.put(toAdd, Set.copyOf(set));
        }
        {
            String toAdd = "bamboo_fence";
            Set<Pair<ResourceKey<Block>, Boolean>> set = new HashSet<>();
            addCommons(set, BlockItemIds.BAMBOO_FENCE.block(), BlockItemIds.BAMBOO_FENCE_GATE.block());
            map.put(toAdd, Set.copyOf(set));
        }
        {
            String toAdd = "black_stone_family";
            Set<Pair<ResourceKey<Block>, Boolean>> set = new HashSet<>();
            addCommons(set, BlockItemIds.BLACKSTONE.block(), BlockItemIds.CHISELED_POLISHED_BLACKSTONE.block(),
                    BlockItemIds.CRACKED_POLISHED_BLACKSTONE_BRICKS.block(), BlockItemIds.POLISHED_BLACKSTONE.block(),
                    BlockItemIds.POLISHED_BLACKSTONE_BRICKS.block());
            addNotCommons(set, BlockItemIds.BLACKSTONE_SLAB.block(), BlockItemIds.BLACKSTONE_STAIRS.block(),
                    BlockItemIds.BLACKSTONE_WALL.block(), BlockItemIds.POLISHED_BLACKSTONE_BRICK_SLAB.block(),
                    BlockItemIds.POLISHED_BLACKSTONE_BRICK_STAIRS.block(), BlockItemIds.POLISHED_BLACKSTONE_BRICK_WALL.block(),
                    BlockItemIds.POLISHED_BLACKSTONE_SLAB.block(), BlockItemIds.POLISHED_BLACKSTONE_WALL.block(),
                    BlockItemIds.POLISHED_BLACKSTONE_STAIRS.block());
            map.put(toAdd, Set.copyOf(set));
        }
        {
            String toAdd = "bricks";
            Set<Pair<ResourceKey<Block>, Boolean>> set = new HashSet<>();
            addCommons(set, BlockItemIds.BRICKS.block());
            addNotCommons(set, BlockItemIds.BRICK_SLAB.block(), BlockItemIds.BRICK_STAIRS.block(),
                    BlockItemIds.BRICK_WALL.block());
            map.put(toAdd, Set.copyOf(set));
        }
        {
            String toAdd = "cobblestone_family";
            Set<Pair<ResourceKey<Block>, Boolean>> set = new HashSet<>();
            addCommons(set, BlockItemIds.COBBLESTONE.block(), BlockItemIds.MOSSY_COBBLESTONE.block());
            addNotCommons(set, BlockItemIds.COBBLESTONE_SLAB.block(), BlockItemIds.COBBLESTONE_STAIRS.block(),
                    BlockItemIds.COBBLESTONE_WALL.block(), BlockItemIds.MOSSY_COBBLESTONE_SLAB.block(),
                    BlockItemIds.MOSSY_COBBLESTONE_STAIRS.block(), BlockItemIds.MOSSY_COBBLESTONE_WALL.block());
            map.put(toAdd, Set.copyOf(set));
        }
        {
            String toAdd = "concrete";
            Set<Pair<ResourceKey<Block>, Boolean>> set = new HashSet<>();
            BlockItemIds.CONCRETE.forEach(block -> addCommons(set, block.block()));
            map.put(toAdd, Set.copyOf(set));
        }
        {
            String toAdd = "concrete_powder";
            Set<Pair<ResourceKey<Block>, Boolean>> set = new HashSet<>();
            BlockItemIds.CONCRETE_POWDER.forEach(block -> addCommons(set, block.block()));
            map.put(toAdd, Set.copyOf(set));
        }
        {
            String toAdd = "deepslate_family";
            Set<Pair<ResourceKey<Block>, Boolean>> set = new HashSet<>();
            addCommons(set, BlockItemIds.DEEPSLATE.block(), BlockItemIds.COBBLED_DEEPSLATE.block(),
                    BlockItemIds.POLISHED_DEEPSLATE.block(), BlockItemIds.DEEPSLATE_BRICKS.block(),
                    BlockItemIds.DEEPSLATE_TILES.block(), BlockItemIds.CHISELED_DEEPSLATE.block(),
                    BlockItemIds.CRACKED_DEEPSLATE_BRICKS.block(), BlockItemIds.CRACKED_DEEPSLATE_TILES.block());
            addNotCommons(set, BlockItemIds.COBBLED_DEEPSLATE_SLAB.block(), BlockItemIds.COBBLED_DEEPSLATE_STAIRS.block(),
                    BlockItemIds.COBBLED_DEEPSLATE_WALL.block(), BlockItemIds.POLISHED_DEEPSLATE_SLAB.block(),
                    BlockItemIds.POLISHED_DEEPSLATE_STAIRS.block(), BlockItemIds.POLISHED_DEEPSLATE_WALL.block(),
                    BlockItemIds.DEEPSLATE_BRICK_SLAB.block(), BlockItemIds.DEEPSLATE_BRICK_STAIRS.block(),
                    BlockItemIds.DEEPSLATE_BRICK_WALL.block(), BlockItemIds.DEEPSLATE_TILE_SLAB.block(),
                    BlockItemIds.DEEPSLATE_TILE_STAIRS.block(), BlockItemIds.DEEPSLATE_TILE_WALL.block());
            map.put(toAdd, Set.copyOf(set));
        }
        {
            String toAdd = "fence_family";
            Set<Pair<ResourceKey<Block>, Boolean>> set = new HashSet<>();
            addCommons(set, BlockItemIds.ACACIA_FENCE.block(), BlockItemIds.BIRCH_FENCE.block(),
                    BlockItemIds.CHERRY_FENCE.block(), BlockItemIds.ACACIA_FENCE_GATE.block(),
                    BlockItemIds.BIRCH_FENCE_GATE.block(), BlockItemIds.CHERRY_FENCE_GATE.block(),
                    BlockItemIds.OAK_FENCE.block(), BlockItemIds.OAK_FENCE_GATE.block(),
                    BlockItemIds.SPRUCE_FENCE.block(), BlockItemIds.SPRUCE_FENCE_GATE.block(),
                    BlockItemIds.JUNGLE_FENCE.block(), BlockItemIds.JUNGLE_FENCE_GATE.block(),
                    BlockItemIds.MANGROVE_FENCE.block(), BlockItemIds.MANGROVE_FENCE_GATE.block(),
                    BlockItemIds.CRIMSON_FENCE.block(), BlockItemIds.CRIMSON_FENCE_GATE.block(),
                    BlockItemIds.WARPED_FENCE.block(), BlockItemIds.WARPED_FENCE_GATE.block(),
                    BlockItemIds.POPLAR_FENCE.block(), BlockItemIds.POPLAR_FENCE_GATE.block());
            map.put(toAdd, Set.copyOf(set));
        }
        {
            String toAdd = "glass_family";
            Set<Pair<ResourceKey<Block>, Boolean>> set = new HashSet<>();
            addCommons(set, BlockItemIds.GLASS.block());
            BlockItemIds.STAINED_GLASS.forEach(block -> addCommons(set, block.block()));
            addNotCommons(set, BlockItemIds.GLASS_PANE.block());
            BlockItemIds.STAINED_GLASS_PANE.forEach(block -> addNotCommons(set, block.block()));
            map.put(toAdd, Set.copyOf(set));
        }
        {
            String toAdd = "glass_pane_extra";
            Set<Pair<ResourceKey<Block>, Boolean>> set = new HashSet<>();
            BlockItemIds.STAINED_GLASS_PANE.forEach(block -> addCommons(set, block.block()));
            map.put(toAdd, Set.copyOf(set));
        }
        {
            String toAdd = "log_family";
            Set<Pair<ResourceKey<Block>, Boolean>> set = new HashSet<>();
            addCommons(set, BlockItemIds.ACACIA_LOG.block(), BlockItemIds.BIRCH_LOG.block(),
                    BlockItemIds.CHERRY_LOG.block(), BlockItemIds.DARK_OAK_LOG.block(),
                    BlockItemIds.JUNGLE_LOG.block(), BlockItemIds.MANGROVE_LOG.block(),
                    BlockItemIds.OAK_LOG.block(), BlockItemIds.SPRUCE_LOG.block(),
                    BlockItemIds.WARPED_STEM.block(), BlockItemIds.CRIMSON_STEM.block(),
                    BlockItemIds.POPLAR_LOG.block());
            map.put(toAdd, Set.copyOf(set));
        }
        {
            String toAdd = "packed";
            Set<Pair<ResourceKey<Block>, Boolean>> set = new HashSet<>();
            addCommons(set, BlockItemIds.PACKED_MUD.block(), BlockItemIds.MUD_BRICKS.block());
            addNotCommons(set, BlockItemIds.MUD_BRICK_STAIRS.block(), BlockItemIds.MUD_BRICK_SLAB.block(),
                    BlockItemIds.MUD_BRICK_WALL.block());
            map.put(toAdd, Set.copyOf(set));
        }
        {
            String toAdd = "planks_family";
            Set<Pair<ResourceKey<Block>, Boolean>> set = new HashSet<>();
            addCommons(set, BlockItemIds.OAK_PLANKS.block(), BlockItemIds.SPRUCE_PLANKS.block(),
                    BlockItemIds.BIRCH_PLANKS.block(), BlockItemIds.JUNGLE_PLANKS.block(),
                    BlockItemIds.ACACIA_PLANKS.block(), BlockItemIds.DARK_OAK_PLANKS.block(),
                    BlockItemIds.MANGROVE_PLANKS.block(), BlockItemIds.CHERRY_PLANKS.block(),
                    BlockItemIds.CRIMSON_PLANKS.block(), BlockItemIds.WARPED_PLANKS.block(),
                    BlockItemIds.POPLAR_PLANKS.block());
            addNotCommons(set, BlockItemIds.OAK_SLAB.block(), BlockItemIds.OAK_STAIRS.block(),
                    BlockItemIds.SPRUCE_SLAB.block(), BlockItemIds.SPRUCE_STAIRS.block(),
                    BlockItemIds.BIRCH_SLAB.block(), BlockItemIds.BIRCH_STAIRS.block(),
                    BlockItemIds.JUNGLE_SLAB.block(), BlockItemIds.JUNGLE_STAIRS.block(),
                    BlockItemIds.ACACIA_SLAB.block(), BlockItemIds.ACACIA_STAIRS.block(),
                    BlockItemIds.DARK_OAK_SLAB.block(), BlockItemIds.DARK_OAK_STAIRS.block(),
                    BlockItemIds.MANGROVE_SLAB.block(), BlockItemIds.MANGROVE_STAIRS.block(),
                    BlockItemIds.CHERRY_SLAB.block(), BlockItemIds.CHERRY_STAIRS.block(),
                    BlockItemIds.CRIMSON_SLAB.block(), BlockItemIds.CRIMSON_STAIRS.block(),
                    BlockItemIds.WARPED_SLAB.block(), BlockItemIds.WARPED_STAIRS.block(),
                    BlockItemIds.POPLAR_SLAB.block(), BlockItemIds.POPLAR_STAIRS.block());
            map.put(toAdd, Set.copyOf(set));
        }
        {
            String toAdd = "red_sandstone";
            Set<Pair<ResourceKey<Block>, Boolean>> set = new HashSet<>();
            addCommons(set, BlockItemIds.CUT_RED_SANDSTONE.block(), BlockItemIds.CHISELED_RED_SANDSTONE.block(),
                    BlockItemIds.RED_SANDSTONE.block(), BlockItemIds.SMOOTH_RED_SANDSTONE.block());
            addNotCommons(set, BlockItemIds.RED_SANDSTONE_SLAB.block(), BlockItemIds.RED_SANDSTONE_STAIRS.block(),
                    BlockItemIds.RED_SANDSTONE_WALL.block(), BlockItemIds.SMOOTH_RED_SANDSTONE_SLAB.block(),
                    BlockItemIds.SMOOTH_RED_SANDSTONE_STAIRS.block(), BlockItemIds.CUT_RED_SANDSTONE_SLAB.block());
            map.put(toAdd, Set.copyOf(set));
        }
        {
            String toAdd = "sandstone";
            Set<Pair<ResourceKey<Block>, Boolean>> set = new HashSet<>();
            addCommons(set, BlockItemIds.CUT_SANDSTONE.block(), BlockItemIds.CHISELED_SANDSTONE.block(),
                    BlockItemIds.SANDSTONE.block(), BlockItemIds.SMOOTH_SANDSTONE.block());
            addNotCommons(set, BlockItemIds.SANDSTONE_SLAB.block(), BlockItemIds.SANDSTONE_STAIRS.block(),
                    BlockItemIds.SANDSTONE_WALL.block(), BlockItemIds.SMOOTH_SANDSTONE_SLAB.block(),
                    BlockItemIds.SMOOTH_SANDSTONE_STAIRS.block(), BlockItemIds.CUT_SANDSTONE_SLAB.block());
            map.put(toAdd, Set.copyOf(set));
        }
        {
            String toAdd = "stone_bricks";
            Set<Pair<ResourceKey<Block>, Boolean>> set = new HashSet<>();
            addCommons(set, BlockItemIds.STONE_BRICKS.block(), BlockItemIds.CRACKED_STONE_BRICKS.block(),
                    BlockItemIds.MOSSY_STONE_BRICKS.block(), BlockItemIds.CHISELED_STONE_BRICKS.block());
            addNotCommons(set, BlockItemIds.STONE_BRICK_STAIRS.block(), BlockItemIds.STONE_BRICK_SLAB.block(),
                    BlockItemIds.STONE_BRICK_WALL.block(), BlockItemIds.MOSSY_STONE_BRICK_STAIRS.block(),
                    BlockItemIds.MOSSY_STONE_BRICK_SLAB.block(), BlockItemIds.MOSSY_STONE_BRICK_WALL.block());
            map.put(toAdd, Set.copyOf(set));
        }
        {
            String toAdd = "stone_family";
            Set<Pair<ResourceKey<Block>, Boolean>> set = new HashSet<>();
            addCommons(set, BlockItemIds.STONE.block(), BlockItemIds.CALCITE.block(),
                    BlockItemIds.DIORITE.block(), BlockItemIds.TUFF.block(),
                    BlockItemIds.GRANITE.block(), BlockItemIds.ANDESITE.block(),
                    BlockItemIds.POLISHED_ANDESITE.block(), BlockItemIds.POLISHED_GRANITE.block(),
                    BlockItemIds.POLISHED_DIORITE.block());
            addNotCommons(set, BlockItemIds.STONE_SLAB.block(), BlockItemIds.STONE_STAIRS.block(),
                    BlockItemIds.ANDESITE_SLAB.block(), BlockItemIds.ANDESITE_STAIRS.block(),
                    BlockItemIds.ANDESITE_WALL.block(), BlockItemIds.POLISHED_ANDESITE_SLAB.block(),
                    BlockItemIds.POLISHED_ANDESITE_STAIRS.block(), BlockItemIds.GRANITE_SLAB.block(),
                    BlockItemIds.GRANITE_STAIRS.block(), BlockItemIds.GRANITE_WALL.block(),
                    BlockItemIds.POLISHED_GRANITE_SLAB.block(), BlockItemIds.POLISHED_GRANITE_STAIRS.block(),
                    BlockItemIds.DIORITE_SLAB.block(), BlockItemIds.DIORITE_STAIRS.block(),
                    BlockItemIds.DIORITE_WALL.block(), BlockItemIds.POLISHED_DIORITE_SLAB.block(),
                    BlockItemIds.POLISHED_DIORITE_STAIRS.block());
            map.put(toAdd, Set.copyOf(set));
        }
        {
            String toAdd = "terracotta";
            Set<Pair<ResourceKey<Block>, Boolean>> set = new HashSet<>();
            addCommons(set, BlockItemIds.TERRACOTTA.block());
            BlockItemIds.DYED_TERRACOTTA.forEach(block -> addCommons(set, block.block()));
            BlockItemIds.GLAZED_TERRACOTTA.forEach(block -> addNotCommons(set, block.block()));
            map.put(toAdd, Set.copyOf(set));
        }
        {
            String toAdd = "glazed_terracotta_extra";
            Set<Pair<ResourceKey<Block>, Boolean>> set = new HashSet<>();
            BlockItemIds.GLAZED_TERRACOTTA.forEach(block -> addCommons(set, block.block()));
            map.put(toAdd, Set.copyOf(set));
        }
        {
            String toAdd = "wool_family";
            Set<Pair<ResourceKey<Block>, Boolean>> set = new HashSet<>();
            BlockItemIds.WOOL.forEach(block -> addCommons(set, block.block()));
            map.put(toAdd, Set.copyOf(set));
        }
        {
            String toAdd = "copper_blocks";
            Set<Pair<ResourceKey<Block>, Boolean>> set = new HashSet<>();
            BlockItemIds.COPPER_BLOCK.forEach(block -> addCommons(set, block.block()));
            map.put(toAdd, Set.copyOf(set));
        }
        return Map.copyOf(map);
    };

    public static final Supplier<Set<ResourceKey<Block>>> EXPERT_MODE_SKIP_LIST_BLOCKS = () -> {
        Set<ResourceKey<Block>> set = new HashSet<>();
        set.add(BlockItemIds.BEDROCK.block());
        set.add(BlockItemIds.COMMAND_BLOCK.block());
        set.add(BlockItemIds.CHAIN_COMMAND_BLOCK.block());
        set.add(BlockItemIds.REPEATING_COMMAND_BLOCK.block());
        set.add(BlockItemIds.BARRIER.block());
        set.add(BlockItemIds.STRUCTURE_BLOCK.block());
        set.add(BlockItemIds.STRUCTURE_VOID.block());
        set.add(BlockItemIds.JIGSAW.block());
        set.add(ModBlocks.BUILD_BOX.key());
        set.add(ModBlocks.CITY_CORE.key());
        set.add(ModBlocks.RESIDENTIAL_CONTROL_BOX.key());
        set.add(ModBlocks.COMMERCIAL_CONTROL_BOX.key());
        set.add(ModBlocks.INDUSTRIAL_CONTROL_BOX.key());
        set.add(ModBlocks.OTHER_CONTROL_BOX.key());
        set.add(ModBlocks.MEDICAL_CONTROL_BOX.key());
        set.add(ModBlocks.BANK_CONTROL_BOX.key());
        set.add(ModBlocks.EXCHANGE_CONTROL_BOX.key());
        set.add(ModBlocks.NSUK_FARMLAND_BOX.key());
        return Set.copyOf(set);
    };

    private static void regReplaceable(Set<Pair<ResourceKey<Block>, Boolean>> list, boolean common, ResourceKey<Block>[] blocks) {
        for (ResourceKey<Block> block : blocks) {
            list.add(Pair.of(block, common));
        }
    }

    @SafeVarargs
    private static void addCommons(Set<Pair<ResourceKey<Block>, Boolean>> list, ResourceKey<Block>... blocks) {
        regReplaceable(list, true, blocks);
    }

    @SafeVarargs
    private static void addNotCommons(Set<Pair<ResourceKey<Block>, Boolean>> list, ResourceKey<Block>... blocks) {
        regReplaceable(list, false, blocks);
    }

    public static final List<String> ALL_MODE_BLOCK_BLACKLIST = List.of(
            "minecraft:bedrock",
            "minecraft:command_block",
            "minecraft:chain_command_block",
            "minecraft:repeating_command_block",
            "minecraft:barrier",
            "minecraft:structure_block",
            "minecraft:structure_void",
            "minecraft:jigsaw",
            "simukraft:build_box",
            "simukraft:city_core",
            "simukraft:residential_control_box",
            "simukraft:commercial_control_box",
            "simukraft:industrial_control_box",
            "simukraft:other_control_box",
            "simukraft:medical_control_box",
            "simukraft:bank_control_box",
            "simukraft:exchange_control_box",
            "simukraft:logistics_client_box",
            "simukraft:logistics_server_box",
            "simukraft:nsuk_farmland_box"
    );

    public static final List<String> BASIC_MATERIALS = List.of(
            "minecraft:iron_block",
            "minecraft:gold_block",
            "minecraft:diamond_block",
            "minecraft:emerald_block",
            "minecraft:copper_block",
            "minecraft:netherite_block",
            "minecraft:lapis_block",
            "minecraft:redstone_block",
            "minecraft:coal_block",
            "minecraft:bamboo_block"
    );

    public static final List<String> MATERIAL_CATEGORY_GROUPS = List.of(
            "bamboo_block|minecraft:bamboo_planks|minecraft:bamboo_slab,minecraft:bamboo_stairs,minecraft:bamboo_planks",
            "bamboo_fence||minecraft:bamboo_fence,minecraft:bamboo_fence_gate",
            "black_stone_family|minecraft:blackstone,minecraft:chiseled_polished_blackstone,minecraft:cracked_polished_blackstone_bricks,minecraft:polished_blackstone,minecraft:polished_blackstone_bricks|minecraft:blackstone,minecraft:blackstone_slab,minecraft:blackstone_stairs,minecraft:blackstone_wall,minecraft:chiseled_polished_blackstone,minecraft:cracked_polished_blackstone_bricks,minecraft:polished_blackstone,minecraft:polished_blackstone_brick_slab,minecraft:polished_blackstone_brick_stairs,minecraft:polished_blackstone_brick_wall,minecraft:polished_blackstone_bricks,minecraft:polished_blackstone_slab,minecraft:polished_blackstone_wall,minecraft:polished_blackstone_stairs",
            "bricks|minecraft:bricks|minecraft:bricks,minecraft:brick_slab,minecraft:brick_stairs,minecraft:brick_wall",
            "cobblestone_family|minecraft:cobblestone,minecraft:mossy_cobblestone|minecraft:cobblestone,minecraft:cobblestone_slab,minecraft:cobblestone_stairs,minecraft:cobblestone_wall,minecraft:mossy_cobblestone,minecraft:mossy_cobblestone_slab,minecraft:mossy_cobblestone_stairs,minecraft:mossy_cobblestone_wall",
            "concrete||minecraft:white_concrete,minecraft:orange_concrete,minecraft:magenta_concrete,minecraft:light_blue_concrete,minecraft:yellow_concrete,minecraft:lime_concrete,minecraft:pink_concrete,minecraft:gray_concrete,minecraft:light_gray_concrete,minecraft:cyan_concrete,minecraft:purple_concrete,minecraft:blue_concrete,minecraft:brown_concrete,minecraft:green_concrete,minecraft:red_concrete,minecraft:black_concrete",
            "concrete_powder||minecraft:black_concrete_powder,minecraft:blue_concrete_powder,minecraft:brown_concrete_powder,minecraft:white_concrete_powder,minecraft:orange_concrete_powder,minecraft:magenta_concrete_powder,minecraft:light_blue_concrete_powder,minecraft:yellow_concrete_powder,minecraft:lime_concrete_powder,minecraft:pink_concrete_powder,minecraft:gray_concrete_powder,minecraft:light_gray_concrete_powder,minecraft:cyan_concrete_powder,minecraft:purple_concrete_powder,minecraft:green_concrete_powder,minecraft:red_concrete_powder",
            "deepslate_family|minecraft:deepslate,minecraft:cobbled_deepslate,minecraft:polished_deepslate,minecraft:deepslate_bricks,minecraft:deepslate_tiles,minecraft:chiseled_deepslate,minecraft:cracked_deepslate_bricks,minecraft:cracked_deepslate_tiles|minecraft:cobbled_deepslate_slab,minecraft:cobbled_deepslate_stairs,minecraft:cobbled_deepslate_wall,minecraft:polished_deepslate_slab,minecraft:polished_deepslate_stairs,minecraft:polished_deepslate_wall,minecraft:deepslate_brick_slab,minecraft:deepslate_brick_stairs,minecraft:deepslate_brick_wall,minecraft:deepslate_tile_slab,minecraft:deepslate_tile_stairs,minecraft:deepslate_tile_wall,minecraft:deepslate,minecraft:cobbled_deepslate,minecraft:polished_deepslate,minecraft:deepslate_bricks,minecraft:deepslate_tiles,minecraft:chiseled_deepslate,minecraft:cracked_deepslate_bricks,minecraft:cracked_deepslate_tiles",
            "fence_family||minecraft:acacia_fence,minecraft:birch_fence,minecraft:cherry_fence,minecraft:acacia_fence_gate,minecraft:birch_fence_gate,minecraft:cherry_fence_gate,minecraft:oak_fence,minecraft:oak_fence_gate,minecraft:spruce_fence,minecraft:spruce_fence_gate,minecraft:jungle_fence,minecraft:jungle_fence_gate,minecraft:mangrove_fence,minecraft:mangrove_fence_gate,minecraft:crimson_fence,minecraft:crimson_fence_gate,minecraft:warped_fence,minecraft:warped_fence_gate",
            "glass_family|minecraft:black_stained_glass,minecraft:blue_stained_glass,minecraft:brown_stained_glass,minecraft:cyan_stained_glass,minecraft:white_stained_glass,minecraft:orange_stained_glass,minecraft:magenta_stained_glass,minecraft:light_blue_stained_glass,minecraft:yellow_stained_glass,minecraft:lime_stained_glass,minecraft:pink_stained_glass,minecraft:gray_stained_glass,minecraft:light_gray_stained_glass,minecraft:purple_stained_glass,minecraft:green_stained_glass,minecraft:red_stained_glass,minecraft:glass|minecraft:glass,minecraft:black_stained_glass,minecraft:black_stained_glass_pane,minecraft:blue_stained_glass,minecraft:blue_stained_glass_pane,minecraft:brown_stained_glass,minecraft:brown_stained_glass_pane,minecraft:cyan_stained_glass,minecraft:cyan_stained_glass_pane,minecraft:white_stained_glass,minecraft:white_stained_glass_pane,minecraft:orange_stained_glass,minecraft:orange_stained_glass_pane,minecraft:magenta_stained_glass,minecraft:magenta_stained_glass_pane,minecraft:light_blue_stained_glass,minecraft:light_blue_stained_glass_pane,minecraft:yellow_stained_glass,minecraft:yellow_stained_glass_pane,minecraft:pink_stained_glass,minecraft:pink_stained_glass_pane,minecraft:lime_stained_glass,minecraft:lime_stained_glass_pane,minecraft:gray_stained_glass,minecraft:gray_stained_glass_pane,minecraft:light_gray_stained_glass,minecraft:light_gray_stained_glass_pane,minecraft:glass_pane,minecraft:purple_stained_glass,minecraft:purple_stained_glass_pane,minecraft:green_stained_glass,minecraft:green_stained_glass_pane,minecraft:red_stained_glass,minecraft:red_stained_glass_pane",
            "log_family||minecraft:acacia_log,minecraft:birch_log,minecraft:cherry_log,minecraft:dark_oak_log,minecraft:jungle_log,minecraft:mangrove_log,minecraft:oak_log,minecraft:spruce_log,minecraft:warped_stem,minecraft:crimson_stem",
            "packed|minecraft:packed_mud,minecraft:mud_bricks|minecraft:packed_mud,minecraft:mud_bricks,minecraft:mud_brick_stairs,minecraft:mud_brick_slab,minecraft:mud_brick_wall",
            "planks_family|minecraft:oak_planks,minecraft:spruce_planks,minecraft:birch_planks,minecraft:jungle_planks,minecraft:acacia_planks,minecraft:dark_oak_planks,minecraft:mangrove_planks,minecraft:cherry_planks,minecraft:crimson_planks,minecraft:warped_planks|minecraft:oak_slab,minecraft:oak_stairs,minecraft:spruce_slab,minecraft:spruce_stairs,minecraft:birch_slab,minecraft:birch_stairs,minecraft:jungle_slab,minecraft:jungle_stairs,minecraft:acacia_slab,minecraft:acacia_stairs,minecraft:dark_oak_slab,minecraft:dark_oak_stairs,minecraft:mangrove_slab,minecraft:mangrove_stairs,minecraft:cherry_slab,minecraft:cherry_stairs,minecraft:crimson_slab,minecraft:crimson_stairs,minecraft:warped_slab,minecraft:warped_stairs,minecraft:oak_planks,minecraft:dark_oak_planks,minecraft:spruce_planks,minecraft:birch_planks,minecraft:jungle_planks,minecraft:acacia_planks,minecraft:mangrove_planks,minecraft:cherry_planks,minecraft:crimson_planks,minecraft:warped_planks",
            "red_sandstone|minecraft:cut_red_sandstone,minecraft:chiseled_red_sandstone,minecraft:red_sandstone,minecraft:smooth_red_sandstone|minecraft:red_sandstone,minecraft:smooth_red_sandstone,minecraft:chiseled_red_sandstone,minecraft:cut_red_sandstone,minecraft:cut_red_sandstone_slab,minecraft:red_sandstone_stairs,minecraft:smooth_red_sandstone_stairs,minecraft:red_sandstone_wall",
            "sandstone|minecraft:cut_sandstone,minecraft:chiseled_sandstone,minecraft:smooth_sandstone,minecraft:sandstone|minecraft:sandstone,minecraft:smooth_sandstone,minecraft:chiseled_sandstone,minecraft:cut_sandstone,minecraft:sandstone_stairs,minecraft:sandstone_slab,minecraft:sandstone_wall,minecraft:smooth_sandstone_slab,minecraft:smooth_sandstone_stairs,minecraft:cut_sandstone_slab",
            "stone_bricks|minecraft:stone_bricks,minecraft:cracked_stone_bricks,minecraft:mossy_stone_bricks,minecraft:chiseled_stone_bricks|minecraft:stone_brick_stairs,minecraft:stone_brick_slab,minecraft:stone_brick_wall,minecraft:mossy_stone_brick_stairs,minecraft:mossy_stone_brick_slab,minecraft:mossy_stone_brick_wall,minecraft:stone_bricks,minecraft:chiseled_stone_bricks,minecraft:cracked_stone_bricks,minecraft:mossy_stone_bricks",
            "stone_family|minecraft:stone,minecraft:calcite,minecraft:diorite,minecraft:tuff,minecraft:granite,minecraft:andesite,minecraft:polished_andesite,minecraft:polished_granite,minecraft:polished_diorite|minecraft:stone_slab,minecraft:stone_stairs,minecraft:andesite_slab,minecraft:andesite_stairs,minecraft:andesite_wall,minecraft:polished_andesite,minecraft:polished_andesite_slab,minecraft:polished_andesite_stairs,minecraft:granite_slab,minecraft:granite_stairs,minecraft:granite_wall,minecraft:polished_granite,minecraft:polished_granite_slab,minecraft:polished_granite_stairs,minecraft:diorite_slab,minecraft:diorite_stairs,minecraft:diorite_wall,minecraft:polished_diorite,minecraft:polished_diorite_slab,minecraft:polished_diorite_stairs,minecraft:stone,minecraft:tuff,minecraft:diorite,minecraft:granite,minecraft:andesite,minecraft:calcite",
            "terracotta||minecraft:terracotta,minecraft:white_terracotta,minecraft:orange_terracotta,minecraft:magenta_terracotta,minecraft:light_blue_terracotta,minecraft:yellow_terracotta,minecraft:lime_terracotta,minecraft:pink_terracotta,minecraft:gray_terracotta,minecraft:light_gray_terracotta,minecraft:cyan_terracotta,minecraft:purple_terracotta,minecraft:blue_terracotta,minecraft:brown_terracotta,minecraft:green_terracotta,minecraft:red_terracotta,minecraft:black_terracotta",
            "wool_family||minecraft:white_wool,minecraft:orange_wool,minecraft:magenta_wool,minecraft:light_blue_wool,minecraft:yellow_wool,minecraft:lime_wool,minecraft:pink_wool,minecraft:gray_wool,minecraft:light_gray_wool,minecraft:cyan_wool,minecraft:purple_wool,minecraft:blue_wool,minecraft:brown_wool,minecraft:green_wool,minecraft:red_wool,minecraft:black_wool"
    );

    public static final List<String> EXPERT_MODE_SKIP_LIST = List.of(
            "minecraft:bedrock",
            "minecraft:command_block",
            "minecraft:chain_command_block",
            "minecraft:repeating_command_block",
            "minecraft:barrier",
            "minecraft:structure_block",
            "minecraft:structure_void",
            "minecraft:jigsaw",
            "simukraft:build_box",
            "simukraft:city_core",
            "simukraft:residential_control_box",
            "simukraft:commercial_control_box",
            "simukraft:industrial_control_box",
            "simukraft:other_control_box",
            "simukraft:medical_control_box",
            "simukraft:bank_control_box",
            "simukraft:exchange_control_box",
            "simukraft:wool_farm_control_box",
            "simukraft:building_material_store_control_box",
            "simukraft:beef_farm_control_box",
            "simukraft:nsuk_meat_shop_control_box",
            "simukraft:nsuk_farmland_box",
            "simukraft:fruit_shop_control_box",
            "simukraft:bakery_control_box",
            "minecraft:petrified_oak_slab",
            "simukraft:white_carpet"
    );
}
