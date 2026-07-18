package net.desolatesky.world.biome;

import net.minestom.server.MinecraftServer;
import net.minestom.server.registry.RegistryKey;
import net.minestom.server.world.biome.Biome;

public final class Biomes {


    private Biomes() {
        throw new UnsupportedOperationException();
    }

    private static RegistryKey<Biome> desolateBiome;
    private static RegistryKey<Biome> voidBiome;

    public static RegistryKey<Biome> desolateBiome() {
        return desolateBiome;
    }

    public static RegistryKey<Biome> voidBiome() {
        return voidBiome;
    }

    public static void registerBiomes() {
        desolateBiome = MinecraftServer.getBiomeRegistry().register(DesolateBiome.KEY, DesolateBiome.INSTANCE);
        voidBiome = MinecraftServer.getBiomeRegistry().register(VoidBiome.KEY, VoidBiome.INSTANCE);
    }

}
