package net.desolatesky.sound;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;

public final class Sounds {

    private Sounds() {
        throw new UnsupportedOperationException();
    }

    public static final Sound BLOCK_SAND_BREAK = Sound.sound(Key.key("block.sand.break"), Sound.Source.BLOCK, 1.0f, 1.0f);
    public static final Sound BLOCK_SAND_HIT = Sound.sound(Key.key("block.sand.hit"), Sound.Source.BLOCK, 0.5f, 0.5f);
    public static final Sound BLOCK_SAND_PLACE = Sound.sound(Key.key("block.sand.place"), Sound.Source.BLOCK, 1.0f, 1.0f);
}
