package net.desolatesky.block.entity.custom.powered.generator;

import net.desolatesky.DesolateSkyServer;
import net.desolatesky.block.BlockTags;
import net.desolatesky.block.entity.BlockEntityHandler;
import net.desolatesky.block.entity.custom.powered.PowerBlockEntity;
import net.desolatesky.block.settings.BlockSettings;
import net.desolatesky.instance.DSInstance;
import net.kyori.adventure.key.Key;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.block.Block;
import net.minestom.server.utils.Direction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class SolarPanelBlockEntity extends PowerBlockEntity<SolarPanelBlockEntity> {

    public static BlockEntityHandler<SolarPanelBlockEntity> createHandler(BlockSettings blockSettings) {
        return new Handler(blockSettings);
    }

    private static final Direction OUTPUT_DIRECTION = Direction.DOWN;
    private static final List<Direction> OUTPUT_DIRECTIONS = List.of(OUTPUT_DIRECTION);

    private final PowerGeneratorSettings generatorSettings;

    public SolarPanelBlockEntity(Key key, DesolateSkyServer server) {
        super(key, server);
        this.generatorSettings = Objects.requireNonNull(this.blockHandler().getSetting(BlockTags.POWER_GENERATOR_SETTINGS));
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
        return Collections.emptyList();
    }

    @Override
    public int getFlowInDirection(Direction direction) {
        if (!this.canFlowInDirection(direction)) {
            return 0;
        }
        return Math.min(this.getStored(), this.generatorSettings.transferRate());
    }

    @Override
    public int getMaxPower() {
        return this.generatorSettings.maxPower();
    }

    @Override
    public int getTransferRate() {
        return this.generatorSettings.maxPower();
    }

    @Override
    public boolean canFlowInDirection(Direction direction) {
        return direction == OUTPUT_DIRECTION;
    }

    protected void addPower(int amount) {
        this.addStored(amount);
    }

    protected void generatePower() {
        this.addPower(this.generatorSettings.generationRate());
    }

    @Override
    public int getTickInterval() {
        return this.generatorSettings.tickInterval();
    }

    protected static class Handler extends PowerBlockEntity.Handler<SolarPanelBlockEntity> {

        public Handler(BlockSettings blockSettings) {
            super(blockSettings, SolarPanelBlockEntity.class);
        }

        @Override
        protected void onTick(long tick, DSInstance instance, Block block, Point blockPosition, SolarPanelBlockEntity entity) {
            super.onTick(tick, instance, block, blockPosition, entity);
            if (tick % entity.generatorSettings.tickInterval() != 0) {
                return;
            }
            entity.generatePower();
        }

    }

}
