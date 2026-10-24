package client.cn.nsimu.simukraft.client.fluid;

import common.cn.nsimu.simukraft.SimuKraft;
import common.cn.nsimu.simukraft.registry.ModFluidTypes;
import common.cn.nsimu.simukraft.registry.ModFluids;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.event.RegisterFluidModelsEvent;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

public final class ClientFluidExtensions {
    private static final Identifier STILL = Identifier.fromNamespaceAndPath(SimuKraft.MOD_ID, "block/milk_still");
    private static final Identifier FLOW = Identifier.fromNamespaceAndPath(SimuKraft.MOD_ID, "block/milk_flow");

    private ClientFluidExtensions() {
    }

    /**
     * registerModels: 26.3 用 FluidModel 给静止和流动牛奶贴图。不注册会显示缺失纹理。
     */
    public static void registerModels(RegisterFluidModelsEvent event) {
        event.register(new FluidModel.Unbaked(
                new Material(STILL),
                new Material(FLOW),
                null,
                null), ModFluids.SOURCE_MILK, ModFluids.FLOWING_MILK);
    }

    public static void register(RegisterClientExtensionsEvent event) {
        event.registerFluidType(new IClientFluidTypeExtensions() {
        }, ModFluidTypes.MILK);
    }
}
