package net.desolatesky.block.property;

import net.minestom.server.instance.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public interface BlockProperty<T> {

    String name();

    @Nullable T read(Block block);

    Block write(Block block, T value);

    boolean canWrite(Block block, T value);
}
