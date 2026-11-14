package net.acetheeldritchking.art_of_forging.util;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

public class AoFTags {

    public static class Entities {
        public static final TagKey<EntityType<?>> BOSS_ENTITIES = TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath("art_of_forging", "bosses"));
    }
}
