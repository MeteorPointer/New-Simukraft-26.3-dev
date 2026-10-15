package client.cn.nsimu.simukraft.mixin.sodium;

import client.cn.nsimu.simukraft.client.freecamera.FreeCameraManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Sodium 按相机落点剔除区块朝外的面。透视下看不见，正交平行视线仍能看见左右侧面。
 */
@Mixin(targets = "net.caffeinemc.mods.sodium.client.render.chunk.DefaultChunkRenderer", remap = false)
public class MixinSodiumBlockFaceCulling {
    /**
     * simukraft$keepOrthoSideFaces: RTS 正交视图保留区块六个方向的面。
     */
    @Inject(method = "getVisibleFaces(IIIIII)I", at = @At("HEAD"), cancellable = true, remap = false)
    private static void simukraft$keepOrthoSideFaces(int cameraX, int cameraY, int cameraZ, int originX, int originY, int originZ,
                                                     CallbackInfoReturnable<Integer> callback) {
        if (FreeCameraManager.isRtsActive()) {
            callback.setReturnValue(-1);
        }
    }
}
