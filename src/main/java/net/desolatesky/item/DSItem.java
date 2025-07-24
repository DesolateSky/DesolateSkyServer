package net.desolatesky.item;

import net.desolatesky.item.handler.ItemHandler;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public final class DSItem implements Keyed {

    private final Key key;
    private final ItemStack itemStack;
    private final @Nullable ItemHandler itemHandler;

    public static DSItem create(Material material) {
        return create(ItemStack.of(material));
    }

    public static DSItem create(ItemStack itemStack) {
        final Key key = Objects.requireNonNullElse(itemStack.getTag(ItemTags.ID), itemStack.material().key());
        return new DSItem(key, itemStack, null);
    }

    public static DSItem create(Key key, ItemStack itemStack, @Nullable ItemHandler itemHandler) {
        if (!key.equals(itemStack.material().key())) {
            itemStack = itemStack.withTag(ItemTags.ID, key);
        }
        return new DSItem(key, itemStack, itemHandler);
    }

    public static DSItem create(Key key, ItemStack itemStack) {
        return new DSItem(key, itemStack, null);
    }

    private DSItem(Key key, ItemStack itemStack, @Nullable ItemHandler itemHandler) {
        this.key = key;
        this.itemStack = itemStack;
        this.itemHandler = itemHandler;
    }

    public ItemStack create() {
        return this.itemStack;
    }

    public ItemStack create(int amount) {
        return this.itemStack.withAmount(amount);
    }

    public ItemHandler itemHandler() {
        return this.itemHandler;
    }

    @Override
    public @NotNull Key key() {
        return this.key;
    }

    public boolean is(@NotNull ItemStack itemStack) {
        return this.itemStack.isSimilar(itemStack);
    }

}