package net.desolatesky.util;

import net.desolatesky.item.ItemTags;
import net.kyori.adventure.key.Key;
import net.minestom.server.item.ItemStack;

public final class ItemUtil {

    public static Key getItemId(ItemStack itemStack) {
        final Key key = itemStack.getTag(ItemTags.ID);
        if (key == null) {
            return itemStack.material().key();
        }
        return key;
    }

    private ItemUtil() {
    }

}
