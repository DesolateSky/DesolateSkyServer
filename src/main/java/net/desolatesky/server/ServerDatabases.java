package net.desolatesky.server;

import net.desolatesky.server.player.PlayerBanDatabase;
import net.desolatesky.server.player.PlayerUUIDDatabase;

import java.io.IOException;
import java.sql.SQLException;

public record ServerDatabases(PlayerUUIDDatabase playerUUIDDatabase, PlayerBanDatabase banDatabase) {

    public void initializeAll() throws IOException, SQLException {
        this.playerUUIDDatabase.initialize();
        this.banDatabase.initialize();
    }

    public void shutdownAll() throws SQLException {
        this.playerUUIDDatabase.shutdown();
        this.banDatabase.shutdown();
    }
}
