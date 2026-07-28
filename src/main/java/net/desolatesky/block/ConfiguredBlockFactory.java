package net.desolatesky.block;

import net.desolatesky.block.behavior.BlockBehavior;
import net.desolatesky.block.behavior.BlockDropBehavior;
import net.desolatesky.block.behavior.MiningSpeedBehavior;
import net.desolatesky.block.behavior.PlaceRequirementsBehavior;
import net.desolatesky.block.behavior.core.VoidCoreBehavior;
import net.desolatesky.block.behavior.impl.CactusBehavior;
import net.desolatesky.block.behavior.impl.CactusFlowerBehavior;
import net.desolatesky.block.behavior.impl.ComposterBehavior;
import net.desolatesky.block.behavior.impl.CraftingTableBehavior;
import net.desolatesky.block.behavior.impl.CropBehavior;
import net.desolatesky.block.behavior.impl.DryGrassBehavior;
import net.desolatesky.block.behavior.impl.SupportedBlockBehavior;
import net.desolatesky.block.behavior.impl.WoodPlanksBehavior;
import net.desolatesky.block.definition.BlockDefinition;
import net.desolatesky.block.handler.DSBlockHandler;
import net.desolatesky.block.property.IntBlockProperty;
import net.desolatesky.item.ItemIds;
import net.desolatesky.util.BlockUtil;
import net.kyori.adventure.key.Key;
import net.minestom.server.MinecraftServer;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockHandler;
import net.minestom.server.item.Material;
import net.minestom.server.item.MaterialKeys;
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
                .skipAttributes()
                .defineBehaviors(new VoidCoreBehavior(5))
                .build());
        this.register(BlockDefinition.builder().key(Block.SCULK.key())
                .defaultBlock(Block.SCULK)
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
                .attributes(Set.of(BlockAttributes.HOE_MINEABLE))
                .defineBehaviors(DryGrassBehavior.DRY_GRASS_BEHAVIOR)
                .defineBehaviors(PlaceRequirementsBehavior.DIRT_SUPPORT_REQUIREMENT)
                .build()
        );
        this.register(BlockDefinition.builder().key(Block.SHORT_DRY_GRASS.key())
                .defaultBlock(Block.SHORT_DRY_GRASS)
                .attributes(Set.of(BlockAttributes.HOE_MINEABLE))
                .defineBehaviors(DryGrassBehavior.DRY_GRASS_BEHAVIOR)
                .defineBehaviors(PlaceRequirementsBehavior.DIRT_SUPPORT_REQUIREMENT)
                .build()
        );
        this.register(BlockDefinition.builder().key(Block.TALL_DRY_GRASS.key())
                .defaultBlock(Block.TALL_DRY_GRASS)
                .attributes(Set.of(BlockAttributes.HOE_MINEABLE))
                .defineBehaviors(DryGrassBehavior.DRY_GRASS_BEHAVIOR)
                .defineBehaviors(PlaceRequirementsBehavior.DIRT_SUPPORT_REQUIREMENT)
                .build()
        );
        this.register(BlockDefinition.builder().key(BlockIds.VOID_INFUSED_BUSH)
                .defaultBlock(Block.CLOSED_EYEBLOSSOM)
                .attributes(Set.of(BlockAttributes.HOE_MINEABLE))
                .defineBehaviors(DryGrassBehavior.DRY_GRASS_BEHAVIOR)
                .defineBehaviors(PlaceRequirementsBehavior.DIRT_SUPPORT_REQUIREMENT)
                .build()
        );
        this.register(BlockDefinition.builder().key(Block.CARROTS.key())
                .defaultBlock(Block.CARROTS)
                .attributes(Set.of(BlockAttributes.HOE_MINEABLE))
                .defineBehaviors(new CropBehavior(
                        new IntBlockProperty("age", 0, 7),
                        50,
                        Material.CARROT.key(),
                        ItemIds.VOID_INFUSED_CARROT,
                        60,
                        1,
                        4,
                        3
                ))
                .build());
        this.register(BlockDefinition.builder().key(Block.CACTUS.key())
                .defaultBlock(Block.CACTUS)
                .attributes(Set.of(BlockAttributes.AXE_MINEABLE))
                .defineBehaviors(new CactusBehavior(10, 50))
                .build());
        this.register(BlockDefinition.builder().key(Block.CACTUS_FLOWER.key())
                .defaultBlock(Block.CACTUS_FLOWER)
                .attributes(Set.of(BlockAttributes.HOE_MINEABLE))
                .defineBehaviors(new CactusFlowerBehavior(100, 0.2, 3))
                .defineBehaviors(new SupportedBlockBehavior(Direction.DOWN, false, Set.of(Block.CACTUS.key())))
                .build());
    }

    private void registerBlocks() {
        this.register(BlockDefinition.builder().key(Block.DIRT.key())
                .defaultBlock(Block.DIRT)
                .attributes(Set.of(BlockAttributes.SHOVEL_MINEABLE))
                .defineBehaviors(MiningSpeedBehavior.ticks(20))
                .defineBehaviors(BlockDropBehavior.constantDrop(MaterialKeys.DIRT.key()))
                .build());
        this.register(BlockDefinition.builder().key(Block.GRASS_BLOCK.key())
                .defaultBlock(Block.GRASS_BLOCK)
                .skipAttributes()
                .build());
        this.register(BlockDefinition.builder().key(Block.FARMLAND.key())
                .defaultBlock(Block.FARMLAND)
                .attributes(Set.of(BlockAttributes.SHOVEL_MINEABLE))
                .defineBehaviors(MiningSpeedBehavior.ticks(20))
                        .defineBehaviors(BlockDropBehavior.constantDrop(MaterialKeys.DIRT.key()))
                .build());
    }

    private void registerWood() {
        this.register(BlockDefinition.builder().key(BlockIds.THATCH_PLANKS)
                .defaultBlock(Block.BAMBOO_PLANKS)
                .attributes(Set.of(BlockAttributes.AXE_MINEABLE))
                .defineBehaviors(new WoodPlanksBehavior(ItemIds.THATCH_PLANKS))
                .build());
        this.register(BlockDefinition.builder().key(BlockIds.THATCH_SLAB)
                .defaultBlock(Block.BAMBOO_SLAB)
                .attributes(Set.of(BlockAttributes.AXE_MINEABLE))
                .defineBehaviors(new WoodPlanksBehavior(ItemIds.THATCH_SLAB))
                .build());
    }

    private void registerInventoryBlocks() {
        this.register(BlockDefinition.builder().key(Block.CRAFTING_TABLE.key())
                .defaultBlock(Block.CRAFTING_TABLE)
                .attributes(Set.of(BlockAttributes.AXE_MINEABLE))
                .defineBehaviors(new CraftingTableBehavior())
                .build());
        this.register(BlockDefinition.builder().key(Block.COMPOSTER.key())
                .defaultBlock(Block.COMPOSTER)
                .attributes(Set.of(BlockAttributes.AXE_MINEABLE))
                .defineBehaviors(new ComposterBehavior())
                .build()
        );
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
