package net.desolatesky.loot.generator;

public final class LootGeneratorTypes {

    private LootGeneratorTypes() {
        throw new UnsupportedOperationException();
    }

    public static final LootGeneratorType SIFTER = LootGeneratorType.create("sifter");
    public static final LootGeneratorType COMPOSTER = LootGeneratorType.create("composter");
    public static final LootGeneratorType DEBRIS_CATCHER = LootGeneratorType.create("debris_catcher");
    public static final LootGeneratorType BLOCK_BREAK = LootGeneratorType.create("block_break");

}
