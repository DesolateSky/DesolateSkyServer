package net.desolatesky.block.entity.custom.crop;

import net.desolatesky.misc.Rarity;

public enum CropRarity {

    COMMON(Rarity.COMMON, 100, 100),
    UNCOMMON(Rarity.UNCOMMON, 0.2, 0.3),
    RARE(Rarity.RARE, 0.3, 0.4),
    EPIC(Rarity.EPIC, 0.4, 0.5),
    LEGENDARY(Rarity.LEGENDARY, 0.5, 0.6),
    MYTHICAL(Rarity.MYTHICAL, 0.6, 0.7);


    private final Rarity rarity;
    private final double minGrowthChance;
    private final double maxGrowthChance;

    /**
     * @param minGrowthChance per random tick
     * @param maxGrowthChance per random tick
     */
    CropRarity(Rarity rarity, double minGrowthChance, double maxGrowthChance) {
        this.rarity = rarity;
        this.minGrowthChance = minGrowthChance;
        this.maxGrowthChance = maxGrowthChance;
    }

    public Rarity rarity() {
        return this.rarity;
    }

    public double minGrowthChance() {
        return this.minGrowthChance;
    }

    public double maxGrowthChance() {
        return this.maxGrowthChance;
    }

}
