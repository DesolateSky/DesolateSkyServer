package net.desolatesky.loot.table;

import net.desolatesky.loot.generator.LootGenerator;
import net.desolatesky.loot.generator.LootGeneratorType;
import net.desolatesky.util.Namespace;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.SplittableRandom;
import java.util.random.RandomGenerator;

/**
 * This is associated with the class that is being consumed for the loot, not the class that generates the loot
 */
public final class LootTable implements Keyed {

    public static final RandomGenerator DEFAULT_RANDOM_SOURCE = new SplittableRandom();

    public static final LootTable EMPTY = new LootTable(Namespace.EMPTY_KEY, DEFAULT_RANDOM_SOURCE, Map.of());

    public static LootTable create(Key key, Map<LootGeneratorType, LootGenerator> generators) {
        return new LootTable(key, DEFAULT_RANDOM_SOURCE, generators);
    }

    public static LootTable create(Key key, RandomGenerator randomSource, Map<LootGeneratorType, LootGenerator> generators) {
        return new LootTable(key, randomSource, generators);
    }

    private final Key key;
    private final RandomGenerator randomSource;
    private final Map<LootGeneratorType, LootGenerator> generators;

    private LootTable(Key key, RandomGenerator randomSource, Map<LootGeneratorType, LootGenerator> generators) {
        this.key = key;
        this.randomSource = randomSource;
        this.generators = generators;
    }

    public @Nullable LootGenerator getGenerator(LootGeneratorType type) {
        return this.generators.get(type);
    }

    public RandomGenerator randomSource() {
        return this.randomSource;
    }

    @Override
    public @NotNull Key key() {
        return this.key;
    }
}
