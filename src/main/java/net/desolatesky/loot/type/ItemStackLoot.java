package net.desolatesky.loot.type;

import net.desolatesky.item.DSItem;
import net.desolatesky.loot.Loot;
import net.desolatesky.loot.LootContext;
import net.minestom.server.item.ItemStack;

public record ItemStackLoot(
        DSItem item,
        double weight,
        int minAmount,
        int maxAmount
) implements Loot<ItemStack> {

    public static ItemStackLoot single(DSItem item, double weight) {
        return new ItemStackLoot(item, weight, 1, 1);
    }

    @Override
    public ItemStack generate(LootContext lootContext) {
        return this.item.create(lootContext.randomSource().nextInt(this.minAmount, this.maxAmount + 1));
    }

}
