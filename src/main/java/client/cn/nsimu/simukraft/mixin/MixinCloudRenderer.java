package client.cn.nsimu.simukraft.mixin;

import client.cn.nsimu.simukraft.client.freecamera.FreeCameraManager;
import com.mojang.renderpearl.api.commands.RenderPass;
import com.mojang.renderpearl.api.textures.GpuTextureView;
import net.minecraft.client.CloudStatus;
import net.minecraft.client.renderer.CloudRenderer;
import net.minecraft.client.renderer.oit.OitRenderPassProvider;
import net.minecraft.client.renderer.oit.OitStage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * RTS 关闭云提交。26.3 的原版云和 Iris 的云都从 CloudRenderer 这两个入口出去。
 */
@Mixin(CloudRenderer.class)
public class MixinCloudRenderer {
    /**
     * simukraft$skipRtsClouds: 经典半透明路径不提交云网格。
     */
    @Inject(method = "render(Lnet/minecraft/client/CloudStatus;Lcom/mojang/renderpearl/api/commands/RenderPass;)V", at = @At("HEAD"), cancellable = true)
    private void simukraft$skipRtsClouds(CloudStatus cloudStatus, RenderPass renderPass, CallbackInfo callbackInfo) {
        if (FreeCameraManager.isRtsActive()) {
            callbackInfo.cancel();
        }
    }

    /**
     * simukraft$skipRtsCloudsOit: 顺序无关透明路径不提交云网格。
     */
    @Inject(method = "renderOit", at = @At("HEAD"), cancellable = true)
    private void simukraft$skipRtsCloudsOit(
            CloudStatus cloudStatus,
            OitStage stage,
            GpuTextureView mainDepthTextureView,
            OitRenderPassProvider.Parameters params,
            CallbackInfo callbackInfo
    ) {
        if (FreeCameraManager.isRtsActive()) {
            callbackInfo.cancel();
        }
    }
}
