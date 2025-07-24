package net.desolatesky.loot;

import java.util.random.RandomGenerator;

public interface LootContext {

    RandomGenerator randomSource();

    default int modifyTotalLootGenerated(int amount) {
        return amount;
    }

    LootContext perElementLootContext();

    static LootContext create(RandomGenerator randomSource) {
        return new Impl(randomSource);
    }

    static LootContext create(RandomGenerator randomSource, LootContext perElementLootContext) {
        return new Impl(randomSource, perElementLootContext);
    }

    class Impl implements LootContext {

        private final RandomGenerator randomSource;
        private final LootContext perElementLootContext;

        private Impl(RandomGenerator randomSource, LootContext perElementLootContext) {
            this.randomSource = randomSource;
            this.perElementLootContext = perElementLootContext;
        }

        private Impl(RandomGenerator randomSource) {
            this.randomSource = randomSource;
            this.perElementLootContext = this;
        }

        @Override
        public RandomGenerator randomSource() {
            return this.randomSource;
        }

        @Override
        public LootContext perElementLootContext() {
            return this.perElementLootContext;
        }

    }

}
