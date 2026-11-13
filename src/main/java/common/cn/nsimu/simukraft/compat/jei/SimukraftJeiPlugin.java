package common.cn.nsimu.simukraft.compat.jei;

import common.cn.nsimu.simukraft.SimuKraft;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.Identifier;
import net.neoforged.fml.loading.FMLEnvironment;

/**
 * JEI 服务入口。类必须留在 common，专用服务端会滤掉 client 包。
 */
@JeiPlugin
public class SimukraftJeiPlugin implements IModPlugin {
    private static final String CLIENT_PLUGIN = "client.cn.nsimu.simukraft.client.compat.jei.SimukraftClientJeiPlugin";
    private final IModPlugin clientPlugin = createClientPlugin();

    /**
     * getPluginUid: JEI 用来区分插件的唯一标识。
     */
    @Override
    public Identifier getPluginUid() {
        return Identifier.fromNamespaceAndPath(SimuKraft.MOD_ID, "jei");
    }

    /**
     * registerCategories: 仅在物理客户端转发给真实 JEI 插件。
     */
    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        if (clientPlugin != null) {
            clientPlugin.registerCategories(registration);
        }
    }

    /**
     * registerRecipes: 仅在物理客户端转发给真实 JEI 插件。
     */
    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        if (clientPlugin != null) {
            clientPlugin.registerRecipes(registration);
        }
    }

    /**
     * registerRecipeCatalysts: 仅在物理客户端转发给真实 JEI 插件。
     */
    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        if (clientPlugin != null) {
            clientPlugin.registerRecipeCatalysts(registration);
        }
    }

    private static IModPlugin createClientPlugin() {
        if (FMLEnvironment.getDist() == null || FMLEnvironment.getDist().isDedicatedServer()) {
            return null;
        }
        try {
            return (IModPlugin) Class.forName(CLIENT_PLUGIN).getConstructor().newInstance();
        } catch (ReflectiveOperationException | RuntimeException exception) {
            SimuKraft.LOGGER.warn("Simukraft: Failed to load client JEI plugin.", exception);
            return null;
        }
    }
}
