package common.cn.nsimu.simukraft.item;

import common.cn.nsimu.simukraft.clientbridge.ClientInteractionBridge;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;

/**
 * 指南书。右键后在客户端打开书本界面。
 */
public final class GuideBookItem extends Item {
    public GuideBookItem(Properties properties) {
        super(properties);
    }

    @Override
    public @Nonnull InteractionResult use(@Nonnull Level level, @Nonnull Player player, @Nonnull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide()) {
            ClientInteractionBridge.openGuideBook(stack);
        }
        return InteractionResult.SUCCESS.heldItemTransformedTo(stack);
    }
}
