package net.desolatesky.item.handler;

import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public record ItemInteractionResult(@Nullable ItemStack newItem, boolean cancel, boolean passthrough) {

    public static ItemInteractionResult noEffect() {
        return new ItemInteractionResult(null, false, true);
    }

}
