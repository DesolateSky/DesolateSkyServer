package net.desolatesky.block.settings;

import net.desolatesky.category.Category;
import net.desolatesky.item.DSItem;
import net.desolatesky.item.DSItemRegistry;
import net.desolatesky.item.handler.breaking.MiningLevel;
import net.desolatesky.item.handler.breaking.MiningLevels;
import net.desolatesky.loot.generator.ItemStackLootGenerator;
import net.desolatesky.loot.generator.LootGeneratorTypes;
import net.desolatesky.loot.table.LootTable;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.kyori.adventure.sound.Sound;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.tag.Tag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public final class BlockSettings implements Keyed {

    public static final Duration UNBREAKABLE_BREAK_TIME = Duration.ofMillis(-1);

    private final Key key;
    private final LootTable lootTable;
    private final Duration breakTime;
    private final @Nullable Key blockItemKey;
    private final MiningLevel miningLevel;
    private final @Unmodifiable Set<Category> categories;
    private final @Nullable BlockSoundSettings soundSettings;
    /**
     * For when this block needs to be displayed in a menu
     */
    private final ItemStack menuItem;
    private final CompoundBinaryTag taggedSettings;

    public BlockSettings(Key key, LootTable lootTable, Duration breakTime, @Nullable Key blockItemkey, MiningLevel miningLevel, Set<Category> categories, @Nullable BlockSoundSettings soundSettings, ItemStack menuItem, CompoundBinaryTag taggedSettings) {
        this.key = key;
        this.lootTable = lootTable;
        this.breakTime = breakTime;
        this.blockItemKey = blockItemkey;
        this.miningLevel = miningLevel;
        this.categories = Set.copyOf(categories);
        this.soundSettings = soundSettings;
        this.menuItem = menuItem;
        this.taggedSettings = taggedSettings;
    }

    /**
     * @param breakTime break time in milliseconds
     */
    public BlockSettings(Key key, LootTable lootTable, int breakTime, @Nullable Key blockItemkey, MiningLevel miningLevel, Set<Category> categories, @Nullable BlockSoundSettings soundSettings, ItemStack menuItem, CompoundBinaryTag taggedSettings) {
        this(key, lootTable, Duration.ofMillis(breakTime), blockItemkey, miningLevel, categories, soundSettings, menuItem, taggedSettings);
    }

    @Override
    public @NotNull Key key() {
        return this.key;
    }

    public LootTable lootTable() {
        return this.lootTable;
    }

    public Duration breakTime() {
        return this.breakTime;
    }

    public @Nullable Key blockItemKey() {
        return this.blockItemKey;
    }

    public MiningLevel miningLevel() {
        return this.miningLevel;
    }

    public @Nullable ItemStack createBlockItem(DSItemRegistry itemRegistry) {
        if (this.blockItemKey == null) {
            return null;
        }
        return itemRegistry.create(this.blockItemKey);
    }

    public @Unmodifiable Set<Category> categories() {
        return this.categories;
    }

    public @Nullable BlockSoundSettings soundSettings() {
        return this.soundSettings;
    }

    public @Nullable Sound breakSound() {
        return this.soundSettings != null ? this.soundSettings.breakSound() : null;
    }

    public @Nullable Sound digSound() {
        return this.soundSettings != null ? this.soundSettings.digSound() : null;
    }

    public boolean isCategory(Category category) {
        return this.categories.contains(category);
    }

    public ItemStack menuItem() {
        return this.menuItem;
    }

    public <T> @Nullable T getSetting(Tag<T> tag) {
        return tag.read(this.taggedSettings);
    }

    public static Builder builder(Key key, ItemStack menuItem) {
        return new Builder(key, menuItem);
    }

    public static Builder builder(Key key, Material menuMaterial) {
        return new Builder(key, ItemStack.of(menuMaterial));
    }

    public static class Builder {

        private final Key key;
        private LootTable lootTable = LootTable.EMPTY;
        private Duration breakTime = UNBREAKABLE_BREAK_TIME;
        private @Nullable Key blockItemKey = null;
        private MiningLevel miningLevel = MiningLevels.NONE;
        private final Set<Category> categories = new HashSet<>();
        private @Nullable BlockSoundSettings soundSettings = null;
        private final ItemStack menuItem;
        private CompoundBinaryTag.Builder taggedSettings = CompoundBinaryTag.builder();

        private Builder(Key key, ItemStack menuItem) {
            this.key = key;
            this.menuItem = menuItem;
        }

        public Builder lootTable(LootTable lootTable) {
            this.lootTable = lootTable;
            return this;
        }

        public Builder withDropLootTable(DSItem item) {
            return this.lootTable(LootTable.builder(this.key)
                    .generator(LootGeneratorTypes.BLOCK_BREAK, ItemStackLootGenerator.forDrop(item))
                    .build());
        }

        /**
         * @param breakTime in milliseconds
         */
        public Builder breakTime(int breakTime) {
            this.breakTime = Duration.ofMillis(breakTime);
            return this;
        }

        public Builder unbreakable() {
            this.breakTime = UNBREAKABLE_BREAK_TIME;
            return this;
        }

        public Builder blockItem(DSItem item, boolean withLootTable) {
            this.blockItemKey = item.key();
            if (withLootTable) {
                return this.withDropLootTable(item);
            }
            return this;
        }

        public Builder blockItem(@Nullable Key blockItemKey) {
            this.blockItemKey = blockItemKey;
            return this;
        }

        public Builder miningLevel(MiningLevel miningLevel) {
            this.miningLevel = miningLevel;
            return this;
        }

        public Builder categories(Collection<Category> categories) {
            this.categories.addAll(categories);
            return this;
        }

        public Builder categories(Category... categories) {
            Collections.addAll(this.categories, categories);
            return this;
        }

        public Builder soundSettings(@Nullable BlockSoundSettings soundSettings) {
            this.soundSettings = soundSettings;
            return this;
        }

        public Builder soundSettings(Consumer<BlockSoundSettings.Builder> soundSettingsConsumer) {
            final BlockSoundSettings.Builder builder = BlockSoundSettings.builder();
            soundSettingsConsumer.accept(builder);
            this.soundSettings = builder.build();
            return this;
        }

        public Builder taggedSettings(CompoundBinaryTag taggedSettings) {
            this.taggedSettings = CompoundBinaryTag.builder();
            return this;
        }

        public <T> Builder tag(Tag<T> tag, T value) {
            tag.write(this.taggedSettings, value);
            return this;
        }

        public BlockSettings build() {
            return new BlockSettings(this.key, this.lootTable, this.breakTime, this.blockItemKey, this.miningLevel, this.categories, this.soundSettings, this.menuItem, this.taggedSettings.build());
        }

    }

}
