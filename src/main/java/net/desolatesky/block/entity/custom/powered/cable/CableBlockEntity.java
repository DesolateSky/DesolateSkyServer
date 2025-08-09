package net.desolatesky.block.entity.custom.powered.cable;

import net.desolatesky.DesolateSkyServer;
import net.desolatesky.block.BlockKeys;
import net.desolatesky.block.BlockTags;
import net.desolatesky.block.entity.custom.powered.PowerBlockEntity;
import net.desolatesky.block.handler.BlockHandlerResult;
import net.desolatesky.block.handler.entity.BlockEntityHandler;
import net.desolatesky.block.settings.BlockSettings;
import net.desolatesky.entity.EntityKey;
import net.desolatesky.entity.SimpleEntity;
import net.desolatesky.instance.DSInstance;
import net.desolatesky.player.DSPlayer;
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
import org.jetbrains.annotations.Nullable;

import java.awt.Color;

public class CableBlockEntity extends PowerBlockEntity<CableBlockEntity> {

    private static final double CABLE_SCALE = 0.1;

    public static BlockEntityHandler<CableBlockEntity> createHandler(BlockSettings settings) {
        return new Handler(settings);
    }

    private static final EntityKey ENTITY_KEY = new EntityKey(BlockKeys.CABLE, Component.text("Cable"), ItemStack.AIR);
    private Direction direction;
    private SimpleEntity displayEntity;

    public CableBlockEntity(Key key, DesolateSkyServer server) {
        super(key, server);
    }

    @Override
    public int getMaxElectricity() {
        return 0;
    }

    @Override
    public @Nullable Block save(DSInstance instance, Point point, Block block) {
        if (this.direction == null) {
            return null;
        }
        return block.withTag(BlockTags.DIRECTION_TAG, this.direction);
    }

    @Override
    public void load(Placement placement, DSInstance instance) {
        final Direction direction = placement.getBlock().getTag(BlockTags.DIRECTION_TAG);
        if (direction == null) {
            return;
        }
        this.spawnDisplay(instance, placement.getBlockPosition(), direction);
    }

    private void spawnDisplay(DSInstance instance, Point point, Direction direction) {
        if (this.displayEntity != null) {
            return;
        }

        final CableSettings cableSettings = this.handler.settings().getSetting(BlockTags.CABLE_SETTINGS);
        if (cableSettings == null) {
            return;
        }
        this.displayEntity = new SimpleEntity(EntityType.BLOCK_DISPLAY, ENTITY_KEY);
        this.displayEntity.setInstance(instance, point);
        this.displayEntity.editEntityMeta(BlockDisplayMeta.class, data -> {
            data.setHasNoGravity(true);
            data.setGlowColorOverride(Color.BLUE.getRGB());
            data.setBlockState(cableSettings.displayBlock());
            data.setHasGlowingEffect(true);
            data.setScale(getScaleFromDirection(direction));
            data.setTranslation(getTranslationFromDirection(direction));
        });
    }

    private static Vec getScaleFromDirection(Direction direction) {
        return switch (direction) {
            case UP, DOWN -> new Vec(CABLE_SCALE, 1, CABLE_SCALE);
            case NORTH, SOUTH -> new Vec(CABLE_SCALE, CABLE_SCALE, 1.0);
            case EAST, WEST -> new Vec(1.0, CABLE_SCALE, CABLE_SCALE);
        };
    }

    private static Vec getTranslationFromDirection(Direction direction) {
        return switch (direction) {
            case UP, DOWN -> new Vec(0.5, 0, 0.5);
            case NORTH, SOUTH -> new Vec(0.5, 0.5, 0);
            case EAST, WEST -> new Vec(0.0, 0.5, 0.5);
        };
    }

    private void removeDisplay() {
        System.out.println("Removing entity");
        if (this.displayEntity == null) {
            return;
        }
        this.displayEntity.remove();
        this.displayEntity = null;
        this.direction = null;
    }

    protected static class Handler extends PowerBlockEntity.Handler<CableBlockEntity> {

        public Handler(BlockSettings blockSettings) {
            super(blockSettings, CableBlockEntity.class);
        }

        @Override
        public BlockHandlerResult.Place onPlace(DSInstance instance, Block block, Point blockPosition, CableBlockEntity entity) {
            entity.spawnDisplay(instance, blockPosition, Direction.UP);
            return BlockHandlerResult.passthroughPlace(block);
        }

        @Override
        public BlockHandlerResult.Place onPlayerPlace(DSPlayer player, DSInstance instance, Block block, Point blockPosition, PlayerHand hand, BlockFace face, Point cursorPosition, CableBlockEntity entity) {
            entity.spawnDisplay(instance, blockPosition, face.toDirection().opposite());
            return BlockHandlerResult.passthroughPlace(block);
        }

        @Override
        public BlockHandlerResult onDestroy(DSInstance instance, Block block, Point blockPosition, CableBlockEntity entity) {
            entity.removeDisplay();
            return BlockHandlerResult.PASS_THROUGH;
        }

        @Override
        public BlockHandlerResult onPlayerDestroy(DSPlayer player, DSInstance instance, Block block, Point blockPosition, CableBlockEntity entity) {
            entity.removeDisplay();
            return BlockHandlerResult.PASS_THROUGH;
        }

    }

}
