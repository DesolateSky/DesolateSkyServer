package net.desolatesky.util;


import net.kyori.adventure.key.Key;

public final class Namespace {

    private Namespace() {
        throw new UnsupportedOperationException();
    }

    public static final String KEY_SEPARATOR = ":";
    private static final String PATH_SEPARATOR = "/";

    public static final String NAMESPACE = "desolatesky";

    public static Key key(String first, String... path) {
        if (path.length == 0) {
            return Key.key(NAMESPACE, first);
        }
        if (first.contains(KEY_SEPARATOR)) {
            final String[] parts = first.split(KEY_SEPARATOR);
            if (parts.length != 2) {
                throw new IllegalArgumentException("Invalid key format: " + first);
            }
            return Key.key(parts[0], parts[1] + PATH_SEPARATOR + buildPathString(path));
        }
        return Namespace.key(first + PATH_SEPARATOR + buildPathString(path));
    }

    private static String buildPathString(String... path) {
        final StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < path.length; i++) {
            stringBuilder.append(path[i]);
            if (i < path.length - 1) {
                stringBuilder.append(PATH_SEPARATOR);
            }
        }
        return stringBuilder.toString();
    }

    public static Key key(String key) {
        final String[] parts = key.split(":");
        if (parts.length == 2) {
            return Key.key(parts[0], parts[1]);
        }
        return Key.key(NAMESPACE, key);
    }

    public static Key minecraftKey(String key) {
        return Key.key(key);
    }

}
