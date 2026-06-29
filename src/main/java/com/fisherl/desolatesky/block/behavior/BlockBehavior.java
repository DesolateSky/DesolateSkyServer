package com.fisherl.desolatesky.block.behavior;

public interface BlockBehavior {

    record Type<T extends BlockBehavior>(Class<T> blockBehaviorClass) {
        public static final Type<RandomTickBehavior> RANDOM_TICK = new Type<>(RandomTickBehavior.class);
        public static final Type<MiningSpeedBehavior> MINING_SPEED = new Type<>(MiningSpeedBehavior.class);
        public static final Type<BlockUpdateBehavior> UPDATE = new Type<>(BlockUpdateBehavior.class);
    }
}
