package net.desolatesky.message;

import net.desolatesky.logging.LoggerUtil;
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
        LoggerUtil.info(this.getClass(), "No message (%s) found sending to player (%s)".formatted(this.id, player.getName()));
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