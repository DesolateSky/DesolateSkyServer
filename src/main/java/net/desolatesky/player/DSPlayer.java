package net.desolatesky.player;

import net.desolatesky.DesolateSkyServer;
import net.desolatesky.cooldown.CooldownConfig;
import net.desolatesky.cooldown.PlayerCooldowns;
import net.desolatesky.database.MongoCodec;
import net.desolatesky.entity.DSEntity;
import net.desolatesky.entity.EntityKey;
import net.desolatesky.entity.EntityKeys;
import net.desolatesky.instance.DSInstance;
import net.desolatesky.instance.InstancePoint;
import net.desolatesky.instance.team.TeamInstance;
import net.desolatesky.inventory.InventoryHolder;
import net.desolatesky.message.MessageKey;
import net.desolatesky.player.database.PlayerData;
import net.desolatesky.util.Namespace;
import net.kyori.adventure.key.Key;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.PlayerHand;
import net.minestom.server.entity.attribute.Attribute;
import net.minestom.server.instance.Instance;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.network.player.GameProfile;
import net.minestom.server.network.player.PlayerConnection;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class DSPlayer extends Player implements DSEntity, InventoryHolder {

    public static void acquireAndSync(Player player, Consumer<DSPlayer> consumer) {
        player.acquirable().sync(p -> consumer.accept((DSPlayer) p));
    }

    public static final MongoCodec<DSPlayer, PlayerData, MongoContext> MONGO_CODEC = new MongoCodec<>() {
        @Override
        public void write(DSPlayer input, Document document) {
            PlayerData.MONGO_CODEC.write(input.playerData(), document);
        }

        @Override
        public @UnknownNullability PlayerData read(Document document, MongoContext context) {
            return PlayerData.MONGO_CODEC.read(document, new PlayerData.MongoContext(context.cooldownConfig()));
        }
    };

    public record MongoContext(CooldownConfig cooldownConfig) {
    }

    private final DesolateSkyServer server;
    private final PlayerData playerData;
    private final PlayerProfile profile;
    private User user;

    public DSPlayer(@NotNull PlayerConnection playerConnection, @NotNull GameProfile gameProfile, DesolateSkyServer server, PlayerData playerData) {
        super(playerConnection, gameProfile);
        this.server = server;
        this.playerData = playerData;
        this.profile = new PlayerProfile(this.getUuid(), this.getUsername());
        this.getAttribute(Attribute.BLOCK_BREAK_SPEED).setBaseValue(0);
        this.getAttribute(Attribute.ENTITY_INTERACTION_RANGE).setBaseValue(5);
    }

    @Override
    public CompletableFuture<Void> setInstance(@NotNull Instance instance) {
        if (!(instance instanceof final DSInstance dsInstance)) {
            return super.setInstance(instance);
        }
        return this.setInstance(instance, dsInstance.getSpawnPointFor(this).pos());
    }

    public boolean hasIsland() {
        return this.playerData.islandId() != null;
    }

    public void setIsland(TeamInstance instance) {
        this.setIslandId(instance.getUuid());
    }

    public void setIslandId(@Nullable UUID islandId) {
        this.playerData.setIslandId(islandId);
    }

    public boolean hasPermission(String permission) {
        if (this.user == null) {
            this.user = LuckPermsProvider.get().getUserManager().getUser(this.getUuid());
        }
        if (this.user == null) {
            return false;
        }
        return this.user.getCachedData().getPermissionData().checkPermission(permission).asBoolean();
    }

    public void sendIdMessage(MessageKey message) {
        this.server.messageHandler().sendMessage(this, message);
    }

    @Override
    public InstancePoint<Pos> getInstancePosition() {
        return new InstancePoint<>(this.getInstance(), this.getPosition());
    }

    public CompletableFuture<Void> teleport(InstancePoint<Pos> instancePoint) {
        final Instance instance = instancePoint.instance();
        if (Objects.equals(instance, this.getInstance())) {
            return this.teleport(instancePoint.pos());
        }
        return this.setInstance(instance, instancePoint.pos());
    }

    public PlayerCooldowns cooldowns() {
        return this.playerData.cooldowns();
    }

    public @Nullable UUID islandId() {
        return this.playerData.islandId();
    }

    public void setLastLogoutPos(InstancePoint<Pos> pos) {
        this.playerData.setLastLogoutPos(pos.pos());
        this.playerData.setLastLogoutInstanceId(pos.instance().getUuid());
    }

    @Override
    public DSInstance getDSInstance() {
        return (DSInstance) super.getInstance();
    }

    @Override
    public void onClick(DSEntity clicker, Point interactionPoint, PlayerHand hand) {

    }

    @Override
    public void onPunch(DSEntity attacker) {

    }

    public DesolateSkyServer desolateSkyServer() {
        return this.server;
    }

    @Override
    public EntityKey key() {
        return EntityKeys.PLAYER_ENTITY;
    }

    public PlayerData playerData() {
        return this.playerData;
    }

    public PlayerProfile profile() {
        return this.profile;
    }

}
