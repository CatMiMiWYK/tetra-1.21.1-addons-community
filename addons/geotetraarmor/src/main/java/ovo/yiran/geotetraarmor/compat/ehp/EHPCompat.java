package ovo.yiran.geotetraarmor.compat.ehp;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import ovo.yiran.geotetraarmor.GeoTetraArmor;

import java.lang.reflect.Method;

/**
 * ExtraHoloPage 兼容。1.21.1 的 ExtraHoloPage 为后续移植项，此处以反射调用其 API，
 * 避免在编译期对其产生硬依赖（未安装时由 CompatHandler 守卫）。
 */
public class EHPCompat {
    public static void onClientCompat() {
        try {
            Class<?> registerClass = Class.forName("net.yiran.extraholopage.api.ExtraHoloRegister");
            Method register = registerClass.getMethod("register", Item.class);
            registerOne(register, GeoTetraArmor.HEAD.get(), 42, 42, 84, 0);
            registerOne(register, GeoTetraArmor.CHEST.get(), 42, 42, 42, 0);
            registerOne(register, GeoTetraArmor.LEGS.get(), 42, 42, 126, 0);
            registerOne(register, GeoTetraArmor.FEET.get(), 42, 42, 0, 0);
        } catch (Throwable ignored) {
            // ExtraHoloPage 版本不匹配时静默跳过
        }
    }

    private static void registerOne(Method register, Item item, int width, int height, int x, int y) throws Exception {
        Object builder = register.invoke(null, item);
        builder.getClass()
                .getMethod("setTexture", ResourceLocation.class, int.class, int.class, int.class, int.class)
                .invoke(builder, ResourceLocation.fromNamespaceAndPath("geotetraarmor", "textures/gui/gta.png"), width, height, x, y);
    }
}
