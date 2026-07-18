package net.desolatesky.world.biome;

import net.desolatesky.util.Namespace;
import net.kyori.adventure.key.Key;
import net.minestom.server.color.Color;
import net.minestom.server.world.attribute.EnvironmentAttribute;
import net.minestom.server.world.biome.Biome;
import net.minestom.server.world.biome.BiomeEffects;

public final class DesolateBiome {

    private DesolateBiome() {
        throw new UnsupportedOperationException();
    }

    private static final BiomeEffects BIOME_EFFECTS = BiomeEffects.builder()
            .waterColor(new Color(0x3D4B4B))
            .grassColor(new Color(0x3b1d0b))
            .foliageColor(new Color(0x3b1d0b))
            .build();

    public static final Key KEY = Namespace.key("desolate");
    public static final Biome INSTANCE = Biome.builder()
            .effects(BIOME_EFFECTS)
            .setAttribute(EnvironmentAttribute.FOG_COLOR, new Color(0x4C4C4C))
            .setAttribute(EnvironmentAttribute.WATER_FOG_COLOR, new Color(0x2A2A2A))
            .setAttribute(EnvironmentAttribute.SKY_COLOR, new Color(0x5A5A66))
            .build();
}