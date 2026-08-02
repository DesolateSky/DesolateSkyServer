package net.desolatesky.block.property;

import net.minestom.server.instance.block.Block;
import org.jspecify.annotations.Nullable;

public class EnumBlockProperty<E extends Enum<E>> implements BlockProperty<E>{

    private final String name;
    private final Class<E> enumClass;

    public EnumBlockProperty(String name, Class<E> enumClass) {
        this.name = name;
        this.enumClass = enumClass;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public @Nullable E read(Block block) {
        final String value = block.getProperty(this.name);
        if (value == null) {
            return null;
        }
        return Enum.valueOf(this.enumClass, value.toUpperCase());
    }

    @Override
    public Block write(Block block, E value) {
        return block.withProperty(this.name, value.name().toLowerCase());
    }

    @Override
    public boolean canWrite(Block block, E value) {
        return true;
    }
}
