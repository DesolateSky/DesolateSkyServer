package net.desolatesky.block.handler.vanilla;

import net.desolatesky.DesolateSkyServer;
import net.desolatesky.block.BlockProperties;
import net.desolatesky.block.handler.TransientBlockHandler;
import net.desolatesky.block.settings.BlockSettings;
import net.desolatesky.instance.DSInstance;
import net.kyori.adventure.key.Key;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import org.jetbrains.annotations.NotNull;

public final class TrapdoorHandler extends TransientBlockHandler {

    public TrapdoorHandler(DesolateSkyServer server, Key key, ItemStack menuItem) {
        super(server, BlockSettings.builder(key, menuItem).breakTime(1_000).stateless().build());
    }

    /**
     *
     * @param breakTime break time in milliseconds
     */
    public TrapdoorHandler(DesolateSkyServer server, Key key, ItemStack menuItem, int breakTime) {
        super(server, BlockSettings.builder(key, menuItem).breakTime(breakTime).stateless().build());
    }

    @Override
    public boolean onInteract(@NotNull Interaction interaction, DSInstance instance) {
        final Player player = interaction.getPlayer();
        if (player.isSneaking()) {
            return true;
        }
        final Boolean open = BlockProperties.OPEN.get(interaction.getBlock());
        if (open == null) {
            return true;
        }
        final Block newBlock;
        if (open) {
            newBlock = BlockProperties.OPEN.set(interaction.getBlock(), false);
        } else {
            newBlock = BlockProperties.OPEN.set(interaction.getBlock(), true);
        }
        instance.setBlock(interaction.getBlockPosition(), newBlock);
        return false;
    }

}
