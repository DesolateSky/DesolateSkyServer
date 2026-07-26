package net.desolatesky.island.role;

import net.desolatesky.data.type.Data;
import net.desolatesky.data.type.EnumData;

public enum IslandRole {

    GUEST(0),
    MEMBER(1),
    OWNER(2);

    public static final Data<IslandRole> DATA = EnumData.createEnumData(IslandRole.class);

    private final int priority;

    IslandRole(int priority) {
        this.priority = priority;
    }

    public boolean isHigherThan(IslandRole other) {
        return this.priority > other.priority;
    }
}
