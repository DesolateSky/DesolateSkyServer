package com.fisherl.desolatesky.block.definition;

import com.fisherl.desolatesky.block.behavior.BlockBehavior;
import com.fisherl.desolatesky.block.behavior.RandomTickBehavior;
import com.fisherl.desolatesky.block.setting.BlockSettings;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;
import net.minestom.server.instance.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class BlockDefinition implements Keyed {

    public static BlockDefinitionBuilder builder() {
        return new BlockDefinitionBuilder();
    }

    private final Key key;
    private final Block defaultBlock;
    private final BlockSettings blockSettings;
    private final Map<BlockBehavior.Type<? extends BlockBehavior>, BlockBehavior> blockBehaviors;

    BlockDefinition(Key key, Block defaultBlock, BlockSettings settings, Map<BlockBehavior.Type<? extends BlockBehavior>, BlockBehavior> blockBehaviors) {
        this.key = key;
        this.defaultBlock = defaultBlock;
        this.blockSettings = settings;
        this.blockBehaviors = Collections.unmodifiableMap(blockBehaviors);
    }

    public <T extends BlockBehavior> Optional<T> getBehavior(BlockBehavior.Type<T> behaviorType) {
        final BlockBehavior blockBehavior = this.blockBehaviors.get(behaviorType);
        if (!behaviorType.blockBehaviorClass().isInstance(blockBehavior)) {
            return Optional.empty();
        }
        return Optional.of(behaviorType.blockBehaviorClass().cast(blockBehavior));
    }

    public @Unmodifiable Collection<BlockBehavior> blockBehaviors() {
        return this.blockBehaviors.values();
    }

    @Override
    public @NotNull Key key() {
        return this.key;
    }

    public Block defaultBlock() {
        return this.defaultBlock;
    }

    public BlockSettings settings() {
        return this.blockSettings;
    }
}
