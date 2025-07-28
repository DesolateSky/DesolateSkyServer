package net.desolatesky.item.category;

import net.desolatesky.block.category.BlockCategories;
import net.desolatesky.util.Namespace;

import java.util.Set;

public final class ItemCategories {

    private ItemCategories() {
        throw new UnsupportedOperationException();
    }

    public static final ItemCategory PLANKS = new ItemCategory(BlockCategories.PLANKS.key());
    public static final ItemCategory WOODEN_SLABS = new ItemCategory(BlockCategories.WOODEN_SLABS.key());
    public static final ItemCategory STICK = new ItemCategory(Namespace.key("stick"));
    public static final ItemCategory AXE = new ItemCategory(Namespace.key("axe"), Set.of(BlockCategories.AXE_MINEABLE));
    public static final ItemCategory PICKAXE = new ItemCategory(Namespace.key("pickaxe"), Set.of(BlockCategories.PICKAXE_MINEABLE));
    public static final ItemCategory SHOVEL = new ItemCategory(Namespace.key("shovel"), Set.of(BlockCategories.SHOVEL_MINEABLE));
    public static final ItemCategory HOE = new ItemCategory(Namespace.key("hoe"), Set.of(BlockCategories.HOE_MINEABLE));
    public static final ItemCategory SWORD = new ItemCategory(Namespace.key("sword"), Set.of(BlockCategories.SWORD_MINEABLE));
    public static final ItemCategory COMPOSTABLE = new ItemCategory(Namespace.key("compostable"));

}
