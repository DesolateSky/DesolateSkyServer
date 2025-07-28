package net.desolatesky.util;

import net.minestom.server.coordinate.Pos;
import org.joml.Quaterniond;
import org.joml.Quaternionf;

import java.util.Collection;
import java.util.List;
import java.util.SequencedCollection;
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

}
