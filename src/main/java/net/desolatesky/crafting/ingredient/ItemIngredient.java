package net.desolatesky.crafting.ingredient;

import net.kyori.adventure.key.Key;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public final class ItemIngredient implements RecipeIngredient {

    private final Key key;
    private final int amount;
    private final Predicate<ItemStack> itemChecker;

    public ItemIngredient(Key key, int amount, Predicate<ItemStack> itemChecker) {
        this.key = key;
        this.amount = amount;
        this.itemChecker = itemChecker;
    }

    public int getTotalMatches(ItemStack itemStack) {
        final int itemAmount = itemStack.amount();
        final int matches = itemAmount / this.amount;
        if (matches <= 0) {
            return 0;
        }
        return this.itemChecker.test(itemStack) ? matches : 0;
    }

    @Override
    public @NotNull Key key() {
        return this.key;
    }

}
