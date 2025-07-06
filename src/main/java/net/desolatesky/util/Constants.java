package net.desolatesky.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

public final class Constants {

    private Constants() {
        throw new UnsupportedOperationException();
    }

    //    <bold><gradient:#AFCDE7:#9FC3C1>DesolateSky
//    <bold><gradient:#C46C3A:#B2A17C>DesolateSky
    public static final String PREFIX_STRING = "<bold><gradient:#C46C3A:#B2A17C>DesolateSky</bold>";
    public static final Component PREFIX_COMPONENT = ComponentUtil.parse(PREFIX_STRING);

    public static final TextColor PRIMARY_COLOR = TextColor.color(0xB2A17C);
    public static final TextColor ACCENT_COLOR = TextColor.color(0xC46C3A);
    public static final TextColor SECONDARY_COLOR = TextColor.color(0x6A7158);
    public static final TextColor HIGHLIGHT_COLOR = TextColor.color(0xE3D9C6);
    public static final TextColor BACKGROUND_COLOR = TextColor.color(0x777066);

}
