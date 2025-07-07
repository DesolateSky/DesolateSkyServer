package net.desolatesky.item;

import net.desolatesky.registry.DSRegistry;
import net.desolatesky.util.ComponentUtil;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.tag.Tag;

public final class DSItems extends DSRegistry<ItemStack> {

    public static final Key DUST = Key.key("dust");
    public static final Key STICK = Key.key("stick");

    private static final DSItems INSTANCE = new DSItems();

    public static DSItems get() {
        return INSTANCE;
    }

    private DSItems() {
        super(ItemTags.ID);
    }

    private final ItemStack dustItem = this.register(ItemStack.builder(Material.GRAY_DYE)
            .customName(ComponentUtil.noItalics(Component.text("Dust").color(NamedTextColor.GRAY)))
            .set(ItemTags.ID, DUST)
            .build());
    private final ItemStack stickItem = this.register(ItemStack.builder(Material.STICK)
            .customName(ComponentUtil.noItalics(Component.text("Stick").color(TextColor.color(0x7B3F00))))
            .build());

    public ItemStack dustItem() {
        return this.dustItem;
    }

    public ItemStack stickItem() {
        return this.stickItem;
    }

    @Override
    protected <T> ItemStack withTag(ItemStack itemStack, Tag<T> tag, T value) {
        return itemStack.withTag(tag, value);
    }

    @Override
    protected ItemStack getDefault(Key key) {
        final Material material = Material.fromKey(key);
        if (material == null) {
            throw new IllegalArgumentException("Material not found for key: " + key);
        }
        return ItemStack.of(material);
    }

    @Override
    protected <T> T getTag(ItemStack itemStack, Tag<T> tag) {
        return itemStack.getTag(tag);
    }

    @Override
    protected Key getId(ItemStack itemStack) {
        final Key id = itemStack.getTag(ItemTags.ID);
        if (id == null) {
            return itemStack.material().key();
        }
        return id;
    }
}