package client.cn.nsimu.simukraft.mixin;

import client.cn.nsimu.simukraft.client.freecamera.FreeCameraManager;
import com.mojang.blaze3d.ProjectionType;
import net.minecraft.client.renderer.GameRenderer;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * RTS 正交投影：仅在 RTS 相机启用时替换世界渲染投影矩阵。
 */
@Mixin(GameRenderer.class)
public abstract class MixinGameRendererProjection {
    /**
     * simukraft$applyRtsProjection: 使用 RTS 缩放范围生成正交投影。
     */
    @Inject(method = "getProjectionMatrix", at = @At("HEAD"), cancellable = true, require = 0)
    private void simukraft$applyRtsProjection(double fov, CallbackInfoReturnable<Matrix4f> callbackInfo) {
        if (FreeCameraManager.isRtsActive()) {
            callbackInfo.setReturnValue(FreeCameraManager.rtsProjectionMatrix());
        }
    }

    /**
     * simukraft$rtsProjectionType: 正交矩阵必须标成正交，否则深度比较和半透明排序仍按透视处理。
     */
    @ModifyArg(
            method = "renderLevel",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/systems/RenderSystem;setProjectionMatrix(Lcom/mojang/renderpearl/api/buffers/GpuBufferSlice;Lcom/mojang/blaze3d/ProjectionType;)V"
            ),
            index = 1
    )
    private ProjectionType simukraft$rtsProjectionType(ProjectionType type) {
        return FreeCameraManager.isRtsActive() ? ProjectionType.ORTHOGRAPHIC : type;
    }
}
