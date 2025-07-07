package net.desolatesky.message;

import net.desolatesky.config.ConfigFile;
import net.desolatesky.config.ConfigUtil;
import net.desolatesky.message.impl.ActionBarMessage;
import net.desolatesky.message.impl.ChatMessage;
import net.desolatesky.message.impl.TitleMessage;
import net.desolatesky.util.TimeUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import net.minestom.server.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class MessageHandler {

    public static final MessageHandler DEFAULT_INSTANCE = MessageHandler.create(
            Path.of("messages.conf"),
            "/messages.conf"
    );

    private final ConfigFile configFile;
    private final Map<String, Message> messages;

    private MessageHandler(ConfigFile configFile, Map<String, Message> messages) {
        this.configFile = configFile;
        this.messages = messages;
    }

    public static MessageHandler create(Path filePath, String resoucePath) {
        final ConfigFile config = ConfigFile.get(filePath, resoucePath, builder -> builder
                .defaultOptions(options -> options.serializers(b -> b.register(ChatMessage.class, new ChatMessageSerializer())
                        .register(ActionBarMessage.class, new ActionBarMessageSerializer())
                        .register(TitleMessage.class, new TitleMessageSerializer()))));
        final MessageHandler messageHandler = new MessageHandler(config, new HashMap<>());
        try {
            messageHandler.load();
        } catch (ConfigurateException e) {
            throw new RuntimeException("Failed to load messages from " + filePath, e);
        }
        return messageHandler;
    }

    public @Nullable Message getMessage(String id) {
        return this.messages.get(id);
    }

    public Message getNonNullMessage(String id) {
        return this.messages.getOrDefault(id, Message.EMPTY);
    }

    public void sendMessage(Player player, String messageId, Map<String, Object> placeholders) {
        final Function<Component, Component> placeholderFunction = component -> {
            Component current = component;
            for (Map.Entry<String, Object> entry : placeholders.entrySet()) {
                current = current.replaceText(builder -> builder.matchLiteral("<" + entry.getKey() + ">").replacement(String.valueOf(entry.getValue())));
            }
            return current;
        };
        this.getNonNullMessage(messageId).sendTo(player, placeholderFunction);
    }

    public void sendMessage(Player player, String messageId, Function<Component, Component> placeholderFunction) {
        this.getNonNullMessage(messageId).sendTo(player, placeholderFunction);
    }

    public void sendMessage(Player player, String messageId) {
        this.getNonNullMessage(messageId).sendTo(player);
    }

    public static String replacePlaceholders(String message, Map<String, String> placeholders) {
        if (placeholders == null || placeholders.isEmpty()) {
            return message;
        }
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            message = message.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return message;
    }

    private void load() throws ConfigurateException {
        final Map<Object, ? extends ConfigurationNode> children = this.configFile.rootNode().childrenMap();
        for (var entry : children.entrySet()) {
            final ConfigurationNode value = entry.getValue();
            final ConfigurationNode typeNode = value.node("type");
            String type = typeNode.virtual() ? "" : typeNode.getString();
            if (type == null) {
                type = "";
            }
            final Message message = switch (type.toLowerCase()) {
                case "actionbar" -> value.get(ActionBarMessage.class);
                case "title" -> value.get(TitleMessage.class);
                default -> value.get(ChatMessage.class);
            };
            if (message == null) {
                throw new IllegalArgumentException();
            }
            this.messages.put(message.id(), message);
        }
    }

    private static class ChatMessageSerializer implements TypeSerializer<ChatMessage> {

        @Override
        public ChatMessage deserialize(@NotNull Type type, ConfigurationNode node) throws SerializationException {
            final String singleMessage = node.getString();
            final String id = (String) node.key();
            if (singleMessage != null) {
                return ChatMessage.parse(id, ConfigUtil.getNonNull(node, String.class));
            }
            final List<? extends ConfigurationNode> children = node.childrenList();
            final List<String> contents = new ArrayList<>();
            for (ConfigurationNode child : children) {
                contents.add(ConfigUtil.getNonNull(child, String.class));
            }
            return ChatMessage.parse(id, contents);
        }

        @Override
        public void serialize(@NotNull Type type, @org.checkerframework.checker.nullness.qual.Nullable ChatMessage obj, @NotNull ConfigurationNode node) {

        }

    }

    private static class ActionBarMessageSerializer implements TypeSerializer<ActionBarMessage> {

        @Override
        public ActionBarMessage deserialize(@NotNull Type type, @NotNull ConfigurationNode node) throws SerializationException {
            final String message = ConfigUtil.getNonNull(node, String.class, "message");
            final String id = (String) node.key();
            return ActionBarMessage.parse(id, message);
        }

        @Override
        public void serialize(@NotNull Type type, @org.checkerframework.checker.nullness.qual.Nullable ActionBarMessage obj, @NotNull ConfigurationNode node) {

        }

    }

    private static class TitleMessageSerializer implements TypeSerializer<TitleMessage> {

        @Override
        public TitleMessage deserialize(@NotNull Type type, @NotNull ConfigurationNode node) throws SerializationException {
            final String title = node.node("title").getString();
            final String subtitle = node.node("subtitle").getString();
            final long fadeInTicks = ConfigUtil.getNonNullElse(node, long.class, () -> TimeUtil.toTicks(Title.DEFAULT_TIMES.fadeIn()), "fade-in");
            final long stayTicks = ConfigUtil.getNonNullElse(node, long.class, () -> TimeUtil.toTicks(Title.DEFAULT_TIMES.fadeIn()), "stay");
            final long fadeOutTicks = ConfigUtil.getNonNullElse(node, long.class, () -> TimeUtil.toTicks(Title.DEFAULT_TIMES.fadeIn()), "fade-out");
            final Title.Times times = Title.Times.times(
                    TimeUtil.ticksToDuration(fadeInTicks),
                    TimeUtil.ticksToDuration(stayTicks),
                    TimeUtil.ticksToDuration(fadeOutTicks)
            );
            final String id = (String) node.key();
            return TitleMessage.parse(id, title, subtitle, times);
        }

        @Override
        public void serialize(@NotNull Type type, @org.checkerframework.checker.nullness.qual.Nullable TitleMessage obj, @NotNull ConfigurationNode node) {

        }

    }

}
