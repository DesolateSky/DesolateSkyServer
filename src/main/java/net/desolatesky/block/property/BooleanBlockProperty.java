package net.desolatesky.block.property;

import net.minestom.server.instance.block.Block;
import org.jetbrains.annotations.Nullable;

public record BooleanBlockProperty(String name) implements BlockProperty<Boolean> {

    @Override
    public @Nullable Boolean read(Block block) {
        final String value = block.getProperty(this.name);
        if (value == null) {
            return null;
        }
        return Boolean.valueOf(value);
    }

    public boolean readNullable(Block block) {
        final Boolean value = this.read(block);
        if (value == null) {
            return false;
        }
        return value;
    }

    @Override
    public Block write(Block block, Boolean value) {
        return block.withProperty(this.name, String.valueOf(value));
    }

    @Override
    public boolean canWrite(Block block, Boolean value) {
        return true;
    }
}
