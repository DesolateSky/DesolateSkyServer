package net.desolatesky.block.property;

import net.minestom.server.instance.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record IntBlockProperty(String name, int min, int max) implements BlockProperty<Integer> {

    @Override
    public @Nullable Integer read(Block block) {
        final String value = block.getProperty(this.name);
        if (value == null) {
            return null;
        }
        try {
            return Integer.valueOf(value);
        } catch (Exception _) {
            return null;
        }
    }

    @Override
    public Block write(Block block, Integer value) {
        return block.withProperty(this.name, String.valueOf(value));
    }

    @Override
    public boolean canWrite(Block block, Integer value) {
        return value >= this.min && value <= this.max;
    }
}
