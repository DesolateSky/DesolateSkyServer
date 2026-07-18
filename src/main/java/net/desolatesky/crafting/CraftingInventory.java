package net.desolatesky.crafting;

import net.desolatesky.recipe.type.ShapedRecipe;
import net.desolatesky.world.DSWorld;
import net.kyori.adventure.key.Key;
import net.minestom.server.coordinate.Point;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.InventoryType;
import org.jetbrains.annotations.Nullable;

public final class CraftingInventory extends Inventory implements CraftingMenuHolder {

    private final DSWorld world;
    private final Point blockPos;
    private final CraftingHandler craftingHandler;

    private @Nullable ShapedRecipe.Result currentOutputResult;
    private @Nullable Key currentRecipeId;

    public CraftingInventory(DSWorld world, Point blockPos) {
        super(InventoryType.CRAFTING, "Crafting Table");
        this.craftingHandler = new CraftingHandler(this, 1, 9, 0, 3, 3);
        this.world = world;
        this.blockPos = blockPos;
    }

    public CraftingHandler craftingHandler() {
        return this.craftingHandler;
    }

    public DSWorld world() {
        return this.world;
    }

    public Point blockPos() {
        return this.blockPos;
    }

    @Override
    public void setCurrentOutputResult(@Nullable ShapedRecipe.Result currentOutputResult) {
        this.currentOutputResult = currentOutputResult;
    }

    @Override
    public @Nullable ShapedRecipe.Result currentOutputResult() {
        return this.currentOutputResult;
    }

    @Override
    public void setCurrentRecipeId(@Nullable Key currentRecipeId) {
        this.currentRecipeId = currentRecipeId;
    }

    @Override
    public @Nullable Key currentRecipeId() {
        return this.currentRecipeId;
    }
}
