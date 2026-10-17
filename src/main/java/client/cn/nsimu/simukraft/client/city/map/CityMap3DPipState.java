package client.cn.nsimu.simukraft.client.city.map;

import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.renderer.state.gui.pip.PictureInPictureRenderState;
import org.joml.Matrix3x2fc;
import org.jspecify.annotations.Nullable;

/**
 * 立体沙盘的 Picture-in-Picture 状态。单独的深度纹理挡住外墙，顶面才能留住。
 */
public record CityMap3DPipState(
        SimuMap3DMesh mesh,
        float yaw,
        float mapScale,
        int liveCenterX,
        int liveCenterZ,
        int x0,
        int y0,
        int x1,
        int y1,
        Matrix3x2fc pose,
        @Nullable ScreenRectangle scissorArea,
        @Nullable ScreenRectangle bounds
) implements PictureInPictureRenderState {
    public CityMap3DPipState(
            SimuMap3DMesh mesh, float yaw, float mapScale, int liveCenterX, int liveCenterZ,
            int x, int y, int width, int height, Matrix3x2fc pose, @Nullable ScreenRectangle scissorArea
    ) {
        this(mesh, yaw, mapScale, liveCenterX, liveCenterZ, x, y, x + width, y + height, pose, scissorArea,
                PictureInPictureRenderState.getBounds(x, y, x + width, y + height, pose, scissorArea));
    }

    @Override
    public float scale() {
        return 1.0F;
    }
}