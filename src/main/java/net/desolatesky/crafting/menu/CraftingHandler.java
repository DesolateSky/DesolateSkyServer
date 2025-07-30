package net.desolatesky.crafting.menu;

import net.desolatesky.crafting.CraftingManager;
import net.desolatesky.crafting.recipe.CraftingRecipe;
import net.desolatesky.crafting.recipe.RecipeType;
import net.desolatesky.util.InventoryUtil;
import net.desolatesky.util.Tags;
import net.desolatesky.util.array.ShiftedArray;
import net.minestom.server.inventory.AbstractInventory;
import net.minestom.server.inventory.click.Click;
import net.minestom.server.item.ItemStack;
import net.minestom.server.tag.Tag;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

public final class CraftingHandler {

    public static final Tag<CraftingRecipe.Result> CURRENT_OUTPUT_RESULT_TAG = Tags.Transient("current_output_result");
    public static final Tag<CraftingRecipe> CURRENT_RECIPE_TAG = Tags.Transient("current_recipe");

    private final AbstractInventory inventory;
    private final int inputStartSlot;
    private final int inputEndSlot;
    private final int outputSlot;
    private final int width;
    private final int height;

    public CraftingHandler(AbstractInventory inventory, int inputStartSlot, int inputEndSlot, int outputSlot, int width, int height) {
        this.inventory = inventory;
        this.inputStartSlot = inputStartSlot;
        this.inputEndSlot = inputEndSlot;
        this.outputSlot = outputSlot;
        this.width = width;
        this.height = height;
    }

    public void fillRecipe(CraftingManager craftingManager) {
        final Collection<CraftingRecipe> recipes = craftingManager.getRecipes(RecipeType.CRAFTING);
        final ItemStack[][] input = this.getInputItems();
        CraftingRecipe.Result currentOutputResult = this.inventory.getTag(CURRENT_OUTPUT_RESULT_TAG);
        CraftingRecipe currentRecipe = this.inventory.getTag(CURRENT_RECIPE_TAG);
        for (final CraftingRecipe recipe : recipes) {
            currentOutputResult = recipe.getCraftingResult(input);
            if (!currentOutputResult.success()) {
                currentRecipe = null;
                continue;
            }
            currentRecipe = recipe;
            final ItemStack[][] newInput = currentOutputResult.newInput();
            this.setInputItems(newInput);
            final ItemStack output = currentOutputResult.output();
            this.setOutputItem(output.withAmount(recipe.resultAmount()));
            break;
        }
        if (currentRecipe == null) {
            this.setOutputItem(ItemStack.AIR);
        }
        this.inventory.setTag(CURRENT_OUTPUT_RESULT_TAG, currentOutputResult);
        this.inventory.setTag(CURRENT_RECIPE_TAG, currentRecipe);
    }

    private void collectRecipe(CraftingManager craftingManager, Click click, int amount) {
        final CraftingRecipe currentRecipe = this.inventory.getTag(CURRENT_RECIPE_TAG);
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
            final int newAmount = InventoryUtil.isShiftClick(click) ? inputAmount - craftAmount : inputAmount - 1;
            return itemStack.withAmount(newAmount);
        });
        this.fillRecipe(craftingManager);
    }

    /**
     * @param collectFunction - returns the number of results that were collected, not the amount of items.
     */
    public void collectOutput(CraftingManager craftingManager, AbstractInventory inventory, Click click,  Function<CollectionInput, Integer> collectFunction) {
        final CraftingRecipe.Result currentOutputResult = inventory.getTag(CURRENT_OUTPUT_RESULT_TAG);
        final CraftingRecipe currentRecipe = inventory.getTag(CURRENT_RECIPE_TAG);
        if (currentOutputResult == null || !currentOutputResult.success()) {
            return;
        }
        if (currentRecipe == null) {
            return;
        }
        final ItemStack outputItem = this.getOutputItem();
        if (outputItem.isAir()) {
            return;
        }
        final int outputAmount = currentOutputResult.totalMatches() * currentRecipe.resultAmount();
        if (outputAmount <= 0) {
            return;
        }
        final int collectedAmount = collectFunction.apply(new CollectionInput(outputItem, currentOutputResult.totalMatches(), currentRecipe.resultAmount()));
        this.collectRecipe(craftingManager, click, collectedAmount);
    }

    public record CollectionInput(ItemStack resultItem, int totalMatches, int amountPerCraft) {

    }

    public ItemStack[][] getInputItems() {
        final ItemStack[][] inputItems = new ItemStack[this.height][this.width];
        for (int i = 0; i < this.height; i++) {
            for (int j = 0; j < this.width; j++) {
                final int slot = this.getSlot(i, j);
                inputItems[i][j] = Objects.requireNonNull(this.inventory.getItemStack(slot), "Input item at slot " + slot + " is null");
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
            this.inventory.setItemStack(i, ItemStack.AIR);
        }
    }

    public void setInputItems(ItemStack[][] inputItems) {
        for (int i = 0; i < this.height; i++) {
            for (int j = 0; j < this.width; j++) {
                this.inventory.setItemStack(this.getSlot(i, j), inputItems[i][j]);
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
        this.inventory.setItemStack(this.outputSlot, itemStack);
    }

    public ItemStack getOutputItem() {
        return this.inventory.getItemStack(this.outputSlot);
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
