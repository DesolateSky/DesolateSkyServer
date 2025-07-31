package net.desolatesky.team.role;

import net.desolatesky.util.ComponentUtil;
import net.desolatesky.util.Namespace;
import net.desolatesky.util.TextUtil;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import org.jetbrains.annotations.NotNull;

public enum RolePermissionType implements Keyed {

    BREAK_BLOCK(ItemStack.of(Material.IRON_PICKAXE)),
    PLACE_BLOCK(ItemStack.of(Material.GRASS_BLOCK)),
    INTERACT_BLOCK(ItemStack.of(Material.OAK_BUTTON)),
    INTERACT_ENTITY(ItemStack.of(Material.CHICKEN_SPAWN_EGG)),
    USE_ITEM(ItemStack.of(Material.IRON_HOE)),
    INVITE_MEMBER(ItemStack.of(Material.PAPER)),
    KICK_MEMBER(ItemStack.of(Material.BARRIER)),
    MANAGE_PERMISSIONS(ItemStack.of(Material.WRITABLE_BOOK)),

    ;

    private final Key key;
    private final ItemStack icon;

    RolePermissionType(ItemStack icon) {
        this.key = Namespace.key("permissions", this.name().toLowerCase());
        this.icon = icon.withCustomName(ComponentUtil.noItalics(TextUtil.capitalize(this.name(), "_")).color(NamedTextColor.WHITE));
    }

    public ItemStack icon() {
        return this.icon;
    }


    @Override
    public @NotNull Key key() {
        return this.key;
    }

}
