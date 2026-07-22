package net.desolatesky.island;

import net.desolatesky.advancement.AdvancementsProgress;
import net.desolatesky.island.permission.IslandPermission;
import net.desolatesky.island.role.IslandRole;
import net.desolatesky.player.DSPlayer;
import net.desolatesky.world.DSWorld;
import net.desolatesky.world.WorldType;
import net.desolatesky.world.region.SquareRegion;
import net.kyori.adventure.text.Component;
import net.minestom.server.coordinate.Point;
import org.jetbrains.annotations.NotNullByDefault;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collection;
import java.util.UUID;

@NotNullByDefault
public interface Island {

    UUID islandId();

    UUID getWorldId(WorldType type);

    Component displayName();

    boolean isMember(UUID playerId);

    @Unmodifiable
    Collection<UUID> getMembers();

    IslandRole getIslandRole(UUID playerId);

    void setIslandRole(UUID playerId, IslandRole role);

    void invite(UUID islandMember, UUID invitedPlayer);

    boolean isInvited(UUID playerId);

    void acceptInvite(UUID playerId);

    void setName(UUID player, Component newName);

    boolean hasPermission(UUID player, IslandPermission permission);

    Point getSpawnPosition();

    SquareRegion worldSize();

    IslandSnapshot createSnapshot();

    void onMemberJoin(DSPlayer player, DSWorld world);

    void onMemberLeave(DSPlayer player, DSWorld world);

    AdvancementsProgress getAdvancementsProgress();
}
