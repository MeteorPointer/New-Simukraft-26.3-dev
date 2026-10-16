package common.cn.nsimu.simukraft.item;

import common.cn.nsimu.simukraft.network.geology.GeologicalSurveyHintService;
import common.cn.nsimu.simukraft.virtualvein.*;
import net.minecraft.ChatFormatting;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.function.Consumer;

/**
 * GeologicalHammerItem: 勘探当前位置所属矿区的矿脉。
 */

public final class GeologicalHammerItem extends Item {
    private static final int MAX_DURABILITY = 800;
    private static final int DURABILITY_COST_PER_BLOCK = 2;
    private static final int PROSPECTING_DEPTH = 60;
    private static final int COOLDOWN_TICKS = 20;
    /**
     * 以铁质工具属性为基准，仅将耐久提升至 MAX_DURABILITY 的自定义工具材质。
     */
    private static final ToolMaterial HAMMER_MATERIAL = new ToolMaterial(
            BlockTags.INCORRECT_FOR_IRON_TOOL,
            MAX_DURABILITY,
            ToolMaterial.IRON.speed(),
            ToolMaterial.IRON.attackDamageBonus(),
            ToolMaterial.IRON.enchantmentValue(),
            ItemTags.IRON_TOOL_MATERIALS
    );

    public GeologicalHammerItem(Properties properties) {
        super(properties.stacksTo(1)
                .pickaxe(HAMMER_MATERIAL, 1.0F, -2.8F)
                .delayedComponent(DataComponents.TOOL, GeologicalHammerItem::toolWithConfiguredDurabilityCost));
    }

    /**
     * pickaxe 固定写入每次破坏消耗 1 点耐久。地质锤覆盖为 2，挖掘规则和速度保持铁镐。
     */
    private static Tool toolWithConfiguredDurabilityCost(HolderLookup.Provider context) {
        return new Tool(List.of(
                Tool.Rule.deniesDrops(context.getOrThrow(BlockTags.INCORRECT_FOR_IRON_TOOL)),
                Tool.Rule.minesAndDrops(context.getOrThrow(BlockTags.MINEABLE_WITH_PICKAXE), HAMMER_MATERIAL.speed())),
                1.0F,
                DURABILITY_COST_PER_BLOCK,
                true);
    }

    /**
     * appendHoverText: 在物品提示中显示地质锤的叙述性描述。
     */
    @Override
    public void appendHoverText(ItemStack stack,
                                TooltipContext context,
                                TooltipDisplay tooltipDisplay,
                                Consumer<Component> tooltipComponents,
                                TooltipFlag isAdvanced) {
        super.appendHoverText(stack, context, tooltipDisplay, tooltipComponents, isAdvanced);
        tooltipComponents.accept(Component.translatable("tooltip.simukraft.geological_hammer.description")
                .withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
    }

    /**
     * useOn: 服务端探查右键位置向下 60 格范围内的矿脉。
     */
    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        if (!(level instanceof ServerLevel serverLevel) || !(context.getPlayer() instanceof ServerPlayer player)) {
            return InteractionResult.PASS;
        }
        ItemStack heldStack = context.getItemInHand();
        if (player.getCooldowns().isOnCooldown(heldStack)) {
            return InteractionResult.CONSUME;
        }
        if (!serverLevel.dimension().equals(Level.OVERWORLD)) {
            sendHint(player, Component.translatable("message.simukraft.geological_hammer.not_overworld"));
            return InteractionResult.FAIL;
        }
        player.getCooldowns().addCooldown(heldStack, COOLDOWN_TICKS);
        VirtualVeinLookupResult lookup = VirtualVeinService.getOrCreateField(serverLevel, context.getClickedPos());
        if (!lookup.isReady()) {
            sendLookupFailure(player, lookup.status());
            return InteractionResult.FAIL;
        }
        int clickedY = context.getClickedPos().getY();
        showNearbyVeins(player, lookup.profile(), scanMinimumY(clickedY, serverLevel.getMinY()), clickedY);
        context.getItemInHand().hurtAndBreak(1, player,
                context.getHand() == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
        return InteractionResult.CONSUME;
    }

    /**
     * showNearbyVeins: 提示右键位置向下探查范围内仍可开采的矿脉痕迹。
     */
    private static void showNearbyVeins(ServerPlayer player,
                                        VirtualVeinFieldProfile profile,
                                        int rangeMinY,
                                        int rangeMaxY) {
        List<VirtualVeinSlot> nearbySlots = profile.slots().stream()
                .filter(slot -> slot.intersectsYRange(rangeMinY, rangeMaxY))
                .toList();
        List<VirtualVeinSlot> activeSlots = nearbySlots.stream()
                .filter(slot -> slot.state() == VirtualVeinSlotState.ACTIVE)
                .toList();
        if (!activeSlots.isEmpty()) {
            MutableComponent result = Component.empty();
            for (int index = 0; index < activeSlots.size(); index++) {
                VirtualVeinSlot slot = activeSlots.get(index);
                if (index > 0) {
                    result.append(Component.literal("\n"));
                }
                result.append(Component.translatable(
                        "message.simukraft.geological_hammer.result",
                        slot.displayName()));
            }
            sendHint(player, result);
            return;
        }
        if (!nearbySlots.isEmpty()) {
            sendHint(player, Component.translatable("message.simukraft.geological_hammer.depleted"));
            return;
        }
        sendHint(player, Component.translatable("message.simukraft.geological_hammer.no_nearby"));
    }

    private static void sendHint(ServerPlayer player, Component message) {
        player.sendSystemMessage(message);
    }

    /**
     * scanMinimumY: 计算向下探查 60 格后的最低高度，并限制在世界可用高度内。
     */
    static int scanMinimumY(int clickedY, int minBuildHeight) {
        return Math.max(minBuildHeight, clickedY - PROSPECTING_DEPTH);
    }

    private static void sendLookupFailure(ServerPlayer player, VirtualVeinLookupStatus status) {
        switch (status) {
            case NOT_OVERWORLD ->
                    GeologicalSurveyHintService.send(player, Component.translatable("message.simukraft.geological_hammer.not_overworld"));
            case DEFINITIONS_UNAVAILABLE ->
                    GeologicalSurveyHintService.send(player, Component.translatable("message.simukraft.geological_hammer.definitions_unavailable"));
            case DATABASE_UNAVAILABLE ->
                    GeologicalSurveyHintService.send(player, Component.translatable("message.simukraft.geological_hammer.database_unavailable"));
            case UNSUPPORTED_WORLDGEN ->
                    GeologicalSurveyHintService.send(player, Component.translatable("message.simukraft.geological_hammer.unsupported_worldgen"));
            case READY -> {
            }
        }
    }
}
