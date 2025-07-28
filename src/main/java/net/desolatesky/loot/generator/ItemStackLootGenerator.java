package net.desolatesky.loot.generator;

import net.desolatesky.loot.LootContext;
import net.desolatesky.loot.type.ItemStackLoot;
import net.desolatesky.util.collection.WeightedCollection;
import net.kyori.adventure.key.Key;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;

public record ItemStackLootGenerator(
        LootGeneratorType type,
        WeightedCollection<ItemStackLoot> lootTable,
        int minAmount,
        int maxAmount
) implements LootGenerator {

    public static ItemStackLootGenerator create(LootGeneratorType type, Collection<ItemStackLoot> loot, int minAmount, int maxAmount) {
        final WeightedCollection<ItemStackLoot> weightedCollection = new WeightedCollection<>();
        for (final ItemStackLoot itemStackLoot : loot) {
            weightedCollection.add(itemStackLoot.weight(), itemStackLoot);
        }
        return new ItemStackLootGenerator(type, weightedCollection, minAmount, maxAmount);
    }

    @Override
    public Collection<ItemStack> generateLoot(LootContext context) {
        final Collection<ItemStack> items = new ArrayList<>();
        final int randomAmount = context.modifyTotalLootGenerated(context.randomSource().nextInt(this.minAmount, this.maxAmount + 1));
        for (int i = 0; i < randomAmount; i++) {
            final ItemStackLoot loot = this.lootTable.next();
            items.add(loot.generate(context.perElementLootContext()));
        }
        return items;
    }

    @Override
    public @NotNull Key key() {
        return this.type.key();
    }
}
