package net.desolatesky.message.impl;

import net.desolatesky.message.Message;
import net.desolatesky.util.ComponentUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import net.minestom.server.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public final class TitleMessage implements Message {

    private final String id;
    private final @Nullable Component title;
    private final @Nullable Component subtitle;
    private final Title.Times times;

    public TitleMessage(String id, @Nullable Component title, @Nullable Component subtitle, Title.Times times) {
        this.id = id;
        this.title = title;
        this.subtitle = subtitle;
        this.times = times;
    }

    public static TitleMessage parse(String id, @Nullable Component title, @Nullable Component subTitle, Title.Times times) {
        return new TitleMessage(id, title, subTitle, times);
    }

    public static TitleMessage parse(String id, @Nullable Component title, @Nullable Component subTitle) {
        return new TitleMessage(id, title, subTitle, Title.DEFAULT_TIMES);
    }

    public static TitleMessage parse(String id, @Nullable String title, @Nullable String subTitle, Title.Times times) {
        return parse(id, title == null ? null : ComponentUtil.parse(title), subTitle == null ? null : ComponentUtil.parse(subTitle), times);
    }

    public static TitleMessage parse(String id, @Nullable String title, @Nullable String subTitle) {
        return parse(id, title == null ? null : ComponentUtil.parse(title), subTitle == null ? null : ComponentUtil.parse(subTitle));
    }

    @Override
    public String id() {
        return this.id;
    }

    @Override
    public void sendTo(Player player) {
        if (this.title == null && this.subtitle == null) {
            return;
        }
        final Component titleComponent = this.title != null ? this.title : Component.empty();
        final Component subtitleComponent = this.subtitle != null ? this.subtitle : Component.empty();
        player.showTitle(Title.title(titleComponent, subtitleComponent, this.times));
    }

    @Override
    public void sendTo(Player player, Function<Component, Component> placeholderFunction) {
        final Component titleComponent = this.title != null ? this.title : Component.empty();
        final Component subtitleComponent = this.subtitle != null ? this.subtitle : Component.empty();
        player.showTitle(Title.title(placeholderFunction.apply(titleComponent), placeholderFunction.apply(subtitleComponent), this.times));
    }

    @Override
    public String toString() {
        return "TitleMessage{" +
                "id='" + this.id + '\'' +
                ", title=" + this.title +
                ", subtitle=" + this.subtitle +
                ", times=" + this.times +
                '}';
    }
}
