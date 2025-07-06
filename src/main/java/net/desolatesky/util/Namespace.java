package net.desolatesky.util;


import net.kyori.adventure.key.Key;

public final class Namespace {

    private Namespace() {
        throw new UnsupportedOperationException();
    }

    public static final String NAMESPACE = "desolatesky";

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
