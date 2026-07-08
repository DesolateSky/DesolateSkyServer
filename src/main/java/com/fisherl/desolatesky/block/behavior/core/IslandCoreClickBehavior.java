package com.fisherl.desolatesky.block.behavior.core;

import com.fisherl.desolatesky.block.BlockIds;
import com.fisherl.desolatesky.block.BlockTags;
import com.fisherl.desolatesky.block.behavior.ClickBehavior;
import com.fisherl.desolatesky.entity.EntityFactory;
import com.fisherl.desolatesky.entity.EntityIds;
import com.fisherl.desolatesky.entity.EntityTags;
import com.fisherl.desolatesky.island.permission.IslandPermission;
import com.fisherl.desolatesky.item.ItemTags;
import com.fisherl.desolatesky.item.definition.ItemDefinition;
import com.fisherl.desolatesky.player.DSPlayer;
import com.fisherl.desolatesky.world.DSWorld;
import com.fisherl.desolatesky.world.IslandWorld;
import net.kyori.adventure.key.Key;
import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.PlayerHand;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;

public final class IslandCoreClickBehavior implements ClickBehavior {

    @Override
    public ClickBehavior.Result onRightClick(DSWorld world, DSPlayer player, PlayerHand hand, Point clickedPos, Block clickedBlock, ItemStack clickedWith) {
        if (!(world instanceof final IslandWorld islandWorld)) {
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
        if (!islandWorld.island().hasPermission(player.getUuid(), IslandPermission.INTERACT_CORE)) {
            return Result.BLOCK_INTERACTION;
        }
        final EntityFactory entityFactory = world.entityFactory();
        entityFactory.createEntity(EntityIds.ISLAND_CORE_SPAWNER_DISPLAY, islandWorld.island(), e -> {
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
        if (!(world instanceof final IslandWorld islandWorld)) {
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
        if (!islandWorld.island().hasPermission(player.getUuid(), IslandPermission.INTERACT_CORE)) {
            return Result.BLOCK_INTERACTION;
        }
        return world.itemFactory().getItemDefinition(spawner.itemDisplayKey())
                .map(ItemDefinition::defaultItemStack)
                .map(item -> {
                    final IslandCoreTickBehavior behavior = world.blockFactory().getBlockDefinition(BlockIds.ISLAND_CORE)
                            .flatMap(def -> def.getBehavior(Type.ISLAND_CORE_BEHAVIOR))
                            .orElse(null);
                    if (behavior == null) {
                        return Result.BLOCK_INTERACTION;
                    }
                    behavior.removeSpawner(world, clickedPos, clickedBlock);
                    player.getInventory().addItemStack(item);
                    world.setBlock(clickedPos, clickedBlock.withTag(BlockTags.ISLAND_CORE_SPAWNER_KEY, null));
                    return Result.ALLOW;
                }).orElse(Result.BLOCK_INTERACTION);
    }
}
