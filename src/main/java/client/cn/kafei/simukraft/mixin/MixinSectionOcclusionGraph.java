package client.cn.kafei.simukraft.mixin;

import client.cn.kafei.simukraft.client.freecamera.FreeCameraManager;
import net.minecraft.client.renderer.SectionOcclusionGraph;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 自由相机区段可见性：相机离开玩家后，平移也要重新收集可见区块。
 */
@Mixin(SectionOcclusionGraph.class)
public class MixinSectionOcclusionGraph {
    /**
     * simukraft$forceFrustumUpdate: 建筑预览和 RTS 平移时每帧应用当前视锥。
     */
    @Inject(method = "consumeFrustumUpdate", at = @At("HEAD"), cancellable = true, require = 0)
    private void simukraft$forceFrustumUpdate(CallbackInfoReturnable<Boolean> callbackInfo) {
        if (FreeCameraManager.isActive()) {
            callbackInfo.setReturnValue(true);
        }
    }
}
