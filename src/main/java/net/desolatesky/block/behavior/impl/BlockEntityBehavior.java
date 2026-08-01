package net.desolatesky.block.behavior.impl;

import net.desolatesky.block.behavior.BlockBehavior;
import net.kyori.adventure.key.Key;
import net.minestom.server.instance.block.BlockHandler;

public interface BlockEntityBehavior extends BlockBehavior {

    BlockHandler createBlockHandler();

    Key blockEntityId();

}
