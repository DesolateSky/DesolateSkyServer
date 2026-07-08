package com.fisherl.desolatesky.recipe.type;

import com.fisherl.desolatesky.item.ItemFactory;
import com.fisherl.desolatesky.item.definition.ItemDefinition;
import com.fisherl.desolatesky.player.DSPlayer;
import com.fisherl.desolatesky.recipe.Recipe;
import com.fisherl.desolatesky.recipe.RecipeType;
import com.fisherl.desolatesky.recipe.input.RecipeInput;
import com.fisherl.desolatesky.recipe.result.RecipeResult;
import com.fisherl.desolatesky.util.ItemUtil;
import com.google.common.collect.Multimap;
import net.kyori.adventure.key.Key;
import net.minestom.server.entity.ItemEntity;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class CatalystRecipe implements Recipe<CatalystRecipe.Input, CatalystRecipe.Result> {

    private final Key key;
    private final Key catalystId;
    private final @Unmodifiable Map<Key, Integer> requiredIngredients;
    private final Key result;

    public CatalystRecipe(
            Key key,
            Key catalystId,
            Map<Key, Integer> requiredIngredients,
            Key result
    ) {
        this.key = key;
        this.catalystId = catalystId;
        this.requiredIngredients = Collections.unmodifiableMap(requiredIngredients);
        this.result = result;
    }

    @Override
    public Key key() {
        return this.key;
    }

    @Override
    public RecipeType<Input, Result> type() {
        return RecipeType.CATALYST;
    }

    @Override
    public Optional<Result> craft(ItemFactory itemFactory, Input input) {
        final Map<Key, Integer> ingredientCount = new HashMap<>();
        if (!this.catalystId.equals(ItemUtil.getItemId(input.catalystEntity.getItemStack()))) {
            return Optional.empty();
        }
        if (input.items.isEmpty()) {
            return Optional.empty();
        }
        for (final Map.Entry<Key, ItemEntity> entry : input.items.entries()) {
            final ItemStack itemStack = entry.getValue().getItemStack();
            final Key itemId = ItemUtil.getItemId(itemStack);
            if (!this.requiredIngredients.containsKey(itemId)) {
                continue;
            }
            ingredientCount.merge(itemId, itemStack.amount(), Integer::sum);
        }
        int minMultiple = Integer.MAX_VALUE;
        for (final Map.Entry<Key, Integer> entry : this.requiredIngredients.entrySet()) {
            final Key key = entry.getKey();
            final int required = entry.getValue();
            minMultiple = Math.min(ingredientCount.getOrDefault(key, 0) / required, minMultiple);
        }
        if (minMultiple <= 0) {
            return Optional.empty();
        }
        final Map<Key, Integer> used = new HashMap<>();
        for (final Map.Entry<Key, ItemEntity> entry : input.items.entries()) {
            final Key key = entry.getKey();
            final ItemEntity item = entry.getValue();
            final ItemStack itemStack = item.getItemStack();
            final int size = itemStack.amount();
            final int required = minMultiple * this.requiredIngredients.getOrDefault(key, 1) - used.getOrDefault(key, 0);
            if (required <= 0) {
                continue;
            }
            if (size > required) {
                item.setItemStack(itemStack.withAmount(size - required));
                used.merge(key, required, Integer::sum);
                continue;
            }
            item.remove();
            used.merge(key, size, Integer::sum);
        }
        return Optional.of(new Result(this.result, minMultiple));
    }

    public static final class Input implements RecipeInput {

        private final ItemEntity catalystEntity;
        private final Multimap<Key, ItemEntity> items;

        public Input(ItemEntity catalystEntity, Multimap<Key, ItemEntity> items) {
            this.catalystEntity = catalystEntity;
            this.items = items;
        }
    }

    public static final class Result implements RecipeResult {

        private final Key itemId;
        private final int amount;

        public Result(Key itemId, int amount) {
            this.itemId = itemId;
            this.amount = amount;
        }

        @Override
        public ItemStack create(ItemFactory itemFactory) {
            return this.getItemStack(itemFactory);
        }

        @Override
        public ItemStack getDisplay(ItemFactory itemFactory) {
            return this.getItemStack(itemFactory);
        }

        private ItemStack getItemStack(ItemFactory itemFactory) {
            return itemFactory.getItemDefinition(this.itemId)
                    .map(ItemDefinition::defaultItemStack)
                    .map(item -> item.withAmount(this.amount))
                    .orElse(ItemStack.AIR);
        }
    }

}
