package net.desolatesky.item.handler;

import net.desolatesky.instance.DSInstance;
import net.desolatesky.item.category.ItemCategory;
import net.desolatesky.item.handler.breaking.MiningLevel;
import net.desolatesky.item.handler.breaking.calculator.BreakTimeCalculator;
import net.desolatesky.item.loot.ItemLootRegistry;
import net.desolatesky.loot.table.LootTable;
import net.desolatesky.player.DSPlayer;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.PlayerHand;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockFace;
import net.minestom.server.item.ItemStack;
import net.minestom.server.tag.Tag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.time.Duration;
import java.util.Collection;
import java.util.Set;

public class ItemHandler implements Keyed {

    protected final Key key;
    protected final BreakTimeCalculator breakTimeCalculator;
    protected final @Unmodifiable Collection<ItemCategory> categories;
    protected final MiningLevel miningLevel;
    private final CompoundBinaryTag tagData;

    public ItemHandler(Key key, BreakTimeCalculator breakTimeCalculator, Collection<ItemCategory> categories, MiningLevel miningLevel, CompoundBinaryTag tagData) {
        this.key = key;
        this.breakTimeCalculator = breakTimeCalculator;
        this.categories = Set.copyOf(categories);
        this.miningLevel = miningLevel;
        this.tagData = tagData;
    }

    public ItemHandler(Key key, BreakTimeCalculator breakTimeCalculator, Collection<ItemCategory> categories, MiningLevel miningLevel, CompoundBinaryTag.Builder tagData) {
        this.key = key;
        this.breakTimeCalculator = breakTimeCalculator;
        this.categories = Set.copyOf(categories);
        this.miningLevel = miningLevel;
        this.tagData = tagData.build();
    }

    public ItemHandler(Key key, BreakTimeCalculator breakTimeCalculator, Collection<ItemCategory> categories, MiningLevel miningLevel) {
        this(key, breakTimeCalculator, categories, miningLevel, CompoundBinaryTag.empty());
    }

    public ItemStack onInteractBlock(DSPlayer player, DSInstance instance, ItemStack usedItem, PlayerHand hand, Point blockPoint, Block block, Point cursorPosition, BlockFace blockFace) {
        return usedItem;
    }

    public ItemStack onInteractEntity(DSPlayer player, DSInstance instance, ItemStack usedItem, Entity interacted) {
        return usedItem;
    }

    public ItemStack onInteractAir(DSPlayer player, DSInstance instance, ItemStack usedItem) {
        return usedItem;
    }

    public ItemStack onPunchBlock(DSPlayer player, DSInstance instance, ItemStack usedItem, Point blockPoint, Block block) {
        return usedItem;
    }

    public ItemStack onPunchAir(DSPlayer player, DSInstance instance, ItemStack usedItem) {
        return usedItem;
    }

    public ItemStack onPunchEntity(DSPlayer player, DSInstance instance, ItemStack usedItem, Entity interacted) {
        return usedItem;
    }

    public Duration calculateBreakTime(ItemStack usedItem, Block block) {
        return this.breakTimeCalculator.calculateBreakTime(this, usedItem, block);
    }

    public @Unmodifiable Collection<ItemCategory> categories() {
        return this.categories;
    }

    public boolean isCategory(ItemCategory category) {
        return this.categories.contains(category);
    }

    public MiningLevel miningLevel() {
        return this.miningLevel;
    }

    @Override
    public @NotNull Key key() {
        return this.key;
    }

    public <T> @Nullable T getTagData(ItemStack itemStack, Tag<T> tag) {
        final T itemData = itemStack.getTag(tag);
        if (itemData != null) {
            return itemData;
        }
        return tag.read(this.tagData);
    }

    public CompoundBinaryTag tagData() {
        return this.tagData;
    }

}
