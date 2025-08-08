package net.desolatesky.loot.registry;

import net.desolatesky.loot.generator.LootGenerator;
import net.desolatesky.loot.generator.LootGeneratorType;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public final class LootGeneratorRegistry {

    public static LootGeneratorRegistry create() {
        return new LootGeneratorRegistry(new HashMap<>());
    }

    private final Map<LootGeneratorType, LootGeneratorMap> generators;

    private LootGeneratorRegistry(Map<LootGeneratorType, LootGeneratorMap> generators) {
        this.generators = generators;
    }

    public @Nullable LootGenerator getGenerator(LootGeneratorType type, Key key) {
        final LootGeneratorMap map = this.generators.get(type);
        if (map == null) {
            return null;
        }
        return map.generators.get(key);
    }

    private record LootGeneratorMap(Map<Key, LootGenerator> generators) {

    }

}
