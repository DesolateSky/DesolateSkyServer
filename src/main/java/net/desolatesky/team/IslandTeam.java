package net.desolatesky.team;

import net.desolatesky.DesolateSkyServer;
import net.desolatesky.database.MongoCodec;
import net.desolatesky.database.Saveable;
import net.desolatesky.message.MessageHandler;
import net.desolatesky.message.Messages;
import net.desolatesky.player.DSPlayer;
import net.desolatesky.team.invite.InviteManager;
import net.desolatesky.team.player.PlayerIslandData;
import net.desolatesky.team.role.RolePermissionType;
import net.desolatesky.team.role.RolePermissionsRegistry;
import net.desolatesky.team.role.TeamRole;
import net.desolatesky.team.role.TeamRoles;
import net.kyori.adventure.key.Key;
import net.minestom.server.MinecraftServer;
import org.bson.Document;
import org.jetbrains.annotations.UnknownNullability;
import org.jetbrains.annotations.UnmodifiableView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public class IslandTeam implements Team, Saveable<IslandTeam.SaveData> {

    private static final Logger LOGGER = LoggerFactory.getLogger(IslandTeam.class.getName());

    public static final MongoCodec<SaveData, IslandTeam, MongoContext> MONGO_CODEC = new MongoCodec<>() {
        @Override
        public void write(SaveData input, Document document) {
            document.append("id", input.id.toString());
            document.append("name", input.name);
            final Document rolesDocument = new Document();
            TeamRoles.MONGO_CODEC.write(input.teamRoles, rolesDocument);
            document.append("roles", rolesDocument);
            final Document allPlayerDataDocument = new Document();
            for (final Map.Entry<UUID, PlayerIslandData.SaveData> entry : input.playerData.entrySet()) {
                final UUID playerId = entry.getKey();
                final PlayerIslandData.SaveData data = entry.getValue();
                final Document playerDataDocument = new Document();
                PlayerIslandData.MONGO_CODEC.write(data, playerDataDocument);
                allPlayerDataDocument.append(playerId.toString(), playerDataDocument);
            }
            document.append("playerData", allPlayerDataDocument);
        }

        @Override
        public @UnknownNullability IslandTeam read(Document document, MongoContext context) {
            final UUID id = UUID.fromString(document.getString("id"));
            final String name = document.getString("name");
            final Document rolesDocument = document.get("roles", Document.class);
            final TeamRoles teamRoles = TeamRoles.MONGO_CODEC.read(rolesDocument, new TeamRoles.MongoContext(context.rolePermissionsRegistry()));
            final Map<UUID, PlayerIslandData> playerData = new HashMap<>();
            final Document allPlayerDataDocument = document.get("playerData", Document.class);
            for (final String playerIdStr : allPlayerDataDocument.keySet()) {
                final UUID playerId = UUID.fromString(playerIdStr);
                final Document playerDataDocument = allPlayerDataDocument.get(playerIdStr, Document.class);
                final PlayerIslandData data = PlayerIslandData.MONGO_CODEC.read(playerDataDocument, null);
                playerData.put(playerId, data);
            }
            return new IslandTeam(id, name, teamRoles, playerData, new InviteManager(context.messageHandler));
        }

    };

    public record MongoContext(RolePermissionsRegistry rolePermissionsRegistry, MessageHandler messageHandler) {
    }

    public static IslandTeam createNewTeam(
            DesolateSkyServer server,
            UUID id,
            UUID owner,
            String name,
            RolePermissionsRegistry rolePermissionsRegistry
    ) {
        final TeamRoles roles = TeamRoles.createNew(rolePermissionsRegistry);
        final Map<UUID, PlayerIslandData> playerData = new HashMap<>();
        final TeamRole ownerRole = roles.getRole(TeamRoles.OWNER_ID);
        playerData.put(owner, new PlayerIslandData(id, owner, Instant.now(), ownerRole.id()));
        return new IslandTeam(id, name, roles, playerData, new InviteManager(server.messageHandler()));
    }

    private final UUID id;
    private final String name;
    private final TeamRoles teamRoles;
    private final Map<UUID, PlayerIslandData> playerData;
    private final InviteManager inviteManager;
    private State state = State.LOADING;

    private final ReadWriteLock lock = new ReentrantReadWriteLock(true);

    public IslandTeam(UUID id, String name, TeamRoles teamRoles, Map<UUID, PlayerIslandData> playerData, InviteManager inviteManager) {
        this.id = id;
        this.name = name;
        this.teamRoles = teamRoles;
        this.playerData = playerData;
        this.inviteManager = inviteManager;
    }

    public boolean hasPermission(DSPlayer player, RolePermissionType type, Key value) {
        this.lock.readLock().lock();
        try {
            final PlayerIslandData data = this.playerData.get(player.getUuid());
            final TeamRole role;
            if (data == null) {
                role = this.teamRoles.getRole(TeamRoles.VISITOR_ID);
            } else {
                role = this.teamRoles.getRole(data.roleId());
            }
            return role.hasPermission(type, value);
        } finally {
            this.lock.readLock().unlock();
        }
    }

    public boolean hasTogglePermission(DSPlayer player, RolePermissionType type) {
        this.lock.readLock().lock();
        try {
            final PlayerIslandData data = this.playerData.get(player.getUuid());
            final TeamRole role;
            if (data == null) {
                role = this.teamRoles.getRole(TeamRoles.VISITOR_ID);
            } else {
                role = this.teamRoles.getRole(data.roleId());
            }
            return role.hasTogglePermission(type);
        } finally {
            this.lock.readLock().unlock();
        }
    }

    public void invite(MessageHandler messageHandler, DSPlayer inviter, DSPlayer toAdd) {
        this.lock.readLock().lock();
        try {
            if (!this.hasTogglePermission(inviter, RolePermissionType.INVITE_MEMBER)) {
                messageHandler.sendMessage(inviter, Messages.ISLAND_PERMISSION_DENIED);
                return;
            }
            if (this.inviteManager.isInvited(toAdd.getUuid())) {
                messageHandler.sendMessage(inviter, Messages.INVITE_ALREADY_EXISTS, Map.of("player", toAdd.getName()));
                return;
            }
            if (this.playerData.containsKey(toAdd.getUuid())) {
                messageHandler.sendMessage(inviter, Messages.INVITE_ALREADY_MEMBER, Map.of("player", toAdd.getName()));
                return;
            }
        } finally {
            this.lock.readLock().unlock();
        }
        this.lock.writeLock().lock();
        try {
            this.inviteManager.addInvite(inviter.profile(), toAdd.profile(), this.name);
            messageHandler.sendMessage(inviter, Messages.INVITE_SENT, Map.of("player", toAdd.getName()));
            messageHandler.sendMessage(toAdd, Messages.INVITE_RECEIVED, Map.of("player", inviter.getName(), "island", this.name));
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    void acceptInvite(MessageHandler messageHandler, DSPlayer player, Runnable onSuccess) {
        final UUID inviteId = player.getUuid();
        this.lock.readLock().lock();
        try {
            if (!this.inviteManager.isInvited(inviteId)) {
                messageHandler.sendMessage(player, Messages.INVITE_NOT_FOUND, Map.of("island", this.name));
                return;
            }
            final PlayerIslandData data = this.playerData.get(player.getUuid());
            if (data != null) {
                messageHandler.sendMessage(player, Messages.ALREADY_HAS_ISLAND);
                return;
            }
            if (!this.inviteManager.isInvited(inviteId)) {
                messageHandler.sendMessage(player, Messages.INVITE_NOT_FOUND, Map.of("island", this.name));
                return;
            }
        } finally {
            this.lock.readLock().unlock();
        }
        this.lock.writeLock().lock();
        try {
            final TeamRole role = this.teamRoles.getRole(TeamRoles.MEMBER_ID);
            this.playerData.put(player.getUuid(), new PlayerIslandData(this.id, player.getUuid(), Instant.now(), role.id()));
            this.inviteManager.removeInvite(inviteId);
            DSPlayer.acquireAndSync(player, p -> p.setIslandId(this.id));
            messageHandler.sendMessage(player, Messages.RECEIVED_INVITE_ACCEPTED, Map.of("island", this.name));
            onSuccess.run();
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    void leave(MessageHandler messageHandler, DSPlayer player, Runnable onSuccess) {
        final UUID playerId = player.getUuid();
            this.lock.readLock().lock();
        try {
            if (!this.playerData.containsKey(playerId)) {
                messageHandler.sendMessage(player, Messages.NOT_ISLAND_MEMBER, Map.of("island", this.name));
                return;
            }
            if (this.isOwner(player)) {
                messageHandler.sendMessage(player, Messages.OWNER_CANNOT_LEAVE);
                return;
            }
        } finally {
            this.lock.readLock().unlock();
        }
            this.lock.writeLock().lock();
        try {
            this.playerData.remove(playerId);
            DSPlayer.acquireAndSync(player, p -> p.setIslandId(null));
            messageHandler.sendMessage(player, Messages.LEFT_ISLAND, Map.of("island", this.name));
            onSuccess.run();
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    public TeamRole getRole(DSPlayer player) {
            this.lock.readLock().lock();
        try {
            final PlayerIslandData data = this.playerData.get(player.getUuid());
            if (data == null) {
                return this.teamRoles.getRole(TeamRoles.VISITOR_ID);
            }
            return this.teamRoles.getRole(data.roleId());
        } finally {
            this.lock.readLock().unlock();
        }
    }

    public UUID id() {
        return this.id;
    }

    public String name() {
        return this.name;
    }

    public TeamRoles teamRoles() {
        return this.teamRoles;
    }

    public @UnmodifiableView Map<UUID, PlayerIslandData> playerData() {
            this.lock.readLock().lock();
        try {
            return Map.copyOf(this.playerData);
        } finally {
            this.lock.readLock().unlock();
        }
    }

    public boolean isOwner(DSPlayer player) {
            this.lock.readLock().lock();
        try {
            final UUID playerId = player.getUuid();
            final PlayerIslandData data = this.playerData.get(playerId);
            return data != null && data.roleId().equals(TeamRoles.OWNER_ID);
        } finally {
            this.lock.readLock().unlock();
        }
    }

    public UUID getOwnerId() {
            this.lock.readLock().lock();
        try {
            return this.playerData.entrySet()
                    .stream()
                    .filter(entry -> entry.getValue().roleId().equals(TeamRoles.OWNER_ID))
                    .map(Map.Entry::getKey)
                    .findFirst()
                    .orElseThrow();
        } finally {
            this.lock.readLock().unlock();
        }
    }

    public boolean isMember(DSPlayer player) {
            this.lock.readLock().lock();
        try {
            return this.isMember(player.getUuid());
        } finally {
            this.lock.readLock().unlock();
        }
    }

    public boolean isMember(UUID playerId) {
            this.lock.readLock().lock();
        try {
            final PlayerIslandData data = this.playerData.get(playerId);
            return data != null && !data.roleId().equals(TeamRoles.VISITOR_ID);
        } finally {
            this.lock.readLock().unlock();
        }
    }

    public @UnmodifiableView Map<UUID, PlayerIslandData> getPlayerData() {
            this.lock.readLock().lock();
        try {
            return Map.copyOf(this.playerData);
        } finally {
            this.lock.readLock().unlock();
        }
    }

    public int getMemberCount() {
            this.lock.readLock().lock();
        try {
            return (int) this.playerData.values()
                    .stream()
                    .filter(data -> !data.roleId().equals(TeamRoles.VISITOR_ID))
                    .count();
        } finally {
            this.lock.readLock().unlock();
        }
    }

    public int getOnlinePlayersCount() {
            this.lock.readLock().lock();
        try {
            return (int) this.playerData.keySet()
                    .stream()
                    .map(id -> MinecraftServer.getConnectionManager().getOnlinePlayerByUuid(id))
                    .filter(player -> player != null && player.isOnline())
                    .count();
        } finally {
            this.lock.readLock().unlock();
        }
    }

    public record SaveData(
            IslandTeam team,
            UUID id,
            String name,
            TeamRoles.SaveData teamRoles,
            Map<UUID, PlayerIslandData.SaveData> playerData
    ) {
    }

    @Override
    public SaveData createSnapshot() {
            this.lock.readLock().lock();
        try {
            final Map<UUID, PlayerIslandData.SaveData> playerDataSnapshot = this.playerData.entrySet()
                    .stream()
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            entry -> entry.getValue().createSnapshot()
                    ));
            return new SaveData(
                    this,
                    this.id,
                    this.name,
                    this.teamRoles.createSnapshot(),
                    playerDataSnapshot
            );
        } finally {
            this.lock.readLock().unlock();
        }
    }

    public State state() {
            this.lock.readLock().lock();
        try {
            return this.state;
        } finally {
            this.lock.readLock().unlock();
        }
    }

    public void setState(State state) {
            this.lock.writeLock().lock();
        try {
            if (this.state == State.DELETED) {
                LOGGER.warn("Attempted to set state of deleted island team {} to {}", this.id, state);
                return;
            }
            if (this.state == State.DELETING && state != State.DELETED) {
                LOGGER.warn("Attempted to set state of deleting island team {} to {}", this.id, state);
                return;
            }
            this.state = state;
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    public enum State {

        LOADING,
        LOADED,
        SAVING,
        DELETING,
        DELETED;

        public boolean isSaving() {
            return this == SAVING || this == DELETING;
        }

        public boolean isLoaded() {
            return this == LOADED;
        }

        public boolean isDeleted() {
            return this == DELETED;
        }

        public boolean isDeleting() {
            return this == DELETING;
        }

    }
}
