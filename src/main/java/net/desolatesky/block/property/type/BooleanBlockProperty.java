package net.desolatesky.block.property.type;

import net.minestom.server.instance.block.Block;
import org.jetbrains.annotations.Nullable;

public final class BooleanBlockProperty implements BlockProperty<Boolean> {

    private final String name;

    public BooleanBlockProperty(String name) {
        this.name = name;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public Block set(Block block, Boolean value) {
        return block.withProperty(this.name, value ? "true" : "false");
    }

    @Override
    public @Nullable Boolean get(Block block) {
        final String value = block.getProperty(this.name);
        if (value == null) {
            return null;
        }
        return value.equals("true");
    }

}
