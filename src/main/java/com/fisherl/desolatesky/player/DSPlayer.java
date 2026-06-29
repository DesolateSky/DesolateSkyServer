package com.fisherl.desolatesky.player;

import com.fisherl.desolatesky.permission.Permission;
import net.luckperms.api.LuckPermsProvider;
import net.minestom.server.entity.attribute.Attribute;
import net.minestom.server.network.player.GameProfile;
import net.minestom.server.network.player.PlayerConnection;
import net.luckperms.api.model.user.User;

public final class DSPlayer extends net.minestom.server.entity.Player {

    private User user;

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

}

