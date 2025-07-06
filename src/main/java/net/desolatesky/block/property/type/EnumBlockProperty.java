package net.desolatesky.block.property.type;

import net.minestom.server.instance.block.Block;
import org.jetbrains.annotations.Nullable;

public class EnumBlockProperty<E extends Enum<E>> implements BlockProperty<E> {

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
    public Block set(Block block, E value) {
        if (!this.enumClass.isInstance(value)) {
            throw new IllegalArgumentException("Value must be an instance of " + this.enumClass.getName());
        }
        return block.withProperty(this.name, value.name().toLowerCase());
    }

    @Override
    public @Nullable E get(Block block) {
        final String value = block.getProperty(this.name);
        if (value == null) {
            return null;
        }
        try {
            return Enum.valueOf(this.enumClass, value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

}
