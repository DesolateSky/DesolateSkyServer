package com.fisherl.desolatesky.island.permission;

import com.fisherl.desolatesky.island.role.IslandRole;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public interface IslandPermissions {

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
    }
}
