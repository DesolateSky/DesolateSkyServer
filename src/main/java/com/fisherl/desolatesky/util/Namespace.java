package com.fisherl.desolatesky.util;

import net.kyori.adventure.key.Key;

public final class Namespace {

    private Namespace() {}

    public static Key key(String key) {
        return Key.key(Namespaces.DESOLATE_SKY, key);
    }

    public static Key minecraftKey(String key) {
        return Key.key(key);
    }
}
