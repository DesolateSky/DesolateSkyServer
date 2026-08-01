package net.desolatesky.crafting;

import net.desolatesky.item.ItemFactory;
import net.desolatesky.recipe.RecipeFactory;
import net.desolatesky.recipe.type.ShapedRecipe;
import net.desolatesky.util.InventoryUtil;
import net.desolatesky.util.array.ShiftedArray;
import net.kyori.adventure.key.Key;
import net.minestom.server.inventory.click.Click;
import net.minestom.server.item.ItemStack;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

public final class CraftingHandler {

    private final CraftingMenuHolder holder;
    private final int inputStartSlot;
    private final int inputEndSlot;
    private final int outputSlot;
    private final int width;
    private final int height;

    public CraftingHandler(CraftingMenuHolder holder, int inputStartSlot, int inputEndSlot, int outputSlot, int width, int height) {
        this.holder = holder;
        this.inputStartSlot = inputStartSlot;
        this.inputEndSlot = inputEndSlot;
        this.outputSlot = outputSlot;
        this.width = width;
        this.height = height;
    }

    public void fillRecipe(RecipeFactory recipeFactory, ItemFactory itemFactory) {
        final Collection<ShapedRecipe> recipes = recipeFactory.getShapedRecipes();
        final ItemStack[][] input = this.getInputItems();
        for (final ShapedRecipe recipe : recipes) {
            this.holder.setCurrentOutputResult(recipe.getCraftingResult(itemFactory, input));
            if (this.holder.currentOutputResult() == null) {
                this.setOutputItem(ItemStack.AIR);
                continue;
            }
            this.holder.setCurrentRecipeId(recipe.key());
            final ItemStack output = this.holder.currentOutputResult().create(itemFactory);
            this.setOutputItem(output);
            break;
        }
        if (this.holder.currentRecipeId() == null) {
            this.setOutputItem(ItemStack.AIR);
        }
    }

    private void collectRecipe(RecipeFactory recipeFactory, ItemFactory itemFactory, Click click, int amount) {
        if (this.holder.currentRecipeId() == null) {
            return;
        }
        final ShapedRecipe currentRecipe = recipeFactory.getShapedRecipe(this.holder.currentRecipeId());
        if (currentRecipe == null) {
            return;
        }
        this.modifyInputItems((slot, itemStack) -> {
            if (itemStack.isAir()) {
                return itemStack;
            }
            final int inputAmount = itemStack.amount();
            if (inputAmount == 1) {
                return ItemStack.AIR;
            }
            final int row = (slot - this.inputStartSlot) / this.width;
            final int col = (slot - this.inputStartSlot) % this.width;
            final int matches = currentRecipe.getMatches(itemStack, row, col);
            if (matches <= 0) {
                return itemStack;
            }
            final int ratio = matches / itemStack.amount();
            if (ratio <= 0) {
                return itemStack;
            }
            final int craftAmount = amount * ratio;
            final int newAmount = !InventoryUtil.isRightClick(click) ? inputAmount - craftAmount : inputAmount - 1;
            return itemStack.withAmount(newAmount);
        });
        this.fillRecipe(recipeFactory, itemFactory);
    }

    /**
     * @param collectFunction - returns the number of results that were collected, not the amount of items.
     */
    public void collectOutput(RecipeFactory recipeFactory, ItemFactory itemFactory, Click click,  Function<CollectionInput, Integer> collectFunction) {
        if (this.holder.currentOutputResult() == null) {
            return;
        }
        if (this.holder.currentRecipeId() == null) {
            return;
        }
        final ShapedRecipe currentRecipe = recipeFactory.getShapedRecipe(this.holder.currentRecipeId());
        if (currentRecipe == null) {
            return;
        }
        final ItemStack outputItem = this.getOutputItem();
        if (outputItem.isAir()) {
            return;
        }
        final int outputAmount = this.holder.currentOutputResult().amount()  * currentRecipe.resultAmount();
        if (outputAmount <= 0) {
            return;
        }
        final int collectedAmount = collectFunction.apply(new CollectionInput(this.holder.currentRecipeId(), outputItem, this.holder.currentOutputResult().amount(), currentRecipe.resultAmount()));
        this.collectRecipe(recipeFactory, itemFactory, click, collectedAmount);
    }

    public record CollectionInput(Key recipeId, ItemStack resultItem, int totalMatches, int amountPerCraft) {

    }

    public ItemStack[][] getInputItems() {
        final ItemStack[][] inputItems = new ItemStack[this.height][this.width];
        for (int i = 0; i < this.height; i++) {
            for (int j = 0; j < this.width; j++) {
                final int slot = this.getSlot(i, j);
                inputItems[i][j] = Objects.requireNonNull(this.holder.getItemStack(slot), "Input item at slot " + slot + " is null");
            }
        }
        return inputItems;
    }

    public ShiftedArray<ItemStack> getShiftedInputItems() {
        final ItemStack[][] inputItems = new ItemStack[this.height][this.width];
        for (int i = 0; i < this.height; i++) {
            for (int j = 0; j < this.width; j++) {
                inputItems[i][j] = ItemStack.AIR;
            }
        }
        return ShiftedArray.shiftToTopLeftCorner(this.getInputItems(), inputItems, ItemStack::isAir, ItemStack.AIR);
    }

    private void resetInputItems() {
        for (int i = this.inputStartSlot; i <= this.inputEndSlot; i++) {
            this.holder.setItemStack(i, ItemStack.AIR);
        }
    }

    public void setInputItems(ItemStack[][] inputItems) {
        for (int i = 0; i < this.height; i++) {
            for (int j = 0; j < this.width; j++) {
                this.holder.setItemStack(this.getSlot(i, j), inputItems[i][j]);
            }
        }
    }

    public void modifyInputItems(BiFunction<Integer, ItemStack, ItemStack> modifier) {
        final ShiftedArray<ItemStack> shifted = this.getShiftedInputItems();
        final ItemStack[][] input = shifted.array();
        final ItemStack[][] newInput = new ItemStack[this.height][this.width];
        for (int i = 0; i < this.height; i++) {
            Arrays.fill(newInput[i], ItemStack.AIR);
        }
        final int rowShift = shifted.rowShift();
        final int colShift = shifted.colShift();
        for (int i = shifted.rowShift(); i < input.length; i++) {
            for (int j = shifted.colShift(); j < input[i].length; j++) {
                final int slot = this.getSlot((i - rowShift), (j - colShift));
                final ItemStack itemStack = input[i - rowShift][j - colShift];
                if (!itemStack.isAir()) {
                    newInput[i][j] = modifier.apply(slot, itemStack);
                }
            }
        }
        this.setInputItems(newInput);
    }

    public void setOutputItem(ItemStack itemStack) {
        this.holder.setItemStack(this.outputSlot, itemStack);
    }

    public ItemStack getOutputItem() {
        return this.holder.getItemStack(this.outputSlot);
    }

    public boolean isCraftingSlot(int slot) {
        return slot >= this.inputStartSlot && slot <= this.inputEndSlot;
    }

    public boolean isOutputSlot(int slot) {
        return slot == this.outputSlot;
    }

    public int getSlot(int row, int col) {
        if (row < 0 || row >= this.height || col < 0 || col >= this.width) {
            throw new IndexOutOfBoundsException("Row or column out of bounds");
        }
        return this.inputStartSlot + row * this.height + col;
    }

}