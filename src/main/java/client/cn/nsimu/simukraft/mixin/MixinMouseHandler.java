package client.cn.nsimu.simukraft.mixin;

import client.cn.nsimu.simukraft.client.freecamera.CameraMouseLock;
import client.cn.nsimu.simukraft.client.freecamera.FreeCameraManager;
import client.cn.nsimu.simukraft.client.freecamera.FreeCameraScreen;
import client.cn.nsimu.simukraft.client.rts.RtsSelectionManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.gui.screens.Screen;
import org.lwjgl.sdl.SDLMouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public final class MixinMouseHandler {
    @Shadow
    private double accumulatedDX;
    @Shadow
    private double accumulatedDY;
    @Shadow
    private boolean mouseGrabbed;
    @Unique
    private double simukraft$lastX = Double.NaN;
    @Unique
    private double simukraft$lastY = Double.NaN;
    @Unique
    private boolean simukraft$suppressWarpMove;

    @Inject(method = "onMove", at = @At("HEAD"))
    private void simukraft$onMove(long window, double xpos, double ypos, double xrel, double yrel, CallbackInfo callbackInfo) {
        if (simukraft$suppressWarpMove) {
            simukraft$suppressWarpMove = false;
            simukraft$lastX = xpos;
            simukraft$lastY = ypos;
            accumulatedDX = 0.0D;
            accumulatedDY = 0.0D;
            return;
        }
        if (RtsSelectionManager.isActive()) {
            if (RtsSelectionManager.isCameraRotationActive()) {
                simukraft$turnFromPointer(xpos, ypos, true);
            } else {
                simukraft$lastX = Double.NaN;
                simukraft$lastY = Double.NaN;
            }
            accumulatedDX = 0.0D;
            accumulatedDY = 0.0D;
            return;
        }
        if (!FreeCameraManager.isActive()) {
            simukraft$lastX = Double.NaN;
            simukraft$lastY = Double.NaN;
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        Screen screen = minecraft.gui.screen();
        if (!(screen instanceof FreeCameraScreen) && screen != null) {
            return;
        }
        // 相对鼠标模式下光标钉住，位移在 xrel/yrel。用绝对坐标差会得到 0。
        if (CameraMouseLock.isLocked() && (xrel != 0.0D || yrel != 0.0D)) {
            simukraft$lastX = xpos;
            simukraft$lastY = ypos;
            simukraft$applyLook(xrel, yrel);
        } else {
            simukraft$turnFromPointer(xpos, ypos, true);
        }
        accumulatedDX = 0.0D;
        accumulatedDY = 0.0D;
    }

    /**
     * turnFromPointer: 用绝对坐标差转向。recenter 时把光标拉回窗口中心，避免贴边后增量变成 0。
     */
    @Unique
    private void simukraft$turnFromPointer(double xpos, double ypos, boolean recenter) {
        if (Double.isNaN(simukraft$lastX)) {
            simukraft$lastX = xpos;
            simukraft$lastY = ypos;
            return;
        }
        double deltaX = xpos - simukraft$lastX;
        double deltaY = ypos - simukraft$lastY;
        simukraft$lastX = xpos;
        simukraft$lastY = ypos;
        if (Math.abs(deltaX) <= 0.1D && Math.abs(deltaY) <= 0.1D) {
            return;
        }
        simukraft$applyLook(deltaX, deltaY);
        if (recenter) {
            simukraft$recenterCursor();
        }
    }

    @Unique
    private void simukraft$applyLook(double deltaX, double deltaY) {
        double sensitivity = Minecraft.getInstance().options.sensitivity().get() * 0.6D + 0.2D;
        double multiplier = sensitivity * sensitivity * sensitivity * 0.6D;
        FreeCameraManager.handleRotation((float) (deltaX * multiplier), (float) (deltaY * multiplier));
    }

    @Unique
    private void simukraft$recenterCursor() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.getWindow() == null) {
            return;
        }
        double centerX = minecraft.getWindow().getScreenWidth() / 2.0D;
        double centerY = minecraft.getWindow().getScreenHeight() / 2.0D;
        simukraft$suppressWarpMove = true;
        simukraft$lastX = centerX;
        simukraft$lastY = centerY;
        SDLMouse.SDL_WarpMouseInWindow(minecraft.getWindow().handle(), (float) centerX, (float) centerY);
    }

    @Inject(method = "turnPlayer(D)V", at = @At("HEAD"), cancellable = true)
    private void simukraft$turnPlayer(double movementTime, CallbackInfo callbackInfo) {
        if (RtsSelectionManager.isActive()) {
            accumulatedDX = 0.0D;
            accumulatedDY = 0.0D;
            callbackInfo.cancel();
            return;
        }
        if (FreeCameraManager.isActive()) {
            Minecraft minecraft = Minecraft.getInstance();
            if (minecraft.gui.screen() == null || minecraft.gui.screen() instanceof FreeCameraScreen) {
                callbackInfo.cancel();
                accumulatedDX = 0.0D;
                accumulatedDY = 0.0D;
            }
        }
    }

    @Inject(method = "grabMouse()V", at = @At("HEAD"), cancellable = true)
    private void simukraft$grabMouse(CallbackInfo callbackInfo) {
        if (RtsSelectionManager.isActive()) {
            mouseGrabbed = false;
            callbackInfo.cancel();
            return;
        }
        if (FreeCameraManager.isActive()) {
            mouseGrabbed = true;
            callbackInfo.cancel();
        }
    }

    @Inject(method = "releaseMouse()V", at = @At("HEAD"), cancellable = true)
    private void simukraft$releaseMouse(CallbackInfo callbackInfo) {
        if (RtsSelectionManager.isActive()) {
            return;
        }
        if (FreeCameraManager.isActive()) {
            mouseGrabbed = true;
            callbackInfo.cancel();
        }
    }
}
