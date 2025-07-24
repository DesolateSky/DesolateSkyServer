package net.desolatesky.player;

import net.desolatesky.player.database.PlayerData;
import net.desolatesky.player.database.PlayerDatabaseAccessor;
import net.minestom.server.instance.Instance;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.UUID;
import java.util.function.Function;

public final class DSPlayerManager {

    private final PlayerDatabaseAccessor playerDatabase;

    private DSPlayerManager(PlayerDatabaseAccessor playerDatabase) {
        this.playerDatabase = playerDatabase;
    }

    public PlayerDatabaseAccessor playerDatabase() {
        return this.playerDatabase;
    }

    public @Nullable PlayerData loadPlayerData(UUID playerUuid) {
        return this.playerDatabase.load(playerUuid);
    }

    public void savePlayer(DSPlayer player) {
        final PlayerData playerData = player.playerData();
        playerData.prepareForSave(player);
        this.playerDatabase.save(player.getUuid(), player);
    }

    public static DSPlayerManager create(PlayerDatabaseAccessor playerDatabase) {
        return new DSPlayerManager(playerDatabase);
    }

}
