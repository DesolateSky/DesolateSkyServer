package com.fisherl.desolatesky.world;

import com.fisherl.desolatesky.block.BlockFactory;
import com.fisherl.desolatesky.block.id.BlockIds;
import com.fisherl.desolatesky.world.biome.Biomes;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.generator.GenerationUnit;
import net.minestom.server.instance.generator.Generator;
import net.minestom.server.instance.generator.UnitModifier;
import org.jetbrains.annotations.NotNullByDefault;

@NotNullByDefault
public final class PlayerWorldGenerator implements Generator {

    private final BlockFactory blockFactory;

    public PlayerWorldGenerator(BlockFactory blockFactory) {
        this.blockFactory = blockFactory;
    }

    @Override
    public void generate(GenerationUnit unit) {
        final UnitModifier unitModifier = unit.modifier();
        unitModifier.fillBiome(Biomes.desolateBiome());
        final Point start = unit.absoluteStart();
        final Point spawnPoint = PlayerWorld.DEFAULT_SPAWN_POINT.sub(0, 1, 0);
        if (start.x() == 0 && start.z() == 0) {
            this.blockFactory.getBlockDefinition(BlockIds.ISLAND_CORE)
                    .ifPresent(def -> unitModifier.setBlock(spawnPoint, def.defaultBlock()));
            this.blockFactory.getBlockDefinition(BlockIds.ISLAND_CORE_SUPPORT)
                    .ifPresent(def -> unitModifier.setBlock(spawnPoint.sub(0, 1, 0), def.defaultBlock()));
        }
    }

}
