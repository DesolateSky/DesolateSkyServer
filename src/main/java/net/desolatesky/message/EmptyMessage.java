package net.desolatesky.message;

import net.desolatesky.logging.DSLogger;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;

import java.util.function.Function;

public final class EmptyMessage implements Message {

    private final String id;

    EmptyMessage(String id) {
        this.id = id;
    }

    @Override
    public String id() {
        return this.id;
    }

    @Override
    public void sendTo(Player player) {
        DSLogger.getLogger().config("No message (%s) found sending to player (%s)".formatted(this.id, player.getUsername()));
    }

    @Override
    public void sendTo(Player player, Function<Component, Component> placeholderFunction) {
        this.sendTo(player);
    }

    @Override
    public String toString() {
        return "EmptyMessage{" +
                "id='" + this.id + '\'' +
                '}';
    }
}