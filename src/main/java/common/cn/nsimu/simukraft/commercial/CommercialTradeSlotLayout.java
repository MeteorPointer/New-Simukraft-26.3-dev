package common.cn.nsimu.simukraft.commercial;

import com.lowdragmc.lowdraglib2.gui.slot.LocalSlot;
import com.lowdragmc.lowdraglib2.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import com.lowdragmc.lowdraglib2.gui.ui.elements.ItemSlot;
import com.lowdragmc.lowdraglib2.gui.ui.elements.inventory.InventorySlots;
import common.cn.nsimu.simukraft.network.commercial.CommercialTradeOpenResponsePacket;
import dev.vfyjxf.taffy.style.TaffyPosition;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * 交易容器的真实槽位布局，服务端和客户端必须使用相同添加顺序。
 */
public final class CommercialTradeSlotLayout {
    public static final int IMAGE_WIDTH = 276;
    public static final int IMAGE_HEIGHT = 166;
    public static final int GLOBAL_MARKET_TAB_TOP_OFFSET = 28;
    public static final int COST_A_X = 136;
    public static final int COST_B_X = 162;
    public static final int RESULT_X = 220;
    public static final int TRADE_SLOT_Y = 37;
    public static final int INVENTORY_X = 108;
    public static final int INVENTORY_Y = 84;
    public static final int SLOT_SIZE = 18;

    private CommercialTradeSlotLayout() {
    }

    /**
     * create: 创建 3 个展示槽和玩家原版背包槽位。
     */
    public static UIElement create(CommercialTradeOpenResponsePacket packet) {
        int panelTop = panelTop(packet);
        UIElement root = new UIElement().layout(layout -> {
            layout.width(IMAGE_WIDTH);
            layout.height(IMAGE_HEIGHT + panelTop);
        });
        root.addChild(displaySlot(COST_A_X, TRADE_SLOT_Y, panelTop));
        root.addChild(displaySlot(COST_B_X, TRADE_SLOT_Y, panelTop));
        root.addChild(displaySlot(RESULT_X, TRADE_SLOT_Y, panelTop));
        root.addChild(playerInventorySlots(panelTop));
        return root;
    }

    /**
     * panelTop: 全球市场顶部模式 Tab 占用的额外高度。
     */
    public static int panelTop(CommercialTradeOpenResponsePacket packet) {
        return packet != null && packet.globalMarket() ? GLOBAL_MARKET_TAB_TOP_OFFSET : 0;
    }

    private static ItemSlot displaySlot(int x, int y, int panelTop) {
        ItemSlot slot = new ItemSlot(new LocalSlot() {
            @Override
            public boolean mayPickup(Player player) {
                return false;
            }

            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });
        slot.layout(layout -> layout.positionType(TaffyPosition.ABSOLUTE)
                .left(x - 1)
                .top(y + panelTop - 1)
                .width(SLOT_SIZE)
                .height(SLOT_SIZE));
        slot.style(style -> style.backgroundTexture(IGuiTexture.EMPTY));
        slot.slotStyle(style -> style.slotOverlay(IGuiTexture.EMPTY).showItemTooltips(true));
        return slot;
    }

    private static InventorySlots playerInventorySlots(int panelTop) {
        InventorySlots slots = new InventorySlots();
        slots.layout(layout -> layout.positionType(TaffyPosition.ABSOLUTE)
                .left(INVENTORY_X - 1)
                .top(INVENTORY_Y + panelTop - 1)
                .width(162)
                .height(76));
        slots.hotbar.layout(layout -> layout.marginTop(4));
        slots.apply(slot -> slot.style(style -> style.backgroundTexture(IGuiTexture.EMPTY)));
        return slots;
    }
}
