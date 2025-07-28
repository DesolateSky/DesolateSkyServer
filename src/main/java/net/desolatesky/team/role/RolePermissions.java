package net.desolatesky.team.role;

import net.desolatesky.database.MongoCodec;
import net.desolatesky.database.Saveable;
import net.kyori.adventure.key.Key;
import org.bson.Document;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;
import org.jetbrains.annotations.UnmodifiableView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * This class is not thread-safe, but {@link TeamRole} is thread-safe which is why the methods in this class
 * are package private.
 */
public final class RolePermissions implements Saveable<RolePermissions.SaveData> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RolePermissions.class);

    public static final MongoCodec<SaveData, RolePermissions, MongoContext> MONGO_CODEC = new MongoCodec<>() {
        @Override
        public void write(SaveData input, Document document) {
            for (final Map.Entry<RolePermissionType, RolePermission.SaveData> entry : input.permissions().entrySet()) {
                final RolePermission.SaveData permission = entry.getValue();
                final Document permissionDocument = new Document();
                RolePermission.MONGO_CODEC.write(permission, permissionDocument);
                document.append(entry.getKey().name(), permissionDocument);
            }
        }

        @Override
        public @UnknownNullability RolePermissions read(Document document, MongoContext context) {
            final RolePermissionsRegistry registry = context.rolePermissionsRegistry;
            final Map<RolePermissionType, RolePermission> permissions = registry.getDefinitions().stream()
                    .collect(Collectors.toMap(
                            RolePermissionDefinition::type,
                            definition -> {
                                final Document permissionDocument = document.get(definition.type().name(), Document.class);
                                return RolePermission.MONGO_CODEC.read(permissionDocument, new RolePermission.MongoContext(context.rolePermissionsRegistry));
                            }
                    ));
            return new RolePermissions(permissions);
        }

    };

    public record MongoContext(RolePermissionsRegistry rolePermissionsRegistry) {
    }

    private final Map<RolePermissionType, RolePermission> permissions;

    public RolePermissions(Map<RolePermissionType, RolePermission> permissions) {
        this.permissions = permissions;
    }

    public @Nullable RolePermission getPermission(RolePermissionType type) {
        return this.permissions.get(type);
    }

    boolean hasPermission(RolePermissionType type, Key value) {
        final RolePermission permission = this.getPermission(type);
        if (permission == null) {
            return false;
        }
        return permission.isAllowed(value);
    }

    boolean hasTogglePermission(RolePermissionType type) {
        final RolePermission permission = this.getPermission(type);
        if (permission == null) {
            return false;
        }
        return permission.isEnabled();
    }

    boolean togglePermission(RolePermissionType type) {
        final RolePermission permission = this.getPermission(type);
        if (permission != null) {
            return permission.toggle();
        }
        logPermissionNotFound(type);
        return false;
    }

    void setSetting(RolePermissionType type, RolePermissionSetting setting) {
        final RolePermission permission = this.getPermission(type);
        if (permission == null) {
            logPermissionNotFound(type);
            return;
        }
        permission.setSetting(setting);
    }

    void toggleSetting(RolePermissionType type) {
        final RolePermission permission = this.getPermission(type);
        if (permission == null) {
            logPermissionNotFound(type);
            return;
        }
        final RolePermissionSetting currentSetting = permission.setting();
        final RolePermissionSetting newSetting = currentSetting == RolePermissionSetting.WHITELIST ? RolePermissionSetting.BLACKLIST : RolePermissionSetting.WHITELIST;
        permission.setSetting(newSetting);
    }

    void addValue(RolePermissionType type, Key value) {
        final RolePermission permission = this.getPermission(type);
        if (permission == null) {
            logPermissionNotFound(type);
            return;
        }
        permission.addValue(value);
    }

    void removeValue(RolePermissionType type, Key value) {
        final RolePermission permission = this.getPermission(type);
        if (permission == null) {
            logPermissionNotFound(type);
            return;
        }
        permission.removeValue(value);
    }

    void setEnabled(RolePermissionType type, boolean enabled) {
        final RolePermission permission = this.getPermission(type);
        if (permission == null) {
            logPermissionNotFound(type);
            return;
        }
        permission.setEnabled(enabled);
    }

    /**
     * @return the new enabled state after toggling
     */
    boolean toggle(RolePermissionType type) {
        final RolePermission permission = this.getPermission(type);
        if (permission == null) {
            logPermissionNotFound(type);
            return false;
        }
        return permission.toggle();
    }

    @UnmodifiableView Map<RolePermissionType, RolePermission> getPermissions() {
        return Collections.unmodifiableMap(this.permissions);
    }

    private static void logPermissionNotFound(RolePermissionType type) {
        LOGGER.warn("No permission found for type: {}", type);
    }

    public record SaveData(Map<RolePermissionType, RolePermission.SaveData> permissions) {

    }

    @Override
    public SaveData createSnapshot() {
        final Map<RolePermissionType, RolePermission.SaveData> savedPermissions = this.permissions.entrySet().stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().createSnapshot()
                ));
        return new SaveData(savedPermissions);
    }

}
