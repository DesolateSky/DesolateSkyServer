package com.fisherl.desolatesky.island;

import com.fisherl.desolatesky.entity.EntityFactory;
import com.fisherl.desolatesky.island.permission.IslandPermission;
import com.fisherl.desolatesky.island.role.IslandRole;
import com.fisherl.desolatesky.world.region.SquareRegion;
import net.kyori.adventure.text.Component;
import net.minestom.server.coordinate.Point;

import java.util.UUID;

public interface Island {

    UUID islandId();

    Component displayName();

    boolean isMember(UUID playerId);

    IslandRole getIslandRole(UUID playerId);

    void setIslandRole(UUID playerId, IslandRole role);

    void invite(UUID islandMember, UUID invitedPlayer);

    boolean isInvited(UUID playerId);

    void acceptInvite(UUID playerId);

    void setName(UUID player, Component newName);

    boolean hasPermission(UUID player, IslandPermission permission);

    Point getSpawnPosition();

    SquareRegion worldSize();

    Point getCorePosition();

}
