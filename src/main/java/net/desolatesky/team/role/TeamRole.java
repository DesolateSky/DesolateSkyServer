package net.desolatesky.team.role;

import net.desolatesky.database.MongoCodec;
import net.desolatesky.database.Saveable;
import net.desolatesky.message.MessageHandler;
import net.desolatesky.message.Messages;
import net.desolatesky.player.DSPlayer;
import net.desolatesky.team.IslandTeam;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer;
import org.bson.Document;
import org.jetbrains.annotations.UnknownNullability;
import org.jetbrains.annotations.Unmodifiable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * This class is thread safe
 */
public final class TeamRole implements Saveable<TeamRole.SaveData> {

    public static final MongoCodec<SaveData, TeamRole, MongoContext> MONGO_CODEC = new MongoCodec<>() {
        @Override
        public void write(SaveData input, Document document) {
            document.append("id", input.id());
            final Document permissionsDocument = new Document();
            RolePermissions.MONGO_CODEC.write(input.permissions(), permissionsDocument);
            document.append("permissions", permissionsDocument);
            final String displayNameJson = JSONComponentSerializer.json().serialize(input.displayName());
            document.append("displayName", displayNameJson);
            document.append("priority", input.priority());
        }

        @Override
        public @UnknownNullability TeamRole read(Document document, MongoContext context) {
            final String id = document.getString("id");
            final Document permissionsDocument = document.get("permissions", Document.class);
            final RolePermissions permissions = RolePermissions.MONGO_CODEC.read(permissionsDocument, new RolePermissions.MongoContext(context.rolePermissionsRegistry));
            final String displayNameJson = document.getString("displayName");
            final Component displayName = JSONComponentSerializer.json().deserialize(displayNameJson);
            final int priority = document.getInteger("priority");
            return new TeamRole(id, permissions, displayName, priority);
        }
    };

    public record MongoContext(RolePermissionsRegistry rolePermissionsRegistry) {
    }

    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    private final String id;
    private final RolePermissions permissions;
    private Component displayName;
    /**
     * The priority of the role, lower values indicate higher priority.
     * For example, the owner role has the highest priority with a value of 0
     */
    private int priority;

    public TeamRole(String id, RolePermissions permissions, Component displayName, int priority) {
        this.id = id;
        this.permissions = permissions;
        this.displayName = displayName;
        this.priority = priority;
    }

    public boolean togglePermission(MessageHandler messageHandler, IslandTeam team, DSPlayer player, RolePermissionType type) {
        try {
            this.lock.writeLock().lock();
            if (!this.hasManagePermissionsPermission(team, player, type)) {
                sendNoPermissionMessage(messageHandler, player);
                return false;
            }
            return this.permissions.togglePermission(type);
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    public void setSetting(MessageHandler messageHandler, IslandTeam team, DSPlayer player, RolePermissionType type, RolePermissionSetting setting) {
        try {
            this.lock.writeLock().lock();
            if (!this.hasManagePermissionsPermission(team, player, type)) {
                sendNoPermissionMessage(messageHandler, player);
                return;
            }
            this.permissions.setSetting(type, setting);
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    public void toggleSetting(MessageHandler messageHandler, IslandTeam team, DSPlayer player, RolePermissionType type) {
        try {
            this.lock.writeLock().lock();
            if (!this.hasManagePermissionsPermission(team, player, type)) {
                sendNoPermissionMessage(messageHandler, player);
                return;
            }
            this.permissions.toggleSetting(type);
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    public void addValue(MessageHandler messageHandler, IslandTeam team, DSPlayer player, RolePermissionType type, Key value) {
        try {
            this.lock.writeLock().lock();
            if (!this.hasManagePermissionsPermission(team, player, type)) {
                sendNoPermissionMessage(messageHandler, player);
                return;
            }
            this.permissions.addValue(type, value);
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    public void removeValue(MessageHandler messageHandler, IslandTeam team, DSPlayer player, RolePermissionType type, Key value) {
        try {
            this.lock.writeLock().lock();
            if (!this.hasManagePermissionsPermission(team, player, type)) {
                sendNoPermissionMessage(messageHandler, player);
                return;
            }
            this.permissions.removeValue(type, value);
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    public void setEnabled(MessageHandler messageHandler, IslandTeam team, DSPlayer player, RolePermissionType type, boolean enabled) {
        try {
            this.lock.writeLock().lock();
            if (!this.hasManagePermissionsPermission(team, player, type)) {
                sendNoPermissionMessage(messageHandler, player);
                return;
            }
            this.permissions.setEnabled(type, enabled);
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    /**
     * @return the new enabled state after toggling
     */
    public boolean toggle(MessageHandler messageHandler, IslandTeam team, DSPlayer player, RolePermissionType type) {
        try {
            this.lock.writeLock().lock();
            if (!this.hasManagePermissionsPermission(team, player, type)) {
                sendNoPermissionMessage(messageHandler, player);
                return false;
            }
            return this.permissions.toggle(type);
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    public boolean hasPermission(RolePermissionType type, Key value) {
        try {
            this.lock.readLock().lock();
            return this.permissions.hasPermission(type, value);
        } finally {
            this.lock.readLock().unlock();
        }
    }

    public boolean hasTogglePermission(RolePermissionType type) {
        try {
            this.lock.readLock().lock();
            return this.permissions.hasTogglePermission(type);
        } finally {
            this.lock.readLock().unlock();
        }
    }

    @Unmodifiable
    public Map<RolePermissionType, RolePermission> getPermissions() {
        try {
            this.lock.readLock().lock();
            return Map.copyOf(this.permissions.getPermissions());
        } finally {
            this.lock.readLock().unlock();
        }
    }

    private static void sendNoPermissionMessage(MessageHandler messageHandler, DSPlayer player) {
        messageHandler.sendMessage(player, Messages.ISLAND_PERMISSION_DENIED);
    }

    private boolean hasManagePermissionsPermission(IslandTeam team, DSPlayer player, RolePermissionType type) {
        final TeamRole playerRole = team.getRole(player);
        return playerRole.priority() < this.priority && team.hasPermission(player, RolePermissionType.MANAGE_PERMISSIONS, type.key());
    }

    public String id() {
        return this.id;
    }

    public RolePermissions permissions() {
        return this.permissions;
    }

    public Component displayName() {
        return this.displayName;
    }

    public void setDisplayName(Component displayName) {
        this.displayName = displayName;
    }

    public int priority() {
        return this.priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public record SaveData(
            String id,
            RolePermissions.SaveData permissions,
            Component displayName,
            int priority
    ) {

    }

    @Override
    public SaveData createSnapshot() {
        try {
            this.lock.readLock().lock();
            return new SaveData(this.id, this.permissions.createSnapshot(), this.displayName, this.priority);
        } finally {
            this.lock.readLock().unlock();
        }
    }
}
