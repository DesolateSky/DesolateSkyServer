package net.desolatesky.crafting.recipe;

import net.desolatesky.crafting.ingredient.RecipeIngredient;
import net.desolatesky.util.ArrayUtil;

public final class CraftingRecipe implements Recipe {

    private final RecipeIngredient[][] recipe;

    public CraftingRecipe(RecipeIngredient[][] recipe) {
        this.recipe = recipe;
    }

    // todo
    public boolean matches(RecipeIngredient[][] input) {
        final RecipeIngredient[][] shifted = new RecipeIngredient[recipe.length][recipe[0].length];
        ArrayUtil.shiftToTopLeftCorner(input, shifted, i -> i == RecipeIngredient.EMPTY, RecipeIngredient.EMPTY);
        return false;
    }

    public static class Result {

    }

}
