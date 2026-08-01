package net.desolatesky.island.permission;

import net.desolatesky.data.type.Data;
import net.desolatesky.data.type.EnumData;

public enum IslandPermission {
    INVITE,
    KICK,
    INTERACT_VOID_CORE,
    SET_NAME,
    DROP_ITEMS,
    BREAK_BLOCK,
    PLACE_BLOCK,
    USE_INVENTORY;

    public static final Data<IslandPermission> DATA = EnumData.createEnumData(IslandPermission.class);
}
