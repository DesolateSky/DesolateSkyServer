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

    private static final String CREATE_PLAYER_DATA_TABLE_QUERY = """
            CREATE TABLE IF NOT EXISTS player_data (
                uuid TEXT PRIMARY KEY,
                name TEXT NOT NULL,
                island_id CHAR(36)
            );
            """;

    private static final String CREATE_LOGOUT_TABLE_QUERY = """
            CREATE TABLE IF NOT EXISTS player_logout_data (
                uuid TEXT PRIMARY KEY,
                world_id CHAR(36) NOT NULL,
                x_pos REAL NOT NULL,
                y_pos REAL NOT NULL,
                z_pos REAL NOT NULL,
                yaw REAL NOT NULL,
                pitch REAL NOT NULL
            );
            """;

    private static final String GET_PLAYER_DATA_AND_LOGOUT_QUERY = """
            SELECT pd.uuid, pd.name, pd.island_id, pld.world_id, pld.x_pos, pld.y_pos, pld.z_pos, pld.yaw, pld.pitch
            FROM player_data pd
            LEFT JOIN player_logout_data pld ON pd.uuid = pld.uuid
            WHERE pd.uuid = ?;
            """;

    private static final String SAVE_PLAYER_LOGOUT_DATA_QUERY = """
            INSERT INTO player_logout_data (uuid, world_id, x_pos, y_pos, z_pos, yaw, pitch)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT(uuid) DO UPDATE SET
                world_id = excluded.world_id,
                x_pos = excluded.x_pos,
                y_pos = excluded.y_pos,
                z_pos = excluded.z_pos,
                yaw = excluded.yaw,
                pitch = excluded.pitch;
            """;

    private static final String SAVE_PLAYER_DATA_QUERY = """
            INSERT INTO player_data (uuid, name, island_id)
            VALUES (?, ?, ?)
            ON CONFLICT(uuid) DO UPDATE SET
                name = excluded.name,
                island_id = excluded.island_id;
            """;

    public PlayerDatabase(Path filePath) {
        super(filePath);
    }

    public @Nullable PlayerData getPlayerData(UUID playerUUID, Function<UUID, Instance> instanceGetter) {
        try (
                final Connection connection = this.getConnection();
                final PreparedStatement statement = connection.prepareStatement(GET_PLAYER_DATA_AND_LOGOUT_QUERY)
        ) {
            statement.setString(1, playerUUID.toString());
            try (final ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return null;
                }

                final String uuidStr = resultSet.getString("uuid");
                final String islandIdStr = resultSet.getString("island_id");
                final UUID islandId = islandIdStr != null ? UUID.fromString(islandIdStr) : null;

                final String worldIdStr = resultSet.getString("world_id");
                final double xPos = resultSet.getDouble("x_pos");
                final double yPos = resultSet.getDouble("y_pos");
                final double zPos = resultSet.getDouble("z_pos");
                final float yaw = resultSet.getFloat("yaw");
                final float pitch = resultSet.getFloat("pitch");

                InstancePos logoutPosition = null;
                if (worldIdStr != null) {
                    final UUID worldId = UUID.fromString(worldIdStr);
                    final Pos position = new Pos(xPos, yPos, zPos, yaw, pitch);
                    logoutPosition = new InstancePos(instanceGetter.apply(worldId), position);
                }

                return new PlayerData(UUID.fromString(uuidStr), islandId, logoutPosition);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void savePlayer(DSPlayer player) {
        try (final Connection connection = this.getConnection()) {
            this.savePlayerData(connection, player);
            final InstancePos lastPosition = player.getInstancePosition();
            this.savePlayerLogoutData(connection, player.getUuid(), lastPosition);
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void savePlayerLogoutData(Connection connection, UUID playerId, InstancePos lastPosition) throws SQLException {
        try (final PreparedStatement statement = connection.prepareStatement(SAVE_PLAYER_LOGOUT_DATA_QUERY)) {
            final String worldId = lastPosition.instance().getUuid().toString();
            final Pos position = lastPosition.pos();
            statement.setString(1, playerId.toString());
            statement.setString(2, worldId);
            statement.setDouble(3, position.x());
            statement.setDouble(4, position.y());
            statement.setDouble(5, position.z());
            statement.setFloat(6, position.yaw());
            statement.setFloat(7, position.pitch());
            statement.executeUpdate();
        }
    }

    private void savePlayerData(Connection connection, DSPlayer player) throws SQLException {
        try (final PreparedStatement statement = connection.prepareStatement(SAVE_PLAYER_DATA_QUERY)) {
            final UUID islandId = player.islandId();
            System.out.println("Island ID for player " + player.getUsername() + ": " + (islandId != null ? islandId.toString() : "null"));
            statement.setString(1, player.getUuid().toString());
            statement.setString(2, player.getUsername());
            statement.setString(3, islandId != null ? islandId.toString() : null);
            statement.executeUpdate();
        }
    }

    @Override
    protected void createTables(Connection connection) throws SQLException {
        try (
                final PreparedStatement createLogoutDataTableStatement = connection.prepareStatement(CREATE_LOGOUT_TABLE_QUERY);
                final PreparedStatement createPlayerDataTableStatement = connection.prepareStatement(CREATE_PLAYER_DATA_TABLE_QUERY)
        ) {
            createLogoutDataTableStatement.execute();
            createPlayerDataTableStatement.execute();
        }
    }
}
