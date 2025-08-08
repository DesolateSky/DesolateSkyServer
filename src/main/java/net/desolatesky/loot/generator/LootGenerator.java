package net.desolatesky.loot.generator;

import net.desolatesky.loot.LootContext;
import net.minestom.server.item.ItemStack;

import java.util.Collection;

public interface LootGenerator {

    Collection<ItemStack> generateLoot(LootContext context);

}
