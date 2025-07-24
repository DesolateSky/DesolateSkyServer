package net.desolatesky.crafting.recipe;

import net.desolatesky.block.MaterialTags;
import net.desolatesky.crafting.CraftingManager;
import net.desolatesky.crafting.ingredient.ItemIngredient;
import net.desolatesky.item.DSItems;

public final class Recipes {

    private Recipes() {
        throw new IllegalStateException();
    }

    public static void registerCrafting(CraftingManager craftingManager) {
        craftingManager.addRecipe(new CraftingRecipe(new ItemIngredient[][]{
                {ItemIngredient.exact(DSItems.STICK), ItemIngredient.exact(DSItems.STICK), ItemIngredient.EMPTY},
                {ItemIngredient.exact(DSItems.STICK), ItemIngredient.exact(DSItems.STICK), ItemIngredient.EMPTY},
                {ItemIngredient.EMPTY, ItemIngredient.EMPTY, ItemIngredient.EMPTY}
        }, DSItems.OAK_PLANKS::create));
        craftingManager.addRecipe(new CraftingRecipe(new ItemIngredient[][]{
                {ItemIngredient.exact(DSItems.DUST), ItemIngredient.exact(DSItems.DUST), ItemIngredient.EMPTY},
                {ItemIngredient.exact(DSItems.DUST), ItemIngredient.exact(DSItems.DUST), ItemIngredient.EMPTY},
                {ItemIngredient.EMPTY, ItemIngredient.EMPTY, ItemIngredient.EMPTY}
        }, DSItems.DUST_BLOCK::create));
        craftingManager.addRecipe(new CraftingRecipe(new ItemIngredient[][]{
                {ItemIngredient.tag(MaterialTags.MINECRAFT_PLANKS), ItemIngredient.tag(MaterialTags.MINECRAFT_PLANKS), ItemIngredient.EMPTY},
                {ItemIngredient.tag(MaterialTags.MINECRAFT_PLANKS), ItemIngredient.tag(MaterialTags.MINECRAFT_PLANKS), ItemIngredient.EMPTY},
                {ItemIngredient.EMPTY, ItemIngredient.EMPTY, ItemIngredient.EMPTY}
        }, DSItems.CRAFTING_TABLE::create));
        craftingManager.addRecipe(new CraftingRecipe(new ItemIngredient[][]{
                {ItemIngredient.exact(DSItems.FIBER), ItemIngredient.exact(DSItems.FIBER), ItemIngredient.EMPTY},
                {ItemIngredient.exact(DSItems.FIBER), ItemIngredient.exact(DSItems.FIBER), ItemIngredient.EMPTY},
                {ItemIngredient.EMPTY, ItemIngredient.EMPTY, ItemIngredient.EMPTY}
        }, DSItems.FIBER_MESH::create));
        craftingManager.addRecipe(new CraftingRecipe(new ItemIngredient[][]{
                {ItemIngredient.exact(DSItems.OAK_PLANKS), ItemIngredient.exact(DSItems.OAK_PLANKS), ItemIngredient.exact(DSItems.OAK_PLANKS)},
                {ItemIngredient.exact(DSItems.OAK_PLANKS), ItemIngredient.exact(DSItems.FIBER_MESH), ItemIngredient.exact(DSItems.OAK_PLANKS)},
                {ItemIngredient.exact(DSItems.OAK_PLANKS), ItemIngredient.exact(DSItems.OAK_PLANKS), ItemIngredient.exact(DSItems.OAK_PLANKS)}
        }, DSItems.SIFTER::create));
    }

}
