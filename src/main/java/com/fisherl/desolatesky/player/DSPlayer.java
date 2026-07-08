package com.fisherl.desolatesky.player;

import com.fisherl.desolatesky.lock.Lockable;
import com.fisherl.desolatesky.permission.Permission;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.minestom.server.entity.attribute.Attribute;
import net.minestom.server.network.player.GameProfile;
import net.minestom.server.network.player.PlayerConnection;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public final class DSPlayer extends net.minestom.server.entity.Player implements Lockable {

    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private @Nullable UUID islandId;
    private User user;
    private boolean creatingIsland = false;

    public DSPlayer(PlayerConnection playerConnection, GameProfile gameProfile) {
        super(playerConnection, gameProfile);
        this.getAttribute(Attribute.BLOCK_BREAK_SPEED).setBaseValue(0);
    }

    public boolean hasPermission(String permission) {
        if (this.user == null) {
            this.user = LuckPermsProvider.get().getUserManager().getUser(this.getUuid());
        }
        if (this.user == null) {
            return false;
        }
        return this.user.getCachedData().getPermissionData().checkPermission(permission).asBoolean();
    }

    public boolean hasPermission(Permission permission) {
        return this.hasPermission(permission.path());
    }

    public Optional<UUID> getIslandId() {
        return this.lockRead(() -> Optional.ofNullable(this.islandId));
    }

    public boolean hasIsland() {
        return this.lockRead(() -> this.islandId != null);
    }

    public void setIslandId(UUID islandId) {
        this.lockWrite(() -> this.islandId = islandId);
    }

    public void setCreatingIsland(boolean creatingIsland) {
        this.creatingIsland = creatingIsland;
    }

    public boolean isCreatingIsland() {
        return this.creatingIsland;
    }

    @Override
    public ReadWriteLock lock() {
        return this.lock;
    }
}

