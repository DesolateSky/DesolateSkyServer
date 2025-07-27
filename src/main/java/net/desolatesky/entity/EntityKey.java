package net.desolatesky.entity;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;
import net.kyori.adventure.text.Component;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

public record EntityKey(Key key, Component displayName, ItemStack menuItem) implements Keyed {

    public EntityKey(Key key, Component displayName, Material menuMaterial) {
        this(key, displayName, ItemStack.of(menuMaterial));
    }

}
