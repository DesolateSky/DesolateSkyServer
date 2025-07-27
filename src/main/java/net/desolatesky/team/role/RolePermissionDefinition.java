package net.desolatesky.team.role;

import com.google.common.base.Preconditions;
import net.kyori.adventure.key.Key;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @param type
 * @param allValues
 * @param defaultValuesSupplier
 * @param menuIconFunction      Function to get the icon for the permission in the menu, only null if the permission is a toggle permission
 * @param isTogglePermission    If this permission only uses enabled/disabled and not specific values
 */
public record RolePermissionDefinition(
        RolePermissionType type,
        @Unmodifiable Collection<Key> allValues,
        Supplier<Collection<Key>> defaultValuesSupplier,
        @UnknownNullability Function<Key, ItemStack> menuIconFunction,
        boolean isTogglePermission,
        RolePermissionSetting defaultSetting
) {

    public RolePermissionDefinition {
        Preconditions.checkArgument(isTogglePermission || menuIconFunction != null, "Menu icon function cannot be null for non-toggle permissions in " + type.name());
        Preconditions.checkArgument(isTogglePermission || !allValues.isEmpty(), "All values cannot be empty for non-toggle permissions in " + type.name());
    }

    public RolePermissionDefinition(
            RolePermissionType type,
            @Unmodifiable Collection<Key> allValues,
            Supplier<Collection<Key>> defaultValuesSupplier,
            Function<Key, ItemStack> menuIconFunction,
            RolePermissionSetting defaultSetting
    ) {
        this(type, allValues, defaultValuesSupplier, menuIconFunction, allValues.isEmpty(), defaultSetting);
    }

    public RolePermissionDefinition(
            RolePermissionType type,
            @Unmodifiable Collection<Key> allValues,
            @UnknownNullability Function<Key, ItemStack> menuIconFunction,
            RolePermissionSetting defaultSetting
    ) {
        this(type, allValues, allValues.isEmpty() ? Collections::emptyList : HashSet::new, menuIconFunction, allValues.isEmpty(), defaultSetting);
    }


    public RolePermissionDefinition(RolePermissionType type, RolePermissionSetting defaultSetting) {
        this(type, Collections.emptyList(), Collections::emptyList, null, true, defaultSetting);
    }

    public RolePermission createPermission() {
        return new RolePermission(this, this.defaultSetting, this.defaultValuesSupplier.get());
    }

    public RolePermission createAllAllowed() {
        if (this.isTogglePermission) {
            return new RolePermission(this, true);
        }
        return new RolePermission(this, RolePermissionSetting.BLACKLIST, new HashSet<>());
    }

    public RolePermission createAllDenied() {
        if (this.isTogglePermission) {
            return new RolePermission(this, false);
        }
        return new RolePermission(this, RolePermissionSetting.WHITELIST, new HashSet<>());
    }

}
