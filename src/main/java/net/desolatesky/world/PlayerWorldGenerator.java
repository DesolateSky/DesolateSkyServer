package net.desolatesky.world;

import net.desolatesky.block.BlockFactory;
import net.desolatesky.block.BlockIds;
import net.desolatesky.util.Constants;
import net.desolatesky.world.biome.Biomes;
import net.desolatesky.world.region.SquareRegion;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.Chunk;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.generator.GenerationUnit;
import net.minestom.server.instance.generator.Generator;
import net.minestom.server.instance.generator.UnitModifier;
import org.jetbrains.annotations.NotNullByDefault;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.SplittableRandom;
import java.util.random.RandomGenerator;

@NotNullByDefault
public final class PlayerWorldGenerator implements Generator {


    private static final RandomGenerator RANDOM = new SplittableRandom(12345);
    private static final Collection<Point> DRY_GRASS_POSITIONS = generateDryGrassPositions(RANDOM, PlayerWorld.SPAWN_PLATFORM_SIZE, PlayerWorld.DEFAULT_SPAWN_POINT.blockY());

    private static final int DRY_GRASS_COUNT = 10;

    private final BlockFactory blockFactory;

    public PlayerWorldGenerator(BlockFactory blockFactory) {
        this.blockFactory = blockFactory;
    }

    @Override
    public void generate(GenerationUnit unit) {
        final UnitModifier unitModifier = unit.modifier();
        unitModifier.fillBiome(Biomes.desolateBiome());
        final Point start = unit.absoluteStart();
        final Point floorPoint = PlayerWorld.DEFAULT_SPAWN_POINT.sub(0, 1, 0);
        final Block grass = Objects.requireNonNull(this.blockFactory.getBlockDefinition(Block.GRASS_BLOCK)).createBlock();
        final Block dryGrass = Objects.requireNonNull(this.blockFactory.getBlockDefinition(BlockIds.DRY_GRASS_SEEDS)).createBlock();
        for (int x = 0; x < Chunk.CHUNK_SIZE_X; x++) {
            for (int z = 0; z < Chunk.CHUNK_SIZE_Z; z++) {
                final Point point = new Vec(start.x() + x, floorPoint.y(), start.z() + z);
                if (!PlayerWorld.SPAWN_PLATFORM_SIZE.contains(point)) {
                    continue;
                }
                unitModifier.setBlock(point, grass);
                for (final Point grassPoint : DRY_GRASS_POSITIONS) {
                    if (grassPoint.blockX() != point.blockX() || grassPoint.blockZ() != point.blockZ()) {
                        continue;
                    }
                    unitModifier.setBlock(grassPoint, dryGrass);
                }
            }
        }
    }

    private static Collection<Point> generateDryGrassPositions(RandomGenerator randomGenerator, SquareRegion region, int y) {
        final Set<Point> generated = new HashSet<>();
        final Point min = region.min();
        final Point max = region.max();
        for (int i = 0; i < DRY_GRASS_COUNT; i++) {
            final int x = randomGenerator.nextInt(min.blockX(), max.blockX() + 1);
            final int z = randomGenerator.nextInt(min.blockZ(), max.blockZ());
            final Point point = new BlockVec(x, y, z);
            if (!generated.add(point)) {
                i--;
            }
        }
        return generated;
    }
}
