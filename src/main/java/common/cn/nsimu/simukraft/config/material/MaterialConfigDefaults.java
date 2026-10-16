package common.cn.nsimu.simukraft.config.material;

import common.cn.nsimu.simukraft.registry.ModBlocks;
import net.minecraft.references.BlockItemIds;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;

import java.util.HashSet;
import java.util.Set;
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
        set.add(BlockItemIds.REINFORCED_DEEPSLATE.block());
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

    public static final Supplier<Set<ReplaceableMaterialHolder>> MATERIAL_CATEGORY_GROUPS_BLOCKS = () -> {
        Set<ReplaceableMaterialHolder> set = new HashSet<>();
        {
            ReplaceableMaterialHolder bamboo_block = new ReplaceableMaterialHolder("bamboo_block");
            bamboo_block.addUnreplaceableBlock(BlockItemIds.BAMBOO_BLOCK.block());

            ReplaceableMaterialHolder bamboo_block_variants = new ReplaceableMaterialHolder("bamboo_block_variants");
            bamboo_block_variants.addUnreplaceableBlock(BlockItemIds.STRIPPED_BAMBOO_BLOCK.block());
            bamboo_block_variants.addReplaceableHolder(bamboo_block);

            set.add(bamboo_block);
            set.add(bamboo_block_variants);
        }
        {
            ReplaceableMaterialHolder bamboo_planks = new ReplaceableMaterialHolder("bamboo_planks");
            bamboo_planks.addUnreplaceableBlock(BlockItemIds.BAMBOO_PLANKS.block());

            ReplaceableMaterialHolder bamboo_planks_variants = new ReplaceableMaterialHolder("bamboo_planks_variants");
            bamboo_planks_variants.addUnreplaceableBlock(BlockItemIds.BAMBOO_SLAB.block(), BlockItemIds.BAMBOO_STAIRS.block());
            bamboo_planks_variants.addReplaceableHolder(bamboo_planks);

            ReplaceableMaterialHolder bamboo_mosaic = new ReplaceableMaterialHolder("bamboo_mosaic");
            bamboo_mosaic.addUnreplaceableBlock(BlockItemIds.BAMBOO_MOSAIC.block());
            bamboo_mosaic.addReplaceableHolder(bamboo_planks);

            ReplaceableMaterialHolder bamboo_mosaic_variants = new ReplaceableMaterialHolder("bamboo_mosaic_variants");
            bamboo_mosaic_variants.addUnreplaceableBlock(BlockItemIds.BAMBOO_MOSAIC_SLAB.block(), BlockItemIds.BAMBOO_MOSAIC_STAIRS.block());
            bamboo_mosaic_variants.addReplaceableHolder(bamboo_mosaic);

            set.add(bamboo_planks);
            set.add(bamboo_planks_variants);
            set.add(bamboo_mosaic);
            set.add(bamboo_mosaic_variants);
        }
        {
            ReplaceableMaterialHolder bamboo_fence = new ReplaceableMaterialHolder("bamboo_fence");
            bamboo_fence.addReplaceableBlock(BlockItemIds.BAMBOO_FENCE.block(), BlockItemIds.BAMBOO_FENCE_GATE.block());

            set.add(bamboo_fence);
        }
        {
            ReplaceableMaterialHolder black_stone_family = new ReplaceableMaterialHolder("black_stone_family");
            black_stone_family.addReplaceableBlock(BlockItemIds.BLACKSTONE.block(), BlockItemIds.CHISELED_POLISHED_BLACKSTONE.block(),
                    BlockItemIds.CRACKED_POLISHED_BLACKSTONE_BRICKS.block(), BlockItemIds.POLISHED_BLACKSTONE.block(),
                    BlockItemIds.POLISHED_BLACKSTONE_BRICKS.block());

            ReplaceableMaterialHolder black_stone_family_variants = new ReplaceableMaterialHolder("black_stone_family_variants");
            black_stone_family_variants.addReplaceableBlock(BlockItemIds.BLACKSTONE_SLAB.block(),
                    BlockItemIds.POLISHED_BLACKSTONE_SLAB.block(), BlockItemIds.POLISHED_BLACKSTONE_BRICK_SLAB.block());
            black_stone_family_variants.addReplaceableBlock(BlockItemIds.BLACKSTONE_STAIRS.block(),
                    BlockItemIds.POLISHED_BLACKSTONE_STAIRS.block(), BlockItemIds.POLISHED_BLACKSTONE_BRICK_STAIRS.block());
            black_stone_family_variants.addReplaceableBlock(BlockItemIds.BLACKSTONE_WALL.block(),
                    BlockItemIds.POLISHED_BLACKSTONE_BRICK_WALL.block(), BlockItemIds.POLISHED_BLACKSTONE_WALL.block());
            black_stone_family_variants.addReplaceableHolder(black_stone_family);

            set.add(black_stone_family);
            set.add(black_stone_family_variants);
        }
        {
            ReplaceableMaterialHolder bricks = new ReplaceableMaterialHolder("bricks");
            bricks.addUnreplaceableBlock(BlockItemIds.BRICKS.block());

            ReplaceableMaterialHolder bricks_variants = new ReplaceableMaterialHolder("bricks_variants");
            bricks_variants.addUnreplaceableBlock(BlockItemIds.BRICK_SLAB.block(), BlockItemIds.BRICK_STAIRS.block(),
                    BlockItemIds.BRICK_WALL.block());
            bricks_variants.addReplaceableHolder(bricks);

            set.add(bricks);
            set.add(bricks_variants);
        }
        {
            ReplaceableMaterialHolder stone_family = new ReplaceableMaterialHolder("stone_family");
            stone_family.addReplaceableBlock(BlockItemIds.STONE.block(), BlockItemIds.CALCITE.block(),
                    BlockItemIds.DIORITE.block(), BlockItemIds.TUFF.block(),
                    BlockItemIds.GRANITE.block(), BlockItemIds.ANDESITE.block(),
                    BlockItemIds.POLISHED_ANDESITE.block(), BlockItemIds.POLISHED_GRANITE.block(),
                    BlockItemIds.POLISHED_DIORITE.block());

            ReplaceableMaterialHolder stone_family_variants = new ReplaceableMaterialHolder("stone_family_variants");
            stone_family_variants.addReplaceableBlock(BlockItemIds.STONE_SLAB.block(), BlockItemIds.ANDESITE_SLAB.block(),
                    BlockItemIds.POLISHED_ANDESITE_SLAB.block(), BlockItemIds.GRANITE_SLAB.block(),
                    BlockItemIds.POLISHED_GRANITE_SLAB.block(), BlockItemIds.DIORITE_SLAB.block(),
                    BlockItemIds.POLISHED_DIORITE_SLAB.block());
            stone_family_variants.addReplaceableBlock(BlockItemIds.STONE_STAIRS.block(), BlockItemIds.ANDESITE_STAIRS.block(),
                    BlockItemIds.POLISHED_ANDESITE_STAIRS.block(), BlockItemIds.GRANITE_STAIRS.block(),
                    BlockItemIds.POLISHED_GRANITE_STAIRS.block(), BlockItemIds.DIORITE_STAIRS.block(),
                    BlockItemIds.POLISHED_DIORITE_STAIRS.block());
            stone_family_variants.addReplaceableBlock(BlockItemIds.ANDESITE_WALL.block(), BlockItemIds.GRANITE_WALL.block(),
                    BlockItemIds.DIORITE_WALL.block());
            stone_family_variants.addReplaceableHolder(stone_family);

            set.add(stone_family);
            set.add(stone_family_variants);
        }
        {
            ReplaceableMaterialHolder stone_bricks = new ReplaceableMaterialHolder("stone_bricks");
            stone_bricks.addReplaceableBlock(BlockItemIds.STONE_BRICKS.block(), BlockItemIds.CRACKED_STONE_BRICKS.block(),
                    BlockItemIds.MOSSY_STONE_BRICKS.block(), BlockItemIds.CHISELED_STONE_BRICKS.block());

            ReplaceableMaterialHolder stone_bricks_variants = new ReplaceableMaterialHolder("stone_bricks_variants");

            stone_bricks_variants.addReplaceableBlock(BlockItemIds.STONE_BRICK_SLAB.block(), BlockItemIds.MOSSY_STONE_BRICK_SLAB.block());
            stone_bricks_variants.addReplaceableBlock(BlockItemIds.STONE_BRICK_STAIRS.block(), BlockItemIds.MOSSY_STONE_BRICK_STAIRS.block());
            stone_bricks_variants.addReplaceableBlock(BlockItemIds.STONE_BRICK_WALL.block(), BlockItemIds.MOSSY_STONE_BRICK_WALL.block());
            stone_bricks_variants.addReplaceableHolder(stone_bricks);

            set.add(stone_bricks);
            set.add(stone_bricks_variants);
        }
        {
            ReplaceableMaterialHolder cobblestone_family = new ReplaceableMaterialHolder("cobblestone_family");
            cobblestone_family.addReplaceableBlock(BlockItemIds.COBBLESTONE.block(), BlockItemIds.MOSSY_COBBLESTONE.block());

            ReplaceableMaterialHolder cobblestone_family_variants = new ReplaceableMaterialHolder("cobblestone_family_variants");
            cobblestone_family_variants.addReplaceableBlock(BlockItemIds.COBBLESTONE_SLAB.block(), BlockItemIds.MOSSY_COBBLESTONE_SLAB.block());
            cobblestone_family_variants.addReplaceableBlock(BlockItemIds.COBBLESTONE_STAIRS.block(), BlockItemIds.MOSSY_COBBLESTONE_STAIRS.block());
            cobblestone_family_variants.addReplaceableBlock(BlockItemIds.COBBLESTONE_WALL.block(), BlockItemIds.MOSSY_COBBLESTONE_WALL.block());
            cobblestone_family_variants.addReplaceableHolder(cobblestone_family);

            set.add(cobblestone_family);
            set.add(cobblestone_family_variants);
        }
        {
            ReplaceableMaterialHolder concrete = new ReplaceableMaterialHolder("concrete");
            Set<ResourceKey<Block>> concrete_blocks = new HashSet<>();
            BlockItemIds.CONCRETE.forEach(block -> concrete_blocks.add(block.block()));
            concrete.addReplaceableBlock(concrete_blocks);

            ReplaceableMaterialHolder concrete_variants = new ReplaceableMaterialHolder("concrete_variants");
            Set<ResourceKey<Block>> concrete_slabs = new HashSet<>();
            BlockItemIds.CONCRETE_SLAB.forEach(block -> concrete_slabs.add(block.block()));
            concrete_variants.addReplaceableBlock(concrete_slabs);
            Set<ResourceKey<Block>> concrete_stairs = new HashSet<>();
            BlockItemIds.CONCRETE_STAIRS.forEach(block -> concrete_stairs.add(block.block()));
            concrete_variants.addReplaceableBlock(concrete_stairs);

            set.add(concrete);
            set.add(concrete_variants);
        }
        {
            ReplaceableMaterialHolder concrete_powder = new ReplaceableMaterialHolder("concrete_powder");
            Set<ResourceKey<Block>> concrete_powder_blocks = new HashSet<>();
            BlockItemIds.CONCRETE_POWDER.forEach(block -> concrete_powder_blocks.add(block.block()));
            concrete_powder.addReplaceableBlock(concrete_powder_blocks);

            set.add(concrete_powder);
        }
        {
            ReplaceableMaterialHolder deepslate_family = new ReplaceableMaterialHolder("deepslate_family");
            deepslate_family.addReplaceableBlock(BlockItemIds.DEEPSLATE.block(), BlockItemIds.COBBLED_DEEPSLATE.block(),
                    BlockItemIds.POLISHED_DEEPSLATE.block(), BlockItemIds.DEEPSLATE_BRICKS.block(),
                    BlockItemIds.DEEPSLATE_TILES.block(), BlockItemIds.CHISELED_DEEPSLATE.block(),
                    BlockItemIds.CRACKED_DEEPSLATE_BRICKS.block(), BlockItemIds.CRACKED_DEEPSLATE_TILES.block());

            ReplaceableMaterialHolder deepslate_family_variants = new ReplaceableMaterialHolder("deepslate_family_variants");
            deepslate_family_variants.addReplaceableBlock(BlockItemIds.COBBLED_DEEPSLATE_SLAB.block(), BlockItemIds.POLISHED_DEEPSLATE_SLAB.block(),
                    BlockItemIds.DEEPSLATE_BRICK_SLAB.block(), BlockItemIds.DEEPSLATE_TILE_SLAB.block());
            deepslate_family_variants.addReplaceableBlock(BlockItemIds.COBBLED_DEEPSLATE_STAIRS.block(), BlockItemIds.POLISHED_DEEPSLATE_STAIRS.block(),
                    BlockItemIds.DEEPSLATE_BRICK_STAIRS.block(), BlockItemIds.DEEPSLATE_TILE_STAIRS.block());
            deepslate_family_variants.addReplaceableBlock(BlockItemIds.COBBLED_DEEPSLATE_WALL.block(), BlockItemIds.POLISHED_DEEPSLATE_WALL.block(),
                    BlockItemIds.DEEPSLATE_BRICK_WALL.block(), BlockItemIds.DEEPSLATE_TILE_WALL.block());
            deepslate_family_variants.addReplaceableHolder(deepslate_family);

            set.add(deepslate_family);
            set.add(deepslate_family_variants);
        }
        {
            ReplaceableMaterialHolder fence_family = new ReplaceableMaterialHolder("fence_family");
            fence_family.addReplaceableBlock(BlockItemIds.ACACIA_FENCE.block(), BlockItemIds.BIRCH_FENCE.block(),
                    BlockItemIds.CHERRY_FENCE.block(), BlockItemIds.ACACIA_FENCE_GATE.block(),
                    BlockItemIds.BIRCH_FENCE_GATE.block(), BlockItemIds.CHERRY_FENCE_GATE.block(),
                    BlockItemIds.OAK_FENCE.block(), BlockItemIds.OAK_FENCE_GATE.block(),
                    BlockItemIds.SPRUCE_FENCE.block(), BlockItemIds.SPRUCE_FENCE_GATE.block(),
                    BlockItemIds.JUNGLE_FENCE.block(), BlockItemIds.JUNGLE_FENCE_GATE.block(),
                    BlockItemIds.MANGROVE_FENCE.block(), BlockItemIds.MANGROVE_FENCE_GATE.block(),
                    BlockItemIds.CRIMSON_FENCE.block(), BlockItemIds.CRIMSON_FENCE_GATE.block(),
                    BlockItemIds.WARPED_FENCE.block(), BlockItemIds.WARPED_FENCE_GATE.block(),
                    BlockItemIds.PALE_OAK_FENCE.block(), BlockItemIds.PALE_OAK_FENCE_GATE.block(),
                    BlockItemIds.POPLAR_FENCE.block(), BlockItemIds.POPLAR_FENCE_GATE.block());

            set.add(fence_family);
        }
        {
            ReplaceableMaterialHolder glass_family = new ReplaceableMaterialHolder("glass_family");
            int glass_family_id = glass_family.addReplaceableBlock(BlockItemIds.GLASS.block());
            BlockItemIds.STAINED_GLASS.forEach(block -> glass_family.addReplaceableBlock(glass_family_id, block.block()));

            ReplaceableMaterialHolder glass_pane_family = new ReplaceableMaterialHolder("glass_pane_family");
            int glass_pane_family_id = glass_pane_family.addReplaceableBlock(BlockItemIds.GLASS_PANE.block());
            BlockItemIds.STAINED_GLASS_PANE.forEach(block -> glass_pane_family.addReplaceableBlock(glass_pane_family_id, block.block()));
            glass_pane_family.addReplaceableHolder(glass_family);

            set.add(glass_family);
            set.add(glass_pane_family);
        }
        {
            ReplaceableMaterialHolder log_family = new ReplaceableMaterialHolder("log_family");
            log_family.addReplaceableBlock(BlockItemIds.ACACIA_LOG.block(), BlockItemIds.BIRCH_LOG.block(),
                    BlockItemIds.CHERRY_LOG.block(), BlockItemIds.DARK_OAK_LOG.block(),
                    BlockItemIds.JUNGLE_LOG.block(), BlockItemIds.MANGROVE_LOG.block(),
                    BlockItemIds.OAK_LOG.block(), BlockItemIds.SPRUCE_LOG.block(),
                    BlockItemIds.WARPED_STEM.block(), BlockItemIds.CRIMSON_STEM.block(),
                    BlockItemIds.PALE_OAK_LOG.block(), BlockItemIds.POPLAR_LOG.block());

            ReplaceableMaterialHolder stripped_log_family = new ReplaceableMaterialHolder("stripped_log_family");
            stripped_log_family.addReplaceableBlock(BlockItemIds.STRIPPED_ACACIA_LOG.block(), BlockItemIds.STRIPPED_BIRCH_LOG.block(),
                    BlockItemIds.STRIPPED_ACACIA_LOG.block(), BlockItemIds.STRIPPED_DARK_OAK_LOG.block(),
                    BlockItemIds.STRIPPED_JUNGLE_LOG.block(), BlockItemIds.STRIPPED_MANGROVE_LOG.block(),
                    BlockItemIds.STRIPPED_OAK_LOG.block(), BlockItemIds.STRIPPED_SPRUCE_LOG.block(),
                    BlockItemIds.STRIPPED_WARPED_STEM.block(), BlockItemIds.STRIPPED_CRIMSON_STEM.block(),
                    BlockItemIds.STRIPPED_PALE_OAK_LOG.block(), BlockItemIds.STRIPPED_POPLAR_LOG.block());
            stripped_log_family.addReplaceableHolder(log_family);

            set.add(log_family);
            set.add(stripped_log_family);
        }
        {
            ReplaceableMaterialHolder wood_family = new ReplaceableMaterialHolder("wood_family");
            wood_family.addReplaceableBlock(BlockItemIds.ACACIA_WOOD.block(), BlockItemIds.BIRCH_WOOD.block(),
                    BlockItemIds.CHERRY_WOOD.block(), BlockItemIds.DARK_OAK_WOOD.block(),
                    BlockItemIds.JUNGLE_WOOD.block(), BlockItemIds.MANGROVE_WOOD.block(),
                    BlockItemIds.OAK_WOOD.block(), BlockItemIds.SPRUCE_WOOD.block(),
                    BlockItemIds.WARPED_HYPHAE.block(), BlockItemIds.CRIMSON_HYPHAE.block(),
                    BlockItemIds.PALE_OAK_WOOD.block(), BlockItemIds.POPLAR_WOOD.block());

            ReplaceableMaterialHolder stripped_wood_family = new ReplaceableMaterialHolder("stripped_wood_family");
            stripped_wood_family.addReplaceableBlock(BlockItemIds.STRIPPED_ACACIA_WOOD.block(), BlockItemIds.STRIPPED_BIRCH_WOOD.block(),
                    BlockItemIds.STRIPPED_CHERRY_WOOD.block(), BlockItemIds.STRIPPED_DARK_OAK_WOOD.block(),
                    BlockItemIds.STRIPPED_JUNGLE_WOOD.block(), BlockItemIds.STRIPPED_MANGROVE_WOOD.block(),
                    BlockItemIds.STRIPPED_OAK_WOOD.block(), BlockItemIds.STRIPPED_SPRUCE_WOOD.block(),
                    BlockItemIds.STRIPPED_WARPED_HYPHAE.block(), BlockItemIds.STRIPPED_CRIMSON_HYPHAE.block(),
                    BlockItemIds.STRIPPED_PALE_OAK_WOOD.block(), BlockItemIds.STRIPPED_POPLAR_WOOD.block());
            stripped_wood_family.addReplaceableHolder(wood_family);

            set.add(wood_family);
            set.add(stripped_wood_family);
        }
        {
            ReplaceableMaterialHolder mud = new ReplaceableMaterialHolder("mud");
            mud.addReplaceableBlock(BlockItemIds.MUD.block(), BlockItemIds.PACKED_MUD.block());

            ReplaceableMaterialHolder mud_variants = new ReplaceableMaterialHolder("mud_variants");
            mud_variants.addUnreplaceableBlock(BlockItemIds.MUD_BRICKS.block(), BlockItemIds.MUD_BRICK_SLAB.block(),
                    BlockItemIds.MUD_BRICK_STAIRS.block(), BlockItemIds.MUD_BRICK_WALL.block());
            mud_variants.addReplaceableHolder(mud);

            set.add(mud);
            set.add(mud_variants);
        }
        {
            ReplaceableMaterialHolder planks_family = new ReplaceableMaterialHolder("planks_family");
            planks_family.addReplaceableBlock(BlockItemIds.OAK_PLANKS.block(), BlockItemIds.SPRUCE_PLANKS.block(),
                    BlockItemIds.BIRCH_PLANKS.block(), BlockItemIds.JUNGLE_PLANKS.block(),
                    BlockItemIds.ACACIA_PLANKS.block(), BlockItemIds.DARK_OAK_PLANKS.block(),
                    BlockItemIds.MANGROVE_PLANKS.block(), BlockItemIds.CHERRY_PLANKS.block(),
                    BlockItemIds.CRIMSON_PLANKS.block(), BlockItemIds.WARPED_PLANKS.block(),
                    BlockItemIds.PALE_OAK_PLANKS.block(), BlockItemIds.POPLAR_PLANKS.block());

            ReplaceableMaterialHolder planks_family_variants = new ReplaceableMaterialHolder("planks_family_variants");
            planks_family_variants.addReplaceableBlock(BlockItemIds.OAK_SLAB.block(), BlockItemIds.SPRUCE_SLAB.block(),
                    BlockItemIds.BIRCH_SLAB.block(), BlockItemIds.JUNGLE_SLAB.block(),
                    BlockItemIds.ACACIA_SLAB.block(), BlockItemIds.DARK_OAK_SLAB.block(),
                    BlockItemIds.MANGROVE_SLAB.block(), BlockItemIds.CHERRY_SLAB.block(),
                    BlockItemIds.CRIMSON_SLAB.block(), BlockItemIds.WARPED_SLAB.block(),
                    BlockItemIds.PALE_OAK_SLAB.block(), BlockItemIds.POPLAR_SLAB.block());
            planks_family_variants.addReplaceableBlock(BlockItemIds.OAK_STAIRS.block(), BlockItemIds.SPRUCE_STAIRS.block(),
                    BlockItemIds.BIRCH_STAIRS.block(), BlockItemIds.JUNGLE_STAIRS.block(),
                    BlockItemIds.ACACIA_STAIRS.block(), BlockItemIds.DARK_OAK_STAIRS.block(),
                    BlockItemIds.MANGROVE_STAIRS.block(), BlockItemIds.CHERRY_STAIRS.block(),
                    BlockItemIds.CRIMSON_STAIRS.block(), BlockItemIds.WARPED_STAIRS.block(),
                    BlockItemIds.PALE_OAK_STAIRS.block(), BlockItemIds.POPLAR_STAIRS.block());
            planks_family_variants.addReplaceableBlock(BlockItemIds.OAK_BUTTON.block(), BlockItemIds.SPRUCE_BUTTON.block(),
                    BlockItemIds.BIRCH_BUTTON.block(), BlockItemIds.JUNGLE_BUTTON.block(),
                    BlockItemIds.ACACIA_BUTTON.block(), BlockItemIds.DARK_OAK_BUTTON.block(),
                    BlockItemIds.MANGROVE_BUTTON.block(), BlockItemIds.CHERRY_BUTTON.block(),
                    BlockItemIds.CRIMSON_BUTTON.block(), BlockItemIds.WARPED_BUTTON.block(),
                    BlockItemIds.PALE_OAK_BUTTON.block(), BlockItemIds.POPLAR_BUTTON.block());
            planks_family_variants.addReplaceableHolder(planks_family);

            set.add(planks_family);
            set.add(planks_family_variants);
        }
        {
            ReplaceableMaterialHolder red_sandstone = new ReplaceableMaterialHolder("red_sandstone");
            red_sandstone.addReplaceableBlock(BlockItemIds.CUT_RED_SANDSTONE.block(), BlockItemIds.CHISELED_RED_SANDSTONE.block(),
                    BlockItemIds.RED_SANDSTONE.block(), BlockItemIds.SMOOTH_RED_SANDSTONE.block());

            ReplaceableMaterialHolder red_sandstone_variants = new ReplaceableMaterialHolder("red_sandstone_variants");
            red_sandstone_variants.addReplaceableBlock(BlockItemIds.RED_SANDSTONE_SLAB.block(), BlockItemIds.SMOOTH_RED_SANDSTONE_SLAB.block(),
                    BlockItemIds.CUT_RED_SANDSTONE_SLAB.block());
            red_sandstone_variants.addReplaceableBlock(BlockItemIds.RED_SANDSTONE_STAIRS.block(), BlockItemIds.SMOOTH_RED_SANDSTONE_STAIRS.block());
            red_sandstone_variants.addUnreplaceableBlock(BlockItemIds.RED_SANDSTONE_WALL.block());
            red_sandstone_variants.addReplaceableHolder(red_sandstone);

            set.add(red_sandstone);
            set.add(red_sandstone_variants);
        }
        {
            ReplaceableMaterialHolder sandstone = new ReplaceableMaterialHolder("sandstone");
            sandstone.addReplaceableBlock(BlockItemIds.CUT_SANDSTONE.block(), BlockItemIds.CHISELED_SANDSTONE.block(),
                    BlockItemIds.SANDSTONE.block(), BlockItemIds.SMOOTH_SANDSTONE.block());

            ReplaceableMaterialHolder sandstone_variants = new ReplaceableMaterialHolder("sandstone_variants");
            sandstone_variants.addReplaceableBlock(BlockItemIds.SANDSTONE_SLAB.block(), BlockItemIds.SMOOTH_SANDSTONE_SLAB.block(),
                    BlockItemIds.CUT_SANDSTONE_SLAB.block());
            sandstone_variants.addReplaceableBlock(BlockItemIds.SANDSTONE_STAIRS.block(), BlockItemIds.SMOOTH_SANDSTONE_STAIRS.block());
            sandstone_variants.addUnreplaceableBlock(BlockItemIds.SANDSTONE_WALL.block());
            sandstone_variants.addReplaceableHolder(sandstone);

            set.add(sandstone);
            set.add(sandstone_variants);
        }
        {
            ReplaceableMaterialHolder terracotta = new ReplaceableMaterialHolder("terracotta");
            int terracottaId = terracotta.addReplaceableBlock(BlockItemIds.TERRACOTTA.block());
            BlockItemIds.DYED_TERRACOTTA.forEach(block -> terracotta.addReplaceableBlock(terracottaId, block.block()));

            set.add(terracotta);
        }
        {
            ReplaceableMaterialHolder wool = new ReplaceableMaterialHolder("wool");
            Set<ResourceKey<Block>> wool_blocks = new HashSet<>();
            BlockItemIds.WOOL.forEach(block -> wool_blocks.add(block.block()));
            wool.addReplaceableBlock(wool_blocks);

            ReplaceableMaterialHolder wool_variants = new ReplaceableMaterialHolder("wool_variants");
            Set<ResourceKey<Block>> wool_slabs = new HashSet<>();
            BlockItemIds.WOOL_SLAB.forEach(block -> wool_slabs.add(block.block()));
            wool_variants.addReplaceableBlock(wool_slabs);
            Set<ResourceKey<Block>> wool_stairs = new HashSet<>();
            BlockItemIds.WOOL_STAIRS.forEach(block -> wool_stairs.add(block.block()));
            wool_variants.addReplaceableBlock(wool_stairs);
            wool_variants.addReplaceableHolder(wool);

            set.add(wool);
            set.add(wool_variants);
        }
        {
            ReplaceableMaterialHolder copper_blocks = new ReplaceableMaterialHolder("copper_blocks");
            Set<ResourceKey<Block>> copper_blocks_set = new HashSet<>();
            BlockItemIds.COPPER_BLOCK.forEach(block -> copper_blocks_set.add(block.block()));
            BlockItemIds.CUT_COPPER.forEach(block -> copper_blocks_set.add(block.block()));
            BlockItemIds.CHISELED_COPPER.forEach(block -> copper_blocks_set.add(block.block()));
            copper_blocks.addReplaceableBlock(copper_blocks_set);

            ReplaceableMaterialHolder copper_blocks_variants = new ReplaceableMaterialHolder("copper_blocks_variants");
            Set<ResourceKey<Block>> copper_slabs_set = new HashSet<>();
            BlockItemIds.CUT_COPPER_SLAB.forEach(block -> copper_slabs_set.add(block.block()));
            copper_blocks_variants.addReplaceableBlock(copper_slabs_set);
            Set<ResourceKey<Block>> copper_stairs_set = new HashSet<>();
            BlockItemIds.CUT_COPPER_STAIRS.forEach(block -> copper_stairs_set.add(block.block()));
            copper_blocks_variants.addReplaceableBlock(copper_stairs_set);
            copper_blocks_variants.addReplaceableHolder(copper_blocks);

            set.add(copper_blocks);
            set.add(copper_blocks_variants);
        }
        return Set.copyOf(set);
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
        set.add(BlockItemIds.REINFORCED_DEEPSLATE.block());
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
}
