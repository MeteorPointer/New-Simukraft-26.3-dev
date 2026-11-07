package client.cn.nsimu.simukraft.client.rts;

import client.cn.nsimu.simukraft.client.freecamera.FreeCameraManager;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import common.cn.nsimu.simukraft.network.rts.RtsLogisticsRoutesSyncPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Util;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.SubmitCustomGeometryEvent;
import org.joml.Matrix4f;

import java.util.List;

/**
 * RTS 物流线路渲染：细芯加薄光晕，看起来像光纤，粗细不随 RTS 缩放变化。
 */
public final class RtsLogisticsRouteRenderer {
    private static final int COLOR_HALO = 0x33FFAA22;
    private static final int COLOR_SHEATH = 0x99FFD56A;
    private static final int COLOR_CORE = 0xF4FFF6D2;
    private static final int COLOR_FLOW = 0xFFFFFFFF;
    private static final float HALO_PIXELS = 3.2F;
    private static final float SHEATH_PIXELS = 1.7F;
    private static final float CORE_PIXELS = 0.9F;
    private static final float FLOW_PIXELS = 1.15F;
    private static final long FLOW_PERIOD_MS = 900L;
    private static final double FLOW_SPACING = 3.2D;
    private static final double FLOW_LENGTH = 0.55D;

    private RtsLogisticsRouteRenderer() {
    }

    /**
     * onRender: 提交当前悬浮节点的线路。
     */
    public static void onRender(SubmitCustomGeometryEvent event) {
        if (!FreeCameraManager.isRtsActive()) {
            return;
        }
        List<RtsLogisticsRoutesSyncPacket.Line> lines = RtsLogisticsRouteManager.lines();
        if (!hasDrawableLine(lines)) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.gameRenderer == null || !minecraft.gameRenderer.mainCamera().isInitialized()) {
            return;
        }
        Vec3 camera = event.getLevelRenderState().cameraRenderState.pos;
        Vec3 look = cameraAxis(minecraft.gameRenderer.mainCamera().forwardVector());
        Matrix4f projection = event.getLevelRenderState().cameraRenderState.projectionMatrix;
        PoseStack poseStack = event.getPoseStack();
        double worldPerPixel = worldUnitsPerPixel(projection);
        float phase = (Util.getMillis() % FLOW_PERIOD_MS) / (float) FLOW_PERIOD_MS;
        event.getSubmitNodeCollector().submitCustomGeometry(poseStack, RenderTypes.debugQuads(), (pose, buffer) -> {
            for (RtsLogisticsRoutesSyncPacket.Line line : lines) {
                Vec3 from = center(line.from());
                Vec3 to = center(line.to());
                if (from.distanceToSqr(to) < 0.01D) {
                    continue;
                }
                drawRibbon(buffer, pose, camera, look, from, to, worldPerPixel * HALO_PIXELS * 0.5D, COLOR_HALO);
                drawRibbon(buffer, pose, camera, look, from, to, worldPerPixel * SHEATH_PIXELS * 0.5D, COLOR_SHEATH);
                drawRibbon(buffer, pose, camera, look, from, to, worldPerPixel * CORE_PIXELS * 0.5D, COLOR_CORE);
                drawFlow(buffer, pose, camera, look, from, to, worldPerPixel * FLOW_PIXELS * 0.5D, phase);
            }
        });
    }

    private static void drawFlow(VertexConsumer buffer, PoseStack.Pose pose, Vec3 camera, Vec3 look, Vec3 from, Vec3 to, double halfWidth, float phase) {
        Vec3 delta = to.subtract(from);
        double length = delta.length();
        if (length < 0.05D) {
            return;
        }
        Vec3 dir = delta.scale(1.0D / length);
        double dash = Math.min(FLOW_LENGTH, length * 0.35D);
        for (double distance = phase * FLOW_SPACING; distance < length; distance += FLOW_SPACING) {
            double end = Math.min(length, distance + dash);
            drawRibbon(buffer, pose, camera, look, from.add(dir.scale(distance)), from.add(dir.scale(end)), halfWidth, COLOR_FLOW);
        }
    }

    private static void drawRibbon(VertexConsumer buffer, PoseStack.Pose pose, Vec3 camera, Vec3 look, Vec3 from, Vec3 to, double halfWidth, int color) {
        Vec3 side = sideVector(from, to, look, halfWidth);
        if (side == Vec3.ZERO) {
            return;
        }
        vertex(buffer, pose, camera, from.subtract(side), color);
        vertex(buffer, pose, camera, from.add(side), color);
        vertex(buffer, pose, camera, to.add(side), color);
        vertex(buffer, pose, camera, to.subtract(side), color);
    }

    private static Vec3 sideVector(Vec3 from, Vec3 to, Vec3 look, double halfWidth) {
        Vec3 dir = to.subtract(from);
        if (dir.lengthSqr() < 1.0E-8D || halfWidth <= 0.0D) {
            return Vec3.ZERO;
        }
        Vec3 side = dir.cross(look);
        if (side.lengthSqr() < 1.0E-8D) {
            side = dir.cross(new Vec3(0.0D, 1.0D, 0.0D));
        }
        if (side.lengthSqr() < 1.0E-8D) {
            return Vec3.ZERO;
        }
        return side.normalize().scale(halfWidth);
    }

    /**
     * worldUnitsPerPixel: 把当前投影下的 1 个屏幕像素换成世界单位，线宽才不随 RTS 缩放变化。
     */
    private static double worldUnitsPerPixel(Matrix4f projection) {
        int width = Math.max(1, Minecraft.getInstance().getWindow().getWidth());
        float scaleX = Math.abs(projection.m00());
        if (scaleX < 1.0E-6F) {
            return 0.02D;
        }
        return 2.0D / (scaleX * width);
    }

    private static boolean hasDrawableLine(List<RtsLogisticsRoutesSyncPacket.Line> lines) {
        for (RtsLogisticsRoutesSyncPacket.Line line : lines) {
            if (line != null && line.from() != null && line.to() != null
                    && center(line.from()).distanceToSqr(center(line.to())) >= 0.01D) {
                return true;
            }
        }
        return false;
    }

    private static Vec3 center(net.minecraft.core.BlockPos pos) {
        return new Vec3(pos.getX() + 0.5D, pos.getY() + 0.85D, pos.getZ() + 0.5D);
    }

    private static Vec3 cameraAxis(org.joml.Vector3fc axis) {
        return new Vec3(axis.x(), axis.y(), axis.z());
    }

    private static void vertex(VertexConsumer buffer, PoseStack.Pose pose, Vec3 camera, Vec3 pos, int color) {
        float red = ((color >> 16) & 0xFF) / 255.0F;
        float green = ((color >> 8) & 0xFF) / 255.0F;
        float blue = (color & 0xFF) / 255.0F;
        float alpha = ((color >> 24) & 0xFF) / 255.0F;
        buffer.addVertex(pose, (float) (pos.x - camera.x), (float) (pos.y - camera.y), (float) (pos.z - camera.z))
                .setColor(red, green, blue, alpha);
    }
}
