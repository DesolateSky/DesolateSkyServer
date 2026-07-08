package com.fisherl.desolatesky.world;

import com.fisherl.desolatesky.block.BlockFactory;
import com.fisherl.desolatesky.block.BlockIds;
import com.fisherl.desolatesky.world.biome.Biomes;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.Chunk;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.generator.GenerationUnit;
import net.minestom.server.instance.generator.Generator;
import net.minestom.server.instance.generator.UnitModifier;
import org.jetbrains.annotations.NotNullByDefault;

@NotNullByDefault
public final class PlayerWorldGenerator implements Generator {

    public static final Point CORE_POSITION = IslandWorld.DEFAULT_SPAWN_POINT.sub(0, 1, 0);

    private final BlockFactory blockFactory;

    public PlayerWorldGenerator(BlockFactory blockFactory) {
        this.blockFactory = blockFactory;
    }

    @Override
    public void generate(GenerationUnit unit) {
        final UnitModifier unitModifier = unit.modifier();
        unitModifier.fillBiome(Biomes.desolateBiome());
        final Point start = unit.absoluteStart();
        if (start.x() == 0 && start.z() == 0) {
            this.blockFactory.getBlockDefinition(BlockIds.ISLAND_CORE)
                    .ifPresent(def -> unitModifier.setBlock(CORE_POSITION, def.defaultBlock()));
        }
        final Point floorPoint = CORE_POSITION.sub(0, 1, 0);
        final Block sculk = this.blockFactory.getBlockDefinition(Block.SCULK).orElseThrow().defaultBlock();
        for (int x = 0; x < Chunk.CHUNK_SIZE_X; x++) {
            for (int z = 0; z < Chunk.CHUNK_SIZE_Z; z++) {
                final Point point = new Vec(start.x() + x, floorPoint.y(), start.z() + z);
                if (!IslandWorld.MAX_REGION_SIZE.contains(point)) {
                    continue;
                }
                unitModifier.setBlock(point, sculk);
            }
        }
    }
}
