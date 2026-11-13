package common.cn.nsimu.simukraft.logistics.menu;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerInput;

import java.util.concurrent.atomic.AtomicReference;

/**
 * 隔离物流仓库菜单的客户端点击发包。
 */
public final class LogisticsWarehouseGridClientBridge {
    private static final AtomicReference<Handler> HANDLER = new AtomicReference<>();

    private LogisticsWarehouseGridClientBridge() {
    }

    /**
     * install: 客户端启动时安装仓库点击处理。
     */
    public static void install(Handler handler) {
        HANDLER.set(handler);
    }

    /**
     * handleClick: 仅在物理客户端把聚合槽点击发给服务端。
     */
    public static void handleClick(LogisticsWarehouseGridMenu menu, int slotId, int dragType, ContainerInput clickType, Player player) {
        Handler handler = HANDLER.get();
        if (handler != null) {
            handler.handleClick(menu, slotId, dragType, clickType, player);
        }
    }

    @FunctionalInterface
    public interface Handler {
        void handleClick(LogisticsWarehouseGridMenu menu, int slotId, int dragType, ContainerInput clickType, Player player);
    }
}
