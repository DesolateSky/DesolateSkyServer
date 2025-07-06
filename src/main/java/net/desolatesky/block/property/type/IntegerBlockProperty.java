package net.desolatesky.block.property.type;

import net.minestom.server.instance.block.Block;

public final class IntegerBlockProperty implements BlockProperty<Integer> {

    private final String name;

    public IntegerBlockProperty(String name) {
        this.name = name;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public Block set(Block block, Integer value) {
        return block.withProperty(this.name, value.toString());
    }

    @Override
    public Integer get(Block block) {
        final String value = block.getProperty(this.name);
        if (value == null) {
            return null;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

}
