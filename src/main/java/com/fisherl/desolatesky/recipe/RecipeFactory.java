package com.fisherl.desolatesky.recipe;

import com.fisherl.desolatesky.item.ItemFactory;
import com.fisherl.desolatesky.item.ItemIds;
import com.fisherl.desolatesky.recipe.input.RecipeInput;
import com.fisherl.desolatesky.recipe.result.RecipeResult;
import com.fisherl.desolatesky.recipe.type.CatalystRecipe;
import net.kyori.adventure.key.Key;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class RecipeFactory {

    private final ItemFactory itemFactory;
    private final Map<Key, Recipe<? extends RecipeInput, ? extends RecipeResult>> recipes = new HashMap<>();

    public RecipeFactory(ItemFactory itemFactory) {
        this.itemFactory = itemFactory;
    }

    public void initialize() {
        this.recipes.put(RecipeIds.STONE_SLAB, new CatalystRecipe(RecipeIds.STONE_SLAB,
                ItemIds.SILVERFISH_CRAFTING_CATALYST,
                Map.of(ItemIds.PEBBLE, 6, ItemIds.SILVERFISH_EYE, 2),
                ItemIds.STONE_SLAB));
    }

    public <I extends RecipeInput, R extends RecipeResult> Optional<R> craft(RecipeType<I, R> type, Key recipeId, I input) {
        @SuppressWarnings("unchecked")
        final Recipe<I, R> recipe = (Recipe<I, R>) this.recipes.get(recipeId);
        if (recipe == null) {
            return Optional.empty();
        }
        return recipe.craft(this.itemFactory, input);
    }
}
