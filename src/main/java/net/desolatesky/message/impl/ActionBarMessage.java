package net.desolatesky.message.impl;

import net.desolatesky.message.Message;
import net.desolatesky.util.ComponentUtil;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;

import java.util.function.Function;

public final class ActionBarMessage implements Message {

    private final String id;
    private final Component message;

    public ActionBarMessage(String id, Component message) {
        this.id = id;
        this.message = message;
    }

    public static ActionBarMessage parse(String id, String message) {
        return new ActionBarMessage(id, ComponentUtil.parse(message));
    }

    @Override
    public String id() {
        return this.id;
    }

    @Override
    public void sendTo(Player player) {
        player.sendActionBar(this.message);
    }

    @Override
    public void sendTo(Player player, Function<Component, Component> placeholderFunction) {
        player.sendActionBar(placeholderFunction.apply(this.message));
    }

    @Override
    public String toString() {
        return "ActionBarMessage{" +
                "id='" + this.id + '\'' +
                ", message=" + this.message +
                '}';
    }
}
