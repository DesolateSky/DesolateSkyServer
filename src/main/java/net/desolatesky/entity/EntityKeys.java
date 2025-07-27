package net.desolatesky.entity;

import net.desolatesky.item.DSItems;
import net.desolatesky.util.ComponentUtil;
import net.desolatesky.util.Namespace;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class EntityKeys {

    private static final Map<Key, EntityKey> keys = new HashMap<>();

    private EntityKeys() {
        throw new UnsupportedOperationException();
    }

    public static final EntityKey PLAYER_ENTITY = register(Namespace.key("player_entity"), "Player", Material.PLAYER_HEAD);
    public static final EntityKey DEBRIS_ENTITY = register(Namespace.key("debris_entity"), "Debris", ItemStack.of(Material.LEAF_LITTER));
    public static final EntityKey SIFTER_BLOCK_ENTITY = register(Namespace.key("sifter_block_entity"), "Sifter", DSItems.SIFTER.create());

    public static @Unmodifiable Map<Key, EntityKey> getEntityKeys() {
        return Collections.unmodifiableMap(keys);
    }

    public static @Unmodifiable Set<Key> getKeys() {
        return Collections.unmodifiableSet(keys.keySet());
    }

    public static @Nullable EntityKey get(Key key) {
        return keys.get(key);
    }

    private static EntityKey register(EntityKey entityKey) {
        keys.put(entityKey.key(), entityKey);
        return entityKey;
    }

    private static EntityKey register(Key key, Component displayName, ItemStack menuItem) {
        return register(new EntityKey(key, displayName, menuItem));
    }

    private static EntityKey register(Key key, String displayName, ItemStack menuItem) {
        return register(key, ComponentUtil.noItalics(displayName), menuItem);
    }

    private static EntityKey register(Key key, Component displayName, Material menuMaterial) {
        return register(new EntityKey(key, displayName, menuMaterial));
    }

    private static EntityKey register(Key key, String displayName, Material menuMaterial) {
        return register(key, ComponentUtil.noItalics(displayName), menuMaterial);
    }

}
