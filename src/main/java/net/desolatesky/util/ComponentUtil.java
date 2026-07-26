package net.desolatesky.util;

import ch.qos.logback.core.rolling.SizeAndTimeBasedFileNamingAndTriggeringPolicy;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;

import java.util.Map;

public final class ComponentUtil {

    private ComponentUtil() {
        throw new UnsupportedOperationException();
    }

    public static final MiniMessage MINI_MESSAGE = MiniMessage.builder()
            .editTags(b -> b.tag("primary-color", (_, _) -> createTag(Constants.PRIMARY_COLOR))
                    .tag("secondary-color", (_, _) -> createTag(Constants.SECONDARY_COLOR))
                    .tag("text-color", (_, _) -> createTag(Constants.SECONDARY_COLOR))
                    .tag("highlight-color", (_, _) -> createTag(Constants.HIGHLIGHT_COLOR))
                    .tag("background-color", (_, _) -> createTag(Constants.BACKGROUND_COLOR))
                    .resolver(TagResolver.resolver(Placeholder.parsed("prefix", Constants.PREFIX_STRING)))
            )
            .postProcessor(component -> component.decoration(TextDecoration.ITALIC, false))
            .build();

    public static final MiniMessage SAFE_MINI_MESSAGE = MiniMessage.builder()
            .tags(TagResolver.resolver(
                    StandardTags.hoverEvent(),
                    StandardTags.color(),
                    StandardTags.font(),
                    StandardTags.decorations(),
                    StandardTags.gradient(),
                    StandardTags.rainbow(),
                    StandardTags.reset(),
                    StandardTags.pride(),
                    StandardTags.shadowColor()
            ))
            .build();

    private static Tag createTag(TextColor color) {
        return Tag.styling(color);
    }

    public static Component parse(String text) {
        return MINI_MESSAGE.deserialize(text);
    }

    public static Component safeParse(String text) {
        return SAFE_MINI_MESSAGE.deserialize(text);
    }

    public static String serialize(Component component) {
        return MINI_MESSAGE.serialize(component);
    }

    public static Component replaceText(Component component, Map<String, String> replacements) {
        Component current = component;
        for (Map.Entry<String, String> entry : replacements.entrySet()) {
            final String placeholder = entry.getKey();
            final String replacement = entry.getValue();
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
