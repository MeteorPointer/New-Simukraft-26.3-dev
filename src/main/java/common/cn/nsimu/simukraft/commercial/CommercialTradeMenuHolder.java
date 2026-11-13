package common.cn.nsimu.simukraft.commercial;

import com.lowdragmc.lowdraglib2.gui.factory.IContainerUIHolder;
import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import com.lowdragmc.lowdraglib2.gui.ui.UI;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import common.cn.nsimu.simukraft.network.commercial.CommercialTradeOpenResponsePacket;
import common.cn.nsimu.simukraft.network.rts.RtsRemoteCitizenAccess;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;


public final class CommercialTradeMenuHolder implements IContainerUIHolder {
    private final CommercialTradeOpenResponsePacket packet;

    public CommercialTradeMenuHolder(CommercialTradeOpenResponsePacket packet) {
        this.packet = packet;
    }

    /**
     * createUI: 两端创建相同顺序的菜单槽位，客户端额外绘制完整交易界面。
     */
    @Override
    public ModularUI createUI(Player player) {
        if (player != null && player.level().isClientSide()) {
            ModularUI clientUi = CommercialTradeUiBridge.create(packet, player);
            if (clientUi != null) {
                return clientUi;
            }
        }
        UIElement root = CommercialTradeSlotLayout.create(packet);
        return ModularUI.of(UI.of(root), player);
    }

    /**
     * isStillValid: 校验容器使用期间玩家仍在交易范围内。
     */
    @Override
    public boolean isStillValid(Player player) {
        if (player == null || player.level().isClientSide()) {
            return true;
        }
        if (packet.boxPos() == null) {
            return false;
        }
        if (player instanceof ServerPlayer serverPlayer && serverPlayer.level() instanceof ServerLevel level) {
            if (packet.globalMarket()) {
                return serverPlayer.blockPosition().closerThan(packet.boxPos(), 16.0D)
                        || RtsRemoteCitizenAccess.hasTradeAccess(serverPlayer, packet.boxPos(), packet.workerId());
            }
            return packet.workerId() != null
                    && (CommercialTradeAccessValidator.canUseTradeMenu(level, serverPlayer, packet.boxPos(), packet.workerId())
                    || RtsRemoteCitizenAccess.hasTradeAccess(serverPlayer, packet.boxPos(), packet.workerId()));
        }
        return false;
    }
}
