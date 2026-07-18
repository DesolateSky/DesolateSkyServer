package net.desolatesky.block;

import net.desolatesky.block.behavior.BlockBehavior;
import net.desolatesky.block.behavior.MiningSpeedBehavior;
import net.desolatesky.block.behavior.core.VoidCoreClickBehavior;
import net.desolatesky.block.behavior.core.VoidCoreTickBehavior;
import net.desolatesky.block.behavior.impl.CactusBehavior;
import net.desolatesky.block.behavior.impl.CactusFlowerBehavior;
import net.desolatesky.block.behavior.impl.CraftingTableBehavior;
import net.desolatesky.block.behavior.impl.CropBehavior;
import net.desolatesky.block.behavior.impl.DryGrassBehavior;
import net.desolatesky.block.behavior.impl.WoodPlanksBehavior;
import net.desolatesky.block.definition.BlockDefinition;
import net.desolatesky.block.handler.DSBlockHandler;
import net.desolatesky.block.property.BlockProperties;
import net.desolatesky.block.property.IntBlockProperty;
import net.desolatesky.block.setting.BlockSetting;
import net.desolatesky.block.setting.BlockSettings;
import net.desolatesky.block.setting.SupportedBlockSetting;
import net.desolatesky.item.ItemIds;
import net.desolatesky.util.BlockUtil;
import net.kyori.adventure.key.Key;
import net.minestom.server.MinecraftServer;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockHandler;
import net.minestom.server.item.Material;
import net.minestom.server.utils.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class ConfiguredBlockFactory implements BlockFactory {

    private final Map<Key, BlockDefinition> blocks;

    public ConfiguredBlockFactory() {
        this.blocks = new HashMap<>();
    }

    @Override
    public void initialize() {
//        this.register(BlockDefinition.builder().key(BlockIds.WHEAT)
//                .defaultBlock(Block.WHEAT)
//                .settings(BlockSettings.builder().setting(BlockSetting.Type.SUPPORTED_BLOCK, SupportedBlockSetting.blocks(Direction.DOWN, Set.of(Block.FARMLAND.key()))).build())
//                .defineBehavior(BlockBehavior.Type.RANDOM_TICK, new GrowthBehavior(new IntBlockProperty("age", 0, 7), 100)).build());
//        this.register(BlockDefinition.builder().key(BlockIds.BAMBOO)
//                .defaultBlock(Block.BAMBOO)
//                .settings(BlockSettings.builder().setting(BlockSetting.Type.SUPPORTED_BLOCK, SupportedBlockSetting.blocks(Direction.DOWN, Set.of(Block.GRASS_BLOCK.key()))).build())
//                .defineBehavior(BlockBehavior.Type.RANDOM_TICK, new DirectionalSpreadBehavior(100, Direction.UP, 5)).build());
        this.register(BlockDefinition.builder().key(BlockIds.VOID_CORE)
                .defaultBlock(Block.SCULK_SHRIEKER.withHandler(DSBlockHandler.newTickingBlockHandler(BlockIds.VOID_CORE, this)))
                .settings(BlockSettings.NONE)
                .skipAttributes()
                .defineBehavior(BlockBehavior.Type.MINING_SPEED, MiningSpeedBehavior.UNBREAKABLE)
                .defineBehaviors(new VoidCoreTickBehavior(5))
                .defineBehavior(BlockBehavior.Type.CLICK, new VoidCoreClickBehavior())
                .build());
        this.register(BlockDefinition.builder().key(Block.SCULK.key())
                .defaultBlock(Block.SCULK)
                .settings(BlockSettings.NONE)
                .skipAttributes()
                .defineBehavior(BlockBehavior.Type.MINING_SPEED, MiningSpeedBehavior.UNBREAKABLE)
                .build());
//        this.register(BlockDefinition.builder().key(Block.DIRT.key())
//                .defaultBlock(Block.DIRT)
//                .settings(BlockSettings.NONE)
//                .build());

        this.registerBlocks();
        this.registerWood();
        this.registerCrops();
        this.registerInventoryBlocks();

        this.blocks.forEach((key, definition) -> {
            final BlockHandler blockHandler = definition.defaultBlock().handler();
            if (blockHandler == null) {
                return;
            }
            MinecraftServer.getBlockManager().registerHandler(key.key(), () -> blockHandler);
        });
    }

    private void registerCrops() {
        this.register(BlockDefinition.builder().key(BlockIds.DRY_GRASS_SEEDS)
                .defaultBlock(Block.BEETROOTS.withProperty("age", "0"))
                .settings(BlockSettings.builder().setting(BlockSetting.Type.SUPPORTED_BLOCK,
                        SupportedBlockSetting.blocks(Direction.DOWN, false, Set.of(Block.GRASS_BLOCK.key(), Block.DIRT.key()))).build())
                .attributes(Set.of(BlockAttributes.HOE_MINEABLE))
                .defineBehaviors(DryGrassBehavior.DRY_GRASS_BEHAVIOR)
                .build()
        );
        this.register(BlockDefinition.builder().key(Block.SHORT_DRY_GRASS.key())
                .defaultBlock(Block.SHORT_DRY_GRASS)
                .settings(BlockSettings.builder().setting(BlockSetting.Type.SUPPORTED_BLOCK,
                        SupportedBlockSetting.blocks(Direction.DOWN, false, Set.of(Block.GRASS_BLOCK.key(), Block.DIRT.key()))).build())
                .attributes(Set.of(BlockAttributes.HOE_MINEABLE))
                .defineBehaviors(DryGrassBehavior.DRY_GRASS_BEHAVIOR)
                .build()
        );
        this.register(BlockDefinition.builder().key(Block.TALL_DRY_GRASS.key())
                .defaultBlock(Block.TALL_DRY_GRASS)
                .settings(BlockSettings.builder().setting(BlockSetting.Type.SUPPORTED_BLOCK,
                        SupportedBlockSetting.blocks(Direction.DOWN, false, Set.of(Block.GRASS_BLOCK.key(), Block.DIRT.key()))).build())
                .attributes(Set.of(BlockAttributes.HOE_MINEABLE))
                .defineBehaviors(DryGrassBehavior.DRY_GRASS_BEHAVIOR)
                .build()
        );
        this.register(BlockDefinition.builder().key(BlockIds.VOID_INFUSED_BUSH)
                .defaultBlock(Block.CLOSED_EYEBLOSSOM)
                .settings(BlockSettings.builder().setting(BlockSetting.Type.SUPPORTED_BLOCK,
                        SupportedBlockSetting.blocks(Direction.DOWN, false, Set.of(Block.GRASS_BLOCK.key(), Block.DIRT.key()))).build())
                .attributes(Set.of(BlockAttributes.HOE_MINEABLE))
                .defineBehaviors(DryGrassBehavior.DRY_GRASS_BEHAVIOR)
                .build()
        );
        this.register(BlockDefinition.builder().key(Block.CARROTS.key())
                .defaultBlock(Block.CARROTS)
                .settings(BlockSettings.builder().setting(BlockSetting.Type.SUPPORTED_BLOCK,
                                SupportedBlockSetting.blocks(Direction.DOWN, false, Set.of(Block.FARMLAND.key()), b -> {
                                    final Integer moisture = BlockProperties.FARMLAND_MOISTURE_PROPERTY.read(b);
                                    return moisture != null && moisture >= BlockProperties.FARMLAND_MOISTURE_PROPERTY.max();
                                }))
                        .build())
                .attributes(Set.of(BlockAttributes.HOE_MINEABLE))
                .defineBehaviors(new CropBehavior(
                        new IntBlockProperty("age", 0, 7),
                        100,
                        Material.CARROT.key(),
                        60,
                        1,
                        4
                ))
                .build());
        this.register(BlockDefinition.builder().key(Block.CACTUS.key())
                .defaultBlock(Block.CACTUS)
                .settings(BlockSettings.builder().setting(BlockSetting.Type.SUPPORTED_BLOCK,
                                SupportedBlockSetting.blocks(Direction.DOWN, false, Set.of(Block.GRASS_BLOCK.key())))
                        .build())
                .attributes(Set.of(BlockAttributes.AXE_MINEABLE))
                .defineBehaviors(new CactusBehavior(50))
                .build());
        this.register(BlockDefinition.builder().key(Block.CACTUS_FLOWER.key())
                .defaultBlock(Block.CACTUS_FLOWER)
                .settings(BlockSettings.builder().setting(BlockSetting.Type.SUPPORTED_BLOCK,
                                SupportedBlockSetting.blocks(Direction.DOWN, false, Set.of(Block.CACTUS.key())))
                        .build())
                .attributes(Set.of(BlockAttributes.HOE_MINEABLE))
                .defineBehaviors(new CactusFlowerBehavior(100, 0.2, 3))
                .build());
    }

    private void registerBlocks() {
        this.register(BlockDefinition.builder().key(Block.DIRT.key())
                .defaultBlock(Block.DIRT)
                .settings(BlockSettings.NONE)
                .skipAttributes()
                .build());
        this.register(BlockDefinition.builder().key(Block.GRASS_BLOCK.key())
                .defaultBlock(Block.GRASS_BLOCK)
                .settings(BlockSettings.NONE)
                .skipAttributes()
                .build());
        this.register(BlockDefinition.builder().key(Block.FARMLAND.key())
                .defaultBlock(Block.FARMLAND)
                .settings(BlockSettings.NONE)
                .skipAttributes()
                .build());
    }

    private void registerWood() {
        this.register(BlockDefinition.builder().key(BlockIds.THATCH_PLANKS)
                .defaultBlock(Block.BAMBOO_PLANKS)
                .settings(BlockSettings.NONE)
                .attributes(Set.of(BlockAttributes.AXE_MINEABLE))
                .defineBehaviors(new WoodPlanksBehavior(ItemIds.THATCH_PLANKS))
                .build());
    }

    private void registerInventoryBlocks() {
        this.register(BlockDefinition.builder().key(Block.CRAFTING_TABLE.key())
                .defaultBlock(Block.CRAFTING_TABLE)
                .settings(BlockSettings.NONE)
                .attributes(Set.of(BlockAttributes.AXE_MINEABLE))
                .defineBehaviors(new CraftingTableBehavior())
                .build());
    }

    private void register(BlockDefinition definition) {
        this.blocks.put(definition.key(), definition);
    }

    @Override
    public @Nullable BlockDefinition getBlockDefinition(Key id) {
        return this.blocks.get(id);
    }

    @Override
    public @Nullable BlockDefinition getBlockDefinition(Block block) {
        return this.getBlockDefinition(this.getBlockId(block));
    }

    @Override
    public Key getBlockId(Block block) {
        return BlockUtil.getBlockId(block);
    }
}
