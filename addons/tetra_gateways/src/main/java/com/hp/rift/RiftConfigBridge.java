package com.hp.rift;

import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RiftConfigBridge {
    private static final List<ResourceLocation> ATTRIBUTE_BLACKLIST = new ArrayList<>();
    private static final List<ResourceLocation> POOL_A = new ArrayList<>();
    private static final List<ResourceLocation> POOL_B = new ArrayList<>();
    private static final List<ResourceLocation> POOL_C = new ArrayList<>();

    public static void setAttributeBlacklist(List<String> ids) {
        ATTRIBUTE_BLACKLIST.clear();
        ids.stream().map(ResourceLocation::tryParse).filter(id -> id != null).forEach(ATTRIBUTE_BLACKLIST::add);
    }

    public static boolean isAttributeBlocked(ResourceLocation id) {
        return id != null && ATTRIBUTE_BLACKLIST.contains(id);
    }

    public static void setPool(String pool, List<String> ids) {
        List<ResourceLocation> target = getMutablePool(pool);
        target.clear();
        ids.stream().map(ResourceLocation::tryParse).filter(id -> id != null).forEach(target::add);
    }

    public static List<ResourceLocation> getPool(String pool) {
        return Collections.unmodifiableList(getMutablePool(pool));
    }

    private static List<ResourceLocation> getMutablePool(String pool) {
        return switch (pool) {
            case "b" -> POOL_B;
            case "c" -> POOL_C;
            default -> POOL_A;
        };
    }

    private RiftConfigBridge() {
    }
}
