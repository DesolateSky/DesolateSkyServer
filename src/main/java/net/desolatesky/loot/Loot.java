package net.desolatesky.loot;

public interface Loot<T> {

    double weight();

    T generate(LootContext lootContext);

}
