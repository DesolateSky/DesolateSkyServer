package net.desolatesky.island.permission;

import net.desolatesky.data.definition.DataTranslator;
import net.desolatesky.island.permission.data.IslandPermissionsDataDefinitionV1;
import net.desolatesky.island.role.IslandRole;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public interface IslandPermissions {

    DataTranslator<IslandPermissions> DATA_TRANSLATOR = new DataTranslator<>(List.of(
            new IslandPermissionsDataDefinitionV1()
    ));

    IslandPermissions EMPTY = new Empty();

    static IslandPermissions mutable(IslandRole role, Set<IslandPermission> allowedPermissions) {
        return new Impl(role, allowedPermissions);
    }

    IslandRole role();

    @Unmodifiable
    Set<IslandPermission> getAllowedPermissions();

    boolean hasPermission(IslandPermission permission);

    void addPermission(IslandPermission permission);

    void removePermission(IslandPermission permission);

    IslandPermissions copy();

    class Impl implements IslandPermissions {

        private final IslandRole role;
        private final Set<IslandPermission> allowedPermissions;

        Impl(IslandRole role, Set<IslandPermission> allowedPermissions) {
            this.role = role;
            this.allowedPermissions = EnumSet.copyOf(allowedPermissions);
        }

        public IslandRole role() {
            return this.role;
        }

        public @Unmodifiable Set<IslandPermission> getAllowedPermissions() {
            return Set.copyOf(this.allowedPermissions);
        }

        public boolean hasPermission(IslandPermission permission) {
            return this.allowedPermissions.contains(permission);
        }

        public void addPermission(IslandPermission permission) {
            this.allowedPermissions.add(permission);
        }

        public void removePermission(IslandPermission permission) {
            this.allowedPermissions.remove(permission);
        }

        @Override
        public Impl copy() {
            return new Impl(this.role, new HashSet<>(this.allowedPermissions));
        }
    }

    class Empty implements IslandPermissions {

        private Empty() {
        }

        @Override
        public IslandRole role() {
            return IslandRole.GUEST;
        }

        @Override
        public @Unmodifiable Set<IslandPermission> getAllowedPermissions() {
            return Collections.emptySet();
        }

        @Override
        public boolean hasPermission(IslandPermission permission) {
            return false;
        }

        @Override
        public void addPermission(IslandPermission permission) {

        }

        @Override
        public void removePermission(IslandPermission permission) {

        }

        @Override
        public Empty copy() {
            return this;
        }
    }
}
