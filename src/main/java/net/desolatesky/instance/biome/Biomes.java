package net.desolatesky.instance.biome;

import net.minestom.server.MinecraftServer;
import net.minestom.server.registry.RegistryKey;
import net.minestom.server.world.biome.Biome;

public final class Biomes {

    private Biomes() {
        throw new UnsupportedOperationException();
    }

    private static RegistryKey<Biome> desolateBiome;

    public static RegistryKey<Biome> desolateBiome() {
        return desolateBiome;
    }

    public static void registerBiomes() {
        desolateBiome = MinecraftServer.getBiomeRegistry().register(DesolateBiome.KEY, DesolateBiome.INSTANCE);
    }

}
