package net.desolatesky.item;

import com.google.common.base.Preconditions;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import net.desolatesky.item.category.ItemCategory;
import net.desolatesky.item.handler.ItemHandler;
import net.kyori.adventure.key.Key;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public final class DSItemRegistry {

    private final Map<Key, DSItem> items;
    private final Multimap<ItemCategory, DSItem> itemsByCategory;

    public static DSItemRegistry create(Map<Key, DSItem> items) {
        return new DSItemRegistry(items);
    }

    private DSItemRegistry(Map<Key, DSItem> items) {
        Preconditions.checkArgument(items.isEmpty(), "Items map must be empty when creating DSItemRegistry");
        this.items = items;
        this.itemsByCategory = Multimaps.newSetMultimap(new HashMap<>(), HashSet::new);
    }

    public void register(DSItem dsItem) {
        this.items.put(dsItem.key(), dsItem);
        final ItemHandler itemHandler = dsItem.itemHandler();
        if (itemHandler != null) {
            for (final ItemCategory category : itemHandler.categories()) {
                this.itemsByCategory.put(category, dsItem);
            }
        }
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

    public @Nullable DSItem getItem(Key key) {
        return this.items.get(key);
    }

    public @Nullable DSItem getItem(ItemStack itemStack) {
        Key id = itemStack.getTag(ItemTags.ID);
        if (id == null) {
            id = itemStack.material().key();
        }
        return this.items.get(id);
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

    public @UnmodifiableView Collection<Key> getKeys() {
        return Collections.unmodifiableSet(this.items.keySet());
    }

    public @UnmodifiableView Collection<DSItem> getItemsByCategory(ItemCategory category) {
        return Collections.unmodifiableCollection(this.itemsByCategory.get(category));
    }

}