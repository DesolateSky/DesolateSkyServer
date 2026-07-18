package net.desolatesky.recipe;

import net.desolatesky.item.ItemFactory;
import net.desolatesky.recipe.input.RecipeInput;
import net.desolatesky.recipe.result.RecipeResult;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public interface Recipe<I extends RecipeInput, R extends RecipeResult> {

    Key key();

    RecipeType<I, R> type();

    @Nullable R craft(ItemFactory itemFactory, I input);

}
