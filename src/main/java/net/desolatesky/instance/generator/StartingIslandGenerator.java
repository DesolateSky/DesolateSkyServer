package net.desolatesky.instance.generator;

import net.desolatesky.block.BlockBuilder;
import net.desolatesky.block.BlockProperties;
import net.desolatesky.block.BlockTags;
import net.desolatesky.block.DSBlocks;
import net.desolatesky.block.handler.BlockHandlers;
import net.desolatesky.block.property.SlabType;
import net.desolatesky.instance.team.TeamInstance;
import net.desolatesky.instance.biome.Biomes;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.generator.GenerationUnit;
import net.minestom.server.instance.generator.Generator;
import net.minestom.server.instance.generator.UnitModifier;
import net.minestom.server.utils.Direction;
import org.jetbrains.annotations.NotNull;

public final class StartingIslandGenerator implements Generator {

    private static final int STARTING_BLOCK_BREAK_MILLIS = 1_000;

    private final TeamInstance instance;

    public StartingIslandGenerator(TeamInstance instance) {
        this.instance = instance;
    }

    private final Block bottomBlock = BlockBuilder.of(DSBlocks.get(), Block.WAXED_EXPOSED_CUT_COPPER_SLAB)
            .property(BlockProperties.SLAB_TYPE, SlabType.TOP)
            .tag(BlockTags.UNBREAKABLE, true)
            .build();
    private final Block topTrapdoor = BlockBuilder.of(DSBlocks.get(), Block.WAXED_EXPOSED_COPPER_TRAPDOOR, BlockHandlers.TRAPDOOR_HANDLER)
            .property(BlockProperties.OPEN, false)
            .tag(BlockTags.BREAK_TIME, STARTING_BLOCK_BREAK_MILLIS)
            .build();
    private final Block westTrapdoor = BlockBuilder.of(DSBlocks.get(), Block.WAXED_EXPOSED_COPPER_TRAPDOOR, BlockHandlers.TRAPDOOR_HANDLER)
            .property(BlockProperties.OPEN, true)
            .property(BlockProperties.FACING, Direction.WEST)
            .tag(BlockTags.BREAK_TIME, STARTING_BLOCK_BREAK_MILLIS)
            .build();
    private final Block eastTrapdoor = BlockBuilder.of(DSBlocks.get(), Block.WAXED_EXPOSED_COPPER_TRAPDOOR, BlockHandlers.TRAPDOOR_HANDLER)
            .property(BlockProperties.OPEN, true)
            .property(BlockProperties.FACING, Direction.EAST)
            .tag(BlockTags.BREAK_TIME, STARTING_BLOCK_BREAK_MILLIS)
            .build();
    private final Block southTrapdoor = BlockBuilder.of(DSBlocks.get(), Block.WAXED_EXPOSED_COPPER_TRAPDOOR, BlockHandlers.TRAPDOOR_HANDLER)
            .property(BlockProperties.OPEN, true)
            .property(BlockProperties.FACING, Direction.SOUTH)
            .tag(BlockTags.BREAK_TIME, STARTING_BLOCK_BREAK_MILLIS)
            .build();
    private final Block northTrapdoor = BlockBuilder.of(DSBlocks.get(), Block.WAXED_EXPOSED_COPPER_TRAPDOOR, BlockHandlers.TRAPDOOR_HANDLER)
            .property(BlockProperties.OPEN, true)
            .property(BlockProperties.FACING, Direction.NORTH)
            .tag(BlockTags.BREAK_TIME, STARTING_BLOCK_BREAK_MILLIS)
            .build();

    @Override
    public void generate(@NotNull GenerationUnit unit) {
        final UnitModifier unitModifier = unit.modifier();
        unitModifier.fillBiome(Biomes.desolateBiome());
        final Point start = unit.absoluteStart();
        final Point spawnPoint = this.instance.initialSpawnPoint().sub(0, 1, 0);
        if (start.x() == 0 && start.z() == 0) {
            unitModifier.setBlock(spawnPoint, this.bottomBlock);
            unitModifier.setBlock(spawnPoint.add(0, 3, 0), this.topTrapdoor);
            unitModifier.setBlock(spawnPoint.add(1, 1, 0), this.eastTrapdoor);
            unitModifier.setBlock(spawnPoint.add(1, 2, 0), this.eastTrapdoor);
            unitModifier.setBlock(spawnPoint.add(0, 1, 1), this.southTrapdoor);
            unitModifier.setBlock(spawnPoint.add(0, 2, 1), this.southTrapdoor);
            return;
        }
        if (start.x() == -16 && start.z() == 0) {
            unitModifier.setBlock(spawnPoint.add(-1, 1, 0), this.westTrapdoor);
            unitModifier.setBlock(spawnPoint.add(-1, 2, 0), this.westTrapdoor);
            return;
        }
        if (start.x() == 0 && start.z() == -16) {
            unitModifier.setBlock(spawnPoint.add(0, 1, -1), this.northTrapdoor);
            unitModifier.setBlock(spawnPoint.add(0, 2, -1), this.northTrapdoor);
            return;
        }
    }

}
