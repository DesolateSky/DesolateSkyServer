package net.desolatesky.item.handler;

import net.desolatesky.instance.DSInstance;
import net.desolatesky.player.DSPlayer;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;
import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.PlayerHand;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockFace;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public abstract class ItemHandler implements Keyed {

    private final Key key;

    public ItemHandler(Key key) {
        this.key = key;
    }

    public ItemStack onInteractBlock(DSPlayer player, DSInstance instance, ItemStack usedItem, PlayerHand hand, Point blockPoint, Block block, Point cursorPosition, BlockFace blockFace) {
        return usedItem;
    }

    public ItemStack onInteractEntity(DSPlayer player, DSInstance instance, ItemStack usedItem, Entity interacted) {
        return usedItem;
    }

    public ItemStack onInteractAir(DSPlayer player, DSInstance instance, ItemStack usedItem) {
        return usedItem;
    }

    public ItemStack onPunchBlock(DSPlayer player, DSInstance instance, ItemStack usedItem, Point blockPoint, Block block) {
        return usedItem;
    }

    public ItemStack onPunchAir(DSPlayer player, DSInstance instance, ItemStack usedItem) {
        return usedItem;
    }

    public ItemStack onPunchEntity(DSPlayer player, DSInstance instance, ItemStack usedItem, Entity interacted) {
        return usedItem;
    }

    @Override
    public @NotNull Key key() {
        return this.key;
    }

}
