package net.desolatesky.instance.biome;

import net.desolatesky.util.Namespace;
import net.kyori.adventure.key.Key;
import net.minestom.server.color.Color;
import net.minestom.server.world.biome.Biome;
import net.minestom.server.world.biome.BiomeEffects;

public final class DesolateBiome {

    private DesolateBiome() {
        throw new UnsupportedOperationException();
    }

    private static final BiomeEffects BIOME_EFFECTS = BiomeEffects.builder()
            .fogColor(new Color(0x4C4C4C))
            .waterColor(new Color(0x3D4B4B))
            .waterFogColor(new Color(0x2A2A2A))
            .skyColor(new Color(0x5A5A66))
            .grassColor(new Color(0x6A6F4E))
            .foliageColor(new Color(0x6A6F4E))
            .build();

    public static final Key KEY = Namespace.key("desolate");
    public static final Biome INSTANCE = Biome.builder()
            .effects(BIOME_EFFECTS)
            .build();

}
