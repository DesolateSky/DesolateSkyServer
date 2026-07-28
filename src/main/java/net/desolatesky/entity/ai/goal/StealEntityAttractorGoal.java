package net.desolatesky.entity.ai.goal;

import net.desolatesky.block.behavior.BlockBehavior;
import net.desolatesky.block.behavior.core.VoidCoreBehavior;
import net.desolatesky.block.definition.BlockDefinition;
import net.desolatesky.entity.ai.navigation.NavigationTarget;
import net.desolatesky.entity.type.VoidEntity;
import net.desolatesky.util.RegionUtil;
import net.desolatesky.world.VoidWorld;
import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.ai.GoalSelector;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import org.jspecify.annotations.Nullable;

public final class StealEntityAttractorGoal<T extends VoidEntity<T>> extends GoalSelector {

    private boolean reachedCore = false;
    // point for if this entity has a stolen entity attractor
    private @Nullable Point escapePoint;

    private final T entity;

    public StealEntityAttractorGoal(T entityCreature) {
        super(entityCreature);
        this.entity = entityCreature;
    }

    @Override
    public boolean shouldStart() {
        return this.coreHasAttractor() || this.entity.hasStolenAttractor();
    }

    @Override
    public void start() {
        if (!(this.entityCreature.getInstance() instanceof final VoidWorld world)) {
            return;
        }
        final Point target = world.getVoidCorePosition().add(0.5, 0.5, 0.5);
        this.entity.navigator().setNewTarget(NavigationTarget.createTarget(target, 0.75));
        this.reachedCore = false;
    }

    @Override
    public void tick(long time) {
        if (this.entity.hasStolenAttractor()) {
            if (!(this.entity.getInstance() instanceof final VoidWorld world)) {
                return;
            }
            if (this.escapePoint == null) {
                this.escapePoint = RegionUtil.getClosestBorderPointTo(world.getRegion(), this.entity.getPosition()).add(0.5, 0, 0.5);
                this.escapePoint = this.escapePoint.withY(this.escapePoint.blockY());
            }
            this.entity.navigator().setNewTarget(NavigationTarget.createTarget(this.escapePoint, 1.5));
            if (this.entity.navigator().reachedTarget()) {
                this.entity.remove();
            }
        }
        if (this.entity.navigator().reachedTarget()) {
            this.reachedCore = true;
            this.entity.navigator().setNewTarget(null);
        } else {
            if (!(this.entity.getInstance() instanceof final VoidWorld world)) {
                return;
            }
            final Point voidCorePosition = world.getVoidCorePosition();
            if (voidCorePosition.distanceSquared(this.entity.getPosition()) > 1) {
                return;
            }
            final Block voidCore = world.getBlock(voidCorePosition);
            final BlockDefinition coreDefinition = world.blockFactory().getBlockDefinition(voidCore);
            if (coreDefinition == null) {
                return;
            }
            final VoidCoreBehavior voidCoreBehavior = coreDefinition.getBehavior(BlockBehavior.Type.VOID_CORE);
            if (voidCoreBehavior == null) {
                return;
            }
            if (!voidCoreBehavior.hasSpawner(world, voidCorePosition, voidCore)) {
                return;
            }
            final ItemStack itemStack = voidCoreBehavior.removeSpawner(world, voidCorePosition, voidCore);
            if (itemStack == null) {
                return;
            }
            this.entity.setStolenAttractor(itemStack);
        }
    }

    @Override
    public boolean shouldEnd() {
        return !this.entity.hasStolenAttractor() && (this.reachedCore || !this.coreHasAttractor());
    }

    private boolean coreHasAttractor() {
        if (!(this.entityCreature.getInstance() instanceof final VoidWorld world)) {
            return false;
        }
        final Point voidCorePosition = world.getVoidCorePosition();
        final Block voidCore = world.getBlock(voidCorePosition);
        final BlockDefinition coreDefinition = world.blockFactory().getBlockDefinition(voidCore);
        if (coreDefinition == null) {
            return false;
        }
        final VoidCoreBehavior voidCoreBehavior = coreDefinition.getBehavior(BlockBehavior.Type.VOID_CORE);
        return voidCoreBehavior != null && voidCoreBehavior.hasSpawner(world, voidCorePosition, voidCore);
    }

    @Override
    public void end() {
        this.entity.navigator().setNewTarget(null);
    }
}
