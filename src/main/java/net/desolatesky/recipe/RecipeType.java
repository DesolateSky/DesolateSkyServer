package net.desolatesky.recipe;

import net.desolatesky.recipe.input.RecipeInput;
import net.desolatesky.recipe.result.RecipeResult;
import net.desolatesky.recipe.type.CatalystRecipe;
import net.desolatesky.recipe.type.ShapedRecipe;

public record RecipeType<I extends RecipeInput, R extends RecipeResult>(Class<I> inputClass, Class<R> resultClass) {

    public static final RecipeType<CatalystRecipe.Input, CatalystRecipe.Result> CATALYST = new RecipeType<>(CatalystRecipe.Input.class, CatalystRecipe.Result.class);
    public static final RecipeType<ShapedRecipe.Input, ShapedRecipe.Result> SHAPED = new RecipeType<>(ShapedRecipe.Input.class, ShapedRecipe.Result.class);

}
