package common.cn.nsimu.simukraft.guidebook;

import common.cn.nsimu.simukraft.SimuKraft;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddServerReloadListenersEvent;

/**
 * 把指南书索引加入资源重载。
 */
@EventBusSubscriber(modid = SimuKraft.MOD_ID)
public final class GuideBookReloadEvents {
    private GuideBookReloadEvents() {
    }

    @SubscribeEvent
    public static void onAddReloadListener(AddServerReloadListenersEvent event) {
        event.addListener(Identifier.fromNamespaceAndPath(SimuKraft.MOD_ID, "guidebook"), GuideBookReloadListener.INSTANCE);
    }
}
