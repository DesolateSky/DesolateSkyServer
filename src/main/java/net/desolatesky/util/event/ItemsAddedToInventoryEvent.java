package net.desolatesky.util.event;

import net.minestom.server.entity.Entity;
import net.minestom.server.event.Event;
import net.minestom.server.inventory.AbstractInventory;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collection;
import java.util.Collections;

public final class ItemsAddedToInventoryEvent implements Event {

    private final @Nullable Entity entity;
    private final AbstractInventory inventory;
    private final @Unmodifiable Collection<ItemStack> items;

    public ItemsAddedToInventoryEvent(@Nullable Entity entity, AbstractInventory inventory, Collection<ItemStack> items) {
        this.entity = entity;
        this.inventory = inventory;
        this.items = Collections.unmodifiableCollection(items);
    }

    public @Nullable Entity entity() {
        return this.entity;
    }

    public AbstractInventory inventory() {
        return this.inventory;
    }

    public @Unmodifiable Collection<ItemStack> items() {
        return this.items;
    }
}
