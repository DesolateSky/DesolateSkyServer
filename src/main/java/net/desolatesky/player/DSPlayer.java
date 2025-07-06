package net.desolatesky.player;

import net.desolatesky.DesolateSkyServer;
import net.desolatesky.cooldown.PlayerCooldowns;
import net.desolatesky.instance.DSInstance;
import net.desolatesky.instance.InstancePos;
import net.desolatesky.instance.team.TeamInstance;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;
import net.minestom.server.network.player.GameProfile;
import net.minestom.server.network.player.PlayerConnection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class DSPlayer extends Player {

    private final PlayerCooldowns cooldowns;
    private InstancePos lastLogoutPos;
    private User user;
    private UUID islandId;

    public DSPlayer(@NotNull PlayerConnection playerConnection, @NotNull GameProfile gameProfile) {
        super(playerConnection, gameProfile);
        this.cooldowns = new PlayerCooldowns(DesolateSkyServer.get().cooldownConfig(), new HashMap<>());
    }

    @Override
    public CompletableFuture<Void> setInstance(@NotNull Instance instance) {
        if (!(instance instanceof final DSInstance dsInstance)) {
            return super.setInstance(instance);
        }
        return this.setInstance(instance, dsInstance.getSpawnPoint().pos());
    }

    public boolean hasIsland() {
        return this.islandId != null;
    }

    public void setIsland(TeamInstance instance) {
        this.islandId = instance.getUuid();
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

    public void sendIdMessage(String message) {
        DesolateSkyServer.get().messageHandler().sendMessage(this, message);
    }

    public InstancePos getInstancePosition() {
        return new InstancePos(this.getInstance(), this.getPosition());
    }

    public CompletableFuture<Void> teleport(InstancePos instancePos) {
        final Instance instance = instancePos.instance();
        if (Objects.equals(instance, this.getInstance())) {
            return this.teleport(instancePos.pos());
        }
        return this.setInstance(instance, instancePos.pos());
    }

    public PlayerCooldowns cooldowns() {
        return this.cooldowns;
    }

    public @Nullable UUID islandId() {
        return this.islandId;
    }

    public void setLastLogoutPos(InstancePos pos) {
        this.lastLogoutPos = pos;
    }

    public @Nullable InstancePos lastLogoutPos() {
        return this.lastLogoutPos;
    }

    public DSInstance getDSInstance() {
        return (DSInstance) super.getInstance();
    }

}
