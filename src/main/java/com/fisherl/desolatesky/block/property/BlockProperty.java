package com.fisherl.desolatesky.block.property;

import net.minestom.server.instance.block.Block;

import java.util.Optional;

public interface BlockProperty<T> {

    String name();

    Optional<T> read(Block block);

    Block write(Block block, T value);

    boolean canWrite(Block block, T value);
}
