package net.desolatesky.crafting.menu;

import net.desolatesky.crafting.CraftingManager;
import net.desolatesky.crafting.recipe.CraftingRecipe;
import net.desolatesky.crafting.recipe.RecipeType;
import net.desolatesky.util.array.ShiftedArray;
import net.kyori.adventure.text.Component;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.inventory.click.ClickType;
import net.minestom.server.item.ItemStack;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.function.BiFunction;

public final class CraftingMenu extends Inventory {

    private final CraftingHandler craftingHandler;

    public CraftingMenu() {
        super(InventoryType.CRAFTING, Component.text("Crafting"));
        this.craftingHandler = new CraftingHandler(this, 1, 9, 0, 3, 3);
    }

    public void fillRecipe(CraftingManager craftingManager) {
        this.craftingHandler.fillRecipe(craftingManager);
    }

    public ItemStack[][] getInputItems() {
        return this.craftingHandler.getInputItems();
    }

    public ShiftedArray<ItemStack> getShiftedInputItems() {
        return this.craftingHandler.getShiftedInputItems();
    }

    public void setInputItems(ItemStack[][] inputItems) {
        this.craftingHandler.setInputItems(inputItems);
    }

    public void modifyInputItems(BiFunction<Integer, ItemStack, ItemStack> modifier) {
        this.craftingHandler.modifyInputItems(modifier);
    }

    public void setOutputItem(ItemStack itemStack) {
        this.craftingHandler.setOutputItem(itemStack);
    }

    public ItemStack getOutputItem() {
        return this.craftingHandler.getOutputItem();
    }

    public boolean isCraftingSlot(int slot) {
        return this.craftingHandler.isCraftingSlot(slot);
    }

    public boolean isOutputSlot(int slot) {
        return this.craftingHandler.isOutputSlot(slot);
    }

    public int getSlot(int row, int col) {
        return this.craftingHandler.getSlot(row, col);
    }

    public CraftingHandler craftingHandler() {
        return this.craftingHandler;
    }

}
