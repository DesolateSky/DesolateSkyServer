package net.desolatesky.world.region;

import net.desolatesky.data.type.Data;
import net.desolatesky.data.type.EnumData;

public enum PointType {

    VEC,
    BLOCK_VEC,
    POS;

    public static final Data<PointType> DATA = EnumData.createEnumData(PointType.class);

}
