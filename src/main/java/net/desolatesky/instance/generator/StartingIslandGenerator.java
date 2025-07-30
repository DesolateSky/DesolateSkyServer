package net.desolatesky.instance.generator;

import net.desolatesky.block.BlockProperties;
import net.desolatesky.block.DSBlocks;
import net.desolatesky.block.entity.BlockEntities;
import net.desolatesky.block.property.SlabType;
import net.desolatesky.instance.biome.Biomes;
import net.desolatesky.instance.team.TeamInstance;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.generator.GenerationUnit;
import net.minestom.server.instance.generator.Generator;
import net.minestom.server.instance.generator.UnitModifier;
import net.minestom.server.utils.Direction;
import org.jetbrains.annotations.NotNull;

public final class StartingIslandGenerator implements Generator {

    private final TeamInstance instance;

    private Block bottomBlock;
    private Block topTrapdoor;
    private Block westTrapdoor;
    private Block eastTrapdoor;
    private Block southTrapdoor;
    private Block northTrapdoor;

    public StartingIslandGenerator(BlockEntities blockEntities, TeamInstance instance) {
        this.instance = instance;
        this.initializeBlocks(blockEntities);
    }

    private void initializeBlocks(BlockEntities blockEntities) {
        this.bottomBlock = DSBlocks.UNBREAKABLE_WAXED_EXPOSED_CUT_COPPER_SLAB.createBuilder(blockEntities)
                .property(BlockProperties.SLAB_TYPE, SlabType.TOP)
                .build();
        this.topTrapdoor = DSBlocks.WAXED_EXPOSED_COPPER_TRAPDOOR.createBuilder(blockEntities)
                .property(BlockProperties.OPEN, false)
                .build();
        this.westTrapdoor = DSBlocks.WAXED_EXPOSED_COPPER_TRAPDOOR.createBuilder(blockEntities)
                .property(BlockProperties.OPEN, true)
                .property(BlockProperties.FACING, Direction.WEST)
                .build();
        this.eastTrapdoor = DSBlocks.WAXED_EXPOSED_COPPER_TRAPDOOR.createBuilder(blockEntities)
                .property(BlockProperties.OPEN, true)
                .property(BlockProperties.FACING, Direction.EAST)
                .build();
        this.southTrapdoor = DSBlocks.WAXED_EXPOSED_COPPER_TRAPDOOR.createBuilder(blockEntities)
                .property(BlockProperties.OPEN, true)
                .property(BlockProperties.FACING, Direction.SOUTH)
                .build();
        this.northTrapdoor = DSBlocks.WAXED_EXPOSED_COPPER_TRAPDOOR.createBuilder(blockEntities)
                .property(BlockProperties.OPEN, true)
                .property(BlockProperties.FACING, Direction.NORTH)
                .build();
    }

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
