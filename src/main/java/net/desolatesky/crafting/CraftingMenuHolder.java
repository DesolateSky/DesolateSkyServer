package net.desolatesky.crafting;

import net.desolatesky.recipe.type.ShapedRecipe;
import net.kyori.adventure.key.Key;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface CraftingMenuHolder {

    void setCurrentOutputResult(@Nullable ShapedRecipe.Result currentOutputResult);

    @Nullable ShapedRecipe.Result currentOutputResult();

    void setCurrentRecipeId(@Nullable Key currentRecipeId);

    @Nullable Key currentRecipeId();

    void setItemStack(int slot, ItemStack itemStack);

    ItemStack getItemStack(int slot);

}
