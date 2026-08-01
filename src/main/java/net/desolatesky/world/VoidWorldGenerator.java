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
public final class VoidWorldGenerator implements Generator {

    private final BlockFactory blockFactory;

    public VoidWorldGenerator(BlockFactory blockFactory) {
        this.blockFactory = blockFactory;
    }

    @Override
    public void generate(GenerationUnit unit) {
        final UnitModifier unitModifier = unit.modifier();
        unitModifier.fillBiome(Biomes.voidBiome());
        final Point start = unit.absoluteStart();
        final Point floorPoint = VoidWorld.SPAWN_POINT.sub(0, 2, 0);
        if (start.blockX() == 0 && start.blockZ() == 0) {
            final Block voidCore = Objects.requireNonNull(this.blockFactory.getBlockDefinition(BlockIds.VOID_CORE)).createBlock();
            unitModifier.setBlock(VoidWorld.VOID_CORE_POS, voidCore);
        }
        final Block sculk = Objects.requireNonNull(this.blockFactory.getBlockDefinition(Block.SCULK)).createBlock();
        unitModifier.fillHeight(Constants.WORLD_MIN_Y, Constants.WORLD_MIN_Y + 1, Block.END_PORTAL);
        for (int x = 0; x < Chunk.CHUNK_SIZE_X; x++) {
            for (int z = 0; z < Chunk.CHUNK_SIZE_Z; z++) {
                final Point point = new Vec(start.x() + x, floorPoint.y(), start.z() + z);
                unitModifier.setBlock(point, sculk);
            }
        }
    }
}
