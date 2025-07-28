package net.desolatesky.loot.table;

import net.desolatesky.loot.generator.LootGenerator;
import net.desolatesky.loot.generator.LootGeneratorType;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.SplittableRandom;
import java.util.random.RandomGenerator;

/**
 * This is associated with the class that is being consumed for the loot, not the class that generates the loot
 */
public final class LootTable {

    public static final RandomGenerator DEFAULT_RANDOM_SOURCE = new SplittableRandom();

    public static final LootTable EMPTY = new LootTable(DEFAULT_RANDOM_SOURCE, Map.of());

    public static LootTable create(Map<LootGeneratorType, LootGenerator> generators) {
        return new LootTable(DEFAULT_RANDOM_SOURCE, generators);
    }

    public static LootTable create(RandomGenerator randomSource, Map<LootGeneratorType, LootGenerator> generators) {
        return new LootTable(randomSource, generators);
    }

    private final RandomGenerator randomSource;
    private final Map<LootGeneratorType, LootGenerator> generators;

    private LootTable(RandomGenerator randomSource, Map<LootGeneratorType, LootGenerator> generators) {
        this.randomSource = randomSource;
        this.generators = generators;
    }

    public @Nullable LootGenerator getGenerator(LootGeneratorType type) {
        return this.generators.get(type);
    }

    public RandomGenerator randomSource() {
        return this.randomSource;
    }

}
