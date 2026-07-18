package net.desolatesky.block.behavior.core;

import net.desolatesky.block.BlockIds;
import net.desolatesky.block.BlockTags;
import net.desolatesky.block.behavior.ClickBehavior;
import net.desolatesky.block.definition.BlockDefinition;
import net.desolatesky.entity.EntityFactory;
import net.desolatesky.entity.EntityIds;
import net.desolatesky.entity.EntityTags;
import net.desolatesky.island.permission.IslandPermission;
import net.desolatesky.item.ItemTags;
import net.desolatesky.item.definition.ItemDefinition;
import net.desolatesky.player.DSPlayer;
import net.desolatesky.world.DSWorld;
import net.desolatesky.world.VoidWorld;
import net.kyori.adventure.key.Key;
import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.PlayerHand;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;

import java.util.Collection;
import java.util.List;

public final class VoidCoreClickBehavior implements ClickBehavior {

    @Override
    public ClickBehavior.Result onRightClick(DSWorld world, DSPlayer player, PlayerHand hand, Point clickedPos, Block clickedBlock, ItemStack clickedWith) {
        if (!(world instanceof final VoidWorld voidWorld)) {
            return Result.ALLOW;
        }
        final Key islandCoreSpawnerKey = clickedWith.getTag(ItemTags.ISLAND_CORE_SPAWNER_KEY);
        if (islandCoreSpawnerKey == null) {
            return Result.ALLOW;
        }
        final IslandCoreMobSpawner spawner = IslandCoreMobSpawner.SPAWNERS.get(islandCoreSpawnerKey);
        if (spawner == null) {
            return Result.ALLOW;
        }
        if (!voidWorld.island().hasPermission(player.getUuid(), IslandPermission.INTERACT_VOID_CORE)) {
            return Result.BLOCK_INTERACTION;
        }
        final EntityFactory entityFactory = world.entityFactory();
        entityFactory.createEntity(EntityIds.ISLAND_CORE_SPAWNER_DISPLAY, voidWorld.island(), e -> {
            e.setTag(EntityTags.ITEM_DISPLAY_KEY, spawner.itemDisplayKey());
            e.setInstance(world.asInstance(), clickedPos.asBlockVec().add(0.5, 1.5, 0.5));
            world.setBlock(clickedPos, clickedBlock.withTag(BlockTags.ISLAND_CORE_SPAWNER_KEY, islandCoreSpawnerKey)
                    .withTag(BlockTags.ISLAND_CORE_DISPLAY_ENTITY_ID, e.getUuid()));
            player.setItemInHand(hand, clickedWith.withAmount(clickedWith.amount() - 1));
        });
        return Result.BLOCK_INTERACTION;
    }

    @Override
    public Result onLeftClick(DSWorld world, DSPlayer player, Point clickedPos, Block clickedBlock, ItemStack clickedWith) {
        if (!(world instanceof final VoidWorld voidWorld)) {
            return Result.ALLOW;
        }
        final Key islandCoreSpawnerKey = clickedBlock.getTag(BlockTags.ISLAND_CORE_SPAWNER_KEY);
        if (islandCoreSpawnerKey == null) {
            return Result.ALLOW;
        }
        final IslandCoreMobSpawner spawner = IslandCoreMobSpawner.SPAWNERS.get(islandCoreSpawnerKey);
        if (spawner == null) {
            return Result.ALLOW;
        }
        if (!voidWorld.island().hasPermission(player.getUuid(), IslandPermission.INTERACT_VOID_CORE)) {
            return Result.BLOCK_INTERACTION;
        }
        final ItemDefinition itemDefinition = world.itemFactory().getItemDefinition(spawner.itemDisplayKey());
        if (itemDefinition == null) {
            return Result.BLOCK_INTERACTION;
        }
        final BlockDefinition blockDefinition = world.blockFactory().getBlockDefinition(BlockIds.VOID_CORE);
        if (blockDefinition == null) {
            return Result.BLOCK_INTERACTION;
        }
        final VoidCoreTickBehavior voidCoreTickBehavior = blockDefinition.getBehavior(Type.VOID_CORE_BEHAVIOR);
        if (voidCoreTickBehavior == null) {
            return Result.BLOCK_INTERACTION;
        }
        final ItemStack itemStack = itemDefinition.defaultItemStack();
        voidCoreTickBehavior.removeSpawner(world, clickedPos, clickedBlock);
        player.getInventory().addItemStack(itemStack);
        world.setBlock(clickedPos, clickedBlock.withTag(BlockTags.ISLAND_CORE_SPAWNER_KEY, null));
        return Result.ALLOW;
    }

    @Override
    public Collection<Type<?>> types() {
        return List.of(Type.CLICK);
    }
}
