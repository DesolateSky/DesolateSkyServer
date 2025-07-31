package net.desolatesky.team.role;

import net.desolatesky.database.MongoCodec;
import net.desolatesky.database.Saveable;
import net.kyori.adventure.text.Component;
import org.bson.Document;
import org.jetbrains.annotations.UnknownNullability;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * This class is thread safe
 */
public final class TeamRoles implements Saveable<TeamRoles.SaveData> {

    public static final MongoCodec<SaveData, TeamRoles, MongoContext> MONGO_CODEC = new MongoCodec<>() {
        @Override
        public void write(SaveData input, Document document) {
            final Document rolesDocument = new Document();
            for (final Map.Entry<String, TeamRole.SaveData> entry : input.roles().entrySet()) {
                final String roleId = entry.getKey();
                final TeamRole.SaveData role = entry.getValue();
                final Document roleDocument = new Document();
                TeamRole.MONGO_CODEC.write(role, roleDocument);
                rolesDocument.append(roleId, roleDocument);
            }
            document.append("roles", rolesDocument);
        }

        @Override
        public @UnknownNullability TeamRoles read(Document document, MongoContext context) {
            final RolePermissionsRegistry rolePermissionsRegistry = context.rolePermissionsRegistry;
            final Map<String, TeamRole> roles = new HashMap<>();
            final Document rolesDocument = document.get("roles", Document.class);
            if (rolesDocument != null) {
                for (final String roleId : rolesDocument.keySet()) {
                    final Document roleDocument = rolesDocument.get(roleId, Document.class);
                    final TeamRole role = TeamRole.MONGO_CODEC.read(roleDocument, new TeamRole.MongoContext(rolePermissionsRegistry));
                    roles.put(roleId, role);
                }
            }
            return new TeamRoles(rolePermissionsRegistry, roles);
        }

    };

    public record MongoContext(RolePermissionsRegistry rolePermissionsRegistry) {
    }

    public static final String OWNER_ID = "owner";
    public static final int OWNER_PRIORITY = 0;
    public static final String OFFICER_ID = "officer";
    public static final int OFFICER_PRIORITY = 1;
    public static final String MEMBER_ID = "member";
    public static final int MEMBER_PRIORITY = 2;
    public static final String VISITOR_ID = "visitor";
    public static final int VISITOR_PRIORITY = 3;

    public static TeamRoles createNew(RolePermissionsRegistry rolePermissionsRegistry) {
        final TeamRoles teamRoles = new TeamRoles(rolePermissionsRegistry, new HashMap<>());
        teamRoles.registerDefaults();
        return teamRoles;
    }

    public static TeamRoles create(RolePermissionsRegistry rolePermissionsRegistry, Map<String, TeamRole> roles) {
        return new TeamRoles(rolePermissionsRegistry, roles);
    }

    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    private final RolePermissionsRegistry rolePermissionsRegistry;
    private final Map<String, TeamRole> roles;

    private TeamRoles(
            RolePermissionsRegistry rolePermissionsRegistry,
            Map<String, TeamRole> roles
    ) {
        this.rolePermissionsRegistry = rolePermissionsRegistry;
        this.roles = roles;
    }

    public @UnknownNullability TeamRole getRole(String id) {
        this.lock.readLock().lock();
        try {
            return this.roles.get(id);
        } finally {
            this.lock.readLock().unlock();
        }
    }

    public void addRole(TeamRole role) {
        this.lock.writeLock().lock();
        try {
            this.roles.put(role.id(), role);
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    // no need to synchronize this method as it is only called during initialization
    private void registerDefaults() {
        this.addRole(this.createOwner());
        this.addRole(this.createOfficer());
        this.addRole(this.createMember());
        this.addRole(this.createVisitor());
    }

    // no need to synchronize this method as it is only called during initialization
    private TeamRole createOwner() {
        final Map<RolePermissionType, RolePermission> permissions = new HashMap<>();
        final Collection<RolePermissionDefinition> definitions = this.rolePermissionsRegistry.getDefinitions();
        for (final RolePermissionDefinition definition : definitions) {
            permissions.put(definition.type(), definition.createAllAllowed());
        }
        return new TeamRole(OWNER_ID, new RolePermissions(permissions), Component.text("owner"), OWNER_PRIORITY);
    }

    // no need to synchronize this method as it is only called during initialization
    private TeamRole createOfficer() {
        final Map<RolePermissionType, RolePermission> permissions = new HashMap<>();
        final Collection<RolePermissionDefinition> definitions = this.rolePermissionsRegistry.getDefinitions();
        for (final RolePermissionDefinition definition : definitions) {
            permissions.put(definition.type(), definition.createPermission());
        }
        final RolePermissions teamPermissions = new RolePermissions(permissions);
        teamPermissions.setEnabled(RolePermissionType.MANAGE_PERMISSIONS, true);
        return new TeamRole(OFFICER_ID, teamPermissions, Component.text("officer"), OFFICER_PRIORITY);
    }

    // no need to synchronize this method as it is only called during initialization
    private TeamRole createMember() {
        final Map<RolePermissionType, RolePermission> permissions = new HashMap<>();
        final Collection<RolePermissionDefinition> definitions = this.rolePermissionsRegistry.getDefinitions();
        for (final RolePermissionDefinition definition : definitions) {
            permissions.put(definition.type(), definition.createPermission());
        }
        final RolePermissions teamPermissions = new RolePermissions(permissions);
        teamPermissions.togglePermission(RolePermissionType.INVITE_MEMBER);
        teamPermissions.togglePermission(RolePermissionType.KICK_MEMBER);
        return new TeamRole(MEMBER_ID, teamPermissions, Component.text("member"), MEMBER_PRIORITY);
    }

    // no need to synchronize this method as it is only called during initialization
    private TeamRole createVisitor() {
        final Map<RolePermissionType, RolePermission> permissions = new HashMap<>();
        final Collection<RolePermissionDefinition> definitions = this.rolePermissionsRegistry.getDefinitions();
        for (final RolePermissionDefinition definition : definitions) {
            permissions.put(definition.type(), definition.createAllDenied());
        }
        final RolePermissions teamPermissions = new RolePermissions(permissions);
        return new TeamRole(VISITOR_ID, teamPermissions, Component.text("visitor"), VISITOR_PRIORITY);
    }

    public record SaveData(Map<String, TeamRole.SaveData> roles) {

    }

    @Override
    public SaveData createSnapshot() {
        this.lock.readLock().lock();
        try {
            final Map<String, TeamRole.SaveData> rolesData = new HashMap<>();
            for (final Map.Entry<String, TeamRole> entry : this.roles.entrySet()) {
                final String roleId = entry.getKey();
                final TeamRole role = entry.getValue();
                rolesData.put(roleId, role.createSnapshot());
            }
            return new SaveData(rolesData);
        } finally {
            this.lock.readLock().unlock();
        }
    }

}
