package net.desolatesky.item;

import net.desolatesky.item.handler.ItemHandler;
import net.desolatesky.item.loot.ItemLootRegistry;
import net.desolatesky.loot.generator.LootGenerator;
import net.desolatesky.loot.generator.LootGeneratorType;
import net.desolatesky.loot.table.LootTable;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.tag.Tag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public final class DSItem implements Keyed {

    private final Key key;
    private final @Nullable ItemHandler itemHandler;
    private final ItemStack itemStack;

    public static DSItem create(Material material) {
        return create(ItemStack.of(material));
    }

    public static DSItem create(ItemStack itemStack) {
        final Key key = Objects.requireNonNullElse(itemStack.getTag(ItemTags.ID), itemStack.material().key());
        return new DSItem(key, null, itemStack);
    }

    public static DSItem create(Key key, @Nullable ItemHandler itemHandler, ItemStack itemStack) {
        if (!key.equals(itemStack.material().key())) {
            itemStack = itemStack.withTag(ItemTags.ID, key);
        }
        return new DSItem(key, itemHandler, itemStack);
    }

    public static DSItem create(Key key, ItemStack itemStack) {
        return new DSItem(key, null, itemStack);
    }

    private DSItem(Key key, @Nullable ItemHandler itemHandler, ItemStack itemStack) {
        this.key = key;
        this.itemHandler = itemHandler;
        this.itemStack = itemStack;
    }

    public ItemStack create() {
        return this.itemStack;
    }

    public ItemStack create(int amount) {
        return this.itemStack.withAmount(amount);
    }

    public ItemHandler itemHandler() {
        return this.itemHandler;
    }

    @Override
    public @NotNull Key key() {
        return this.key;
    }

    public boolean is(@NotNull ItemStack itemStack) {
        return this.itemStack.isSimilar(itemStack);
    }

    public <T> @Nullable T getTag(ItemStack itemStack, Tag<T> tag) {
        if (this.itemHandler != null) {
            return this.itemHandler.getTagData(itemStack, tag);
        }
        return itemStack.getTag(tag);
    }

    public @Nullable LootTable getLootTable(ItemLootRegistry lootRegistry) {
        if (this.itemHandler == null) {
            return null;
        }
        return lootRegistry.getLootTable(this.key);
    }

    public @Nullable LootGenerator getLootGenerator(ItemLootRegistry lootRegistry, LootGeneratorType type) {
        final LootTable lootTable = this.getLootTable(lootRegistry);
        if (lootTable == null) {
            return null;
        }
        return lootTable.getGenerator(type);
    }

}