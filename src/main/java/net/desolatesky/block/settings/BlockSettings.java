package net.desolatesky.block.settings;

import net.desolatesky.category.Category;
import net.desolatesky.item.DSItemRegistry;
import net.desolatesky.item.handler.breaking.MiningLevel;
import net.desolatesky.item.handler.breaking.MiningLevels;
import net.desolatesky.loot.table.LootTable;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.tag.Tag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public final class BlockSettings implements Keyed {

    private final Key key;
    private final @Nullable Key lootTableKey;
    /**
     * Break time in milliseconds
     */
    private final int breakTime;
    private final boolean stateless;
    private final @Nullable Key blockItemKey;
    private final MiningLevel miningLevel;
    private final @Unmodifiable Set<Category> categories;
    /**
     * For when this block needs to be displayed in a menu
     */
    private final ItemStack menuItem;
    private final CompoundBinaryTag taggedSettings;

    /**
     * @param breakTime break time in milliseconds
     */
    public BlockSettings(Key key, @Nullable Key lootTableKey, int breakTime, boolean stateless, @Nullable Key blockItemkey, MiningLevel miningLevel, Set<Category> categories, ItemStack menuItem, CompoundBinaryTag taggedSettings) {
        this.key = key;
        this.lootTableKey = lootTableKey;
        this.breakTime = breakTime;
        this.stateless = stateless;
        this.blockItemKey = blockItemkey;
        this.miningLevel = miningLevel;
        this.categories = Set.copyOf(categories);
        this.menuItem = menuItem;
        this.taggedSettings = taggedSettings;
    }

    @Override
    public @NotNull Key key() {
        return this.key;
    }

    public @Nullable Key lootTableKey() {
        return this.lootTableKey;
    }

    public int breakTime() {
        return this.breakTime;
    }

    public boolean stateless() {
        return this.stateless;
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
        return Collections.unmodifiableSet(this.categories);
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
        private @Nullable Key lootTableKey = null;
        private int breakTime = 1_000;
        private boolean stateless = false;
        private @Nullable Key blockItemKey = null;
        private MiningLevel miningLevel = MiningLevels.NONE;
        private final Set<Category> categories = new HashSet<>();
        private final ItemStack menuItem;
        private CompoundBinaryTag.Builder taggedSettings = CompoundBinaryTag.builder();

        private Builder(Key key, ItemStack menuItem) {
            this.key = key;
            this.menuItem = menuItem;
        }

        public Builder lootTable(Key lootTableKey) {
            this.lootTableKey = lootTableKey;
            return this;
        }

        public Builder breakTime(int breakTime) {
            this.breakTime = breakTime;
            return this;
        }

        public Builder stateless(boolean stateless) {
            this.stateless = stateless;
            return this;
        }

        public Builder stateless() {
            return this.stateless(true);
        }

        public Builder stateful() {
            return this.stateless(false);
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

        public Builder taggedSettings(CompoundBinaryTag taggedSettings) {
            this.taggedSettings = CompoundBinaryTag.builder();
            return this;
        }

        public <T> Builder tag(Tag<T> tag, T value) {
            tag.write(this.taggedSettings, value);
            return this;
        }

        public BlockSettings build() {
            return new BlockSettings(this.key, this.lootTableKey, this.breakTime, this.stateless, this.blockItemKey, this.miningLevel, this.categories, this.menuItem, this.taggedSettings.build());
        }

    }

}
