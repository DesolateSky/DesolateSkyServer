package net.desolatesky.world.region;

import net.desolatesky.data.type.Data;
import net.desolatesky.data.type.EnumData;

public enum RegionType {

    SQUARE,
    RECTANGULAR;

    public static final Data<RegionType> DATA = EnumData.createEnumData(RegionType.class);

}
