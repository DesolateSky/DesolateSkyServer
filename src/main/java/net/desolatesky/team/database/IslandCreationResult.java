package net.desolatesky.team.database;

import net.desolatesky.instance.team.TeamInstance;
import net.desolatesky.team.IslandTeam;
import org.jetbrains.annotations.Nullable;

public record IslandCreationResult(@Nullable IslandTeam islandTeam, @Nullable TeamInstance islandInstance, Type type) {

    public IslandCreationResult(Type type) {
        this(null, null, type);
    }

    public static final IslandCreationResult INVALID_NAME = new IslandCreationResult(Type.INVALID_NAME);
    public static final IslandCreationResult ALREADY_EXISTS = new IslandCreationResult(Type.ALREADY_EXISTS);
    public static final IslandCreationResult DATABASE_ERROR = new IslandCreationResult(Type.DATABASE_ERROR);
    public static final IslandCreationResult ALREADY_IN_TEAM = new IslandCreationResult(Type.ALREADY_IN_TEAM);
    public static final IslandCreationResult ON_COOLDOWN = new IslandCreationResult(Type.ON_COOLDOWN);

    public static IslandCreationResult success(IslandTeam team) {
        return new IslandCreationResult(team, null, Type.SUCCESS);
    }

    public static IslandCreationResult createdInstance(IslandTeam team, TeamInstance instance) {
        return new IslandCreationResult(team, instance, Type.CREATED_INSTANCE);
    }

    public boolean isInvalid() {
        return this.type != Type.SUCCESS;
    }

    public enum Type {

        SUCCESS,
        CREATED_INSTANCE,
        ALREADY_EXISTS,
        INVALID_NAME,
        DATABASE_ERROR,
        ALREADY_IN_TEAM,
        ON_COOLDOWN

    }

}
