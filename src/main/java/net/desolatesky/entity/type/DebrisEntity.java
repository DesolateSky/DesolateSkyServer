package net.desolatesky.entity.type;

import net.desolatesky.block.BlockBuilder;
import net.desolatesky.block.BlockProperties;
import net.desolatesky.block.DSBlocks;
import net.desolatesky.entity.DSEntity;
import net.desolatesky.entity.EntityKey;
import net.desolatesky.entity.EntityKeys;
import net.desolatesky.instance.DSInstance;
import net.desolatesky.instance.InstancePoint;
import net.desolatesky.loot.LootContext;
import net.desolatesky.loot.generator.LootGenerator;
import net.desolatesky.loot.generator.LootGeneratorType;
import net.desolatesky.loot.table.LootTable;
import net.desolatesky.player.DSPlayer;
import net.desolatesky.util.InventoryUtil;
import net.desolatesky.util.RandomUtil;
import net.kyori.adventure.util.RGBLike;
import net.minestom.server.collision.BoundingBox;
import net.minestom.server.color.Color;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.PlayerHand;
import net.minestom.server.entity.metadata.display.BlockDisplayMeta;
import net.minestom.server.entity.metadata.other.ArmorStandMeta;
import net.minestom.server.entity.metadata.other.InteractionMeta;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import net.minestom.server.utils.Direction;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.SplittableRandom;
import java.util.random.RandomGenerator;

public class DebrisEntity extends Entity implements DSEntity {

    private static final float WIDTH = 1.0f;
    private static final float HEIGHT = 1.0f;

    public static final LootGeneratorType LOOT_GENERATOR_TYPE = LootGeneratorType.create("debris");

    private static final int DISPLAYS = 3;
    private static final Direction[] POSSIBLE_DIRECTIONS = new Direction[]{
            Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST
    };

    private final RandomGenerator randomGenerator = new SplittableRandom();
    private final DSInstance dsInstance;
    private final Collection<Display> displays = new ArrayList<>();
    private final LootTable lootTable;
    private final float randomPitch;
    private final float randomYaw;
    private final Interaction interactionEntity;

    public DebrisEntity(DSInstance dsInstance, LootTable lootTable) {
        super(EntityType.ARMOR_STAND);
        this.dsInstance = dsInstance;
        this.lootTable = lootTable;
        this.randomPitch = this.randomGenerator.nextFloat(0, 360);
        this.randomYaw = this.randomGenerator.nextFloat(0, 360);
        final ArmorStandMeta armorStandMeta = (ArmorStandMeta) this.getEntityMeta();
        armorStandMeta.setNotifyAboutChanges(false);
        this.setNoGravity(true);
        armorStandMeta.setSmall(true);
        armorStandMeta.setInvisible(true);
        armorStandMeta.setNotifyAboutChanges(true);
        this.setBoundingBox(new BoundingBox(WIDTH, HEIGHT, WIDTH));

        this.interactionEntity = new Interaction();
        this.interactionEntity.setBoundingBox(this.boundingBox);
    }

    @Override
    public void spawn() {
        super.spawn();
        this.interactionEntity.setInstance(this.getInstance(), this.getPosition().add(0, 0.5, 0));

        final Pos currentPos = this.getPosition();
        Point translation = new Vec(-0.5, -0.5, -0.5);

        Entity previous = this;
        for (int i = 0; i < DISPLAYS; i++) {
            final Direction direction = RandomUtil.randomElement(DebrisEntity.this.randomGenerator, POSSIBLE_DIRECTIONS);
            final Display display = new Display(direction, translation);
            translation = translation.add(0, 0.1, 0);
            this.displays.add(display);
            previous.addPassenger(display);
            previous = display;
            display.setInstance(this.getInstance(), currentPos.add(0.5, 0.5, 0.5).withPitch(this.randomPitch).withYaw(this.randomYaw));
        }
    }

    @Override
    public void despawn() {
        for (final Display display : this.displays) {
            display.remove();
        }
        this.interactionEntity.remove();
        super.despawn();
    }

    @Override
    public void onClick(DSEntity clicker, Point interactionPoint, PlayerHand hand) {
        if (!(clicker instanceof final DSPlayer player)) {
            return;
        }
        this.giveLoot(player);
    }

    @Override
    public void onPunch(DSEntity attacker) {
        if (!(attacker instanceof final DSPlayer player)) {
            return;
        }
        this.giveLoot(player);
    }

