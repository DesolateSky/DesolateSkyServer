package net.desolatesky.recipe;

import net.desolatesky.config.ConfigFile;
import net.desolatesky.config.ConfigNode;
import net.desolatesky.item.ItemFactory;
import net.desolatesky.item.ItemIds;
import net.desolatesky.measurement.TemperatureUnit;
import net.desolatesky.measurement.TemperatureValue;
import net.desolatesky.recipe.input.RecipeInput;
import net.desolatesky.recipe.result.RecipeResult;
import net.desolatesky.recipe.type.CatalystRecipe;
import net.desolatesky.recipe.type.CrucibleRecipe;
import net.desolatesky.recipe.type.ShapedRecipe;
import net.kyori.adventure.key.Key;
import net.minestom.server.MinecraftServer;
import net.minestom.server.instance.block.BlockKeys;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class RecipeFactory {

    private final ItemFactory itemFactory;
    private final Map<Key, Recipe<? extends RecipeInput, ? extends RecipeResult>> recipes = new HashMap<>();
    private final Map<Key, ShapedRecipe> shapedRecipes = new HashMap<>();

    public RecipeFactory(ItemFactory itemFactory) {
        this.itemFactory = itemFactory;
    }

    public void initialize() {
        this.recipes.put(RecipeIds.STONE_SLAB, new CatalystRecipe(RecipeIds.STONE_SLAB,
                ItemIds.SILVERFISH_CRAFTING_CATALYST,
                Map.of(ItemIds.PEBBLE, 6, ItemIds.SILVERFISH_EYE, 2),
                ItemIds.STONE_SLAB));

        this.recipes.put(RecipeIds.CRUCIBLE_LAVA, new CrucibleRecipe(RecipeIds.CRUCIBLE_LAVA,
                Map.of(ItemIds.STONE_CHUNK, 16),
                CrucibleRecipe.ResultType.FLUID,
                BlockKeys.LAVA.key(),
                new TemperatureValue(TemperatureUnit.CELSIUS, 500)
        ));
        this.initializeShapedRecipes();
    }

    private void initializeShapedRecipes() {
        final ConfigFile shapedRecipesFile = ConfigFile.get(Path.of("shaped-recipes.conf"), "/shaped-recipes.conf");
        final Map<Object, ConfigNode> children = shapedRecipesFile.rootNode().childrenMap();
        for (final Map.Entry<Object, ConfigNode> entry : children.entrySet()) {
            if (!(entry.getKey() instanceof final String id)) {
                continue;
            }
            final ShapedRecipe recipe = ShapedRecipe.load(id, entry.getValue());
            if (recipe == null) {
                continue;
            }
            this.shapedRecipes.put(recipe.key(), recipe);
        }
        this.shapedRecipes.values().forEach(r -> MinecraftServer.getRecipeManager().addRecipe(r.createMinestomRecipe(this.itemFactory)));
    }

    public <I extends RecipeInput, R extends RecipeResult> @Nullable R craft(RecipeType<I, R> type, Key recipeId, I input) {
        @SuppressWarnings("unchecked") final Recipe<I, R> recipe = (Recipe<I, R>) this.recipes.get(recipeId);
        if (recipe == null) {
            return null;
        }
        return recipe.craft(this.itemFactory, input);
    }

    public @Nullable ShapedRecipe.Result craftShapedRecipe(Key recipeId, ShapedRecipe.Input input) {
        final ShapedRecipe recipe = this.shapedRecipes.get(recipeId);
        if (recipe == null) {
            return null;
        }
        return recipe.craft(this.itemFactory, input);
    }

    public @Nullable ShapedRecipe getShapedRecipe(Key id) {
        return this.shapedRecipes.get(id);
    }

    public @Unmodifiable Collection<ShapedRecipe> getShapedRecipes() {
        return Collections.unmodifiableCollection(this.shapedRecipes.values());
    }
}
