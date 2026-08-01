package net.desolatesky.block.definition;

import net.desolatesky.block.behavior.BlockBehavior;
import net.desolatesky.block.behavior.impl.BlockEntityBehavior;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;
import net.minestom.server.instance.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

public final class BlockDefinition implements Keyed {

    public static BlockDefinitionBuilder builder() {
        return new BlockDefinitionBuilder();
    }

    private final Key key;
    private final Block defaultBlock;
    private final Set<Key> attributes;
    private final Map<BlockBehavior.Type<? extends BlockBehavior>, BlockBehavior> blockBehaviors;

    BlockDefinition(Key key,
                    Block defaultBlock,
                    Set<Key> attributes,
                    Map<BlockBehavior.Type<? extends BlockBehavior>, BlockBehavior> blockBehaviors
    ) {
        this.key = key;
        this.defaultBlock = defaultBlock;
        this.attributes = attributes;
        this.blockBehaviors = Collections.unmodifiableMap(blockBehaviors);
    }

    public <T extends BlockBehavior> @Nullable T getBehavior(BlockBehavior.Type<T> behaviorType) {
        final BlockBehavior blockBehavior = this.blockBehaviors.get(behaviorType);
        if (!behaviorType.blockBehaviorClass().isInstance(blockBehavior)) {
            return null;
        }
        return behaviorType.blockBehaviorClass().cast(blockBehavior);
    }

    public @Unmodifiable Collection<BlockBehavior> blockBehaviors() {
        return this.blockBehaviors.values();
    }

    @Override
    public @NotNull Key key() {
        return this.key;
    }

    public Block createBlock() {
        final BlockEntityBehavior blockEntityBehavior = this.getBehavior(BlockBehavior.Type.BLOCK_ENTITY);
        if (blockEntityBehavior == null) {
            return this.defaultBlock;
        }
        return this.defaultBlock.withHandler(blockEntityBehavior.createBlockHandler());
    }

    public Key minecraftKey() {
        return this.defaultBlock.key();
    }

    public boolean hasAttribute(Key attribute) {
        return this.attributes.contains(attribute);
    }
}
