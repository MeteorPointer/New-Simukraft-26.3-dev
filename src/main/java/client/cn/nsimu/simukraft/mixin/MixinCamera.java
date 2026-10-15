package client.cn.nsimu.simukraft.mixin;

import client.cn.nsimu.simukraft.client.freecamera.FreeCameraManager;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class MixinCamera {
    @Shadow
    private @org.jspecify.annotations.Nullable Entity entity;

    @Shadow
    protected abstract void setPosition(Vec3 position);

    @Shadow
    protected abstract void setRotation(float yRot, float xRot, float roll);

    @Shadow
    public abstract Matrix4f getViewRotationMatrix(Matrix4f dest);

    @Shadow
    protected abstract void prepareCullFrustum(Matrix4fc modelViewMatrix, Matrix4f projectionMatrixForCulling, Vec3 cameraPos);

    /**
     * simukraft$applyFreeCamera: 在视锥和投影使用相机位置之前写入自由相机姿态。
     */
    @Inject(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;alignWithEntity(F)V", shift = At.Shift.AFTER))
    private void simukraft$applyFreeCamera(DeltaTracker deltaTracker, CallbackInfo callbackInfo) {
        if (FreeCameraManager.isActive() && entity instanceof LocalPlayer) {
            setPosition(FreeCameraManager.getPosition());
            setRotation(FreeCameraManager.getYaw(), FreeCameraManager.getPitch(), 0.0F);
        }
    }

    /**
     * simukraft$applyRtsCullFrustum: 用正交投影重建 RTS 视锥，避免透视裁剪裁掉视野里的区块。
     */
    @Inject(method = "update", at = @At("TAIL"))
    private void simukraft$applyRtsCullFrustum(DeltaTracker deltaTracker, CallbackInfo callbackInfo) {
        if (!FreeCameraManager.isRtsActive() || !(entity instanceof LocalPlayer)) {
            return;
        }
        Matrix4f viewRotation = new Matrix4f();
        getViewRotationMatrix(viewRotation);
        prepareCullFrustum(viewRotation, FreeCameraManager.rtsProjectionMatrix(), FreeCameraManager.getPosition());
    }

    /**
     * simukraft$applyRtsProjection: 用 RTS 正交矩阵替换提取后的透视投影。
     */
    @Inject(method = "extractRenderState", at = @At("TAIL"))
    private void simukraft$applyRtsProjection(CameraRenderState cameraState, DeltaTracker deltaTracker, CallbackInfo callbackInfo) {
        if (FreeCameraManager.isRtsActive()) {
            cameraState.projectionMatrix.set(FreeCameraManager.rtsProjectionMatrix());
            // 正交俯视能看到遮挡图从相机所在天空格走不到的表面。那些表面离开屏幕中心就是空的。
            cameraState.smartCull = false;
        }
    }
}
