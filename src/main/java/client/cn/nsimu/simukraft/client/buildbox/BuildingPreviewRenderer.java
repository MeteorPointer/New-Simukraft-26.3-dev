package client.cn.nsimu.simukraft.client.buildbox;

import client.cn.nsimu.simukraft.client.freecamera.FreeCameraManager;
import com.mojang.blaze3d.vertex.PoseStack;
import common.cn.nsimu.simukraft.SimuKraft;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.MovingBlockRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.SubmitCustomGeometryEvent;

@EventBusSubscriber(modid = SimuKraft.MOD_ID, value = Dist.CLIENT)
public final class BuildingPreviewRenderer {
    private static boolean loggedOnce;

    private BuildingPreviewRenderer() {
    }

    @SubscribeEvent
    public static void onSubmitGeometry(SubmitCustomGeometryEvent event) {
        if (!BuildingPreviewManager.isPreviewActive()) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null) {
            return;
        }
        PreviewMesh mesh = BuildingPreviewManager.getCachedMesh();
        if (mesh == null || mesh.isEmpty()) {
            if (!loggedOnce) {
                SimuKraft.LOGGER.warn("SimuKraft: Preview mesh is empty during render");
                loggedOnce = true;
            }
            return;
        }
        loggedOnce = false;
        submitMesh(mesh, event);
    }

    /**
     * submitMesh: 把预览方块提交进 26.3 的几何收集器。
     */
    public static void submitMesh(PreviewMesh mesh, SubmitCustomGeometryEvent event) {
        if (mesh == null || mesh.isEmpty()) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        if (!(minecraft.level instanceof ClientLevel level)) {
            return;
        }
        Vec3 camera = event.getLevelRenderState().cameraRenderState.pos;
        Vec3 rtsFocus = FreeCameraManager.isRtsActive() ? FreeCameraManager.rtsFocus() : null;
        double cullRadiusSqr = cullRadiusSqr(rtsFocus);
        PoseStack poseStack = event.getPoseStack();
        SubmitNodeCollector collector = event.getSubmitNodeCollector();
        Holder<Biome> biome = level.getBiome(mesh.origin());
        BlockAndTintGetter tintGetter = level instanceof BlockAndTintGetter getter ? getter : null;
        for (PreviewBlockData block : mesh.blocks()) {
            submitBlock(poseStack, collector, tintGetter, camera, biome, block, rtsFocus, cullRadiusSqr);
        }
    }

    private static double cullRadiusSqr(Vec3 rtsFocus) {
        if (rtsFocus != null) {
            double half = Math.max(24.0D, FreeCameraManager.rtsZoom() * 0.7D);
            return half * half;
        }
        return 96.0D * 96.0D;
    }

    private static void submitBlock(PoseStack poseStack,
                                    SubmitNodeCollector collector,
                                    BlockAndTintGetter tintGetter,
                                    Vec3 camera,
                                    Holder<Biome> biome,
                                    PreviewBlockData block,
                                    Vec3 rtsFocus,
                                    double cullRadiusSqr) {
        BlockPos pos = block.pos();
        if (rtsFocus != null) {
            double dx = pos.getX() + 0.5D - rtsFocus.x;
            double dz = pos.getZ() + 0.5D - rtsFocus.z;
            if (dx * dx + dz * dz > cullRadiusSqr) {
                return;
            }
        } else {
            double dx = pos.getX() + 0.5D - camera.x;
            double dy = pos.getY() + 0.5D - camera.y;
            double dz = pos.getZ() + 0.5D - camera.z;
            if (dx * dx + dy * dy + dz * dz > cullRadiusSqr) {
                return;
            }
        }
        MovingBlockRenderState renderState = new MovingBlockRenderState();
        renderState.blockPos = pos;
        renderState.randomSeedPos = pos;
        renderState.blockState = block.state();
        renderState.biome = biome;
        if (tintGetter != null) {
            renderState.cardinalLighting = tintGetter.cardinalLighting();
            renderState.lightEngine = tintGetter.getLightEngine();
        }
        poseStack.pushPose();
        poseStack.translate(pos.getX() - camera.x, pos.getY() - camera.y, pos.getZ() - camera.z);
        collector.submitMovingBlock(poseStack, renderState, 0);
        poseStack.popPose();
    }
}
