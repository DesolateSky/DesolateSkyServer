package net.desolatesky.player.database;

import net.desolatesky.database.sqlite.SQLiteDatabase;
import net.desolatesky.instance.InstancePos;
import net.desolatesky.player.DSPlayer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.Instance;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.function.Function;

public final class PlayerDatabase extends SQLiteDatabase {

    private static final String CREATE_TABLE_QUERY = """
            CREATE TABLE IF NOT EXISTS player_logout_data (
                uuid TEXT PRIMARY KEY,
                name TEXT NOT NULL,
                world_id CHAR(36) NOT NULL,
                x_pos REAL NOT NULL,
                y_pos REAL NOT NULL,
                z_pos REAL NOT NULL,
                yaw REAL NOT NULL,
                pitch REAL NOT NULL
            );
            """;

    private static final String GET_PLAYER_WORLD_QUERY = """
            SELECT world_id, x_pos, y_pos, z_pos, yaw, pitch
            FROM player_logout_data
            WHERE uuid = ?;
            """;

    private static final String SAVE_PLAYER_QUERY = """
            INSERT INTO player_logout_data (uuid, name, world_id, x_pos, y_pos, z_pos, yaw, pitch)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT(uuid) DO UPDATE SET
                name = excluded.name,
                world_id = excluded.world_id,
                x_pos = excluded.x_pos,
                y_pos = excluded.y_pos,
                z_pos = excluded.z_pos,
                yaw = excluded.yaw,
                pitch = excluded.pitch;
            """;

    public PlayerDatabase(Path filePath) {
        super(filePath);
    }

    public @Nullable InstancePos getPlayerLogoutPosition(UUID playerUUID, Function<UUID, Instance> instanceGetter) {
        try (final Connection connection = this.getConnection(); final PreparedStatement statement = connection.prepareStatement(GET_PLAYER_WORLD_QUERY)) {
            statement.setString(1, playerUUID.toString());
            try (final ResultSet results = statement.executeQuery()) {
                if (results.next()) {
                    final String worldId = results.getString("world_id");
                    final double xPos = results.getDouble("x_pos");
                    final double yPos = results.getDouble("y_pos");
                    final double zPos = results.getDouble("z_pos");
                    final float yaw = results.getFloat("yaw");
                    final float pitch = results.getFloat("pitch");
                    final Instance instance = instanceGetter.apply(UUID.fromString(worldId));
                    return new InstancePos(instance, new Pos(xPos, yPos, zPos, yaw, pitch));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void savePlayer(DSPlayer player) {
        try (final Connection connection = this.getConnection(); final PreparedStatement statement = connection.prepareStatement(SAVE_PLAYER_QUERY)) {
            final UUID playerId = player.getUuid();
            final String playerName = player.getUsername();
            final InstancePos lastPosition = player.getInstancePosition();
            final String worldId = lastPosition.instance().getUuid().toString();
            final Pos position = lastPosition.pos();
            statement.setString(1, playerId.toString());
            statement.setString(2, playerName);
            statement.setString(3, worldId);
            statement.setDouble(4, position.x());
            statement.setDouble(5, position.y());
            statement.setDouble(6, position.z());
            statement.setFloat(7, position.yaw());
            statement.setFloat(8, position.pitch());
            statement.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void createTables(Connection connection) throws SQLException {
        try (final PreparedStatement createTableStatement = connection.prepareStatement(CREATE_TABLE_QUERY)) {
            createTableStatement.executeUpdate();
        }
    }
}
