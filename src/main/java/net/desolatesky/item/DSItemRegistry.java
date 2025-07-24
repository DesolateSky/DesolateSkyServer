package net.desolatesky.item;

import net.desolatesky.item.handler.ItemHandler;
import net.kyori.adventure.key.Key;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collections;
import java.util.Map;

public final class DSItemRegistry {

    private final Map<Key, DSItem> items;

    public static DSItemRegistry create(Map<Key, DSItem> items) {
        final DSItemRegistry registry = new DSItemRegistry(items);
        DSItems.register(registry);
        return registry;
    }

    private DSItemRegistry(Map<Key, DSItem> items) {
        this.items = items;
    }

    public void register(DSItem dsItem) {
        this.items.put(dsItem.key(), dsItem);
    }

    public @UnknownNullability ItemStack create(Key key) {
        final DSItem item = this.items.get(key);
        if (item == null) {
            final Material material = Material.fromKey(key);
            if (material == null) {
                return null;
            }
            return ItemStack.of(material);
        }
        return item.create();
    }

    public ItemStack create(ItemStack itemStack) {
        final DSItem dsItem = this.items.get(itemStack.material().key());
        if (dsItem == null) {
            return itemStack;
        }
        return dsItem.create();
    }

    public @Nullable ItemHandler getItemHandler(ItemStack itemStack) {
        Key id = itemStack.getTag(ItemTags.ID);
        if (id == null) {
            id = itemStack.material().key();
        }
        final DSItem item = this.items.get(id);
        if (item == null) {
            return null;
        }
        return item.itemHandler();
    }

    public @Nullable ItemStack create(Key key, int amount) {
        final DSItem item = this.items.get(key);
        if (item == null) {
            final Material material = Material.fromKey(key);
            if (material == null) {
                return null;
            }
            return ItemStack.of(material).withAmount(amount);
        }
        return item.create(amount);
    }

    public @UnmodifiableView Map<Key, DSItem> getItems() {
        return Collections.unmodifiableMap(this.items);
    }

}