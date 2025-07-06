package net.desolatesky.message;

import net.desolatesky.message.impl.EmptyMessage;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;

import java.util.function.Function;

public interface Message {

    Message EMPTY = EmptyMessage.INSTANCE;

    String id();

    void sendTo(Player player);

    void sendTo(Player player, Function<Component, Component> placeholderFunction);

}
