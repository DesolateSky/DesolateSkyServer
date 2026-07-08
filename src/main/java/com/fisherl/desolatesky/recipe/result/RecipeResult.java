package com.fisherl.desolatesky.recipe.result;

import com.fisherl.desolatesky.item.ItemFactory;
import net.minestom.server.item.ItemStack;

public interface RecipeResult {

    ItemStack create(ItemFactory  itemFactory);

    ItemStack getDisplay(ItemFactory itemFactory);

}
