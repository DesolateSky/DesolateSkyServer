package net.desolatesky.loot.generator;

import net.desolatesky.item.DSItem;
import net.desolatesky.loot.LootContext;
import net.desolatesky.loot.type.ItemStackLoot;
import net.desolatesky.util.collection.Pair;
import net.desolatesky.util.collection.WeightedCollection;
import net.minestom.server.item.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public record ItemStackLootGenerator(
        WeightedCollection<ItemStackLoot> lootTable,
        int minAmount,
        int maxAmount,
        boolean allowRepeats
) implements LootGenerator {

    public static ItemStackLootGenerator create(Collection<ItemStackLoot> loot, int minAmount, int maxAmount, boolean allowRepeats) {
        final WeightedCollection<ItemStackLoot> weightedCollection = new WeightedCollection<>();
        for (final ItemStackLoot itemStackLoot : loot) {
            weightedCollection.add(itemStackLoot.weight(), itemStackLoot);
        }
        return new ItemStackLootGenerator(weightedCollection, minAmount, maxAmount, allowRepeats);
    }

    public static ItemStackLootGenerator create(Collection<ItemStackLoot> loot, int minAmount, int maxAmount) {
        return create(loot, minAmount, maxAmount, false);
    }

    public static ItemStackLootGenerator forDrop(DSItem item) {
        return create(List.of(new ItemStackLoot(item, 1, 1, 1)), 1, 1);
    }

    @Override
    public Collection<ItemStack> generateLoot(LootContext context) {
        final Collection<ItemStack> items = new ArrayList<>();
        WeightedCollection<ItemStackLoot> currentTable = this.lootTable;
        final int randomAmount = context.randomSource().nextInt(this.minAmount, this.maxAmount + 1);
        for (int i = 0; i < randomAmount; i++) {
            final ItemStackLoot loot;
            if (this.allowRepeats) {
                loot = currentTable.next();
            } else {
                final Pair<ItemStackLoot, WeightedCollection<ItemStackLoot>> next = currentTable.nextWithRemaining();
                loot = next.first();
                currentTable = next.second();
            }
            final ItemStack generated = loot.generate(context.perElementLootContext());
            if (generated.isAir()) {
                continue;
            }
            items.add(generated);
        }
        return items;
    }

}
