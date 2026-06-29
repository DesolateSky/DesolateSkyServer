package com.fisherl.desolatesky.block.property;

import net.minestom.server.instance.block.Block;

import java.util.Optional;

public record IntBlockProperty(String name, int min, int max) implements BlockProperty<Integer> {

    @Override
    public Optional<Integer> read(Block block) {
        final String value = block.getProperty(this.name);
        if (value == null) {
            return Optional.empty();
        }
        try {
            return Optional.of(Integer.valueOf(value));
        } catch (Exception _) {
            return Optional.empty();
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
