package net.desolatesky.block;

import net.desolatesky.util.Namespace;
import net.kyori.adventure.key.Key;

public final class BlockKeys {

    private BlockKeys() {
        throw new UnsupportedOperationException();
    }

    public static final Key DEBRIS_CATCHER = Namespace.key("debris_catcher");
    public static final Key DUST_BLOCK = Namespace.key("dust_block");
    public static final Key SIFTER = Namespace.key("sifter");

}
