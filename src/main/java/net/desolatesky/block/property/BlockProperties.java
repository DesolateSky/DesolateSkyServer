package net.desolatesky.block.property;

import net.desolatesky.block.enums.SlabType;

public final class BlockProperties {

    private BlockProperties() {}

    public static final IntBlockProperty FARMLAND_MOISTURE_PROPERTY = new IntBlockProperty("moisture",0, 7);
    public static final IntBlockProperty COMPOSTER_LEVEL_PROPERTY = new IntBlockProperty("level", 0, 8);
    public static final EnumBlockProperty<SlabType> SLAB_TYPE_PROPERTY = new EnumBlockProperty<>("type", SlabType.class);
    public static final BooleanBlockProperty NORTH = new BooleanBlockProperty("north");
    public static final BooleanBlockProperty SOUTH = new BooleanBlockProperty("south");
    public static final BooleanBlockProperty EAST = new BooleanBlockProperty("east");
    public static final BooleanBlockProperty WEST = new BooleanBlockProperty("west");
    public static final BooleanBlockProperty UP = new BooleanBlockProperty("up");

}
