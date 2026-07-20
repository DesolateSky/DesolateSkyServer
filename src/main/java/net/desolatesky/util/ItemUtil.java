package net.desolatesky.util;

import net.desolatesky.item.ItemTags;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.minestom.server.component.DataComponent;
import net.minestom.server.component.DataComponents;
import net.minestom.server.item.ItemStack;

public final class ItemUtil {

    public static Key getItemId(ItemStack itemStack) {
        final Key key = itemStack.getTag(ItemTags.ID);
        if (key == null) {
            return itemStack.material().key();
        }
        return key;
    }

    public static Component getItemName(ItemStack itemStack) {
        Component name = itemStack.get(DataComponents.CUSTOM_NAME);
        if (name == null) {
            name = Component.translatable(itemStack.material().translationKey());
        }
        return name;
    }

    private ItemUtil() {
    }
}
