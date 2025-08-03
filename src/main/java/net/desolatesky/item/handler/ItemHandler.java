package net.desolatesky.item.handler;

import net.desolatesky.DesolateSkyServer;
import net.desolatesky.entity.DSEntity;
import net.desolatesky.instance.DSInstance;
import net.desolatesky.item.category.ItemCategory;
import net.desolatesky.item.handler.breaking.MiningLevel;
import net.desolatesky.item.handler.breaking.calculator.BreakTimeCalculator;
import net.desolatesky.player.DSPlayer;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.minestom.server.coordinate.Point;
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

public abstract class ItemHandler implements Keyed {

    protected final Key key;
    protected final BreakTimeCalculator breakTimeCalculator;
    protected final @Unmodifiable Collection<ItemCategory> categories;
    private final CompoundBinaryTag tagData;

    public ItemHandler(Key key, BreakTimeCalculator breakTimeCalculator, Collection<ItemCategory> categories, CompoundBinaryTag tagData) {
        this.key = key;
        this.breakTimeCalculator = breakTimeCalculator;
        this.categories = Set.copyOf(categories);
        this.tagData = tagData;
    }

    public ItemHandler(Key key, BreakTimeCalculator breakTimeCalculator, Collection<ItemCategory> categories) {
        this(key, breakTimeCalculator, categories, CompoundBinaryTag.empty());
    }

    public ItemInteractionResult onBreakBlock(DSPlayer player, DSInstance instance, ItemStack usedItem, Block block, Point blockPoint) {
        return ItemInteractionResult.noEffect();
    }

    public ItemInteractionResult onInteractBlock(DSPlayer player, DSInstance instance, ItemStack usedItem, PlayerHand hand, Point blockPoint, Block block, Point cursorPosition, BlockFace blockFace) {
        return ItemInteractionResult.noEffect();
    }

    public ItemInteractionResult onInteractEntity(DSPlayer player, DSInstance instance, ItemStack usedItem, PlayerHand hand, DSEntity interacted) {
        return ItemInteractionResult.noEffect();
    }

    public ItemInteractionResult onInteractAir(DSPlayer player, DSInstance instance, ItemStack usedItem, PlayerHand hand) {
        return ItemInteractionResult.noEffect();
    }

    public ItemInteractionResult onPunchBlock(DSPlayer player, DSInstance instance, ItemStack usedItem, Block block, Point blockPoint) {
        return ItemInteractionResult.noEffect();
    }

    public ItemInteractionResult onPunchAir(DSPlayer player, DSInstance instance, ItemStack usedItem) {
        return ItemInteractionResult.noEffect();
    }

    public ItemInteractionResult onPunchEntity(DSPlayer player, DSInstance instance, ItemStack usedItem, DSEntity interacted) {
        return ItemInteractionResult.noEffect();
    }

    public Duration calculateBreakTime(DesolateSkyServer server, ItemStack usedItem, Block block) {
        return this.breakTimeCalculator.calculateBreakTime(server, this, usedItem, block);
    }

    public @Unmodifiable Collection<ItemCategory> categories() {
        return this.categories;
    }

    public boolean isCategory(ItemCategory category) {
        return this.categories.contains(category);
    }

    public abstract MiningLevel getMiningLevelFor(Block block);

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
