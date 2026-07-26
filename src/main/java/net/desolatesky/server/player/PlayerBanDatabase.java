package net.desolatesky.server.player;

import net.desolatesky.data.SQLDatabase;
import net.desolatesky.util.DateTimeUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.minestom.server.entity.Player;
import org.jspecify.annotations.Nullable;

import java.nio.file.Path;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public final class PlayerBanDatabase extends SQLDatabase {

    private static final String CREATE_PLAYER_BAN_TABLE_STATEMENT = """
            CREATE TABLE IF NOT EXISTS player_bans(
            banned_uuid varchar(36) NOT NULL,
            expiration INTEGER NOT NULL,
            reason text NOT NULL,
            banner_uuid varchar(36) NOT NULL,
            active boolean NOT NULL,
            unbanner varchar(36)
            );
            """;

    private static final String GET_PLAYER_BAN_STATEMENT = """
            SELECT expiration, reason, banner_uuid FROM player_bans
            WHERE
            banned_uuid = ? AND
            active = 1 AND
            ? < player_bans.expiration
            ORDER BY expiration DESC;
            """;

    private static final String INSERT_BAN_STATEMENT = """
            INSERT INTO player_bans (banned_uuid, expiration, reason, banner_uuid, active)
            VALUES (?,?,?,?, true)
            """;

    private static final String UNBAN_PLAYER_STATEMENT = """
            UPDATE player_bans
            SET active = false,
            unbanner = ?
            WHERE banned_uuid = ?
            AND active = true
            """;

    public PlayerBanDatabase(Path filePath) {
        super(filePath);
    }

    public @Nullable Ban getPlayerBanNow(UUID id) {
        return this.executeReadNow(GET_PLAYER_BAN_STATEMENT, preparedStatement -> {
            preparedStatement.setString(1, id.toString());
            preparedStatement.setLong(2, Instant.now().getEpochSecond());
            try (final ResultSet results = preparedStatement.executeQuery()) {
                if (!results.next()) {
                    return null;
                }
                final Instant expiration = Instant.ofEpochSecond(results.getLong(1));
                final String reason = results.getString(2);
                final UUID banner = UUID.fromString(results.getString(3));
                return new Ban(id, expiration, reason, banner);
            }
        });
    }

    public CompletableFuture<Ban> saveBan(UUID banned, Instant expiration, String reason, UUID banner) {
        return this.executeWrite(INSERT_BAN_STATEMENT, preparedStatement -> {
            preparedStatement.setString(1, banned.toString());
            preparedStatement.setLong(2, expiration.getEpochSecond());
            preparedStatement.setString(3, reason);
            preparedStatement.setString(4, banner.toString());
            preparedStatement.executeUpdate();
            return new Ban(banned, expiration, reason, banner);
        });
    }

    public CompletableFuture<Boolean> unbanPlayer(UUID bannedId, UUID unbanner) {
        return this.executeWrite(UNBAN_PLAYER_STATEMENT, preparedStatement -> {
            preparedStatement.setString(1, unbanner.toString());
            preparedStatement.setString(2, bannedId.toString());
            return preparedStatement.executeUpdate() > 0;
        });
    }

    @Override
    protected void createTables() throws SQLException {
        try (final PreparedStatement statement = this.getConnection().prepareStatement(CREATE_PLAYER_BAN_TABLE_STATEMENT)) {
            statement.execute();
        }
    }

    public record Ban(UUID bannedUuid, Instant expiration, String reason, UUID bannerUuid) {

        public void kick(Player banned, String bannerName, String discord) {
            banned.kick(net.kyori.adventure.text.Component.text("You were banned by " + bannerName + " for reason: " + this.reason).appendNewline()
                    .append(net.kyori.adventure.text.Component.text("Ban duration: " + DateTimeUtil.durationToString(Duration.between(Instant.now(), this.expiration()))))
                    .appendNewline()
                    .appendNewline()
                    .append(Component.text("To appeal this ban, create a ticket on the discord server: ")
                            .append(Component.text(discord).clickEvent(ClickEvent.openUrl(discord))))
            );
        }
    }
}
