package net.desolatesky.block.property.type;

import net.minestom.server.instance.block.Block;
import org.jetbrains.annotations.Nullable;

public interface BlockProperty<T> {

    String name();

    Block set(Block block, T value);

    @Nullable T get(Block block);

}
