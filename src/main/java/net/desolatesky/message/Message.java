package net.desolatesky.message;

import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;

import java.util.function.Function;

public interface Message {

    static Message empty(String id) {
        return new EmptyMessage(id);
    }

    String id();

    void sendTo(Player player);

    void sendTo(Player player, Function<Component, Component> placeholderFunction);

}
