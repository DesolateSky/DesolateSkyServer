package net.desolatesky.block.behavior;

import net.desolatesky.block.behavior.serializer.BlockBehaviorSerializer;
import net.desolatesky.player.DSPlayer;
import net.desolatesky.util.Namespace;
import net.desolatesky.world.DSWorld;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.block.Block;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.Collection;
import java.util.List;

public interface MiningSpeedBehavior extends BlockBehavior {

    final class Serializer extends BlockBehaviorSerializer<MiningSpeedBehavior> {

        public Serializer() {
            super(Namespace.key("mining_speed"));
        }

        @Override
        public MiningSpeedBehavior deserialize(java.lang.reflect.Type type, ConfigurationNode node) {
            final int ticks = node.node("ticks").getInt(-1);
            if (ticks < 0) {
                return UNBREAKABLE;
            }
            return ticks(ticks);
        }

        @Override
        public void serialize(java.lang.reflect.Type type, @org.jspecify.annotations.Nullable MiningSpeedBehavior obj, ConfigurationNode node) throws SerializationException {
        }

        @Override
        public Class<MiningSpeedBehavior> behaviorClass() {
            return MiningSpeedBehavior.class;
        }
    }


    int getTicksToMine(DSWorld world, Point blockPos, Block block, DSPlayer player);

    MiningSpeedBehavior UNBREAKABLE = new MiningSpeedBehavior() {
        @Override
        public int getTicksToMine(DSWorld world, Point blockPos, Block block, DSPlayer player) {
            return -1;
        }

        @Override
        public Collection<Type<?>> types() {
            return List.of(Type.MINING_SPEED);
        }
    };

    static MiningSpeedBehavior ticks(int ticks)  {
        return new MiningSpeedBehavior() {
            @Override
            public int getTicksToMine(DSWorld world, Point blockPos, Block block, DSPlayer player) {
                return ticks;
            }

            @Override
            public Collection<Type<?>> types() {
                return List.of(Type.MINING_SPEED);
            }
        };
    }

}
