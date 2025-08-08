package net.desolatesky.loot.generator;

import net.desolatesky.util.Namespace;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;
import org.jetbrains.annotations.NotNull;

public final class LootGeneratorType implements Keyed {

    private final Key key;

    private LootGeneratorType(Key key) {
        this.key = key;
    }

    public static LootGeneratorType create(String key) {
        return new LootGeneratorType(lootKey(key));
    }

    public static Key lootKey(String key) {
        return Namespace.key("loot", key);
    }

    @Override
    public @NotNull Key key() {
        return this.key;
    }

}
