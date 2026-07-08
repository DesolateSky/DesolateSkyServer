package com.fisherl.desolatesky.item;

import com.fisherl.desolatesky.item.definition.ItemDefinition;
import net.kyori.adventure.key.Key;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collection;
import java.util.Optional;

public interface ItemFactory {

    Optional<ItemDefinition> getItemDefinition(Key id);

    Optional<ItemDefinition> getItemDefinition(ItemStack itemStack);

    @Unmodifiable
    Collection<Key> getALlItemIds();

    Key getItemId(ItemStack itemStack);

    void initialize();

}
