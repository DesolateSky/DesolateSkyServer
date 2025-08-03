package net.desolatesky.team.menu.permission;

import net.desolatesky.menu.PaginatedMenu;
import net.desolatesky.menu.action.ClickAction;
import net.desolatesky.menu.item.MenuButton;
import net.desolatesky.menu.pattern.Pattern;
import net.desolatesky.message.MessageHandler;
import net.desolatesky.player.DSPlayer;
import net.desolatesky.team.IslandTeam;
import net.desolatesky.team.role.RolePermission;
import net.desolatesky.team.role.RolePermissionDefinition;
import net.desolatesky.team.role.RolePermissionSetting;
import net.desolatesky.team.role.RolePermissionType;
import net.desolatesky.team.role.RolePermissions;
import net.desolatesky.team.role.TeamRole;
import net.desolatesky.util.ComponentUtil;
import net.desolatesky.util.InventoryUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.component.DataComponents;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.item.component.TooltipDisplay;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class PermissionMenu extends PaginatedMenu {

    private static final InventoryType INVENTORY_TYPE = InventoryType.CHEST_6_ROW;
    private static final int PREVIOUS_PAGE_SLOT = INVENTORY_TYPE.getSize() - 9;
    private static final int NEXT_PAGE_SLOT = INVENTORY_TYPE.getSize() - 1;
    private static final List<Integer> PERMISSION_BUTTON_SLOTS = List.of(
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34
    );

    public static final Pattern BORDER_PATTERN = Pattern.border(List.of(MenuButton.simple(ItemStack.of(Material.GRAY_STAINED_GLASS_PANE).with(DataComponents.TOOLTIP_DISPLAY, new TooltipDisplay(true, Collections.emptySet())).withCustomName(Component.empty()), true)));

    public static PermissionMenu create(DSPlayer player, MessageHandler messageHandler, IslandTeam team, TeamRole role) {
        final Map<Integer, MenuButton> menuItems = new HashMap<>();
        menuItems.put(NEXT_PAGE_SLOT, nextPageButton(ItemStack.of(Material.ARROW).withCustomName(ComponentUtil.noItalics("Next Page"))));
        menuItems.put(PREVIOUS_PAGE_SLOT, previousPageButton(ItemStack.of(Material.ARROW).withCustomName(ComponentUtil.noItalics("Previous Page"))));
        final Map<Integer, ClickAction> clickActions = new HashMap<>();
        final List<? extends MenuButton> permissionButtons = createPermissionButtons(messageHandler, team, role);
        final List<Pattern> patterns = List.of(BORDER_PATTERN);
        final ClickAction defaultClickAction = unused -> ClickAction.Result.CANCEL;
        return new PermissionMenu(
                player,
                INVENTORY_TYPE,
                role.displayName().append(Component.text(" Permissions")),
                menuItems,
                clickActions,
                permissionButtons,
                PERMISSION_BUTTON_SLOTS,
                patterns,
                defaultClickAction,
                team,
                role
        );
    }

    private static List<? extends MenuButton> createPermissionButtons(MessageHandler messageHandler, IslandTeam team, TeamRole role) {
        final RolePermissions permissions = role.permissions();
        return role.getPermissions().entrySet().stream().map(entry -> {
            final RolePermissionType type = entry.getKey();
            final RolePermission permission = entry.getValue();
            final ItemStack icon = createPermissionButtonIcon(permission);
            return MenuButton.simple(icon, createPermissionClickAction(messageHandler, team, role, type));
        }).toList();
    }

    private static ItemStack createPermissionButtonIcon(RolePermission permission) {
        final RolePermissionDefinition definition = permission.definition();
        final RolePermissionType type = definition.type();
        final RolePermissionSetting setting = permission.setting();
        final List<Component> lore = new ArrayList<>();
        if (definition.isTogglePermission()) {
            if (permission.isEnabled()) {
                lore.add(Component.text("Enabled").color(NamedTextColor.GREEN));
            } else {
                lore.add(Component.text("Disabled").color(NamedTextColor.RED));
            }
        }
        lore.add(Component.text("Setting: " + setting.name()).color(NamedTextColor.GRAY));
        return type.icon().withLore(lore);
    }

    private static ClickAction createPermissionClickAction(MessageHandler messageHandler, IslandTeam team, TeamRole role, RolePermissionType type) {
        return clickData -> {
            final RolePermissions permissions = role.permissions();
            final RolePermission permission = permissions.getPermission(type);
            if (permission == null) {
                return ClickAction.Result.CANCEL;
            }
            final RolePermissionDefinition definition = permission.definition();
            final DSPlayer player = clickData.menu().player();
            if (InventoryUtil.isLeftClick(clickData.click())) {
                if (definition.isTogglePermission()) {
                    role.togglePermission(messageHandler, team, player, type);
                    clickData.menu().refresh(clickData.slot());
                    return ClickAction.Result.CANCEL;
                }
                // todo
                clickData.menu().refresh(clickData.slot());
                return ClickAction.Result.CANCEL;
            }
            if (permission.definition().isTogglePermission()) {
                clickData.menu().refresh(clickData.slot());
                return ClickAction.Result.CANCEL;
            }
            return ClickAction.Result.CANCEL;
        };
    }


    private final IslandTeam team;
    private final TeamRole role;

    public PermissionMenu(
            DSPlayer player,
            InventoryType type,
            Component title,
            Map<Integer, MenuButton> menuItems,
            Map<Integer, ClickAction> clickActions,
            List<? extends MenuButton> pageItems,
            List<Integer> pageSlots,
            List<Pattern> patterns,
            @Nullable ClickAction defaultClickAction,
            IslandTeam team,
            TeamRole role
    ) {
        super(player, type, title, menuItems, clickActions, pageItems, pageSlots, patterns, defaultClickAction);
        this.team = team;
        this.role = role;
    }
}
