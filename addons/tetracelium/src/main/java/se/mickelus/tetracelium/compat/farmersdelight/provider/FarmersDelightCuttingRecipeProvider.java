package se.mickelus.tetracelium.compat.farmersdelight.provider;

import java.util.concurrent.CompletableFuture;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;
import se.mickelus.tetracelium.compat.farmersdelight.FarmersDelightToolActions;
import vectorwing.farmersdelight.common.crafting.ingredient.ItemAbilityIngredient;
import vectorwing.farmersdelight.common.registry.ModItems;
import vectorwing.farmersdelight.common.tag.CommonTags;
import vectorwing.farmersdelight.data.builder.CuttingBoardRecipeBuilder;

public class FarmersDelightCuttingRecipeProvider extends RecipeProvider {
    public FarmersDelightCuttingRecipeProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(packOutput, lookupProvider);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        RecipeOutput consumer = wrap(recipeOutput.withConditions(new ModLoadedCondition("farmersdelight")));
        cuttingAnimalItems(consumer);
        cuttingVegetables(consumer);
        cuttingFoods(consumer);
        cuttingFlowers(consumer);
    }

    private static RecipeOutput wrap(RecipeOutput delegate) {
        return new RecipeOutput() {
            @Override
            public void accept(ResourceLocation id, Recipe<?> recipe, AdvancementHolder advancement, ICondition... conditions) {
                delegate.accept(ResourceLocation.fromNamespaceAndPath("tetracelium", id.getPath()), recipe, advancement, conditions);
            }

            @Override
            public Advancement.Builder advancement() {
                return delegate.advancement();
            }
        };
    }

    private static Ingredient blade() {
        return new Ingredient(new ItemAbilityIngredient(FarmersDelightToolActions.bladeCut));
    }

    private static Ingredient of(ItemLike item) {
        return Ingredient.of(item);
    }

    private static void cuttingAnimalItems(RecipeOutput consumer) {
        CuttingBoardRecipeBuilder.cuttingRecipe(of(Items.BEEF), blade(), ModItems.MINCED_BEEF.get(), 2).save(consumer);
        CuttingBoardRecipeBuilder.cuttingRecipe(of(Items.PORKCHOP), blade(), ModItems.BACON.get(), 2).save(consumer);
        CuttingBoardRecipeBuilder.cuttingRecipe(of(Items.CHICKEN), blade(), ModItems.CHICKEN_CUTS.get(), 2).addResult(Items.BONE_MEAL).save(consumer);
        CuttingBoardRecipeBuilder.cuttingRecipe(of(Items.COOKED_CHICKEN), blade(), ModItems.COOKED_CHICKEN_CUTS.get(), 2).addResult(Items.BONE_MEAL).save(consumer);
        CuttingBoardRecipeBuilder.cuttingRecipe(of(Items.COD), blade(), ModItems.COD_SLICE.get(), 2).addResult(Items.BONE_MEAL).save(consumer);
        CuttingBoardRecipeBuilder.cuttingRecipe(of(Items.COOKED_COD), blade(), ModItems.COOKED_COD_SLICE.get(), 2).addResult(Items.BONE_MEAL).save(consumer);
        CuttingBoardRecipeBuilder.cuttingRecipe(of(Items.SALMON), blade(), ModItems.SALMON_SLICE.get(), 2).addResult(Items.BONE_MEAL).save(consumer);
        CuttingBoardRecipeBuilder.cuttingRecipe(of(Items.COOKED_SALMON), blade(), ModItems.COOKED_SALMON_SLICE.get(), 2).addResult(Items.BONE_MEAL).save(consumer);
        CuttingBoardRecipeBuilder.cuttingRecipe(of(ModItems.HAM.get()), blade(), Items.PORKCHOP, 2).addResult(Items.BONE).save(consumer);
        CuttingBoardRecipeBuilder.cuttingRecipe(of(ModItems.SMOKED_HAM.get()), blade(), Items.COOKED_PORKCHOP, 2).addResult(Items.BONE).save(consumer);
        CuttingBoardRecipeBuilder.cuttingRecipe(of(Items.MUTTON), blade(), ModItems.MUTTON_CHOPS.get(), 2).save(consumer);
        CuttingBoardRecipeBuilder.cuttingRecipe(of(Items.COOKED_MUTTON), blade(), ModItems.COOKED_MUTTON_CHOPS.get(), 2).save(consumer);
        CuttingBoardRecipeBuilder.cuttingRecipe(of(Items.INK_SAC), blade(), Items.BLACK_DYE, 2).save(consumer);
    }

    private static void cuttingVegetables(RecipeOutput consumer) {
        CuttingBoardRecipeBuilder.cuttingRecipe(of(ModItems.CABBAGE.get()), blade(), ModItems.CABBAGE_LEAF.get(), 2).save(consumer);
        CuttingBoardRecipeBuilder.cuttingRecipe(of(ModItems.RICE_PANICLE.get()), blade(), ModItems.RICE.get(), 1).addResult(ModItems.STRAW.get()).save(consumer);
        CuttingBoardRecipeBuilder.cuttingRecipe(of(Items.MELON), blade(), Items.MELON_SLICE, 9).save(consumer);
        CuttingBoardRecipeBuilder.cuttingRecipe(of(Items.PUMPKIN), blade(), ModItems.PUMPKIN_SLICE.get(), 4).save(consumer);
        CuttingBoardRecipeBuilder.cuttingRecipe(of(ModItems.BROWN_MUSHROOM_COLONY.get()), blade(), Items.BROWN_MUSHROOM, 5).save(consumer);
        CuttingBoardRecipeBuilder.cuttingRecipe(of(ModItems.RED_MUSHROOM_COLONY.get()), blade(), Items.RED_MUSHROOM, 5).save(consumer);
    }

    private static void cuttingFoods(RecipeOutput consumer) {
        CuttingBoardRecipeBuilder.cuttingRecipe(Ingredient.of(CommonTags.Items.FOODS_DOUGH), blade(), ModItems.RAW_PASTA.get(), 1).save(consumer, ResourceLocation.fromNamespaceAndPath("farmersdelight", "cutting/tag_dough"));
        CuttingBoardRecipeBuilder.cuttingRecipe(of(ModItems.KELP_ROLL.get()), blade(), ModItems.KELP_ROLL_SLICE.get(), 3).save(consumer);
        CuttingBoardRecipeBuilder.cuttingRecipe(of(Items.CAKE), blade(), ModItems.CAKE_SLICE.get(), 7).save(consumer);
        CuttingBoardRecipeBuilder.cuttingRecipe(of(ModItems.APPLE_PIE.get()), blade(), ModItems.APPLE_PIE_SLICE.get(), 4).save(consumer);
        CuttingBoardRecipeBuilder.cuttingRecipe(of(ModItems.SWEET_BERRY_CHEESECAKE.get()), blade(), ModItems.SWEET_BERRY_CHEESECAKE_SLICE.get(), 4).save(consumer);
        CuttingBoardRecipeBuilder.cuttingRecipe(of(ModItems.CHOCOLATE_PIE.get()), blade(), ModItems.CHOCOLATE_PIE_SLICE.get(), 4).save(consumer);
    }

    private static void cuttingFlowers(RecipeOutput consumer) {
        CuttingBoardRecipeBuilder.cuttingRecipe(of(Items.WITHER_ROSE), blade(), Items.BLACK_DYE, 2).save(consumer);
        CuttingBoardRecipeBuilder.cuttingRecipe(of(Items.CORNFLOWER), blade(), Items.BLUE_DYE, 2).save(consumer);
        CuttingBoardRecipeBuilder.cuttingRecipe(of(Items.BLUE_ORCHID), blade(), Items.LIGHT_BLUE_DYE, 2).save(consumer);
        CuttingBoardRecipeBuilder.cuttingRecipe(of(Items.AZURE_BLUET), blade(), Items.LIGHT_GRAY_DYE, 2).save(consumer);
        CuttingBoardRecipeBuilder.cuttingRecipe(of(Items.OXEYE_DAISY), blade(), Items.LIGHT_GRAY_DYE, 2).save(consumer);
        CuttingBoardRecipeBuilder.cuttingRecipe(of(Items.WHITE_TULIP), blade(), Items.LIGHT_GRAY_DYE, 2).save(consumer);
        CuttingBoardRecipeBuilder.cuttingRecipe(of(Items.ALLIUM), blade(), Items.MAGENTA_DYE, 2).save(consumer);
        CuttingBoardRecipeBuilder.cuttingRecipe(of(Items.ORANGE_TULIP), blade(), Items.ORANGE_DYE, 2).save(consumer);
        CuttingBoardRecipeBuilder.cuttingRecipe(of(Items.PINK_TULIP), blade(), Items.PINK_DYE, 2).save(consumer);
        CuttingBoardRecipeBuilder.cuttingRecipe(of(Items.RED_TULIP), blade(), Items.RED_DYE, 2).save(consumer);
        CuttingBoardRecipeBuilder.cuttingRecipe(of(Items.POPPY), blade(), Items.RED_DYE, 2).save(consumer);
        CuttingBoardRecipeBuilder.cuttingRecipe(of(Items.LILY_OF_THE_VALLEY), blade(), Items.WHITE_DYE, 2).save(consumer);
        CuttingBoardRecipeBuilder.cuttingRecipe(of(Items.DANDELION), blade(), Items.YELLOW_DYE, 2).save(consumer);
        CuttingBoardRecipeBuilder.cuttingRecipe(of(ModItems.WILD_BEETROOTS.get()), blade(), Items.BEETROOT_SEEDS, 1).addResult(Items.RED_DYE).save(consumer);
        CuttingBoardRecipeBuilder.cuttingRecipe(of(ModItems.WILD_CABBAGES.get()), blade(), ModItems.CABBAGE_SEEDS.get(), 1).addResultWithChance(Items.YELLOW_DYE, 0.5f, 2).save(consumer);
        CuttingBoardRecipeBuilder.cuttingRecipe(of(ModItems.WILD_CARROTS.get()), blade(), Items.CARROT, 1).addResultWithChance(Items.LIGHT_GRAY_DYE, 0.5f, 2).save(consumer);
        CuttingBoardRecipeBuilder.cuttingRecipe(of(ModItems.WILD_ONIONS.get()), blade(), ModItems.ONION.get(), 1).addResult(Items.MAGENTA_DYE, 2).addResultWithChance(Items.LIME_DYE, 0.1f).save(consumer);
        CuttingBoardRecipeBuilder.cuttingRecipe(of(ModItems.WILD_POTATOES.get()), blade(), Items.POTATO, 1).addResultWithChance(Items.PURPLE_DYE, 0.5f, 2).save(consumer);
        CuttingBoardRecipeBuilder.cuttingRecipe(of(ModItems.WILD_RICE.get()), blade(), ModItems.RICE.get(), 1).addResultWithChance(ModItems.STRAW.get(), 0.5f).save(consumer);
        CuttingBoardRecipeBuilder.cuttingRecipe(of(ModItems.WILD_TOMATOES.get()), blade(), ModItems.TOMATO_SEEDS.get(), 1).addResultWithChance(ModItems.TOMATO.get(), 0.2f).addResultWithChance(Items.GREEN_DYE, 0.1f).save(consumer);
    }
}
