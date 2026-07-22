package net.desolatesky.island;

import net.desolatesky.advancement.AdvancementsProgress;
import net.desolatesky.island.invite.IslandInvite;
import net.desolatesky.island.permission.IslandPermission;
import net.desolatesky.island.permission.IslandPermissions;
import net.desolatesky.island.role.IslandRole;
import net.desolatesky.lock.Lockable;
import net.desolatesky.player.DSPlayer;
import net.desolatesky.world.DSWorld;
import net.desolatesky.world.PlayerWorld;
import net.desolatesky.world.WorldType;
import net.desolatesky.world.region.SquareRegion;
import net.kyori.adventure.text.Component;
import net.minestom.server.coordinate.Point;
import org.jetbrains.annotations.NotNullByDefault;
import org.jetbrains.annotations.Unmodifiable;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@NotNullByDefault
public final class DSIsland implements Island, Lockable {

    private static final Duration ISLAND_INVITE_DURATION = Duration.of(5, TimeUnit.MINUTES.toChronoUnit());

    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    private final UUID islandId;
    private final Map<WorldType, UUID> worldIds;
    private final Collection<IslandInvite> islandInvites = new ArrayList<>();
    private final Map<UUID, IslandRole> members;
    private final Map<IslandRole, IslandPermissions> permissions;
    private final AdvancementsProgress advancementsProgress;
    private SquareRegion islandRegion;
    private Component displayName;

    public DSIsland(
            UUID islandId,
            Map<WorldType, UUID> worldIds,
            Map<UUID, IslandRole> members,
            Map<IslandRole, IslandPermissions> permissions,
            AdvancementsProgress advancementsProgress,
            Component displayName,
            SquareRegion islandRegion
    ) {
        this.islandId = islandId;
        this.worldIds = worldIds;
        for (final WorldType type : WorldType.values()) {
            if (!type.hubWorld() && !this.worldIds.containsKey(type)) {
                this.worldIds.put(type, UUID.randomUUID());
            }
        }
        this.members = members;
        this.permissions = permissions;
        this.advancementsProgress = advancementsProgress;
        this.displayName = displayName;
        this.islandRegion = islandRegion;
    }

    public DSIsland(IslandSnapshot snapshot) {
        this.islandId = snapshot.islandId();
        this.worldIds = snapshot.worldIds();
        this.members = snapshot.members();
        this.islandInvites.addAll(snapshot.islandInvites());
        this.permissions = snapshot.permissions();
        this.advancementsProgress = new AdvancementsProgress(snapshot.advancementsProgress(), snapshot.completedAdvancements());
        this.displayName = snapshot.displayName();
        this.islandRegion = snapshot.islandRegion();
    }

    @Override
    public boolean isMember(UUID playerId) {
        return this.lockRead(() -> this.getIslandRole(playerId) != IslandRole.GUEST);
    }

    @Override
    public @Unmodifiable Collection<UUID> getMembers() {
        return this.lockRead(() -> Set.copyOf(this.members.keySet()));
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
        return this.islandId;
    }

    @Override
    public UUID getWorldId(WorldType type) {
        return Objects.requireNonNull(this.worldIds.get(type));
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
    public Point getSpawnPosition() {
        return PlayerWorld.DEFAULT_SPAWN_POINT;
    }

    @Override
    public IslandSnapshot createSnapshot() {
        return this.lockRead(() -> {
            final Map<IslandRole, IslandPermissions> permissionsCopy = new HashMap<>();
            for (final Map.Entry<IslandRole, IslandPermissions> entry : this.permissions.entrySet()) {
                permissionsCopy.put(entry.getKey(), entry.getValue().copy());
            }
            return IslandSnapshot.create(
                    this.islandId,
                    Map.copyOf(this.worldIds),
                    List.copyOf(this.islandInvites),
                    Map.copyOf(this.members),
                    permissionsCopy,
                    this.advancementsProgress.getCurrentAdvancements(),
                    this.advancementsProgress.getCompletedAdvancements(),
                    this.islandRegion,
                    this.displayName
            );
        });
    }

    @Override
    public void onMemberJoin(DSPlayer player, DSWorld world) {
        this.advancementsProgress.addViewer(player);
    }

    @Override
    public void onMemberLeave(DSPlayer player, DSWorld world) {
        this.advancementsProgress.removeViewer(player);
    }

    @Override
    public AdvancementsProgress getAdvancementsProgress() {
        return this.advancementsProgress;
    }

    @Override
    public ReadWriteLock lock() {
        return this.lock;
    }
}
