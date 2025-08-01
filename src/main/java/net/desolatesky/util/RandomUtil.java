package net.desolatesky.util;

import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.coordinate.CoordConversion;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.Chunk;
import net.minestom.server.instance.Section;
import org.joml.Quaterniond;

import java.util.Collection;
import java.util.List;
import java.util.random.RandomGenerator;

public final class RandomUtil {

    private RandomUtil() {
        throw new UnsupportedOperationException();
    }

    public static Pos randomPos(RandomGenerator random, double xMin, double xMax, double yMin, double yMax, double zMin, double zMax) {
        final double x = random.nextDouble(xMin, xMax);
        final double y = random.nextDouble(yMin, yMax);
        final double z = random.nextDouble(zMin, zMax);
        final float yaw = random.nextFloat() * 360.0f;
        final float pitch = random.nextFloat() * 360.0f;
        return new Pos(x, y, z, yaw, pitch);
    }

    public static BlockVec randomBlockPos(RandomGenerator generator, Section section, int chunkX, int sectionIndex, int chunkZ) {
        final int dimension = section.blockPalette().dimension();
        final int sectionX = generator.nextInt(dimension);
        final int sectionY = generator.nextInt(dimension);
        final int sectionZ = generator.nextInt(dimension);
        final int x = chunkX * Chunk.CHUNK_SIZE_X + sectionX;
        final int y = sectionIndex * Chunk.CHUNK_SECTION_SIZE + sectionY;
        final int z = chunkZ * Chunk.CHUNK_SIZE_Z + sectionZ;
        return new BlockVec(x, y, z);
    }

    public static Quaterniond randomRotation(RandomGenerator random) {
        final float x = random.nextFloat() * 2.0f - 1.0f;
        final float y = random.nextFloat() * 2.0f - 1.0f;
        final float z = random.nextFloat() * 2.0f - 1.0f;
        final float w = random.nextFloat() * 2.0f - 1.0f;
        return new Quaterniond(x, y, z, w).normalize();
    }

    public static <E extends Enum<E>> E randomEnum(RandomGenerator random, Class<E> enumClass) {
        final E[] values = enumClass.getEnumConstants();
        if (values.length == 0) {
            throw new IllegalArgumentException("Enum class must have at least one value");
        }
        return values[random.nextInt(values.length)];
    }

    public static double randomDouble(RandomGenerator random, double min, double max) {
        if (min == max) {
            return min;
        }
        if (min > max) {
            throw new IllegalArgumentException("Min must be less than max");
        }
        return random.nextDouble(min, max);
    }

    public static <E> E randomElement(RandomGenerator random, E[] array) {
        if (array.length == 0) {
            throw new IllegalArgumentException("Array must have at least one element");
        }
        return array[random.nextInt(array.length)];
    }

    public static <E> E randomElement(RandomGenerator random, Collection<E> collection) {
        if (collection instanceof final List<E> list) {
            return randomElement(random, list);
        }
        if (collection.isEmpty()) {
            throw new IllegalArgumentException("Collection must have at least one element");
        }
        final int size = collection.size();
        final int index = random.nextInt(size);
        return collection.stream().skip(index).findFirst().orElseThrow();
    }

    public static <E> E randomElement(RandomGenerator random, List<E> collection) {
        if (collection.isEmpty()) {
            throw new IllegalArgumentException("List must have at least one element");
        }
        final int size = collection.size();
        final int index = random.nextInt(size);
        return collection.get(index);
    }

    /**
     *
     * @param random RandomGenerator instance to use for generating random numbers
     * @param chance Chance of success, must be between 0 and 100
     * @return true if the random chance is successful, false otherwise
     */
    public static boolean checkChance(RandomGenerator random, double chance) {
        if (chance < 0) {
            return false;
        }
        if (chance > 100) {
            return true;
        }
        final double randomValue = random.nextDouble() * 100;
        return randomValue < chance;
    }

}
