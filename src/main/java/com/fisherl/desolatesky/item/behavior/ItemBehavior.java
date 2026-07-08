package com.fisherl.desolatesky.item.behavior;

public interface ItemBehavior {

    record Type<T extends ItemBehavior>(Class<T> itemBehaviorClass) {
        public static final Type<ClickBehavior> CLICK = new Type<>(ClickBehavior.class);
        public static final Type<BlockPlaceBehavior> BLOCK_PLACE = new Type<>(BlockPlaceBehavior.class);
//        public static final Type<TickBehavior> TICK = new Type<>(TickBehavior.class);
//        public static final Type<RandomTickBehavior> RANDOM_TICK = new Type<>(RandomTickBehavior.class);
//        public static final Type<MiningSpeedBehavior> MINING_SPEED = new Type<>(MiningSpeedBehavior.class);
//        public static final Type<BlockUpdateBehavior> UPDATE = new Type<>(BlockUpdateBehavior.class);
    }
}
