package client.cn.nsimu.simukraft.client.commercial;

import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import com.lowdragmc.lowdraglib2.gui.ui.UI;
import common.cn.nsimu.simukraft.network.commercial.CommercialTradeOpenResponsePacket;
import net.minecraft.world.entity.player.Player;

public final class CommercialTradeScreenOpener {
    private CommercialTradeScreenOpener() {
    }

    /**
     * createContainerUi: 为原版容器菜单创建完整商业交易界面。
     */
    public static ModularUI createContainerUi(CommercialTradeOpenResponsePacket packet, Player player) {
        return ModularUI.of(UI.of(new CommercialTradeUiRoot(packet)), player);
    }

    /**
     * open: 兼容旧交易响应包；实际界面现在由服务端容器菜单打开。
     */
    public static void open(CommercialTradeOpenResponsePacket packet) {
        // NPC 购买界面已经迁移到 LDLib ModularUIContainerMenu，客户端包不再直接创建 Screen。
    }
}
