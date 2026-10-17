package client.cn.nsimu.simukraft.client.city.map;

import com.mojang.renderpearl.api.pipeline.CompareOp;
import com.mojang.renderpearl.api.pipeline.DepthStencilState;
import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.event.RegisterRenderPipelinesEvent;

/**
 * 立体沙盘管线。画到 Picture-in-Picture 的深度纹理上，外墙盖不住顶面。
 */
public final class SimuMap3DPipelines {
    public static final RenderPipeline MAP = RenderPipeline.builder(RenderPipelines.DEBUG_FILLED_SNIPPET)
            .withLocation(Identifier.fromNamespaceAndPath("simukraft", "pipeline/city_map_3d"))
            .withCull(false)
            .withDepthStencilState(new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, true))
            .build();
    public static final RenderType TYPE = RenderType.create(
            "simukraft_city_map_3d",
            RenderSetup.builder(MAP).createRenderSetup());

    private SimuMap3DPipelines() {
    }

    public static void register(RegisterRenderPipelinesEvent event) {
        event.registerPipeline(MAP);
    }
}
