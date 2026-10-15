package common.cn.nsimu.simukraft;

import net.minecraft.SharedConstants;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponentInitializers;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.Bootstrap;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 单元测试创建 ItemStack 前先绑定原版组件表。
 * 26.3 起 {@link Bootstrap#bootStrap()} 不再调用 {@link Holder.Reference#bindComponents}，
 * 组件表改由数据包重载写入。测试没有伤害类型等数据包标签，整表初始化会在
 * {@code minecraft:is_fire} 处失败，所以按条目绑定，失败的条目用空表占位。
 */
public final class MinecraftTestBootstrap {
    private static final AtomicBoolean STARTED = new AtomicBoolean();

    private MinecraftTestBootstrap() {
    }

    public static void ensure() {
        if (!STARTED.compareAndSet(false, true)) {
            return;
        }
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
        RegistryAccess registries = RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY);
        bindInitializers(BuiltInRegistries.ITEM, registries);
        bindInitializers(BuiltInRegistries.BLOCK, registries);
        bindEmptyWhereMissing(BuiltInRegistries.ITEM);
        bindEmptyWhereMissing(BuiltInRegistries.BLOCK);
    }

    private static <T> void bindInitializers(Registry<T> registry, RegistryAccess registries) {
        Map<ResourceKey<T>, DataComponentMap.Builder> builders = new IdentityHashMap<>();
        for (Object entry : initializerEntries()) {
            ResourceKey<?> key = entryKey(entry);
            if (!key.isFor(registry.key())) {
                continue;
            }
            bindEntry(registries, builders, entry, key);
        }
        builders.forEach((key, builder) -> registry.get(key).ifPresent(holder -> {
            if (!holder.areComponentsBound()) {
                holder.bindComponents(builder.build());
            }
        }));
    }

    private static <T> void bindEntry(RegistryAccess registries,
                                      Map<ResourceKey<T>, DataComponentMap.Builder> builders,
                                      Object entry,
                                      ResourceKey<?> rawKey) {
        @SuppressWarnings("unchecked")
        ResourceKey<T> key = (ResourceKey<T>) rawKey;
        @SuppressWarnings("unchecked")
        DataComponentInitializers.Initializer<T> initializer =
                (DataComponentInitializers.Initializer<T>) entryInitializer(entry);
        boolean item = key.isFor(BuiltInRegistries.ITEM.key());
        DataComponentMap.Builder probe = DataComponentMap.builder();
        if (item) {
            probe.addAll(DataComponents.COMMON_ITEM_COMPONENTS);
        }
        try {
            initializer.run(probe, registries, key);
            builders.put(key, probe);
        } catch (RuntimeException ignored) {
            if (item) {
                builders.put(key, DataComponentMap.builder().addAll(DataComponents.COMMON_ITEM_COMPONENTS));
            }
        }
    }

    /**
     * 条目记录是私有类型，只能反射读取。
     */
    private static List<?> initializerEntries() {
        try {
            Field field = DataComponentInitializers.class.getDeclaredField("initializers");
            field.setAccessible(true);
            return (List<?>) field.get(BuiltInRegistries.DATA_COMPONENT_INITIALIZERS);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Cannot read vanilla component initializers", exception);
        }
    }

    private static ResourceKey<?> entryKey(Object entry) {
        return (ResourceKey<?>) invokeRecord(entry, "key");
    }

    private static Object entryInitializer(Object entry) {
        return invokeRecord(entry, "initializer");
    }

    private static Object invokeRecord(Object entry, String accessor) {
        try {
            Method method = entry.getClass().getDeclaredMethod(accessor);
            method.setAccessible(true);
            return method.invoke(entry);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Cannot read component initializer " + accessor, exception);
        }
    }

    private static <T> void bindEmptyWhereMissing(Registry<T> registry) {
        registry.listElements().forEach(holder -> {
            if (!holder.areComponentsBound()) {
                holder.bindComponents(DataComponentMap.EMPTY);
            }
        });
    }
}
