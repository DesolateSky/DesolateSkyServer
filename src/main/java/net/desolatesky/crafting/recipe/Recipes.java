package net.desolatesky.crafting.recipe;

import net.desolatesky.block.MaterialTags;
import net.desolatesky.block.entity.custom.crop.Crop;
import net.desolatesky.crafting.CraftingManager;
import net.desolatesky.crafting.ingredient.ItemIngredient;
import net.desolatesky.item.DSItemRegistry;
import net.desolatesky.item.DSItems;
import net.desolatesky.item.ItemTags;
import net.desolatesky.item.category.ItemCategories;
import net.desolatesky.item.category.ItemCategory;
import net.minestom.server.item.ItemStack;

public final class Recipes {

    private Recipes() {
        throw new IllegalStateException();
    }

    public static void registerCrafting(CraftingManager craftingManager, DSItemRegistry itemRegistry) {
        craftingManager.addRecipe(new CraftingRecipe(new ItemIngredient[][]{
                {ItemIngredient.exact(DSItems.PETRIFIED_STICK), ItemIngredient.exact(DSItems.PETRIFIED_STICK), ItemIngredient.EMPTY},
                {ItemIngredient.exact(DSItems.PETRIFIED_STICK), ItemIngredient.exact(DSItems.PETRIFIED_STICK), ItemIngredient.EMPTY},
                {ItemIngredient.EMPTY, ItemIngredient.EMPTY, ItemIngredient.EMPTY}
        }, DSItems.PETRIFIED_PLANKS::create));
        craftingManager.addRecipe(new CraftingRecipe(new ItemIngredient[][]{
                {ItemIngredient.exact(DSItems.PETRIFIED_LOG), ItemIngredient.EMPTY, ItemIngredient.EMPTY},
                {ItemIngredient.EMPTY, ItemIngredient.EMPTY, ItemIngredient.EMPTY},
                {ItemIngredient.EMPTY, ItemIngredient.EMPTY, ItemIngredient.EMPTY}
        }, DSItems.PETRIFIED_PLANKS::create, 4));
        craftingManager.addRecipe(new CraftingRecipe(new ItemIngredient[][]{
                {ItemIngredient.exact(DSItems.PETRIFIED_PLANKS), ItemIngredient.exact(DSItems.PETRIFIED_PLANKS), ItemIngredient.exact(DSItems.PETRIFIED_PLANKS)},
                {ItemIngredient.EMPTY, ItemIngredient.EMPTY, ItemIngredient.EMPTY},
                {ItemIngredient.EMPTY, ItemIngredient.EMPTY, ItemIngredient.EMPTY}
        }, DSItems.PETRIFIED_SLAB::create, 6));
        craftingManager.addRecipe(new CraftingRecipe(new ItemIngredient[][]{
                {ItemIngredient.tag(MaterialTags.MINECRAFT_PLANKS), ItemIngredient.tag(MaterialTags.MINECRAFT_PLANKS), ItemIngredient.EMPTY},
                {ItemIngredient.tag(MaterialTags.MINECRAFT_PLANKS), ItemIngredient.tag(MaterialTags.MINECRAFT_PLANKS), ItemIngredient.EMPTY},
                {ItemIngredient.EMPTY, ItemIngredient.EMPTY, ItemIngredient.EMPTY}
        }, DSItems.CRAFTING_TABLE::create));
        craftingManager.addRecipe(new CraftingRecipe(new ItemIngredient[][]{
                {ItemIngredient.exact(DSItems.DUST), ItemIngredient.exact(DSItems.DUST), ItemIngredient.EMPTY},
                {ItemIngredient.exact(DSItems.DUST), ItemIngredient.exact(DSItems.DUST), ItemIngredient.EMPTY},
                {ItemIngredient.EMPTY, ItemIngredient.EMPTY, ItemIngredient.EMPTY}
        }, DSItems.DUST_BLOCK::create));
        craftingManager.addRecipe(new CraftingRecipe(new ItemIngredient[][]{
                {ItemIngredient.exact(DSItems.FIBER), ItemIngredient.exact(DSItems.FIBER), ItemIngredient.EMPTY},
                {ItemIngredient.exact(DSItems.FIBER), ItemIngredient.exact(DSItems.FIBER), ItemIngredient.EMPTY},
                {ItemIngredient.EMPTY, ItemIngredient.EMPTY, ItemIngredient.EMPTY}
        }, DSItems.FIBER_MESH::create));
        craftingManager.addRecipe(new CraftingRecipe(new ItemIngredient[][]{
                {ItemIngredient.category(ItemCategories.PLANKS, itemRegistry), ItemIngredient.category(ItemCategories.PLANKS, itemRegistry), ItemIngredient.category(ItemCategories.PLANKS, itemRegistry)},
                {ItemIngredient.category(ItemCategories.PLANKS, itemRegistry), ItemIngredient.exact(DSItems.FIBER_MESH), ItemIngredient.category(ItemCategories.PLANKS, itemRegistry)},
                {ItemIngredient.category(ItemCategories.PLANKS, itemRegistry), ItemIngredient.category(ItemCategories.PLANKS, itemRegistry), ItemIngredient.category(ItemCategories.PLANKS, itemRegistry)}
        }, DSItems.SIFTER::create));
        craftingManager.addRecipe(new CraftingRecipe(new ItemIngredient[][]{
                {ItemIngredient.category(ItemCategories.WOODEN_SLABS, itemRegistry), ItemIngredient.EMPTY, ItemIngredient.category(ItemCategories.WOODEN_SLABS, itemRegistry)},
                {ItemIngredient.category(ItemCategories.WOODEN_SLABS, itemRegistry), ItemIngredient.EMPTY, ItemIngredient.category(ItemCategories.WOODEN_SLABS, itemRegistry)},
                {ItemIngredient.category(ItemCategories.WOODEN_SLABS, itemRegistry), ItemIngredient.category(ItemCategories.WOODEN_SLABS, itemRegistry), ItemIngredient.category(ItemCategories.WOODEN_SLABS, itemRegistry)}
        }, DSItems.COMPOSTER::create));
        craftingManager.addRecipe(new CraftingRecipe(new ItemIngredient[][]{
                {ItemIngredient.exact(DSItems.DEAD_LEAVES), ItemIngredient.exact(DSItems.DEAD_LEAVES), ItemIngredient.exact(DSItems.DEAD_LEAVES)},
                {ItemIngredient.exact(DSItems.DEAD_LEAVES), ItemIngredient.exact(DSItems.LIFE_ESSENCE), ItemIngredient.exact(DSItems.DEAD_LEAVES)},
                {ItemIngredient.exact(DSItems.DEAD_LEAVES), ItemIngredient.exact(DSItems.DEAD_LEAVES), ItemIngredient.exact(DSItems.DEAD_LEAVES)}
        }, DSItems.PETRIFIED_SAPLING::create));
        craftingManager.addRecipe(new CraftingRecipe(new ItemIngredient[][]{
                {ItemIngredient.exact(DSItems.SAND_CHUNK), ItemIngredient.exact(DSItems.SAND_CHUNK), ItemIngredient.EMPTY},
                {ItemIngredient.exact(DSItems.SAND_CHUNK), ItemIngredient.exact(DSItems.SAND_CHUNK), ItemIngredient.EMPTY},
                {ItemIngredient.EMPTY, ItemIngredient.EMPTY, ItemIngredient.EMPTY}
        }, DSItems.SAND::create));
        craftingManager.addRecipe(new CraftingRecipe(new ItemIngredient[][]{
                {ItemIngredient.exact(DSItems.GRAVEL_CHUNK), ItemIngredient.exact(DSItems.GRAVEL_CHUNK), ItemIngredient.EMPTY},
                {ItemIngredient.exact(DSItems.GRAVEL_CHUNK), ItemIngredient.exact(DSItems.GRAVEL_CHUNK), ItemIngredient.EMPTY},
                {ItemIngredient.EMPTY, ItemIngredient.EMPTY, ItemIngredient.EMPTY}
        }, DSItems.GRAVEL::create));
        craftingManager.addRecipe(new CraftingRecipe(new ItemIngredient[][]{
                {ItemIngredient.exact(DSItems.CLAY_BALL), ItemIngredient.exact(DSItems.CLAY_BALL), ItemIngredient.EMPTY},
                {ItemIngredient.exact(DSItems.CLAY_BALL), ItemIngredient.exact(DSItems.CLAY_BALL), ItemIngredient.EMPTY},
                {ItemIngredient.EMPTY, ItemIngredient.EMPTY, ItemIngredient.EMPTY}
        }, DSItems.CLAY::create));
        craftingManager.addRecipe(new CraftingRecipe(new ItemIngredient[][]{
                {ItemIngredient.exact(DSItems.DEAD_WHEAT_SEEDS), ItemIngredient.exact(DSItems.LIFE_ESSENCE), ItemIngredient.EMPTY},
                {ItemIngredient.EMPTY, ItemIngredient.EMPTY, ItemIngredient.EMPTY},
                {ItemIngredient.EMPTY, ItemIngredient.EMPTY, ItemIngredient.EMPTY}
        }, input -> {
            final ItemStack wheatSeeds = input[0][0];
            if (wheatSeeds.isAir()) {
                return ItemStack.AIR;
            }
            final Crop crop = wheatSeeds.getTag(ItemTags.CROP);
            if (crop == null) {
                return ItemStack.AIR;
            }
            return DSItems.WHEAT_SEEDS.create().withTag(ItemTags.CROP, crop);
        }, DSItems.WHEAT_SEEDS.create()));
    }

}
