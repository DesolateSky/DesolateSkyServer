package net.desolatesky.block.category;

import net.desolatesky.category.Category;
import net.kyori.adventure.key.Key;

public final class BlockCategories {

    private  BlockCategories() {
        throw new UnsupportedOperationException();
    }

    public static final Category LOGS = new BlockCategory(Key.key("logs"));
    public static final Category WOOD = new BlockCategory(Key.key("wood"));
    public static final Category LEAVES = new BlockCategory(Key.key("leaves"));
    public static final Category AXE_MINEABLE = new BlockCategory(Key.key("axe_mineable"));
    public static final Category PICKAXE_MINEABLE = new BlockCategory(Key.key("pickaxe_mineable"));
    public static final Category SHOVEL_MINEABLE = new BlockCategory(Key.key("shovel_mineable"));
    public static final Category HOE_MINEABLE = new BlockCategory(Key.key("hoe_mineable"));
    public static final Category SWORD_MINEABLE = new BlockCategory(Key.key("sword_mineable"));
    public static final Category SHEARS_MINEABLE = new BlockCategory(Key.key("shears_mineable"));

}
