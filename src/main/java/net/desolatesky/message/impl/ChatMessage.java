package net.desolatesky.message.impl;

import net.desolatesky.message.Message;
import net.desolatesky.util.ComponentUtil;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;

import java.util.List;
import java.util.SequencedCollection;
import java.util.function.Function;

public final class ChatMessage implements Message {

    private final String id;
    private final List<Component> contents;

    public ChatMessage(String id, List<Component> contents) {
        this.id = id;
        this.contents = contents;
    }

    public static ChatMessage parse(String id, SequencedCollection<String> contents) {
        return new ChatMessage(id, contents.stream().map(ComponentUtil::parse).toList());
    }

    public static ChatMessage parse(String id, String message) {
        return parse(id, List.of(message));
    }

    @Override
    public String id() {
        return this.id;
    }

    @Override
    public void sendTo(Player player) {
        this.contents.forEach(player::sendMessage);
    }

    @Override
    public void sendTo(Player player, Function<Component, Component> placeholderFunction) {
        for (Component content : this.contents) {
            player.sendMessage(placeholderFunction.apply(content));
        }
    }

    @Override
    public String toString() {
        return "ChatMessage{" +
                "id='" + this.id + '\'' +
                ", contents=" + this.contents +
                '}';
    }
}
