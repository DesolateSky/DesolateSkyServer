package net.desolatesky.block.definition;

import net.desolatesky.block.behavior.BlockBehavior;
import net.desolatesky.block.setting.BlockSettings;
import net.desolatesky.block.BlockTags;
import net.kyori.adventure.key.Key;
import net.minestom.server.instance.block.Block;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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

        public BlockAttributesStep settings(BlockSettings settings) {
            return new BlockAttributesStep(this.key, this.defaultBlock, settings);
        }
    }

    public static final class BlockAttributesStep {

        private final Key key;
        private final Block defaultBlock;
        private final BlockSettings blockSettings;

        private BlockAttributesStep(Key key, Block defaultBlock, BlockSettings blockSettings) {
            this.key = key;
            this.defaultBlock = defaultBlock;
            this.blockSettings = blockSettings;
        }

        public BlockBehaviorsStep attributes(Set<Key> blockAttributes) {
            return new BlockBehaviorsStep(this.key, this.defaultBlock, this.blockSettings, blockAttributes);
        }

        public BlockBehaviorsStep skipAttributes() {
            return new BlockBehaviorsStep(this.key, this.defaultBlock, this.blockSettings, Collections.emptySet());
        }
    }

    public static final class BlockBehaviorsStep {

        private final Key key;
        private final Block defaultBlock;
        private final BlockSettings settings;
        private final Set<Key> blockAttributes;
        private final Map<BlockBehavior.Type<?>, BlockBehavior> blockBehaviors;

        private BlockBehaviorsStep(Key key, Block defaultBlock, BlockSettings settings, Set<Key> blockAttributes) {
            this.key = key;
            this.defaultBlock = defaultBlock;
            this.settings = settings;
            this.blockBehaviors = new HashMap<>();
            this.blockAttributes = blockAttributes;
        }

        public <T extends BlockBehavior> BlockBehaviorsStep defineBehavior(BlockBehavior.Type<? extends T> type, T blockBehavior) {
            if (this.blockBehaviors.containsKey(type)) {
                throw new IllegalArgumentException("Block behavior of type " + type.blockBehaviorClass().getName() + " is already defined.");
            }
            this.blockBehaviors.put(type, blockBehavior);
            return this;
        }

        public <T extends BlockBehavior> BlockBehaviorsStep defineBehaviors(T blockBehavior) {
            blockBehavior.types().forEach(type -> this.defineBehavior(type, blockBehavior));
            return this;
        }

        public BlockDefinition build() {
            return new BlockDefinition(this.key, this.defaultBlock, this.settings, this.blockAttributes, this.blockBehaviors);
        }
    }
}
