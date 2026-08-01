package net.desolatesky.block.behavior.serializer;

import net.desolatesky.block.behavior.BlockBehavior;
import net.kyori.adventure.key.Key;
import org.spongepowered.configurate.serialize.TypeSerializer;

public abstract class BlockBehaviorSerializer<T extends BlockBehavior> implements TypeSerializer<T> {

    private final Key key;

    public BlockBehaviorSerializer(Key key) {
        this.key = key;
    }

    public Key key() {
        return this.key;
    }

    public abstract Class<T> behaviorClass();
}
