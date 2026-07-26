
package net.desolatesky.world.dimension;

import net.minestom.server.MinecraftServer;
import net.minestom.server.registry.RegistryKey;
import net.minestom.server.world.DimensionType;

public final class Dimensions {

    private Dimensions() {
        throw new UnsupportedOperationException();
    }

    private static RegistryKey<DimensionType> voidDimension;

    public static RegistryKey<DimensionType> voidDimension() {
        return voidDimension;
    }

    public static void registerDimensions() {
        voidDimension = MinecraftServer.getDimensionTypeRegistry().register(VoidDimension.KEY, VoidDimension.INSTANCE);
    }
}
