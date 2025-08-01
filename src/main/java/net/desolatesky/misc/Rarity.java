package net.desolatesky.misc;

import net.desolatesky.util.ComponentUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public enum Rarity {

    COMMON(ComponentUtil.noItalics("Common").color(NamedTextColor.GRAY)),
    UNCOMMON(ComponentUtil.noItalics("Uncommon").color(NamedTextColor.DARK_GRAY)),
    RARE(ComponentUtil.noItalics("Rare").color(NamedTextColor.GREEN)),
    EPIC(ComponentUtil.noItalics("Epic").color(NamedTextColor.DARK_AQUA)),
    LEGENDARY(ComponentUtil.noItalics("Legendary").color(NamedTextColor.AQUA)),
    MYTHICAL(ComponentUtil.noItalics("Mythical").color(NamedTextColor.RED));

    private final Component displayName;

    Rarity(Component displayName) {
        this.displayName = displayName;
    }

    public Component displayName() {
        return this.displayName;
    }

}
