package net.desolatesky.block.category;

import net.desolatesky.category.Category;
import net.desolatesky.util.Namespace;

public final class BlockCategories {

    private  BlockCategories() {
        throw new UnsupportedOperationException();
    }

    public static final Category LOGS = new BlockCategory(Namespace.key("logs"));
    public static final Category PLANKS = new BlockCategory(Namespace.key("planks"));
    public static final Category WOOD = new BlockCategory(Namespace.key("wood"));
    public static final Category LEAVES = new BlockCategory(Namespace.key("leaves"));
    public static final Category AXE_MINEABLE = new BlockCategory(Namespace.key("axe_mineable"));
    public static final Category PICKAXE_MINEABLE = new BlockCategory(Namespace.key("pickaxe_mineable"));
    public static final Category SHOVEL_MINEABLE = new BlockCategory(Namespace.key("shovel_mineable"));
    public static final Category HOE_MINEABLE = new BlockCategory(Namespace.key("hoe_mineable"));
    public static final Category SWORD_MINEABLE = new BlockCategory(Namespace.key("sword_mineable"));
    public static final Category SHEARS_MINEABLE = new BlockCategory(Namespace.key("shears_mineable"));
    public static final Category WOODEN_SLABS = new BlockCategory(Namespace.key("wooden_slabs"));
    public static final Category CROP_GROWABLE = new BlockCategory(Namespace.key("crop_growable"));
    public static final Category CROP = new BlockCategory(Namespace.key("crop"));


    public static final Category DIRT = new BlockCategory(Namespace.key("dirt"));

}
