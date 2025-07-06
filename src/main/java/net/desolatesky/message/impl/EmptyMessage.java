package net.desolatesky.message.impl;

import net.desolatesky.message.Message;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;

import java.util.function.Function;

public final class EmptyMessage implements Message {

    public static final EmptyMessage INSTANCE = new EmptyMessage();

    private EmptyMessage() {

    }

    @Override
    public String id() {
        return "";
    }

    @Override
    public void sendTo(Player player) {

    }

    @Override
    public void sendTo(Player player, Function<Component, Component> placeholderFunction) {

    }

    @Override
    public String toString() {
        return "EmptyMessage{}";
    }

}
