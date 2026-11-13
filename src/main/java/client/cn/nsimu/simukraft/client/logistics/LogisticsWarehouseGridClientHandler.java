package client.cn.nsimu.simukraft.client.logistics;

import common.cn.nsimu.simukraft.logistics.menu.LogisticsWarehouseGridMenu;
import common.cn.nsimu.simukraft.network.logistics.LogisticsWarehouseGridExtractPacket;
import common.cn.nsimu.simukraft.network.logistics.LogisticsWarehouseGridInsertPacket;
import common.cn.nsimu.simukraft.network.logistics.LogisticsWarehouseGridShiftClickPacket;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

/**
 * 物流仓库聚合槽的客户端点击发包。
 */
public final class LogisticsWarehouseGridClientHandler {
    private LogisticsWarehouseGridClientHandler() {
    }

    /**
     * handleClick: 客户端点击聚合槽时发送精确物品操作请求。
     */
    public static void handleClick(LogisticsWarehouseGridMenu menu, int slotId, int dragType, ContainerInput clickType, Player player) {
        if (player == null || !player.level().isClientSide()) {
            return;
        }
        ItemStack carried = menu.getCarried();
        if (clickType == ContainerInput.PICKUP && !carried.isEmpty()) {
            ClientPacketDistributor.sendToServer(new LogisticsWarehouseGridInsertPacket(menu.getWarehousePos()));
            return;
        }
        ItemStack target = menu.targetStackAtVisibleSlot(slotId);
        if (target.isEmpty()) {
            return;
        }
        if (clickType == ContainerInput.PICKUP && carried.isEmpty()) {
            int count = dragType == 1 ? Math.max(1, (target.getCount() + 1) / 2) : target.getCount();
            ClientPacketDistributor.sendToServer(new LogisticsWarehouseGridExtractPacket(menu.getWarehousePos(), target, count));
        } else if (clickType == ContainerInput.QUICK_MOVE && carried.isEmpty()) {
            ClientPacketDistributor.sendToServer(new LogisticsWarehouseGridShiftClickPacket(menu.getWarehousePos(), target));
        }
    }
}
