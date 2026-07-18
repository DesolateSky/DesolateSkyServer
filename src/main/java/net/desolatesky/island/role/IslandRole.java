package net.desolatesky.island.role;

import net.desolatesky.data.type.Data;
import net.desolatesky.data.type.EnumData;

public enum IslandRole {

    GUEST,
    MEMBER,
    OWNER;

    public static final Data<IslandRole> DATA = EnumData.createEnumData(IslandRole.class);

}
