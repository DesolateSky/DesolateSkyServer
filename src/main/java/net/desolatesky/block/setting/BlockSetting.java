package net.desolatesky.block.setting;

import net.desolatesky.world.DSWorld;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.block.Block;

import java.util.Optional;

public interface BlockSetting {

    // ORDER MATTERS
    enum Result {

        DESTROY_AND_DROP,
        DESTROY,
        GOOD

    }

    Result checkState(DSWorld world, Point pos, Block block);

    record Type<T extends BlockSetting>(Class<T> settingClass) {

        public static final Type<SupportedBlockSetting> SUPPORTED_BLOCK = new Type<>(SupportedBlockSetting.class);

        public Optional<T> tryCast(BlockSetting o) {
            try {
                return Optional.ofNullable(this.settingClass.cast(o));
            } catch (ClassCastException _) {
                return Optional.empty();
            }
        }

    }

}
