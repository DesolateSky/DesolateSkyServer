package net.desolatesky.world;

import net.desolatesky.data.type.Data;
import net.desolatesky.data.type.EnumData;

public enum WorldType {

    LOBBY(true),
    ISLAND(false),
    VOID(false);

    public static final Data<WorldType> DATA = EnumData.createEnumData(WorldType.class);

    private final boolean hubWorld;

    WorldType(boolean hubWorld) {
        this.hubWorld = hubWorld;
    }

    public boolean hubWorld() {
        return this.hubWorld;
    }
}
