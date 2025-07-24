package net.desolatesky.crafting.recipe;

public record RecipeType<R extends Recipe>(String name, Class<R> recipeClass) {

    public static final RecipeType<CraftingRecipe> CRAFTING = new RecipeType("crafting", CraftingRecipe.class);
    public static final RecipeType<Recipe> SMELTING = new RecipeType("smelting", Recipe.class);
    public static final RecipeType<Recipe> CAMPFIRE_COOKING = new RecipeType("campfire_cooking", Recipe.class);
    public static final RecipeType<Recipe> STONECUTTING = new RecipeType("stonecutting", Recipe.class);
    public static final RecipeType<Recipe> REPAIRING = new RecipeType("repairing", Recipe.class);
    public static final RecipeType<Recipe> COMPOSTING = new RecipeType("composting", Recipe.class);
    public static final RecipeType<Recipe> BREWING = new RecipeType("brewing", Recipe.class);

}
