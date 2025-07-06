package net.desolatesky.block.handler.vanilla;

import net.desolatesky.block.BlockProperties;
import net.desolatesky.block.handler.DSBlockHandler;
import net.desolatesky.instance.team.TeamInstance;
import net.kyori.adventure.key.Key;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.block.Block;
import org.jetbrains.annotations.NotNull;

public final class TrapdoorHandler extends DSBlockHandler {

    public TrapdoorHandler() {
        super(Key.key("trapdoor_handler"));
    }

    @Override
    public boolean onInteract(@NotNull Interaction interaction, TeamInstance instance) {
        final Player player = interaction.getPlayer();
        if (player.isSneaking()) {
            return false;
        }
        final Boolean open = BlockProperties.OPEN.get(interaction.getBlock());
        if (open == null) {
            return false;
        }
        final Block newBlock;
        if (open) {
            newBlock = BlockProperties.OPEN.set(interaction.getBlock(), false);
        } else {
            newBlock = BlockProperties.OPEN.set(interaction.getBlock(), true);
        }
        instance.setBlock(interaction.getBlockPosition(), newBlock);
        return true;
    }

}
