package client.cn.nsimu.simukraft.client.city.map;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;

/**
 * 把沙盘画进带深度的离屏纹理，再贴回城市地图。
 */
public final class CityMap3DPipRenderer extends PictureInPictureRenderer<CityMap3DPipState> {
    @Override
    public Class<CityMap3DPipState> getRenderStateClass() {
        return CityMap3DPipState.class;
    }

    @Override
    protected void renderToTexture(CityMap3DPipState state, PoseStack poseStack, SubmitNodeCollector collector) {
        if (state.mesh().isEmpty()) {
            return;
        }
        collector.submitCustomGeometry(poseStack, SimuMap3DPipelines.TYPE, (pose, buffer) ->
                state.mesh().emitWorld(buffer, pose, state.yaw(), state.mapScale(),
                        state.liveCenterX(), state.liveCenterZ()));
    }

    @Override
    protected float getTranslateY(int height, int guiScale) {
        return height / 2.0F;
    }

    @Override
    protected String getTextureLabel() {
        return "simukraft city map 3d";
    }
}