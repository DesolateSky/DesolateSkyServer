package net.desolatesky.team.role;

import net.desolatesky.block.DSBlockRegistry;
import net.desolatesky.block.handler.DSBlockHandler;
import net.desolatesky.entity.EntityKey;
import net.desolatesky.entity.EntityKeys;
import net.desolatesky.item.DSItemRegistry;
import net.desolatesky.util.ComponentUtil;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public final class RolePermissionsRegistry {

    public static RolePermissionsRegistry load(DSBlockRegistry blockRegistry, DSItemRegistry itemRegistry) {
        return new RolePermissionsRegistry(blockRegistry, itemRegistry);
    }

    private final Map<RolePermissionType, RolePermissionDefinition> definitions = new HashMap<>();

    private final DSBlockRegistry blockRegistry;
    private final DSItemRegistry itemRegistry;

    private RolePermissionDefinition breakBlock;
    private RolePermissionDefinition placeBlock;
    private RolePermissionDefinition interactBlock;
    private RolePermissionDefinition interactEntity;
    private RolePermissionDefinition useItem;
    private RolePermissionDefinition inviteMember;
    private RolePermissionDefinition kickMember;
    private RolePermissionDefinition managePermissions;

    private RolePermissionsRegistry(DSBlockRegistry blockRegistry, DSItemRegistry itemRegistry) {
        this.blockRegistry = blockRegistry;
        this.itemRegistry = itemRegistry;
    }

    public void initialize() {
        this.breakBlock = this.register(new RolePermissionDefinition(RolePermissionType.BREAK_BLOCK, this.blockRegistry.getKeys(), HashSet::new, this::itemStackFromBlock, RolePermissionSetting.WHITELIST));
        this.placeBlock = this.register(new RolePermissionDefinition(RolePermissionType.PLACE_BLOCK, this.blockRegistry.getKeys(), HashSet::new, this::itemStackFromBlock, RolePermissionSetting.WHITELIST));
        this.interactBlock = this.register(new RolePermissionDefinition(RolePermissionType.INTERACT_BLOCK, this.blockRegistry.getKeys(), HashSet::new, this::itemStackFromBlock, RolePermissionSetting.WHITELIST));
        this.interactEntity = this.register(new RolePermissionDefinition(RolePermissionType.INTERACT_ENTITY, EntityKeys.getKeys(), HashSet::new, this::entityFromKey, RolePermissionSetting.WHITELIST));
        this.useItem = this.register(new RolePermissionDefinition(RolePermissionType.USE_ITEM, this.itemRegistry.getKeys(), HashSet::new, this::itemFromKey, RolePermissionSetting.WHITELIST));
        this.inviteMember = this.register(new RolePermissionDefinition(RolePermissionType.INVITE_MEMBER, RolePermissionSetting.WHITELIST));
        this.kickMember = this.register(new RolePermissionDefinition(RolePermissionType.KICK_MEMBER, RolePermissionSetting.WHITELIST));
        this.managePermissions = this.register(new RolePermissionDefinition(RolePermissionType.MANAGE_PERMISSIONS, RolePermissionSetting.WHITELIST));
    }

    public RolePermissionDefinition breakBlock() {
        return this.breakBlock;
    }

    public RolePermissionDefinition placeBlock() {
        return this.placeBlock;
    }

    public RolePermissionDefinition interactBlock() {
        return this.interactBlock;
    }

    public RolePermissionDefinition interactEntity() {
        return this.interactEntity;
    }

    public RolePermissionDefinition useItem() {
        return this.useItem;
    }

    public RolePermissionDefinition inviteMember() {
        return this.inviteMember;
    }

    public RolePermissionDefinition kickMember() {
        return this.kickMember;
    }

    public RolePermissionDefinition getDefinition(RolePermissionType type) {
        return this.definitions.get(type);
    }

    public RolePermissionDefinition managePermissions() {
        return this.managePermissions;
    }

    private RolePermissionDefinition register(RolePermissionDefinition definition) {
        this.definitions.put(definition.type(), definition);
        return definition;
    }

    public @Unmodifiable Map<RolePermissionType, RolePermissionDefinition> getAll() {
        return Collections.unmodifiableMap(this.definitions);
    }

    public @Unmodifiable Collection<RolePermissionDefinition> getDefinitions() {
        return Collections.unmodifiableCollection(this.definitions.values());
    }

    private ItemStack itemStackFromBlock(Key key) {
        final Block block = this.blockRegistry.create(key);
        if (block == null) {
            return ItemStack.of(Material.BEDROCK).withCustomName(ComponentUtil.noItalics("Unknown Block: " + key.value()).color(NamedTextColor.RED));
        }
        final DSBlockHandler handler = this.blockRegistry.getHandlerForBlock(block);
        if (handler == null) {
            return ItemStack.of(Material.BEDROCK).withCustomName(ComponentUtil.noItalics("Unknown Block: " + key.value()).color(NamedTextColor.RED));
        }
        return handler.menuItem();
    }

    private ItemStack entityFromKey(Key key) {
        final EntityKey entityKey = EntityKeys.get(key);
        if (entityKey == null) {
            return ItemStack.of(Material.BEDROCK).withCustomName(ComponentUtil.noItalics("Unknown Entity: " + key.value()).color(NamedTextColor.RED));
        }
        return entityKey.menuItem().withCustomName(entityKey.displayName());
    }

    private ItemStack itemFromKey(Key key) {
        final ItemStack itemStack = this.itemRegistry.create(key);
        if (itemStack == null) {
            return ItemStack.of(Material.BEDROCK).withCustomName(ComponentUtil.noItalics("Unknown Item: " + key.value()).color(NamedTextColor.RED));
        }
        return itemStack;
    }

}
