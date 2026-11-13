package common.cn.nsimu.simukraft.commercial;

import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import common.cn.nsimu.simukraft.network.commercial.CommercialTradeOpenResponsePacket;
import net.minecraft.world.entity.player.Player;

import java.util.concurrent.atomic.AtomicReference;

/**
 * 隔离 common 交易容器与物理客户端界面实现。
 */
public final class CommercialTradeUiBridge {
    private static final AtomicReference<Factory> FACTORY = new AtomicReference<>();
    private static final AtomicReference<RefreshHandler> REFRESH = new AtomicReference<>();

    private CommercialTradeUiBridge() {
    }

    /**
     * install: 客户端启动时安装 LDLib 交易界面工厂。
     */
    public static void install(Factory factory, RefreshHandler refreshHandler) {
        FACTORY.set(factory);
        REFRESH.set(refreshHandler);
    }

    /**
     * create: 在物理客户端创建交易界面；专用服务器未安装时返回 null。
     */
    public static ModularUI create(CommercialTradeOpenResponsePacket packet, Player player) {
        Factory factory = FACTORY.get();
        return factory != null ? factory.create(packet, player) : null;
    }

    /**
     * refreshActive: 刷新当前打开的商业交易界面快照。
     */
    public static void refreshActive(CommercialTradeOpenResponsePacket packet) {
        RefreshHandler refreshHandler = REFRESH.get();
        if (refreshHandler != null) {
            refreshHandler.refresh(packet);
        }
    }

    @FunctionalInterface
    public interface Factory {
        ModularUI create(CommercialTradeOpenResponsePacket packet, Player player);
    }

    @FunctionalInterface
    public interface RefreshHandler {
        void refresh(CommercialTradeOpenResponsePacket packet);
    }
}
