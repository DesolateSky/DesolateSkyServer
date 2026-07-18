package net.desolatesky.block;

import net.desolatesky.block.definition.BlockDefinition;
import net.kyori.adventure.key.Key;
import net.minestom.server.instance.block.Block;
import org.jetbrains.annotations.Nullable;

public interface BlockFactory {

    @Nullable BlockDefinition getBlockDefinition(Key id);

    @Nullable BlockDefinition getBlockDefinition(Block block);

    Key getBlockId(Block block);

    void initialize();

}
