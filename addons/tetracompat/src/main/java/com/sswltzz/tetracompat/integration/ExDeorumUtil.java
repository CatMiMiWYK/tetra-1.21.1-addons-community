package com.sswltzz.tetracompat.integration;

import net.minecraft.world.item.Item;
import thedarkcolour.exdeorum.recipe.RecipeCaches;
import thedarkcolour.exdeorum.recipe.RecipeUtil;
import thedarkcolour.exdeorum.recipe.hammer.HammerRecipe;

public class ExDeorumUtil {
    public static HammerRecipe getHammerRecipe(Item item) {
        try {
            Class.forName("thedarkcolour.exdeorum.recipe.RecipeUtil");
            RecipeCaches caches = RecipeUtil.getServerRecipeCaches(true);
            return caches == null ? null : caches.getHammerRecipe(item);
        } catch (ClassNotFoundException exception) {
            return null;
        }
    }
}
