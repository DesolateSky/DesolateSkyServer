package net.desolatesky.crafting.recipe;

import net.desolatesky.crafting.ingredient.ItemIngredient;
import net.desolatesky.util.ArrayUtil;
import net.desolatesky.util.array.ShiftedArray;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.recipe.Ingredient;
import net.minestom.server.recipe.RecipeBookCategory;
import net.minestom.server.recipe.display.RecipeDisplay;
import net.minestom.server.recipe.display.SlotDisplay;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public final class CraftingRecipe implements Recipe<CraftingRecipe> {

    private final ItemIngredient[][] recipe;
    private final Function<ItemStack[][], ItemStack> outputSupplier;
    private final ItemStack visualOutput;
    private final int resultAmount;
    private final int width;
    private final int height;
    private final MinestomRecipe minestomRecipe;

    public CraftingRecipe(ItemIngredient[][] recipe, Function<ItemStack[][], ItemStack> outputCreator, ItemStack visualOutput, int resultAmount) {
        this.recipe = recipe;
        this.outputSupplier = outputCreator;
        this.visualOutput = visualOutput;
        this.resultAmount = resultAmount;
        final ItemIngredient[][] shiftedRecipe = new ItemIngredient[recipe.length][recipe[0].length];
        for (int i = 0; i < recipe.length; i++) {
            for (int j = 0; j < recipe[0].length; j++) {
                shiftedRecipe[i][j] = ItemIngredient.EMPTY;
            }
        }
        final ShiftedArray<ItemIngredient> shifted = ShiftedArray.shiftToTopLeftCorner(recipe, shiftedRecipe, ItemIngredient.EMPTY::equals, ItemIngredient.EMPTY);
        this.height = shifted.actualRows();
        this.width = shifted.actualColumns();
        this.minestomRecipe = new MinestomRecipe();
    }

    public CraftingRecipe(ItemIngredient[][] recipe, Function<ItemStack[][], ItemStack> outputCreator, ItemStack visualOutput) {
        this(recipe, outputCreator, visualOutput, 1);
    }

    public CraftingRecipe(ItemIngredient[][] recipe, Supplier<ItemStack> outputSupplier, int amount) {
        this(recipe, input -> outputSupplier.get(), outputSupplier.get(), amount);
    }

    public CraftingRecipe(ItemIngredient[][] recipe, Supplier<ItemStack> outputSupplier) {
        this(recipe, input -> outputSupplier.get(), outputSupplier.get());
    }

    public Result getCraftingResult(ItemStack[] input) {
        final int length = (int) Math.sqrt(input.length);
        final ItemStack[][] twoDimensionalInput = new ItemStack[length][length];
        ArrayUtil.toTwoDimensionalArray(input, twoDimensionalInput);
        return this.getCraftingResult(twoDimensionalInput);
    }

    public Result getCraftingResult(ItemStack[][] input) {
        final ItemStack[][] shifted = new ItemStack[this.recipe.length][this.recipe[0].length];
        for (int i = 0; i < this.recipe.length; i++) {
            for (int j = 0; j < this.recipe[0].length; j++) {
                shifted[i][j] = ItemStack.AIR;
            }
        }
        ShiftedArray.shiftToTopLeftCorner(input, shifted, ItemStack.AIR::equals, ItemStack.AIR);
        int minMatches = 0;

        for (int i = 0; i < this.recipe.length; i++) {
            for (int j = 0; j < this.recipe[i].length; j++) {
                final ItemIngredient recipeIngredient = this.recipe[i][j];
                final ItemIngredient.MatchResult matchResult = recipeIngredient.getMatchResult(shifted[i][j]);
                if (!matchResult.matches()) {
                    return Result.failure(this.recipe.length, this.recipe[0].length);
                }
                if (!recipeIngredient.affectsResultAmount()) {
                    continue;
                }
                if (minMatches == 0) {
                    minMatches = matchResult.totalMatches();
                    continue;
                }
                minMatches = Math.min(minMatches, matchResult.totalMatches());
            }
        }

        final ItemStack result = this.outputSupplier.apply(shifted);
        final int totalAmount = Math.min(result.maxStackSize(), this.resultAmount * minMatches);
        final int craftAmount = totalAmount / this.resultAmount;
        return new Result(true, input, this.outputSupplier.apply(shifted), craftAmount);
    }

    public int getMatches(ItemStack itemStack, int row, int col) {
        final ItemIngredient ingredient = this.recipe[row][col];
        final ItemIngredient.MatchResult result = ingredient.getMatchResult(itemStack);
        if (!result.matches()) {
            return 0;
        }
        if (!ingredient.affectsResultAmount()) {
            return itemStack.amount();
        }
        return result.totalMatches();
    }

    @Override
    public RecipeType<CraftingRecipe> type() {
        return RecipeType.CRAFTING;
    }

    public int resultAmount() {
        return this.resultAmount;
    }

    @Override
    public net.minestom.server.recipe.Recipe toMinestomRecipe() {
        return this.minestomRecipe;
    }

    @Override
    public String toString() {
        return "CraftingRecipe{" +
                "height=" + this.height +
                ", width=" + this.width +
                ", recipe=" + Arrays.deepToString(this.recipe) +
                '}';
    }

    public record Result(boolean success, ItemStack[][] newInput, ItemStack output, int totalMatches) {

        public static Result failure(int rows, int cols) {
            return new Result(false, new ItemStack[rows][cols], ItemStack.AIR, 0);
        }

    }

    private class MinestomRecipe implements net.minestom.server.recipe.Recipe {

        @Override
        public @NotNull List<RecipeDisplay> createRecipeDisplays() {
            final List<SlotDisplay> ingredients = new ArrayList<>();
            for (int i = 0; i < CraftingRecipe.this.height; i++) {
                for (int j = 0; j < CraftingRecipe.this.width; j++) {
                    final ItemIngredient ingredient = CraftingRecipe.this.recipe[i][j];
                    ingredients.add(ingredient.display());
                }
            }
            return List.of(new RecipeDisplay.CraftingShaped(
                    CraftingRecipe.this.width,
                    CraftingRecipe.this.height,
                    ingredients,
                    new SlotDisplay.ItemStack(CraftingRecipe.this.visualOutput.withAmount(CraftingRecipe.this.resultAmount)),
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
            for (final ItemIngredient[] itemIngredients : CraftingRecipe.this.recipe) {
                for (final ItemIngredient ingredient : itemIngredients) {
                    final Ingredient minestomIngredient = ingredient.toMinestomIngredient();
                    if (minestomIngredient == null) {
                        continue;
                    }
                    ingredients.add(minestomIngredient);
                }
            }
            return ingredients.isEmpty() ? null : ingredients;
        }
    }

}
