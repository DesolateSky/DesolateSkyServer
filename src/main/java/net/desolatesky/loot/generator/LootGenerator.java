package net.desolatesky.loot.generator;

import net.desolatesky.loot.LootContext;
import net.kyori.adventure.key.Keyed;
import net.minestom.server.item.ItemStack;

import java.util.Collection;

public interface LootGenerator extends Keyed {

    LootGeneratorType type();

    Collection<ItemStack> generateLoot(LootContext context);

}
