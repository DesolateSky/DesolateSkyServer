package net.desolatesky.player.database;

import net.desolatesky.DesolateSkyServer;
import net.desolatesky.cooldown.PlayerCooldowns;
import net.desolatesky.player.DSPlayer;
import net.desolatesky.player.DSPlayerManager;
import net.minestom.server.network.PlayerProvider;
import net.minestom.server.network.player.GameProfile;
import net.minestom.server.network.player.PlayerConnection;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ConcurrentHashMap;

public final class PlayerLoader implements PlayerProvider {

    private final DesolateSkyServer server;
    private final DSPlayerManager DSPlayerManager;

    public PlayerLoader(DesolateSkyServer server, DSPlayerManager DSPlayerManager) {
        this.server = server;
        this.DSPlayerManager = DSPlayerManager;
    }

    @Override
    public @NotNull DSPlayer createPlayer(@NotNull PlayerConnection connection, @NotNull GameProfile gameProfile) {
        PlayerData playerData = this.DSPlayerManager.loadPlayerData(gameProfile.uuid());
        if (playerData == null) {
            playerData = PlayerData.createNewPlayer(new PlayerCooldowns(this.server.cooldownConfig(), new ConcurrentHashMap<>()));
        }
        final DSPlayer player = new DSPlayer(connection, gameProfile, this.server, playerData);
        playerData.applyToPlayer(player);
        return player;
    }

}
