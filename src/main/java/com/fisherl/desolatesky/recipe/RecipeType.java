package com.fisherl.desolatesky.recipe;

import com.fisherl.desolatesky.recipe.input.RecipeInput;
import com.fisherl.desolatesky.recipe.result.RecipeResult;
import com.fisherl.desolatesky.recipe.type.CatalystRecipe;

public record RecipeType<I extends RecipeInput, R extends RecipeResult>(Class<I> inputClass, Class<R> resultClass) {

    public static final RecipeType<CatalystRecipe.Input, CatalystRecipe.Result> CATALYST = new RecipeType<>(CatalystRecipe.Input.class, CatalystRecipe.Result.class);

}
