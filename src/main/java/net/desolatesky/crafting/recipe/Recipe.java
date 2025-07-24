package net.desolatesky.crafting.recipe;

public interface Recipe<R extends Recipe<R>> {

    RecipeType<R> type();

    net.minestom.server.recipe.Recipe toMinestomRecipe();

}
