package net.desolatesky.block;

import net.desolatesky.block.property.SlabType;
import net.desolatesky.block.property.type.BooleanBlockProperty;
import net.desolatesky.block.property.type.EnumBlockProperty;
import net.minestom.server.utils.Direction;

public final class BlockProperties {

    private BlockProperties() {
        throw new UnsupportedOperationException();
    }

    public static final BooleanBlockProperty OPEN = new BooleanBlockProperty("open");
    public static final EnumBlockProperty<Direction> FACING = new EnumBlockProperty<>("facing", Direction.class);
    public static final EnumBlockProperty<SlabType> SLAB_TYPE = new EnumBlockProperty<>("type", SlabType.class);

}
