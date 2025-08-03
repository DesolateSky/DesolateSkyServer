package net.desolatesky.block.settings;

import net.kyori.adventure.sound.Sound;
import org.jetbrains.annotations.Nullable;

public record BlockSoundSettings(@Nullable Sound placeSound, @Nullable Sound breakSound, @Nullable Sound digSound) {

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private @Nullable Sound placeSound;
        private @Nullable Sound breakSound;
        private @Nullable Sound digSound;

        private Builder() {
        }

        public Builder placeSound(@Nullable Sound placeSound) {
            this.placeSound = placeSound;
            return this;
        }

        public Builder breakSound(@Nullable Sound breakSound) {
            this.breakSound = breakSound;
            return this;
        }

        public Builder digSound(@Nullable Sound digSound) {
            this.digSound = digSound;
            return this;
        }

        public BlockSoundSettings build() {
            return new BlockSoundSettings(this.placeSound, this.breakSound, this.digSound);
        }

    }


}