    private void giveLoot(DSPlayer player) {
        this.remove();
        final Collection<ItemStack> generatedItems = this.generateLoot();
        if (generatedItems.isEmpty()) {
            return;
        }
        InventoryUtil.addItemsToInventory(player, generatedItems, player.getInstancePosition());
    }

    public Collection<ItemStack> generateLoot() {
        final LootGenerator lootGenerator = this.lootTable.getGenerator(LOOT_GENERATOR_TYPE);
        if (lootGenerator == null) {
            return Collections.emptyList();
        }
        return lootGenerator.generateLoot(LootContext.create(this.lootTable.randomSource()));
    }

    public void setGlowing(RGBLike color) {
        this.displays.forEach(display -> {
            final BlockDisplayMeta blockMeta = (BlockDisplayMeta) display.getEntityMeta();
            blockMeta.setGlowColorOverride(Color.fromRGBLike(color).asRGB());
            blockMeta.setNotifyAboutChanges(true);
            display.setGlowing(true);
        });
    }

    @Override
    public void setGlowing(boolean glowing) {
        super.setGlowing(glowing);
        this.displays.forEach(display -> display.setGlowing(glowing));
    }

    @Override
    public void update(long time) {
        this.interactionEntity.teleport(this.getPosition().add(0, 0.5, 0));
    }

    @Override
    public DSInstance getDSInstance() {
        return this.dsInstance;
    }

    @Override
    public void setVelocity(@NotNull Vec velocity) {
        super.setVelocity(velocity);
    }

    @Override
    public @NotNull EntityKey key() {
        return EntityKeys.DEBRIS_ENTITY;
    }

    @Override
    public InstancePoint<Pos> getInstancePosition() {
        return new InstancePoint<>(this.getInstance(), this.getPosition());
    }

    private class Display extends Entity implements DSEntity {

        public Display(Direction direction, Point translation) {
            super(EntityType.BLOCK_DISPLAY);
            final BlockDisplayMeta blockMeta = (BlockDisplayMeta) this.getEntityMeta();
            final Block block = BlockBuilder.from(Block.LEAF_LITTER)
                    .property(BlockProperties.FACING, direction)
                    .property(BlockProperties.SEGMENT_AMOUNT, 4)
                    .build();
            blockMeta.setNotifyAboutChanges(false);
            blockMeta.setTransformationInterpolationDuration(0);
            blockMeta.setPosRotInterpolationDuration(0);
            blockMeta.setBlockState(block);
            blockMeta.setTranslation(translation);
            this.setNoGravity(true);
            blockMeta.setNotifyAboutChanges(true);
        }

        @Override
        public DSInstance getDSInstance() {
            return DebrisEntity.this.dsInstance;
        }

        @Override
        public void onClick(DSEntity clicker, Point interactionPoint, PlayerHand hand) {
            DebrisEntity.this.onClick(clicker, interactionPoint, hand);
        }

        @Override
        public void onPunch(DSEntity attacker) {
            DebrisEntity.this.onPunch(attacker);
        }

        @Override
        public @NotNull EntityKey key() {
            return DebrisEntity.this.key();
        }

        @Override
        public InstancePoint<Pos> getInstancePosition() {
            return new InstancePoint<>(DebrisEntity.this.getInstance(), this.getPosition());
        }
    }

    public class Interaction extends Entity implements DSEntity {

        private Interaction() {
            super(EntityType.INTERACTION);
            final InteractionMeta interactionMeta = (InteractionMeta) this.getEntityMeta();
            interactionMeta.setNotifyAboutChanges(false);
            this.setNoGravity(true);
            this.setInvisible(false);
            interactionMeta.setWidth(WIDTH);
            interactionMeta.setHeight(HEIGHT);
            interactionMeta.setNotifyAboutChanges(true);
            this.setBoundingBox(new BoundingBox(WIDTH, HEIGHT, WIDTH));
        }

        @Override
        public DSInstance getDSInstance() {
            return DebrisEntity.this.dsInstance;
        }

        @Override
        public void onClick(DSEntity clicker, Point interactionPoint, PlayerHand hand) {
            DebrisEntity.this.onClick(clicker, interactionPoint, hand);
        }

        @Override
        public void onPunch(DSEntity attacker) {
            DebrisEntity.this.onPunch(attacker);
        }

        @Override
        public @NotNull EntityKey key() {
            return DebrisEntity.this.key();
        }

        @Override
        public InstancePoint<Pos> getInstancePosition() {
            return new InstancePoint<>(DebrisEntity.this.getInstance(), this.getPosition());
        }

        public DebrisEntity getDebrisEntity() {
            return DebrisEntity.this;
        }

    }

}
