package com.fisherl.desolatesky.recipe;

import com.fisherl.desolatesky.item.ItemFactory;
import com.fisherl.desolatesky.recipe.input.RecipeInput;
import com.fisherl.desolatesky.recipe.result.RecipeResult;
import net.kyori.adventure.key.Key;

import java.util.Optional;

public interface Recipe<I extends RecipeInput, R extends RecipeResult> {

    Key key();

    RecipeType<I, R> type();

    Optional<R> craft(ItemFactory itemFactory, I input);

}
