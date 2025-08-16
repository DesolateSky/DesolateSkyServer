package net.desolatesky.block.entity.custom.powered.machine;

import net.desolatesky.DesolateSkyServer;
import net.desolatesky.block.BlockTags;
import net.desolatesky.block.DSBlocks;
import net.desolatesky.block.entity.BlockEntityHandler;
import net.desolatesky.block.entity.custom.powered.PoweredBlockEntity;
import net.desolatesky.block.settings.BlockSettings;
import net.desolatesky.instance.DSInstance;
import net.kyori.adventure.key.Key;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.block.Block;
import net.minestom.server.utils.Direction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.EnumSet;
import java.util.List;
import java.util.Objects;

public final class CobblestoneGeneratorBlockEntity extends PoweredBlockEntity<CobblestoneGeneratorBlockEntity> {

    private static final List<Direction> INPUT_DIRECTIONS = EnumSet.complementOf(EnumSet.of(Direction.DOWN)).stream().toList();

    public static BlockEntityHandler<CobblestoneGeneratorBlockEntity> createHandler(BlockSettings blockSettings) {
        return new Handler(blockSettings);
    }

    private final int maxPower;
    private final int requiredPower;
    private final int tickInterval;
    private final Direction direction = Direction.UP;

    public CobblestoneGeneratorBlockEntity(Key key, DesolateSkyServer server) {
        super(key, server);
        this.maxPower = Objects.requireNonNull(this.blockHandler().settings().getSetting(BlockTags.MAX_POWER));
        this.requiredPower = Objects.requireNonNull(this.blockHandler().settings().getSetting(BlockTags.REQUIRED_POWER));
        this.tickInterval = Objects.requireNonNull(this.blockHandler().settings().getSetting(BlockTags.TICK_INTERVAL));
    }

    @Override
    public int getMaxPower() {
        return this.maxPower;
    }

    @Override
    public @NotNull Block save(DSInstance instance, Point point, Block block) {
        return super.save(instance, point, block);
    }

    @Override
    public void load(Placement placement, DSInstance instance) {
        super.load(placement, instance);
    }

    @Override
    public @Unmodifiable List<Direction> getInputDirections() {
        return INPUT_DIRECTIONS;
    }

    @Override
    public int getTickInterval() {
        return this.tickInterval;
    }

    protected static class Handler extends PoweredBlockEntity.Handler<CobblestoneGeneratorBlockEntity> {

        public Handler(BlockSettings blockSettings) {
            super(blockSettings, CobblestoneGeneratorBlockEntity.class);
        }

        @Override
        protected void onTick(long tick, DSInstance instance, Block block, Point blockPosition, CobblestoneGeneratorBlockEntity entity) {
            super.onTick(tick, instance, block, blockPosition, entity);
            if (tick % entity.getTickInterval() != 0) {
                return;
            }
            final Point destination = blockPosition.add(entity.direction.vec());
            final Block destinationBlock = instance.getBlock(destination);
            if (!destinationBlock.isAir() && !destinationBlock.registry().isReplaceable()) {
                return;
            }
            if (DSBlocks.COBBLESTONE.is(instance.getBlock(destination))) {
                return;
            }
            if (entity.getStored() >= entity.requiredPower) {
                entity.subtractStored(entity.requiredPower);
                instance.setBlock(destination, DSBlocks.COBBLESTONE.create(entity.server.blockEntities()));
            }
        }

    }

}
