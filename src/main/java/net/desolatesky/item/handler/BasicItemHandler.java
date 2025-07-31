package net.desolatesky.item.handler;

import net.desolatesky.item.category.ItemCategory;
import net.desolatesky.item.handler.breaking.MiningLevel;
import net.desolatesky.item.handler.breaking.calculator.BreakTimeCalculator;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.minestom.server.instance.block.Block;

import java.util.Collection;

public class BasicItemHandler extends ItemHandler {

    private final MiningLevel miningLevel;

    public BasicItemHandler(Key key, BreakTimeCalculator breakTimeCalculator, Collection<ItemCategory> categories, CompoundBinaryTag tagData, MiningLevel miningLevel) {
        super(key, breakTimeCalculator, categories, tagData);
        this.miningLevel = miningLevel;
    }

    public BasicItemHandler(Key key, BreakTimeCalculator breakTimeCalculator, Collection<ItemCategory> categories, MiningLevel miningLevel) {
        super(key, breakTimeCalculator, categories);
        this.miningLevel = miningLevel;
    }

    @Override
    public MiningLevel getMiningLevelFor(Block block) {
        return this.miningLevel;
    }

}
