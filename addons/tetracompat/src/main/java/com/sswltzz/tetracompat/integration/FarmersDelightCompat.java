package com.sswltzz.tetracompat.integration;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.common.ItemAbility;
import net.neoforged.neoforge.common.crafting.CompoundIngredient;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import vectorwing.farmersdelight.common.crafting.CuttingBoardRecipe;
import vectorwing.farmersdelight.common.crafting.ingredient.ChanceResult;
import vectorwing.farmersdelight.common.crafting.ingredient.ItemAbilityIngredient;
import vectorwing.farmersdelight.common.registry.ModSounds;

public class FarmersDelightCompat {
    private static final TagKey<Item> KNIFE_TAG = TagKey.create(Registries.ITEM,
            ResourceLocation.fromNamespaceAndPath("c", "tools/knife"));
    /**
     * Tetracelium is not available for 1.21.1 yet, so the blade cut ability is recreated by name.
     * 1.20.1 tetracelium used ToolAction.get("blade_cut"), the ItemAbility equivalent keeps the same id.
     */
    private static final ItemAbility BLADE_CUT = ItemAbility.get("blade_cut");

    public static void injectRecipes(Map<ResourceLocation, RecipeHolder<?>> recipes, BiConsumer<ResourceLocation, RecipeHolder<?>> add) {
        Map<ResourceLocation, CuttingBoardRecipe> cuttingRecipes = recipes.entrySet().stream()
                .filter(entry -> entry.getValue().value() instanceof CuttingBoardRecipe)
                .filter(entry -> !entry.getKey().getNamespace().equals("farmersdelight"))
                .filter(entry -> !entry.getKey().getNamespace().equals("tetracelium"))
                .filter(entry -> isKnifeTool(((CuttingBoardRecipe) entry.getValue().value()).getTool()))
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> (CuttingBoardRecipe) entry.getValue().value()));

        cuttingRecipes.forEach((originalID, recipe) -> {
            ResourceLocation id = ResourceLocation.fromNamespaceAndPath("tetracompat",
                    String.format("tetracelium_%s_from_%s", originalID.getPath(), originalID.getNamespace()));
            NonNullList<ChanceResult> chanceResults = recipe.getRollableResults();
            Ingredient ingredient = Ingredient.fromValues(recipe.getIngredients().stream()
                    .flatMap(value -> Arrays.stream(value.getValues())));
            CuttingBoardRecipe cuttingBoardRecipe = new CuttingBoardRecipe("", ingredient,
                    new Ingredient(new ItemAbilityIngredient(BLADE_CUT)), chanceResults,
                    Optional.of(ModSounds.BLOCK_CUTTING_BOARD_KNIFE.get()));
            add.accept(id, new RecipeHolder<>(id, cuttingBoardRecipe));
        });
    }

    private static boolean isKnifeTool(Ingredient ingredient) {
        ICustomIngredient customIngredient = ingredient.getCustomIngredient();
        if (customIngredient instanceof CompoundIngredient compoundIngredient) {
            return compoundIngredient.children().stream().anyMatch(FarmersDelightCompat::isKnifeTool);
        }
        if (customIngredient instanceof ItemAbilityIngredient abilityIngredient) {
            return "knife_dig".equals(abilityIngredient.getItemAbility().name());
        }
        for (Ingredient.Value value : ingredient.getValues()) {
            if (value instanceof Ingredient.TagValue tagValue && KNIFE_TAG.equals(tagValue.tag())) {
                return true;
            }
        }
        return false;
    }
}
