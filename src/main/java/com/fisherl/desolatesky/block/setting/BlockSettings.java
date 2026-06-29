package com.fisherl.desolatesky.block.setting;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class BlockSettings {

    public static final BlockSettings NONE = builder().build();

    private final Map<BlockSetting.Type<BlockSetting>, BlockSetting> blockSettings;

    private BlockSettings(Map<BlockSetting.Type<BlockSetting>, BlockSetting> blockSettings) {
        this.blockSettings = Map.copyOf(blockSettings);
    }

    public Collection<BlockSetting> getAllSettings() {
        return Collections.unmodifiableCollection(this.blockSettings.values());
    }

    public static Builder builder() {
        return new Builder();
    }

    public <T extends BlockSetting> Optional<T> getSetting(BlockSetting.Type<T> settingType) {
        final BlockSetting setting = this.blockSettings.get(settingType);
        if (setting == null) {
            return Optional.empty();
        }
        return settingType.tryCast(setting);
    }

    public static class Builder {
        private final Map<BlockSetting.Type<BlockSetting>, BlockSetting> blockSettings = new HashMap<>();

        private Builder() {
        }

        @SuppressWarnings("unchecked")
        public <T extends BlockSetting> Builder setting(BlockSetting.Type<T> type, BlockSetting setting) {
            this.blockSettings.put((BlockSetting.Type<BlockSetting>) type, setting);
            return this;
        }

        public BlockSettings build() {
            return new BlockSettings(this.blockSettings);
        }
    }
}
