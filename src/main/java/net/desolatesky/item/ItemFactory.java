package net.desolatesky.item;

import net.desolatesky.item.definition.ItemDefinition;
import net.kyori.adventure.key.Key;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collection;

public interface ItemFactory {

    @Nullable ItemDefinition getItemDefinition(Key id);

    @Nullable ItemStack getDefaultItem(Key id);

    @Nullable ItemDefinition getItemDefinition(ItemStack itemStack);

    @Unmodifiable
    Collection<Key> getALlItemIds();

    Key getItemId(ItemStack itemStack);

    void initialize();

}
