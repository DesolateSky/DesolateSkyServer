package net.desolatesky.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.Context;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

import java.util.Map;

public final class ComponentUtil {

    private ComponentUtil() {
        throw new UnsupportedOperationException();
    }

    public static final MiniMessage MINI_MESSAGE = MiniMessage.builder()
            .editTags(b -> b.tag("primary-color", (args, context) -> createTag(Constants.PRIMARY_COLOR, args, context))
                    .tag("secondary-color", (args, context) -> createTag(Constants.SECONDARY_COLOR, args, context))
                    .tag("text-color", (args, context) -> createTag(Constants.SECONDARY_COLOR, args, context))
                    .tag("highlight-color", (args, context) -> createTag(Constants.HIGHLIGHT_COLOR, args, context))
                    .tag("background-color", (args, context) -> createTag(Constants.BACKGROUND_COLOR, args, context))
                    .resolver(TagResolver.resolver(Placeholder.parsed("prefix", Constants.PREFIX_STRING)))
            )
            .postProcessor(component -> component.decoration(TextDecoration.ITALIC, false))
            .build();


    private static Tag createTag(TextColor color, ArgumentQueue args, Context context) {
        return Tag.styling(color);
    }

    public static Component parse(String text) {
        return MINI_MESSAGE.deserialize(text);
    }

    public static Component replaceText(Component component, Map<String, String> replacements) {
        Component current = component;
        for (Map.Entry<String, String> entry : replacements.entrySet()) {
            String placeholder = entry.getKey();
            String replacement = entry.getValue();
            current = current.replaceText(builder -> builder.matchLiteral(placeholder).replacement(replacement));
        }
        return current;
    }

    public static Component noItalics(Component component) {
        return component.decoration(TextDecoration.ITALIC, false);
    }

    public static Component noItalics(String text) {
        return noItalics(Component.text(text));
    }

}
