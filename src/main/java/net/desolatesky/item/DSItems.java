package net.desolatesky.item;

import net.desolatesky.block.BlockKeys;
import net.desolatesky.util.ComponentUtil;
import net.kyori.adventure.key.Key;
import net.minestom.server.component.DataComponents;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

import java.util.HashMap;
import java.util.Map;

public final class DSItems {

    private static final Map<Key, DSItem> defaultItems = new HashMap<>();

    private DSItems() {
        throw new UnsupportedOperationException();
    }

    // BLOCKS
    public static final DSItem DUST_BLOCK = addDefault(DSItem.create(ItemKeys.DUST_BLOCK, ItemStack.builder(Material.CLAY)
            .customName(ComponentUtil.noItalics("Dust Block"))
            .set(DataComponents.ITEM_MODEL, ItemKeys.DUST_BLOCK.asString())
            .set(ItemTags.BLOCK_ID, BlockKeys.DUST_BLOCK)
            .build()));
    public static final DSItem DEBRIS_CATCHER = addDefault(DSItem.create(ItemKeys.DEBRIS_CATCHER, ItemStack.builder(Material.COBWEB)
            .customName(ComponentUtil.noItalics("Debris Catcher"))
            .set(ItemTags.BLOCK_ID, BlockKeys.DEBRIS_CATCHER)
            .build()));
    public static final DSItem SIFTER = addDefault(DSItem.create(ItemKeys.SIFTER, ItemStack.builder(Material.SCAFFOLDING)
            .customName(ComponentUtil.noItalics("Sifter"))
            .set(ItemTags.BLOCK_ID, BlockKeys.SIFTER)
            .build()));

    // ITEMS
    public static final DSItem DUST = addDefault(DSItem.create(ItemKeys.DUST, ItemStack.builder(Material.GRAY_DYE)
            .customName(ComponentUtil.noItalics("Dust"))
            .set(DataComponents.ITEM_MODEL, ItemKeys.DUST.asString())
            .build()));
    public static final DSItem COPPER_DUST = addDefault(DSItem.create(ItemKeys.COPPER_DUST, ItemStack.builder(Material.REDSTONE)
            .customName(ComponentUtil.noItalics("Copper Dust"))
            .set(DataComponents.ITEM_MODEL, ItemKeys.COPPER_DUST.asString())
            .build()));
    public static final DSItem FIBER = addDefault(DSItem.create(ItemKeys.FIBER, ItemStack.builder(Material.TALL_DRY_GRASS)
            .customName(ComponentUtil.noItalics("Fiber"))
            .build()));
    public static final DSItem FIBER_MESH = addDefault(DSItem.create(ItemKeys.FIBER_MESH, ItemStack.builder(Material.GLOW_LICHEN)
            .customName(ComponentUtil.noItalics("String Mesh"))
            .build()));

    // vanilla
    public static final DSItem STICK = addDefault(DSItem.create(ItemStack.builder(Material.STICK).set(ItemTags.BLOCK_ID, Material.CRAFTING_TABLE.key()).build()));
    public static final DSItem OAK_PLANKS = addDefault(DSItem.create(ItemStack.builder(Material.OAK_PLANKS).set(ItemTags.BLOCK_ID, Material.OAK_PLANKS.key()).build()));
    public static final DSItem CRAFTING_TABLE = addDefault(DSItem.create(ItemStack.builder(Material.CRAFTING_TABLE).set(ItemTags.BLOCK_ID, Material.CRAFTING_TABLE.key()).build()));


    public static void register(DSItemRegistry itemRegistry) {
        defaultItems.values().forEach(itemRegistry::register);
    }

    private static DSItem addDefault(DSItem dsItem) {
        defaultItems.putIfAbsent(dsItem.key(), dsItem);
        return dsItem;
    }


    public static boolean isBlock(ItemStack itemStack) {
        final Key id = itemStack.getTag(ItemTags.BLOCK_ID);
        if (id == null) {
            return itemStack.material().isBlock();
        }
        return true;
    }

}
