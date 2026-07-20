package net.desolatesky.block.behavior;

import net.desolatesky.player.DSPlayer;
import net.desolatesky.world.DSWorld;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.block.Block;

import java.util.Collection;
import java.util.List;

public interface MiningSpeedBehavior extends BlockBehavior {

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
