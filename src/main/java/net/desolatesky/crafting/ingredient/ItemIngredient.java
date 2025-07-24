package net.desolatesky.crafting.ingredient;

import net.desolatesky.item.DSItem;
import net.desolatesky.item.ItemTags;
import net.kyori.adventure.key.Key;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.recipe.Ingredient;
import net.minestom.server.recipe.display.SlotDisplay;
import net.minestom.server.registry.RegistryTag;
import net.minestom.server.registry.TagKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public final class ItemIngredient implements RecipeIngredient {

    public static final ItemIngredient EMPTY = new ItemIngredient(Key.key("empty"), 0, ItemStack::isAir, SlotDisplay.Empty.INSTANCE, null);

    public static ItemIngredient exact(ItemStack match) {
        final Key key = Objects.requireNonNullElseGet(match.getTag(ItemTags.ID), () -> match.material().key());
        return new ItemIngredient(key, 1, itemStack -> itemStack.isSimilar(match), new SlotDisplay.ItemStack(match), new Ingredient(match.material()));
    }

    public static ItemIngredient exact(DSItem match) {
        final Key key = match.key();
        return new ItemIngredient(key, 1, match::is, new SlotDisplay.ItemStack(match.create()), new Ingredient(match.create().material()));
    }

    public static ItemIngredient tag(RegistryTag<Material> tag) {
        final TagKey<Material> tagKey = tag.key();
        if (tagKey == null) {
            throw new IllegalArgumentException("Tag key cannot be null");
        }
        final List<Material> materials = new ArrayList<>();
        tag.iterator().forEachRemaining(key -> materials.add(Material.fromKey(key.key())));
        return new ItemIngredient(tagKey.key(), 1, itemStack -> tag.contains(itemStack.material()), new SlotDisplay.Tag(tagKey), materials.isEmpty() ? null : new Ingredient(materials));
    }

    private final Key key;
    private final int amount;
    private final Predicate<ItemStack> itemChecker;
    private final SlotDisplay display;
    private final @Nullable Ingredient ingredient;

    public ItemIngredient(Key key, int amount, Predicate<ItemStack> itemChecker, SlotDisplay display, @Nullable Ingredient ingredient) {
        this.key = key;
        this.amount = amount;
        this.itemChecker = itemChecker;
        this.display = display;
        this.ingredient = ingredient;
    }

    public MatchResult getMatchResult(ItemStack itemStack) {
        final boolean matches = this.itemChecker.test(itemStack);
        if (this.amount == 0) {
            return new MatchResult(matches, matches ? itemStack.amount() : 0);
        }
        final int itemAmount = itemStack.amount();
        final int totalMatches = itemAmount / this.amount;
        if (totalMatches <= 0) {
            return new MatchResult(matches, 0);
        }
        return new MatchResult(matches, matches ? totalMatches : 0);
    }

    public boolean affectsResultAmount() {
        return this.amount > 0;
    }

    @Override
    public @NotNull Key key() {
        return this.key;
    }

    @Override
    public SlotDisplay display() {
        return this.display;
    }

    public @Nullable Ingredient toMinestomIngredient() {
        return this.ingredient;
    }

    @Override
    public String toString() {
        return "ItemIngredient{" +
                "key=" + this.key +
                ", amount=" + this.amount +
                '}';
    }

    public record MatchResult(boolean matches, int totalMatches) {
    }

}
