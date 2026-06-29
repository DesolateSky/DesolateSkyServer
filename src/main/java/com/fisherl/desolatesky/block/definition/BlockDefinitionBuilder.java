package com.fisherl.desolatesky.block.definition;

import com.fisherl.desolatesky.block.behavior.BlockBehavior;
import com.fisherl.desolatesky.block.setting.BlockSettings;
import com.fisherl.desolatesky.block.tag.BlockTags;
import net.kyori.adventure.key.Key;
import net.minestom.server.instance.block.Block;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class BlockDefinitionBuilder {

    BlockDefinitionBuilder() {
    }

    public DefaultBlockStep key(Key key) {
        return new DefaultBlockStep(key);
    }

    public static final class DefaultBlockStep {

        private final Key key;

        private DefaultBlockStep(Key key) {
            this.key = key;
        }

        public BlockSettingsStep defaultBlock(Block defaultBlock) {
            if (!this.key.namespace().equals(Key.MINECRAFT_NAMESPACE)) {
                return new BlockSettingsStep(this.key, defaultBlock.withTag(BlockTags.ID, this.key));
            }
            return new BlockSettingsStep(this.key, defaultBlock);
        }
    }

    public static final class BlockSettingsStep {

        private final Key key;
        private final Block defaultBlock;

        private BlockSettingsStep(Key key, Block defaultBlock) {
            this.key = key;
            this.defaultBlock = defaultBlock;
        }

        public BlockBehaviorsStep settings(BlockSettings settings) {
            return new BlockBehaviorsStep(this.key, this.defaultBlock, settings);
        }
    }

    public static final class BlockBehaviorsStep {

        private final Key key;
        private final Block defaultBlock;
        private final BlockSettings settings;
        private final Map<BlockBehavior.Type<?>, BlockBehavior> blockBehaviors;

        private BlockBehaviorsStep(Key key, Block defaultBlock, BlockSettings settings) {
            this.key = key;
            this.defaultBlock = defaultBlock;
            this.settings = settings;
            this.blockBehaviors = new HashMap<>();
        }

        public <T extends BlockBehavior> BlockBehaviorsStep defineBehavior(BlockBehavior.Type<T> type, T blockBehavior) {
            if (this.blockBehaviors.containsKey(type)) {
                throw new IllegalArgumentException("Block behavior of type " + type.blockBehaviorClass().getName() + " is already defined.");
            }
            this.blockBehaviors.put(type, blockBehavior);
            return this;
        }

        public BlockDefinition build() {
            return new BlockDefinition(this.key, this.defaultBlock, this.settings, this.blockBehaviors);
        }
    }
}
