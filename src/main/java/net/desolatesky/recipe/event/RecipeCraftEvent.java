package net.desolatesky.recipe.event;

import net.desolatesky.player.DSPlayer;
import net.kyori.adventure.key.Key;
import net.minestom.server.event.Event;

public final class RecipeCraftEvent implements Event {

    private final DSPlayer player;
    private final Key recipe;
    private final int amount;

    public RecipeCraftEvent(DSPlayer player, Key recipe, int amount) {
        this.player = player;
        this.recipe = recipe;
        this.amount = amount;
    }

    public DSPlayer player() {
        return this.player;
    }

    public Key recipe() {
        return this.recipe;
    }

    public int amount() {
        return this.amount;
    }
}
