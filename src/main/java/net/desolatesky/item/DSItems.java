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
    public static final DSItem DUST_BLOCK = addDefault(DSItem.create(ItemKeys.DUST_BLOCK, ItemHandlers.DUST_BLOCK, ItemStack.builder(Material.CLAY)
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
    public static final DSItem COMPOSTER = addDefault(DSItem.create(ItemKeys.COMPOSTER, ItemStack.builder(Material.COMPOSTER)
            .set(ItemTags.BLOCK_ID, BlockKeys.COMPOSTER)
            .build()));
    public static final DSItem PETRIFIED_PLANKS = addDefault(DSItem.create(ItemKeys.PETRIFIED_PLANKS, ItemHandlers.PETRIFIED_PLANKS, ItemStack.builder(Material.PALE_OAK_PLANKS)
            .customName(ComponentUtil.noItalics("Petrified Planks"))
            .set(ItemTags.BLOCK_ID, BlockKeys.PETRIFIED_PLANKS.key())
            .build()));
    public static final DSItem PETRIFIED_SLAB = addDefault(DSItem.create(ItemKeys.PETRIFIED_SLAB, ItemHandlers.PETRIFIED_SLAB, ItemStack.builder(Material.PALE_OAK_SLAB)
            .customName(ComponentUtil.noItalics("Petrified Slab"))
            .set(ItemTags.BLOCK_ID, BlockKeys.PETRIFIED_SLAB.key())
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
    public static final DSItem PETRIFIED_STICK = addDefault(DSItem.create(ItemKeys.PETRIFIED_STICK, ItemHandlers.PETRIFIED_STICK, ItemStack.builder(Material.STICK)
            .customName(ComponentUtil.noItalics("Petrified Stick"))
            .set(DataComponents.ITEM_MODEL, ItemKeys.PETRIFIED_STICK.asString())
            .build()));
    public static final DSItem DEAD_LEAVES = addDefault(DSItem.create(ItemKeys.DEAD_LEAVES, ItemHandlers.DEAD_LEAVES, ItemStack.builder(Material.LEAF_LITTER)
            .customName(ComponentUtil.noItalics("Dead Leaves"))
            .build()));


    // vanilla
    public static final DSItem CRAFTING_TABLE = addDefault(DSItem.create(ItemStack.builder(Material.CRAFTING_TABLE).set(ItemTags.BLOCK_ID, BlockKeys.CRAFTING_TABLE).build()));
    public static final DSItem DIRT = addDefault(DSItem.create(ItemKeys.DIRT, ItemStack.builder(Material.DIRT).set(ItemTags.BLOCK_ID, BlockKeys.DIRT).build()));
    public static final DSItem WOODEN_AXE = addDefault(DSItem.create(ItemKeys.WOODEN_AXE, ItemHandlers.WOODEN_AXE, ItemStack.builder(Material.WOODEN_AXE)
            .customName(ComponentUtil.noItalics("Wooden Axe"))
            .build()));


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
