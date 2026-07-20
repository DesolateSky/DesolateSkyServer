package net.desolatesky.recipe.type;

import net.desolatesky.config.ConfigNode;
import net.desolatesky.item.ItemFactory;
import net.desolatesky.item.definition.ItemDefinition;
import net.desolatesky.logging.LoggerUtil;
import net.desolatesky.recipe.Recipe;
import net.desolatesky.recipe.RecipeType;
import net.desolatesky.recipe.input.RecipeInput;
import net.desolatesky.recipe.result.RecipeResult;
import net.desolatesky.util.ItemUtil;
import net.desolatesky.util.Pair;
import net.desolatesky.util.array.ArrayUtil;
import net.desolatesky.util.array.ShiftedArray;
import net.kyori.adventure.key.Key;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.recipe.Ingredient;
import net.minestom.server.recipe.RecipeBookCategory;
import net.minestom.server.recipe.display.RecipeDisplay;
import net.minestom.server.recipe.display.SlotDisplay;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.Shape;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ShapedRecipe implements Recipe<ShapedRecipe.Input, ShapedRecipe.Result> {

    public static final int MAX_ROWS = 3;
    public static final int MAX_COLS = 3;

    private final Key key;
    private final Key[][] recipe;
    private final int[][] recipeAmounts;
    private final Key resultId;
    private final int resultAmount;

    public ShapedRecipe(Key key, Key[][] recipe, int[][] recipeAmounts, Key resultId, int resultAmount) {
        this.key = key;
        this.recipe = recipe;
        this.recipeAmounts = recipeAmounts;
        this.resultId = resultId;
        this.resultAmount = resultAmount;
    }

    public static @Nullable ShapedRecipe load(String recipeIdString, ConfigNode recipeNode) {
        final Key recipeId = Key.key(recipeIdString);

        final ConfigNode shapeNode = recipeNode.node("recipe");
        final ConfigNode amountsNode = recipeNode.node("amounts");

        final List<List<Key>> shapeList = new ArrayList<>();
        shapeNode.childrenList().forEach(node -> {
            final List<ConfigNode> children = node.childrenList();
            if (children.isEmpty()) {
                return;
            }
            final List<Key> shape = new ArrayList<>();
            for (final ConfigNode child : children) {
                final String keyString = child.getString();
                if (keyString == null) {
                    LoggerUtil.warn(ShapedRecipe.class, "Error loading shaped recipe %s".formatted(recipeNode.path()));
                    return;
                }
                shape.add(Key.key(keyString));
            }
            if (shape.size() > MAX_COLS) {
                LoggerUtil.warn(ShapedRecipe.class, "Error loading shaped recipe %s (too many columns)".formatted(recipeNode.path()));
                return;
            }
            shapeList.add(shape);
        });
        if (shapeList.size() > MAX_ROWS) {
            LoggerUtil.warn(ShapedRecipe.class, "Error loading shaped recipe %s (too many rows)".formatted(recipeNode.path()));
            return null;
        }
        final List<List<Integer>> amountsList = new ArrayList<>();
        amountsNode.childrenList().forEach(node -> {
            final List<ConfigNode> children = node.childrenList();
            if (children.isEmpty()) {
                return;
            }
            final List<Integer> amounts = new ArrayList<>();
            for (final ConfigNode child : children) {
                amounts.add(child.getInt());
            }
            if (amounts.size() > MAX_COLS) {
                LoggerUtil.warn(ShapedRecipe.class, "Error loading shaped recipe %s (too many columns)".formatted(recipeNode.path()));
                return;
            }
            amountsList.add(amounts);
        });
        if (amountsList.size() > MAX_ROWS) {
            LoggerUtil.warn(ShapedRecipe.class, "Error loading shaped recipe %s (too many rows)".formatted(recipeNode.path()));
            return null;
        }
        final int rows = Math.max(shapeList.size(), amountsList.size());
        int cols = 0;
        for (final List<Key> list : shapeList) {
            cols = Math.max(cols, list.size());
        }
        for (final List<Integer> list : amountsList) {
            cols = Math.max(cols, list.size());
        }
        final Key[][] recipe = new Key[rows][cols];
        final int[][] amounts = new int[rows][cols];
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                if (row < shapeList.size()) {
                    final List<Key> list = shapeList.get(row);
                    if (col < list.size()) {
                        recipe[row][col] = list.get(col);
                    }
                }
                if (row < amountsList.size()) {
                    final List<Integer> list = amountsList.get(row);
                    if (col < list.size()) {
                        amounts[row][col] = list.get(col);
                    }
                }
            }
        }

        final String resultIdString = recipeNode.node("result").getString();
        if (resultIdString == null) {
            LoggerUtil.warn(ShapedRecipe.class, "No result id found for recipe %s".formatted(recipeNode.path()));
            return null;
        }
        final Key resultId = Key.key(resultIdString);
        final int resultAmount = recipeNode.node("resultAmount").getInt();
        if (resultAmount <= 0) {
            LoggerUtil.warn(ShapedRecipe.class, "Invalid result amount found for recipe %s (%d)".formatted(recipeNode.path(), resultAmount));
            return null;
        }

        return new ShapedRecipe(recipeId, recipe, amounts, resultId, resultAmount);
    }

    @Override
    public Key key() {
        return this.key;
    }

    @Override
    public RecipeType<Input, Result> type() {
        return RecipeType.SHAPED;
    }

    @Override
    public @Nullable Result craft(ItemFactory itemFactory, Input input) {
        final Result result = this.getCraftingResult(itemFactory, input);
        if (result == null) {
            return null;
        }
        final ItemStack created = result.create(itemFactory);
        if (created == null) {
            return null;
        }
        final int amount = result.amount;
        for (int i = 0; i < this.recipe.length; i++) {
            for (int j = 0; j < this.recipe.length; j++) {
                final ItemStack ingredient = input.itemStacks[i][j];
                final int newAmount = ingredient.amount() - (this.recipeAmounts[i][j] * amount);
                if (newAmount <= 0) {
                    input.itemStacks[i][j] = ItemStack.AIR;
                } else {
                    input.itemStacks[i][j] = ingredient.withAmount(newAmount);
                }
            }
        }
        return result;
    }

    public @Nullable Result getCraftingResult(ItemFactory itemFactory, Input input) {
        final int length = (int) Math.sqrt(input.itemStacks.length);
        final ItemStack[][] twoDimensionalInput = new ItemStack[length][length];
        ArrayUtil.toTwoDimensionalArray(input.itemStacks, twoDimensionalInput);
        return this.getCraftingResult(itemFactory, twoDimensionalInput);
    }

    public @Nullable Result getCraftingResult(ItemFactory itemFactory, ItemStack[][] input) {
        final ItemStack[][] shifted = new ItemStack[Math.max(this.recipe.length, input.length)][Math.max(this.recipe[0].length, input[0].length)];
        for (int i = 0; i < shifted.length; i++) {
            for (int j = 0; j < shifted[0].length; j++) {
                shifted[i][j] = ItemStack.AIR;
            }
        }
        final ShiftedArray<ItemStack> shiftedArray = ShiftedArray.shiftToTopLeftCorner(input, shifted, ItemStack.AIR::equals, ItemStack.AIR);
        // if there are items outside the area of this recipe shape,
        // the recipe is invalid
        for (int i = shiftedArray.rowShift(); i < input.length; i++) {
            final int row = i + this.recipe.length;
            if (row >= input.length) {
                continue;
            }
            for (int j = 0; j < input[0].length; j++) {
                if (!input[row][j].isAir()) {
                    return null;
                }
            }
        }
        for (int j = shiftedArray.colShift(); j < input[0].length; j++) {
            final int col = j + this.recipe[0].length;
            if (col >= input[0].length) {
                continue;
            }
            for (final ItemStack[] itemStacks : input) {
                if (!itemStacks[col].isAir()) {
                    return null;
                }
            }
        }

        int minMatches = 0;

        for (int i = 0; i < this.recipe.length; i++) {
            for (int j = 0; j < this.recipe[i].length; j++) {
                final Key recipeIngredient = this.recipe[i][j];
                final ItemStack ingredient = shifted[i][j];
                final int amount = ingredient.amount();
                if (this.recipeAmounts[i][j] <= 0) {
                    continue;
                }
                if (!recipeIngredient.equals(ItemUtil.getItemId(ingredient))) {
                    return null;
                }
                final int numMatches = amount / this.recipeAmounts[i][j];
                if (minMatches == 0) {
                    minMatches = numMatches;
                    continue;
                }
                minMatches = Math.min(minMatches, numMatches);
            }
        }

        final ItemDefinition itemDefinition = itemFactory.getItemDefinition(this.resultId);
        if (itemDefinition == null) {
            return null;
        }
        final ItemStack result = itemDefinition.defaultItemStack();
        final int totalAmount = Math.min(result.maxStackSize(), this.resultAmount * minMatches);
        final int craftAmount = totalAmount / this.resultAmount;
        return new Result(this.resultId, craftAmount);
    }

    public int getMatches(ItemStack itemStack, int row, int col) {
        final MatchResult result = this.getMatchResult(row, col, itemStack);
        if (!result.matches()) {
            return 0;
        }
        if (this.recipeAmounts[row][col] <= 0) {
            return itemStack.amount();
        }
        return result.totalMatches();
    }

    private MatchResult getMatchResult(int row, int col, ItemStack itemStack) {
        final Key key = this.recipe[row][col];
        if (!key.equals(ItemUtil.getItemId(itemStack))) {
            return new MatchResult(false, 0);
        }
        final int amount = this.recipeAmounts[row][col];
        if (amount <= 0) {
            return new MatchResult(true, itemStack.amount());
        }
        final int itemAmount = itemStack.amount();
        final int totalMatches = itemAmount / amount;
        return new MatchResult(true, Math.max(0, totalMatches));
    }

    public int resultAmount() {
        return this.resultAmount;
    }

    public static final class Input implements RecipeInput {

        private final ItemStack[][] itemStacks;

        public Input(ItemStack[][] itemStacks) {
            this.itemStacks = itemStacks;
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
            final ItemDefinition itemDefinition = itemFactory.getItemDefinition(this.itemId);
            if (itemDefinition == null) {
                return ItemStack.AIR;
            }
            return itemDefinition.defaultItemStack().withAmount(this.amount);
        }

        public Key itemId() {
            return this.itemId;
        }

        public int amount() {
            return this.amount;
        }
    }

    public record MatchResult(boolean matches, int totalMatches) {
    }

    public net.minestom.server.recipe.Recipe createMinestomRecipe(ItemFactory itemFactory) {
        return new MinestomRecipe(itemFactory);
    }

    private class MinestomRecipe implements net.minestom.server.recipe.Recipe {

        private final ItemFactory itemFactory;

        public MinestomRecipe(ItemFactory itemFactory) {
            this.itemFactory = itemFactory;
        }

        @Override
        public @NotNull List<RecipeDisplay> createRecipeDisplays() {
            final List<SlotDisplay> ingredients = new ArrayList<>();
            final int height = ShapedRecipe.this.recipe.length;
            final int width = ShapedRecipe.this.recipe[0].length;
            for (final Key[] keys : ShapedRecipe.this.recipe) {
                for (int j = 0; j < width; j++) {
                    final Key key = keys[j];
                    final ItemDefinition itemDefinition = this.itemFactory.getItemDefinition(key);
                    if (itemDefinition == null) {
                        ingredients.add(SlotDisplay.Empty.INSTANCE);
                        continue;
                    }
                    final ItemStack itemStack = itemDefinition.defaultItemStack();
                    if (itemStack.isAir()) {
                        ingredients.add(SlotDisplay.Empty.INSTANCE);
                        continue;
                    }
                    ingredients.add(new SlotDisplay.ItemStack(itemStack));
                }
            }
            final ItemDefinition result = this.itemFactory.getItemDefinition(ShapedRecipe.this.resultId);
            if (result == null) {
                return Collections.emptyList();
            }
            return List.of(new RecipeDisplay.CraftingShaped(
                    width,
                    height,
                    ingredients,
                    new SlotDisplay.ItemStack(result.defaultItemStack()),
                    new SlotDisplay.Item(Material.CRAFTING_TABLE)
            ));
        }

        @Override
        public @Nullable RecipeBookCategory recipeBookCategory() {
            return RecipeBookCategory.CRAFTING_MISC;
        }

        @Override
        public @Nullable List<Ingredient> craftingRequirements() {
            final List<Ingredient> ingredients = new ArrayList<>();
            for (final Key[] itemIngredients : ShapedRecipe.this.recipe) {
                for (final Key ingredientId : itemIngredients) {
                    final ItemDefinition itemDefinition = this.itemFactory.getItemDefinition(ingredientId);
                    if (itemDefinition == null) {
                        continue;
                    }
                    final Ingredient minestomIngredient = new Ingredient(List.of(itemDefinition.defaultItemStack().material()));
                    ingredients.add(minestomIngredient);
                }
            }
            return ingredients.isEmpty() ? null : ingredients;
        }
    }
}
