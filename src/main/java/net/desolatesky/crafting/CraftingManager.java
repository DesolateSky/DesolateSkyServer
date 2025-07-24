package net.desolatesky.crafting;

import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import net.desolatesky.crafting.ingredient.ItemIngredient;
import net.desolatesky.crafting.recipe.CraftingRecipe;
import net.desolatesky.crafting.recipe.Recipe;
import net.desolatesky.crafting.recipe.RecipeType;
import net.minestom.server.MinecraftServer;
import net.minestom.server.recipe.RecipeManager;
import org.jetbrains.annotations.Unmodifiable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

public final class CraftingManager {

    public static CraftingManager load() {
        final Multimap<RecipeType<?>, Recipe<?>> recipes = Multimaps.newListMultimap(new HashMap<>(), ArrayList::new);
        return new CraftingManager(recipes);
    }

    private final Multimap<RecipeType<?>, Recipe<?>> recipes;

    public CraftingManager(Multimap<RecipeType<?>, Recipe<?>> recipes) {
        this.recipes = recipes;
    }

    public void addRecipe(Recipe<?> recipe) {
        this.recipes.put(recipe.type(), recipe);
        final RecipeManager recipeManager = MinecraftServer.getRecipeManager();
        recipeManager.addRecipe(recipe.toMinestomRecipe());
    }

    @SuppressWarnings("unchecked")
    public <R extends Recipe<R>> @Unmodifiable Collection<R> getRecipes(RecipeType<R> type) {
        return (Collection<R>) Collections.unmodifiableCollection(this.recipes.get(type));
    }

}
