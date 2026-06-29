package com.fisherl.desolatesky.block;

import com.fisherl.desolatesky.block.definition.BlockDefinition;
import net.kyori.adventure.key.Key;
import net.minestom.server.instance.block.Block;

import java.util.Optional;

public interface BlockFactory {

    Optional<BlockDefinition> getBlockDefinition(Key id);

    Optional<BlockDefinition> getBlockDefinition(Block block);

    Key getBlockId(Block block);

    void initialize();

}
