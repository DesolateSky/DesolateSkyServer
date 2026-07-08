package com.fisherl.desolatesky.block;

import com.fisherl.desolatesky.block.behavior.BlockBehavior;
import com.fisherl.desolatesky.block.behavior.DirectionalSpreadBehavior;
import com.fisherl.desolatesky.block.behavior.GrowthBehavior;
import com.fisherl.desolatesky.block.behavior.MiningSpeedBehavior;
import com.fisherl.desolatesky.block.behavior.core.IslandCoreClickBehavior;
import com.fisherl.desolatesky.block.behavior.core.IslandCoreTickBehavior;
import com.fisherl.desolatesky.block.definition.BlockDefinition;
import com.fisherl.desolatesky.block.handler.DSBlockHandler;
import com.fisherl.desolatesky.block.property.IntBlockProperty;
import com.fisherl.desolatesky.block.setting.BlockSetting;
import com.fisherl.desolatesky.block.setting.BlockSettings;
import com.fisherl.desolatesky.block.setting.SupportedBlockSetting;
import com.fisherl.desolatesky.util.BlockUtil;
import net.kyori.adventure.key.Key;
import net.minestom.server.MinecraftServer;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockHandler;
import net.minestom.server.utils.Direction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public final class ConfiguredBlockFactory implements BlockFactory {

    private final Map<Key, BlockDefinition> blocks;

    public ConfiguredBlockFactory() {
        this.blocks = new HashMap<>();
    }

    @Override
    public void initialize() {
        this.register(BlockDefinition.builder().key(BlockIds.WHEAT)
                .defaultBlock(Block.WHEAT)
                .settings(BlockSettings.builder().setting(BlockSetting.Type.SUPPORTED_BLOCK, SupportedBlockSetting.blocks(Direction.DOWN, Set.of(Block.FARMLAND.key()))).build())
                .defineBehavior(BlockBehavior.Type.RANDOM_TICK, new GrowthBehavior(new IntBlockProperty("age", 0, 7), 100)).build());
        this.register(BlockDefinition.builder().key(BlockIds.BAMBOO)
                .defaultBlock(Block.BAMBOO)
                .settings(BlockSettings.builder().setting(BlockSetting.Type.SUPPORTED_BLOCK, SupportedBlockSetting.blocks(Direction.DOWN, Set.of(Block.GRASS_BLOCK.key()))).build())
                .defineBehavior(BlockBehavior.Type.RANDOM_TICK, new DirectionalSpreadBehavior(100, Direction.UP, 5)).build());
        this.register(BlockDefinition.builder().key(BlockIds.ISLAND_CORE)
                .defaultBlock(Block.SCULK_SHRIEKER.withHandler(DSBlockHandler.newTickingBlockHandler(BlockIds.ISLAND_CORE, this)))
                .settings(BlockSettings.NONE)
                .defineBehavior(BlockBehavior.Type.MINING_SPEED, MiningSpeedBehavior.UNBREAKABLE)
                .defineBehaviors(List.of(BlockBehavior.Type.TICK, BlockBehavior.Type.ISLAND_CORE_BEHAVIOR) , new IslandCoreTickBehavior(5))
                .defineBehavior(BlockBehavior.Type.CLICK, new IslandCoreClickBehavior())
                .build());
        this.register(BlockDefinition.builder().key(Block.SCULK.key())
                .defaultBlock(Block.SCULK)
                .settings(BlockSettings.NONE)
                .defineBehavior(BlockBehavior.Type.MINING_SPEED, MiningSpeedBehavior.UNBREAKABLE)
                .build());

        this.blocks.forEach((key, definition) -> {
            final BlockHandler blockHandler = definition.defaultBlock().handler();
            if (blockHandler == null) {
                return;
            }
            MinecraftServer.getBlockManager().registerHandler(key.key(), () -> blockHandler);
        });
    }

    private void register(BlockDefinition definition) {
        this.blocks.put(definition.key(), definition);
    }

    @Override
    public Optional<BlockDefinition> getBlockDefinition(Key id) {
        return Optional.ofNullable(this.blocks.get(id));
    }

    @Override
    public Optional<BlockDefinition> getBlockDefinition(Block block) {
        return this.getBlockDefinition(this.getBlockId(block));
    }

    @Override
    public Key getBlockId(Block block) {
        return BlockUtil.getBlockId(block);
    }
}
