package net.desolatesky.recipe.type;

import net.desolatesky.item.ItemFactory;
import net.desolatesky.measurement.TemperatureValue;
import net.desolatesky.recipe.Recipe;
import net.desolatesky.recipe.RecipeType;
import net.desolatesky.recipe.input.RecipeInput;
import net.desolatesky.recipe.result.RecipeResult;
import net.kyori.adventure.key.Key;
import net.minestom.server.item.ItemStackTemplate;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class CrucibleRecipe implements Recipe<CrucibleRecipe.Input, CrucibleRecipe.Result> {

    private final Key key;
    private final @Unmodifiable Map<Key, Integer> requiredItems;
    private final ResultType resultType;
    private final Key resultId;
    private final TemperatureValue requiredTemperature;

    public CrucibleRecipe(Key key, Map<Key, Integer> requiredItems, ResultType resultType, Key resultId, TemperatureValue requiredTemperature) {
        this.key = key;
        this.requiredItems = Map.copyOf(requiredItems);
        this.resultType = resultType;
        this.resultId = resultId;
        this.requiredTemperature = requiredTemperature;
    }

    @Override
    public Key key() {
        return this.key;
    }

    @Override
    public RecipeType<Input, Result> type() {
        return RecipeType.CRUCIBLE;
    }

    public @Unmodifiable Map<Key, Integer> requiredItems() {
        return this.requiredItems;
    }

    public TemperatureValue requiredTemperature() {
        return this.requiredTemperature;
    }

    @Override
    public @Nullable Result craft(ItemFactory itemFactory, Input input) {
        // recipe must match exactly
        if (input.items.size() != this.requiredItems.size()) {
            return null;
        }
        int minMultiple = Integer.MAX_VALUE;
        final Map<Key, Integer> leftover = new HashMap<>();
        for (final Map.Entry<Key, Integer> entry : input.items.entrySet()) {
            final Key item = entry.getKey();
            final int value = entry.getValue();
            final Integer required = this.requiredItems.get(item);
            if (required == null) {
                return null;
            }
            final int crafted = value / required;
            final int consumed = crafted * required;
            minMultiple = Math.min(minMultiple, value / required);
            final int notConsumed = crafted * value - consumed;
            if (notConsumed > 0) {
                leftover.put(item, notConsumed);
            }
        }
        if (minMultiple < 1) {
            return null;
        }
        return new Result(this.resultType, this.resultId, minMultiple, leftover);
    }

    public boolean containsIngredient(Key itemId) {
        return this.requiredItems.containsKey(itemId);
    }

    public boolean matchesIngredients(Map<Key, Integer> input) {
        if (input.size() != this.requiredItems.size()) {
            return false;
        }
        for (final Key id : input.keySet()) {
            if (!this.requiredItems.containsKey(id)) {
                return false;
            }
        }
        return true;
    }

    public static final class Input implements RecipeInput {

        private final Map<Key, Integer> items;

        public Input(Map<Key, Integer> items) {
            this.items = items;
        }
    }

    public static final class Result implements RecipeResult {

        private final ResultType resultType;
        private final Key resultId;
        private final int amount;
        private final @Unmodifiable Map<Key, Integer> leftover;

        public Result(ResultType resultType, Key resultId, int amount, Map<Key, Integer> leftover) {
            this.resultType = resultType;
            this.resultId = resultId;
            this.amount = amount;
            this.leftover = Map.copyOf(leftover);
        }

        public ResultType resultType() {
            return this.resultType;
        }

        public @Unmodifiable Map<Key, Integer> leftover() {
            return this.leftover;
        }

        public Key resultId() {
            return this.resultId;
        }

        public int amount() {
            return this.amount;
        }
    }

    public enum ResultType {
        FLUID,
        ITEM
    }
}
