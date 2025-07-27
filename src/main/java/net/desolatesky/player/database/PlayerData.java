package net.desolatesky.player.database;

import com.google.gson.JsonParser;
import net.desolatesky.cooldown.CooldownConfig;
import net.desolatesky.cooldown.PlayerCooldowns;
import net.desolatesky.database.MongoCodec;
import net.desolatesky.database.codec.InstantCodec;
import net.desolatesky.player.DSPlayer;
import net.minestom.server.codec.Transcoder;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.inventory.PlayerInventory;
import net.minestom.server.item.ItemStack;
import org.bson.Document;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public final class PlayerData {

    public static final MongoCodec<PlayerData, PlayerData, MongoContext> MONGO_CODEC = new MongoCodec<>() {
        @Override
        public void write(PlayerData input, Document document) {
            final Document cooldownsDocument = new Document();
            PlayerCooldowns.MONGO_CODEC.write(input.cooldowns, cooldownsDocument);
            document.append("playerCooldowns", cooldownsDocument);
            document.append("firstJoinTime", input.firstJoinTime);

            final ItemStack[] itemStacks = input.savedInventory;
            final List<String> result = Arrays.stream(itemStacks)
                    .map(itemStack -> ItemStack.CODEC.encode(Transcoder.JSON, itemStack).orElseThrow().toString())
                    .toList();
            document.append("inventory", result);

            if (input.lastLogoutPos != null) {
                final Document posDocument = new Document();
                posDocument.append("x", input.lastLogoutPos.x());
                posDocument.append("y", input.lastLogoutPos.y());
                posDocument.append("z", input.lastLogoutPos.z());
                posDocument.append("pitch", input.lastLogoutPos.pitch());
                posDocument.append("yaw", input.lastLogoutPos.yaw());
                document.append("lastLogoutPos", posDocument);
            }
            if (input.lastLogoutInstanceId != null) {
                document.append("lastLogoutInstanceId", input.lastLogoutInstanceId);
            }
            if (input.islandId != null) {
                document.append("islandId", input.islandId);
            }
        }

        @Override
        public @UnknownNullability PlayerData read(Document document, MongoContext context) {
            final Document cooldownsDocument = document.get("playerCooldowns", Document.class);
            final PlayerCooldowns cooldowns = PlayerCooldowns.MONGO_CODEC.read(cooldownsDocument, context.cooldownConfig());
            final Instant firstJoinTime = InstantCodec.decode(document.get("firstJoinTime", Document.class));
            final List<String> inventoryJson = document.getList("inventory", String.class);
            final ItemStack[] savedInventory;
            if (inventoryJson != null) {
                savedInventory = inventoryJson.stream()
                        .map(json -> ItemStack.CODEC.decode(Transcoder.JSON, JsonParser.parseString(json)).orElseThrow())
                        .toArray(ItemStack[]::new);
            } else {
                savedInventory = new PlayerInventory().getItemStacks();
            }

            final Document posDocument = document.get("lastLogoutPos", Document.class);
            final Pos lastLogoutPos;

            if (posDocument != null) {
                final double x = posDocument.getDouble("x");
                final double y = posDocument.getDouble("y");
                final double z = posDocument.getDouble("z");
                final Double pitch = (Double) posDocument.getOrDefault("pitch", 0.0);
                final Double yaw = (Double) posDocument.getOrDefault("yaw", 0.0);
                lastLogoutPos = new Pos(x, y, z, yaw.floatValue(), pitch.floatValue());
            } else {
                lastLogoutPos = null;
            }
            final UUID lastLogoutInstanceId = document.get("lastLogoutInstanceId", UUID.class);
            final UUID islandId = document.get("islandId", UUID.class);
            return new PlayerData(cooldowns, firstJoinTime, savedInventory, lastLogoutPos, lastLogoutInstanceId, islandId, true);
        }

    };

    public record MongoContext(CooldownConfig cooldownConfig) {
    }

    public static PlayerData createNewPlayer(PlayerCooldowns cooldowns) {
        return new PlayerData(cooldowns, Instant.now(), new PlayerInventory().getItemStacks(), null, null, null, false);
    }

    private final PlayerCooldowns cooldowns;
    private final Instant firstJoinTime;
    private ItemStack[] savedInventory;
    private @Nullable Pos lastLogoutPos;
    private @Nullable UUID lastLogoutInstanceId;
    private @Nullable UUID islandId;
    private boolean playedBefore = false;

    public PlayerData(PlayerCooldowns cooldowns, Instant firstJoinTime, ItemStack[] savedInventory, @Nullable Pos lastLogoutPos, @Nullable UUID lastLogoutInstanceId, @Nullable UUID islandId, boolean playedBefore) {
        this.cooldowns = cooldowns;
        this.firstJoinTime = firstJoinTime;
        this.savedInventory = savedInventory;
        this.lastLogoutPos = lastLogoutPos;
        this.lastLogoutInstanceId = lastLogoutInstanceId;
        this.islandId = islandId;
        this.playedBefore = playedBefore;
    }

    public PlayerCooldowns cooldowns() {
        return this.cooldowns;
    }

    public Instant firstJoinTime() {
        return this.firstJoinTime;
    }

    public @Nullable Pos lastLogoutPos() {
        return this.lastLogoutPos;
    }

    public void setLastLogoutPos(@Nullable Pos lastLogoutPos) {
        this.lastLogoutPos = lastLogoutPos;
    }

    public @Nullable UUID lastLogoutInstanceId() {
        return this.lastLogoutInstanceId;
    }

    public void setLastLogoutInstanceId(@Nullable UUID lastLogoutInstanceId) {
        this.lastLogoutInstanceId = lastLogoutInstanceId;
    }

    public @Nullable UUID islandId() {
        return this.islandId;
    }

    public void setIslandId(@Nullable UUID islandId) {
        this.islandId = islandId;
    }

    public boolean playedBefore() {
        return this.playedBefore;
    }

    public void prepareForSave(DSPlayer player) {
        this.lastLogoutPos = player.getPosition();
        this.lastLogoutInstanceId = player.getInstance().getUuid();
        LoggerFactory.getLogger(PlayerData.class).info("Preparing player data for save: {} at position {} in instance {}", player.getUuid(), this.lastLogoutPos, this.lastLogoutInstanceId);
        this.savedInventory = player.getInventory().getItemStacks();
    }

    public void applyToPlayer(DSPlayer player) {
        final PlayerInventory inventory = player.getInventory();
        for (int i = 0; i < this.savedInventory.length; i++) {
            final ItemStack itemStack = this.savedInventory[i];
            inventory.setItemStack(i, itemStack);
        }
    }

}
