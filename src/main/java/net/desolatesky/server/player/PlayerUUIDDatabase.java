package net.desolatesky.server.player;

import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import net.desolatesky.data.SQLDatabase;
import net.desolatesky.util.Constants;
import net.minestom.server.utils.mojang.MojangUtils;
import org.jspecify.annotations.Nullable;

import java.nio.file.Path;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public final class PlayerUUIDDatabase extends SQLDatabase {

    private static final String CREATE_PLAYER_UUID_TABLE_STATEMENT = """
            CREATE TABLE IF NOT EXISTS player_uuids(
            player_name varchar(16),
            player_uuid varchar(36),
            UNIQUE(player_name, player_uuid)
            );
            """;

    private static final String GET_PLAYER_UUID_STATEMENT = """
            SELECT player_uuid FROM player_uuids WHERE player_name = ?;
            """;

    private static final String GET_PLAYER_NAME_STATEMENT = """
            SELECT player_name FROM player_uuids WHERE player_uuid = ?;
            """;

    private static final String SET_PLAYER_UUID_STATEMENT = """
            INSERT INTO player_uuids (player_name, player_uuid) VALUES (?, ?)
            ON CONFLICT(player_name) DO UPDATE SET +
            player_uuid = ?,
            player_name = ?
            """;


    private final AsyncLoadingCache<String, UUID> uuidCache = Caffeine.newBuilder()
            .expireAfterWrite(Duration.ofMinutes(1))
            .maximumSize(50)
            .buildAsync(this::getPlayerUUIDNow);
    private final AsyncLoadingCache<UUID, String> usernameCache = Caffeine.newBuilder()
            .expireAfterWrite(Duration.ofMinutes(1))
            .maximumSize(100)
            .buildAsync(this::getPlayerUsernameNow);

    public PlayerUUIDDatabase(Path filePath) {
        super(filePath);
    }

    public CompletableFuture<@Nullable UUID> getPlayerUUID(String username) {
        if (Constants.CONSOLE_NAME.equals(username)) {
            return CompletableFuture.completedFuture(Constants.CONSOLE_UUID);
        }
        return this.uuidCache.getIfPresent(username);
    }

    public CompletableFuture<@Nullable String> getPlayerName(UUID playerId) {
        if (Constants.CONSOLE_UUID.equals(playerId)) {
            return CompletableFuture.completedFuture(Constants.CONSOLE_NAME);
        }
        return this.usernameCache.getIfPresent(playerId);
    }

    private UUID getPlayerUUIDNow(String username) {
        return this.executeReadNow(GET_PLAYER_UUID_STATEMENT, preparedStatement -> {
            preparedStatement.setString(1, username);
            try (final ResultSet results = preparedStatement.executeQuery()) {
                if (!results.next()) {
                    try {
                        final UUID uuid = MojangUtils.getUUID(username);
                        this.savePlayerUUID(username, uuid);
                        return uuid;
                    } catch (Exception ignored) {
                        return null;
                    }
                }
                return UUID.fromString(results.getString(1));
            }
        });
    }

    private String getPlayerUsernameNow(UUID uuid) {
        return this.executeReadNow(GET_PLAYER_NAME_STATEMENT, preparedStatement -> {
            preparedStatement.setString(1, uuid.toString());
            try (final ResultSet results = preparedStatement.executeQuery()) {
                if (!results.next()) {
                    return null;
                }
                return results.getString(1);
            }
        });
    }

    public CompletableFuture<Void> savePlayerUUID(String username, UUID uuid) {
        this.uuidCache.put(username, CompletableFuture.completedFuture(uuid));
        return this.executeWrite(SET_PLAYER_UUID_STATEMENT, preparedStatement -> {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, uuid.toString());
            preparedStatement.setString(3, uuid.toString());
            preparedStatement.setString(4, username);
            preparedStatement.executeUpdate();
        });
    }


    @Override
    protected void createTables() throws SQLException {
        try (final PreparedStatement statement = this.getConnection().prepareStatement(CREATE_PLAYER_UUID_TABLE_STATEMENT)) {
            statement.execute();
        }
    }
}
