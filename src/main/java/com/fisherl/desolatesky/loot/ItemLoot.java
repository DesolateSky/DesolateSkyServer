package com.fisherl.desolatesky.loot;

import net.kyori.adventure.key.Key;

public record ItemLoot(Key itemId, int min, int max) {

    public static ItemLoot itemLoot(Key itemId, int amount) {
        return new ItemLoot(itemId, amount, amount + 1);
    }
}
