package net.desolatesky.island;

import net.desolatesky.data.definition.DataTranslator;
import net.desolatesky.island.data.IslandDataDefinitionV1;
import net.desolatesky.island.invite.IslandInvite;
import net.desolatesky.island.permission.IslandPermissions;
import net.desolatesky.island.role.IslandRole;
import net.desolatesky.world.WorldType;
import net.desolatesky.world.region.SquareRegion;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface IslandSnapshot {

    DataTranslator<IslandSnapshot> DATA_TRANSLATOR = new DataTranslator<>(List.of(
            new IslandDataDefinitionV1()
    ));

    UUID islandId();

    @Unmodifiable Map<WorldType, UUID> worldIds();

    @Unmodifiable
    List<IslandInvite> islandInvites();

    @Unmodifiable
    Map<UUID, IslandRole> members();

    @Unmodifiable
    Map<IslandRole, IslandPermissions> permissions();

    SquareRegion islandRegion();

    Component displayName();

    static IslandSnapshot create(
            UUID islandId,
            @Unmodifiable Map<WorldType, UUID> worldIds,
            @Unmodifiable List<IslandInvite> islandInvites,
            @Unmodifiable Map<UUID, IslandRole> members,
            @Unmodifiable Map<IslandRole, IslandPermissions> permissions,
            SquareRegion islandRegion,
            Component displayName
    ) {
        return new Impl(islandId, worldIds, islandInvites, members, permissions, islandRegion, displayName);
    }

    record Impl(
            UUID islandId,
            @Unmodifiable Map<WorldType, UUID> worldIds,
            @Unmodifiable List<IslandInvite> islandInvites,
            @Unmodifiable Map<UUID, IslandRole> members,
            @Unmodifiable Map<IslandRole, IslandPermissions> permissions,
            SquareRegion islandRegion,
            Component displayName
    ) implements IslandSnapshot {
    }
}
