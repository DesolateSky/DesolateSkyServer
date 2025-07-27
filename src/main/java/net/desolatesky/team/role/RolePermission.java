package net.desolatesky.team.role;

import com.google.common.base.Preconditions;
import net.desolatesky.database.MongoCodec;
import net.desolatesky.database.Saveable;
import net.kyori.adventure.key.Key;
import org.bson.Document;
import org.jetbrains.annotations.UnknownNullability;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

public final class RolePermission implements Saveable<RolePermission.SaveData> {

    public static final MongoCodec<SaveData, RolePermission, MongoContext> MONGO_CODEC = new MongoCodec<>() {
        @Override
        public void write(SaveData input, Document document) {
            final RolePermissionDefinition definition = input.definition();
            document.append("type", input.definition().type());
            if (definition.isTogglePermission()) {
                document.append("enabled", input.enabled());
            } else {
                document.append("setting", input.setting().name());
                document.append("values", input.values().stream().map(Key::asString).toList());
            }
        }

        @Override
        public @UnknownNullability RolePermission read(Document document, MongoContext context) {
            final String permissionTypeName = document.getString("type");
            final RolePermissionType permissionType = RolePermissionType.valueOf(permissionTypeName);
            final RolePermissionDefinition definition = context.rolePermissionsRegistry.getDefinition(permissionType);
            if (definition == null) {
                throw new IllegalArgumentException("Unknown permission type: " + permissionTypeName);
            }
            if (definition.isTogglePermission()) {
                final boolean enabled = document.getBoolean("enabled", false);
                return new RolePermission(definition, enabled);
            } else {
                final String settingName = document.getString("setting");
                final RolePermissionSetting setting = RolePermissionSetting.valueOf(settingName);
                final Collection<Key> values = document.getList("values", String.class).stream()
                        .map(Key::key).toList();
                return new RolePermission(definition, setting, values);
            }
        }

    };

    public record MongoContext(RolePermissionsRegistry rolePermissionsRegistry) {
    }

    private final RolePermissionDefinition definition;
    private RolePermissionSetting setting;
    private final Collection<Key> values;
    private boolean enabled;

    public RolePermission(RolePermissionDefinition definition, RolePermissionSetting setting, Collection<Key> values) {
        this.definition = definition;
        this.setting = setting;
        this.values = values;
        this.enabled = false;
    }

    public RolePermission(RolePermissionDefinition definition, Collection<Key> values) {
        this(definition, RolePermissionSetting.BLACKLIST, values);
        this.enabled = false;
    }

    public RolePermission(RolePermissionDefinition definition, boolean enabled) {
        Preconditions.checkState(definition.isTogglePermission(), "Cannot create toggle permission with non-toggle definition");
        this.definition = definition;
        this.setting = RolePermissionSetting.BLACKLIST;
        this.values = Collections.emptyList();
        this.enabled = enabled;
    }

    public boolean isAllowed(Key item) {
        if (this.definition.isTogglePermission()) {
            return this.enabled;
        }
        return switch (this.setting) {
            case WHITELIST -> this.values.contains(item);
            case BLACKLIST -> !this.values.contains(item);
        };
    }

    public RolePermissionDefinition definition() {
        return this.definition;
    }

    public RolePermissionSetting setting() {
        return this.setting;
    }

    void setSetting(RolePermissionSetting setting) {
        this.setting = setting;
    }

    public @Unmodifiable Collection<Key> getValues() {
        return Collections.unmodifiableCollection(this.values);
    }

    void addValue(Key value) {
        Preconditions.checkState(!this.definition.isTogglePermission(), "Cannot add value to toggle permission");
        this.values.add(value);
    }

    void removeValue(Key value) {
        this.values.remove(value);
    }

    void setEnabled(boolean enabled) {
        Preconditions.checkState(this.definition.isTogglePermission(), "Cannot set enabled state for non-toggle permission");
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        Preconditions.checkState(this.definition.isTogglePermission(), "Cannot check enabled state for non-toggle permission");
        return this.enabled;
    }

    /**
     * @return the new enabled state after toggling
     */
    boolean toggle() {
        Preconditions.checkState(this.definition.isTogglePermission(), "Cannot toggle non-toggle permission");
        this.enabled = !this.enabled;
        return this.enabled;
    }

    public record SaveData(
            RolePermissionDefinition definition,
            RolePermissionSetting setting,
            Collection<Key> values,
            boolean enabled
    ) {

    }

    @Override
    public SaveData createSnapshot() {
        return new SaveData(this.definition, this.setting, new HashSet<>(this.values), this.enabled);
    }

}
