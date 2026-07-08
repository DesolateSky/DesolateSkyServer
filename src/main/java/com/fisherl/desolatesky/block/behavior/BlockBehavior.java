package com.fisherl.desolatesky.block.behavior;

import com.fisherl.desolatesky.block.behavior.core.IslandCoreTickBehavior;

public interface BlockBehavior {

    record Type<T extends BlockBehavior>(Class<T> blockBehaviorClass) {
        public static final Type<TickBehavior> TICK = new Type<>(TickBehavior.class);
        public static final Type<RandomTickBehavior> RANDOM_TICK = new Type<>(RandomTickBehavior.class);
        public static final Type<MiningSpeedBehavior> MINING_SPEED = new Type<>(MiningSpeedBehavior.class);
        public static final Type<BlockUpdateBehavior> UPDATE = new Type<>(BlockUpdateBehavior.class);
        public static final Type<ClickBehavior> CLICK = new Type<>(ClickBehavior.class);
        public static final Type<IslandCoreTickBehavior> ISLAND_CORE_BEHAVIOR = new Type<>(IslandCoreTickBehavior.class);
    }
}
