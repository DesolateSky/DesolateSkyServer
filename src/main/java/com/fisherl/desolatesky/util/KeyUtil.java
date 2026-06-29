package com.fisherl.desolatesky.util;

import net.kyori.adventure.key.Key;

public final class KeyUtil {

    private KeyUtil() {}

    public static Key desolateSky(String key) {
        return Key.key(Namespaces.DESOLATE_SKY, key);
    }

    public static Key minecraft(String key) {
        return Key.key(key);
    }
}
