package net.desolatesky.block.behavior;

import net.desolatesky.block.behavior.impl.core.VoidCoreBehavior;
import net.desolatesky.block.behavior.impl.BlockEntityBehavior;
import net.desolatesky.block.behavior.impl.PlaceBehavior;
import net.desolatesky.block.behavior.impl.heat.HeatSourceBehavior;
import net.desolatesky.block.behavior.listener.LoadBehavior;

import java.util.Collection;

public interface BlockBehavior {

    Collection<BlockBehavior.Type<?>> types();

    record Type<T extends BlockBehavior>(Class<T> blockBehaviorClass) {
        public static final Type<PlaceBehavior> PLACE = new Type<>(PlaceBehavior.class);
        public static final Type<TickBehavior> TICK = new Type<>(TickBehavior.class);
        public static final Type<RandomTickBehavior> RANDOM_TICK = new Type<>(RandomTickBehavior.class);
        public static final Type<MiningSpeedBehavior> MINING_SPEED = new Type<>(MiningSpeedBehavior.class);
        public static final Type<BlockUpdateBehavior> UPDATE = new Type<>(BlockUpdateBehavior.class);
        public static final Type<ClickBehavior> CLICK = new Type<>(ClickBehavior.class);
        public static final Type<BlockDropBehavior> BLOCK_DROP = new Type<>(BlockDropBehavior.class);
        public static final Type<PlaceRequirementsBehavior> PLACE_REQUIREMENTS = new Type<>(PlaceRequirementsBehavior.class);
        public static final Type<LoadBehavior> LOAD = new Type<>(LoadBehavior.class);
        public static final Type<VoidCoreBehavior> VOID_CORE = new Type<>(VoidCoreBehavior.class);
        public static final Type<BlockEntityBehavior> BLOCK_ENTITY = new Type<>(BlockEntityBehavior.class);
        public static final Type<HeatSourceBehavior> HEAT_SOURCE = new Type<>(HeatSourceBehavior.class);
    }
}
