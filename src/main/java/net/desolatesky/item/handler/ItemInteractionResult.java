package net.desolatesky.item.handler;

import net.minestom.server.item.ItemStack;

public record ItemInteractionResult(ItemStack newItem, boolean cancel, boolean passthrough) {

    public static ItemInteractionResult passthrough(ItemStack item) {
        return new ItemInteractionResult(item, false, true);
    }

}
