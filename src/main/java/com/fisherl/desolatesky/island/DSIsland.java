package com.fisherl.desolatesky.island;

import com.fisherl.desolatesky.island.permission.IslandPermission;
import com.fisherl.desolatesky.island.permission.IslandPermissions;
import com.fisherl.desolatesky.island.role.IslandRole;
import com.fisherl.desolatesky.lock.Lockable;
import com.fisherl.desolatesky.world.IslandWorld;
import com.fisherl.desolatesky.world.PlayerWorldGenerator;
import com.fisherl.desolatesky.world.region.SquareRegion;
import net.kyori.adventure.text.Component;
import net.minestom.server.coordinate.Point;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public final class DSIsland implements Island, Lockable {

    private static final Duration ISLAND_INVITE_DURATION = Duration.of(5, TimeUnit.MINUTES.toChronoUnit());

    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    private final UUID id;
    private final Collection<IslandInvite> islandInvites = new ArrayList<>();
    private final Map<UUID, IslandRole> members;
    private final Map<IslandRole, IslandPermissions> permissions;
    private SquareRegion islandRegion;
    private Component displayName;

    public DSIsland(UUID id,
                    Map<UUID, IslandRole> members,
                    Map<IslandRole, IslandPermissions> permissions,
                    Component displayName,
                    SquareRegion islandRegion
    ) {
        this.id = id;
        this.members = members;
        this.permissions = permissions;
        this.displayName = displayName;
        this.islandRegion = islandRegion;
    }

    @Override
    public boolean isMember(UUID playerId) {
        return this.lockRead(() -> this.getIslandRole(playerId) != IslandRole.GUEST);
    }

    @Override
    public IslandRole getIslandRole(UUID playerId) {
        return this.lockRead(() -> this.members.getOrDefault(playerId, IslandRole.GUEST));
    }

    @Override
    public void setIslandRole(UUID playerId, IslandRole role) {
        this.lockWrite(() -> this.members.put(playerId, role));
    }

    @Override
    public SquareRegion worldSize() {
        return this.islandRegion;
    }

    @Override
    public Point getCorePosition() {
        return PlayerWorldGenerator.CORE_POSITION;
    }

    public void invite(UUID member, UUID invited) {
        this.lockWrite(() -> {
            if (this.isInvited(invited)) {
                return;
            }
            this.islandInvites.add(new IslandInvite(member, invited, Instant.now(), ISLAND_INVITE_DURATION));
        });
    }

    @Override
    public boolean isInvited(UUID playerId) {
        return this.lockWrite(() -> {
            this.islandInvites.removeIf(IslandInvite::isExpired);
            return this.islandInvites.stream().anyMatch(invite -> playerId.equals(invite.invited()));
        });
    }

    @Override
    public void acceptInvite(UUID playerId) {
        this.lockWrite(() -> {
            if (this.isMember(playerId)) {
                return;
            }
            if (!this.isInvited(playerId)) {
                return;
            }
            this.islandInvites.removeIf(invite -> playerId.equals(invite.invited()));
            this.members.put(playerId, IslandRole.MEMBER);
        });
    }

    @Override
    public UUID islandId() {
        return this.id;
    }

    @Override
    public Component displayName() {
        return this.lockRead(() -> this.displayName);
    }

    @Override
    public void setName(UUID player, Component newName) {
        this.lockWrite(() -> {
            if (!this.hasPermission(player, IslandPermission.SET_NAME)) {
                return;
            }
            this.displayName = newName;
        });
    }

    @Override
    public boolean hasPermission(UUID player, IslandPermission permission) {
        return this.lockRead(() -> {
            if (this.getIslandRole(player) == IslandRole.OWNER) {
                return true;
            }
            return this.permissions
                    .getOrDefault(this.getIslandRole(player), IslandPermissions.EMPTY)
                    .hasPermission(permission);
        });
    }

    @Override
    public ReadWriteLock lock() {
        return this.lock;
    }

    @Override
    public Point getSpawnPosition() {
        return IslandWorld.DEFAULT_SPAWN_POINT;
    }
}
