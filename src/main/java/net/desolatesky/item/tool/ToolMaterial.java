package net.desolatesky.item.tool;

import net.desolatesky.util.Namespace;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;
import org.jetbrains.annotations.NotNull;

public enum ToolMaterial implements Keyed {

    WOODEN(Namespace.key("wooden"));

    private final Key key;

    ToolMaterial(Key key) {
        this.key = key;
    }

    @Override
    public @NotNull Key key() {
        return this.key;
    }

}
