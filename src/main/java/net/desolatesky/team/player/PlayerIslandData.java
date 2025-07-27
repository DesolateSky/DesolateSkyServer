package net.desolatesky.team.player;

import net.desolatesky.database.MongoCodec;
import net.desolatesky.database.Saveable;
import org.bson.Document;
import org.jetbrains.annotations.UnknownNullability;

import java.time.Instant;
import java.util.UUID;

public final class PlayerIslandData implements Saveable<PlayerIslandData.SaveData> {

    public static final MongoCodec<SaveData, PlayerIslandData, Void> MONGO_CODEC = new MongoCodec<>() {

        @Override
        public void write(PlayerIslandData.SaveData input, Document document) {
            document.append("islandId", input.islandId().toString());
            document.append("invitedBy", input.invitedBy().toString());
            document.append("invitedInstant", input.invitedInstant().toString());
            document.append("roleId", input.roleId());
        }

        @Override
        public @UnknownNullability PlayerIslandData read(Document document, Void context) {
            final UUID islandId = UUID.fromString(document.getString("islandId"));
            final UUID invitedBy = UUID.fromString(document.getString("invitedBy"));
            final Instant invitedInstant = Instant.parse(document.getString("invitedInstant"));
            final String roleId = document.getString("roleId");
            return new PlayerIslandData(islandId, invitedBy, invitedInstant, roleId);
        }

    };

    private final UUID islandId;
    private final UUID invitedBy;
    private final Instant invitedInstant;
    private String roleId;

    public PlayerIslandData(UUID islandId, UUID invitedBy, Instant invitedInstant, String roleId) {
        this.islandId = islandId;
        this.invitedBy = invitedBy;
        this.invitedInstant = invitedInstant;
        this.roleId = roleId;
    }

    public UUID islandId() {
        return this.islandId;
    }

    public UUID invitedBy() {
        return this.invitedBy;
    }

    public Instant invitedInstant() {
        return this.invitedInstant;
    }

    public String roleId() {
        return this.roleId;
    }

    public record SaveData(UUID islandId, UUID invitedBy, Instant invitedInstant, String roleId) {
    }

    @Override
    public SaveData createSnapshot() {
        return new SaveData(this.islandId, this.invitedBy, this.invitedInstant, this.roleId);
    }

}
