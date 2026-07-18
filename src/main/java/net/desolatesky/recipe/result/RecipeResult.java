package net.desolatesky.recipe.result;

import net.desolatesky.item.ItemFactory;
import net.minestom.server.item.ItemStack;

public interface RecipeResult {

    ItemStack create(ItemFactory  itemFactory);

    ItemStack getDisplay(ItemFactory itemFactory);

}
