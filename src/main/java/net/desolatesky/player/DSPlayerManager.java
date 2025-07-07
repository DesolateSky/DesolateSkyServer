package net.desolatesky.player;

import net.desolatesky.player.database.PlayerData;
import net.desolatesky.player.database.PlayerDatabase;
import net.minestom.server.instance.Instance;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.UUID;
import java.util.function.Function;

public final class DSPlayerManager {

    private final PlayerDatabase playerDatabase;

    private DSPlayerManager(PlayerDatabase playerDatabase) {
        this.playerDatabase = playerDatabase;
    }

    public PlayerDatabase playerDatabase() {
        return this.playerDatabase;
    }

    public @Nullable PlayerData loadPlayerData(UUID playerUuid, Function<UUID, Instance> instanceGetter) {
        return this.playerDatabase.getPlayerData(playerUuid, instanceGetter);
    }

    public void savePlayer(DSPlayer player) {
        this.playerDatabase.savePlayer(player);
    }

    public static DSPlayerManager create(Path databasePath) {
        final PlayerDatabase playerDatabase = new PlayerDatabase(databasePath);
        playerDatabase.init();
        return new DSPlayerManager(playerDatabase);
    }

}
