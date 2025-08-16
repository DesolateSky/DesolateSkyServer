package net.desolatesky.block.entity.custom.powered.cable;

import net.desolatesky.DesolateSkyServer;
import net.desolatesky.block.BlockKeys;
import net.desolatesky.block.BlockTags;
import net.desolatesky.block.entity.BlockEntityHandler;
import net.desolatesky.block.entity.custom.powered.PowerBlockEntity;
import net.desolatesky.block.entity.custom.powered.PoweredBlockEntity;
import net.desolatesky.block.handler.BlockHandlerResult;
import net.desolatesky.block.settings.BlockSettings;
import net.desolatesky.entity.EntityKey;
import net.desolatesky.entity.SimpleEntity;
import net.desolatesky.instance.DSInstance;
import net.desolatesky.player.DSPlayer;
import net.desolatesky.util.DirectionUtil;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.PlayerHand;
import net.minestom.server.entity.metadata.display.BlockDisplayMeta;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockFace;
import net.minestom.server.item.ItemStack;
import net.minestom.server.utils.Direction;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CableBlockEntity extends PowerBlockEntity<CableBlockEntity> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CableBlockEntity.class);

    private static final double CABLE_SCALE = 0.1;

    public static BlockEntityHandler<CableBlockEntity> createHandler(BlockSettings settings) {
        return new Handler(settings);
    }

    private static final EntityKey ENTITY_KEY = new EntityKey(BlockKeys.CABLE, Component.text("Cable"), ItemStack.AIR);
    private Direction direction;
    private final Map<Direction, SimpleEntity> connectionDisplays = new HashMap<>();
    private final CableSettings cableSettings;

    public CableBlockEntity(Key key, DesolateSkyServer server) {
        super(key, server);
        this.cableSettings = Objects.requireNonNull(this.blockHandler().getSetting(BlockTags.CABLE_SETTINGS));
    }

    @Override
    public int getMaxPower() {
        return this.cableSettings.maxPower();
    }

    @Override
    public int getTransferRate() {
        return this.cableSettings.transferRate();
    }

    @Override
    public @NotNull Block save(DSInstance instance, Point point, Block block) {
        if (this.direction == null) {
            LOGGER.warn("Cable block entity at {} has no direction set, cannot save", point);
            return block;
        }
        return super.save(instance, point, block).withTag(BlockTags.DIRECTION, this.direction)
                .withTag(BlockTags.CONNECTIONS_TAG, new ArrayList<>(this.connectionDisplays.keySet()));
    }

    @Override
    public void load(Placement placement, DSInstance instance) {
        super.load(placement, instance);
        if (this.direction != null) {
            return; // Already loaded
        }
        this.setDirection(placement.getBlock().getTag(BlockTags.DIRECTION));
        if (this.direction == null) {
            return;
        }
        final List<Direction> connections = placement.getBlock().getTag(BlockTags.CONNECTIONS_TAG);
        if (connections == null) {
            return;
        }
        for (Direction connection : connections) {
            this.spawnDisplay(instance, placement.getBlockPosition(), connection, connection != this.direction);
        }
    }

    private void setDirection(Direction direction) {
        this.direction = direction;
    }

    /**
     * @param outputConnection If this entity is caused by being an output to another entity
     */
    private void spawnDisplay(DSInstance instance, Point point, Direction direction, boolean outputConnection) {
        if ((direction == this.direction || direction.opposite() == this.direction) && outputConnection) {
            return;
        }
        if (direction != this.direction && !outputConnection) {
            this.setDirection(direction);
        }
        final SimpleEntity displayEntity = new SimpleEntity(EntityType.BLOCK_DISPLAY, ENTITY_KEY);
        displayEntity.setInstance(instance, point);
        displayEntity.editEntityMeta(BlockDisplayMeta.class, data -> {
            data.setHasNoGravity(true);
            data.setGlowColorOverride(Color.BLUE.getRGB());
            data.setBlockState(outputConnection ? this.cableSettings.outputDisplayBlock() : this.cableSettings.inputDisplayBlock());
            data.setScale(getScaleFromDirection(direction, outputConnection));
            data.setTranslation(getTranslationFromDirection(direction, outputConnection));
        });
        final SimpleEntity previous = this.connectionDisplays.put(direction, displayEntity);
        if (previous != null) {
            previous.remove();
        }
    }

    private static Vec getScaleFromDirection(Direction direction, boolean outputConnection) {
        final double importantPartSize = outputConnection ? 0.5 - CABLE_SCALE / 2.0 : 1;
        return switch (direction) {
            case UP, DOWN -> new Vec(CABLE_SCALE, importantPartSize, CABLE_SCALE);
            case NORTH, SOUTH -> new Vec(CABLE_SCALE, CABLE_SCALE, importantPartSize);
            case EAST, WEST -> new Vec(importantPartSize, CABLE_SCALE, CABLE_SCALE);
        };
    }

    private static Vec getTranslationFromDirection(Direction direction, boolean outputConnection) {
        final double importantPartTranslation = outputConnection ? 0.5 + CABLE_SCALE / 2.0 : 0;
        final double commonTranslation = 0.5 - CABLE_SCALE / 2;
        return switch (direction) {
            case UP -> new Vec(commonTranslation, importantPartTranslation, commonTranslation);
            case DOWN -> new Vec(commonTranslation, 0, commonTranslation);
            case SOUTH -> new Vec(commonTranslation, commonTranslation, importantPartTranslation);
            case NORTH -> new Vec(commonTranslation, commonTranslation, 0);
            case EAST -> new Vec(importantPartTranslation, commonTranslation, commonTranslation);
            case WEST -> new Vec(0, commonTranslation, commonTranslation);
        };
    }

    private void glowDisplays(DSInstance instance, Point point) {
        final SimpleEntity mainDisplay = this.connectionDisplays.get(this.direction);
        if (mainDisplay == null) {
            return;
        }
        this.glowDisplay(mainDisplay, this.getStored() > 0);
        for (Direction otherDirection : Direction.values()) {
            if (otherDirection == this.direction) {
                continue;
            }
            final Point neighborPoint = point.add(otherDirection.vec());
            final Block neighbor = instance.getBlock(neighborPoint);
            final SimpleEntity display = this.connectionDisplays.get(otherDirection);
            if (display == null) {
                continue;
            }
            if (!(neighbor.handler() instanceof final PoweredBlockEntity<?> powerNeighbor)) {
                this.glowDisplay(display, false);
                continue;
            }
            if (!powerNeighbor.isFull()) {
                this.glowDisplay(display, false);
                continue;
            }
            this.glowDisplay(display, true);
        }
    }

    private void glowDisplay(SimpleEntity display, boolean glow) {
        display.editEntityMeta(BlockDisplayMeta.class, data -> {
            if (!glow) {
                data.setHasGlowingEffect(false);
                return;
            }
            data.setHasGlowingEffect(true);
            data.setGlowColorOverride(Color.BLUE.getRGB());
        });
    }

    private void removeDisplay() {
        this.connectionDisplays.values().forEach(SimpleEntity::remove);
        this.connectionDisplays.clear();
    }

    private void removeDisplay(Direction direction) {
        if (direction == this.direction) {
            return;
        }
        final SimpleEntity display = this.connectionDisplays.remove(direction);
        if (display != null) {
            display.remove();
        }
    }

    @Override
    public int getFlowInDirection(Direction direction) {
        return this.canFlowInDirection(direction) ? Math.min(this.getStored(), this.cableSettings.transferRate()) : 0;
    }

    @Override
    public boolean canFlowInDirection(Direction direction) {
        return direction != this.direction.opposite();
    }

    @Override
    public int getTickInterval() {
        return this.cableSettings.tickInterval();
    }

    protected static class Handler extends PowerBlockEntity.Handler<CableBlockEntity> {

        public Handler(BlockSettings blockSettings) {
            super(blockSettings, CableBlockEntity.class);
        }

        @Override
        protected BlockHandlerResult.Place onPlace(DSInstance instance, Block block, Point blockPosition, CableBlockEntity entity) {
            entity.spawnDisplay(instance, blockPosition, Direction.UP, false);
            this.checkDisplays(instance, blockPosition, block, entity);
            return BlockHandlerResult.passthroughPlace(block);
        }

        @Override
        protected BlockHandlerResult.Place onPlayerPlace(DSPlayer player, DSInstance instance, Block block, Point blockPosition, PlayerHand hand, BlockFace face, Point cursorPosition, CableBlockEntity entity) {
            entity.spawnDisplay(instance, blockPosition, face.toDirection().opposite(), false);
            this.checkDisplays(instance, blockPosition, block, entity);
            return BlockHandlerResult.passthroughPlace(block);
        }

        @Override
        protected BlockHandlerResult onDestroy(DSInstance instance, Block block, Point blockPosition, CableBlockEntity entity) {
            entity.removeDisplay();
            return BlockHandlerResult.PASS_THROUGH;
        }

        @Override
        protected BlockHandlerResult onPlayerDestroy(DSPlayer player, DSInstance instance, Block block, Point blockPosition, CableBlockEntity entity) {
            entity.removeDisplay();
            return BlockHandlerResult.PASS_THROUGH;
        }

        @Override
        protected BlockHandlerResult onUpdate(DSInstance instance, Point point, Block block, Point causePoint, Block causeBlock, CableBlockEntity entity) {
            return this.checkDisplays(instance, point, block, causePoint, entity);
        }

        private BlockHandlerResult checkDisplays(DSInstance instance, Point point, Block block, CableBlockEntity entity) {
            for (Direction direction : DirectionUtil.ALL_DIRECTIONS) {
                if (direction == entity.direction || direction == entity.direction.opposite()) {
                    continue;
                }
                final Point neighborPosition = point.add(direction.vec());
                this.checkDisplays(instance, point, block, neighborPosition, entity);
            }
            return BlockHandlerResult.PASS_THROUGH;
        }

        private BlockHandlerResult checkDisplays(DSInstance instance, Point point, Block block, Point connectionPoint, CableBlockEntity entity) {
            final Direction direction = DirectionUtil.getDirectionBetween(point, connectionPoint);
            if (direction == entity.direction) {
                return BlockHandlerResult.PASS_THROUGH;
            }
            final Block connectionBlock = instance.getBlock(connectionPoint);
            if (!(connectionBlock.handler() instanceof final PoweredBlockEntity<?> connectionEntity)) {
                entity.removeDisplay(direction);
                return BlockHandlerResult.PASS_THROUGH;
            }
            if (!connectionEntity.canReceivePowerFrom(direction)) {
                return BlockHandlerResult.PASS_THROUGH;
            }
            if (connectionEntity instanceof final CableBlockEntity cableBlockEntity) {
                final Direction sourceDirection = cableBlockEntity.direction;
                if (sourceDirection == entity.direction || sourceDirection.opposite() == entity.direction) {
                    return BlockHandlerResult.PASS_THROUGH;
                }
            }
            entity.spawnDisplay(instance, point, direction, true);
            return BlockHandlerResult.PASS_THROUGH;
        }


        @Override
        protected void onTick(long tick, DSInstance instance, Block block, Point blockPosition, CableBlockEntity entity) {
            super.onTick(tick, instance, block, blockPosition, entity);
            entity.glowDisplays(instance, blockPosition);
        }

    }

}
