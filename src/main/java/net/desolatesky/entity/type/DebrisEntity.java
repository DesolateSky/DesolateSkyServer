package net.desolatesky.entity.type;

import net.desolatesky.block.BlockBuilder;
import net.desolatesky.block.BlockProperties;
import net.desolatesky.block.DSBlocks;
import net.desolatesky.entity.DSEntity;
import net.desolatesky.entity.EntityKeys;
import net.desolatesky.instance.DSInstance;
import net.desolatesky.instance.InstancePoint;
import net.desolatesky.loot.LootContext;
import net.desolatesky.loot.generator.LootGenerator;
import net.desolatesky.loot.generator.LootGeneratorType;
import net.desolatesky.loot.table.LootTable;
import net.desolatesky.player.DSPlayer;
import net.desolatesky.util.InventoryUtil;
import net.desolatesky.util.Namespace;
import net.desolatesky.util.RandomUtil;
import net.kyori.adventure.key.Key;
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
import org.joml.Quaternionf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.SplittableRandom;
import java.util.random.RandomGenerator;

public class DebrisEntity extends Entity implements DSEntity {

    private static final float WIDTH = 2.0f;
    private static final float HEIGHT = 2.0f;

    public static final LootGeneratorType LOOT_GENERATOR_TYPE = LootGeneratorType.create("debris");

    private static final int DISPLAYS = 3;
    private static final Direction[] POSSIBLE_DIRECTIONS = new Direction[]{
            Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST
    };

    private final DSBlocks blocks;
    private final RandomGenerator randomGenerator = new SplittableRandom();
    private final DSInstance dsInstance;
    private final Collection<Display> displays = new ArrayList<>();
    private final Entity baseEntity;
    private final LootTable lootTable;

    public DebrisEntity(DSBlocks blocks, DSInstance dsInstance, LootTable lootTable) {
        super(EntityType.INTERACTION);
        this.blocks = blocks;
        this.dsInstance = dsInstance;
        this.lootTable = lootTable;
        final InteractionMeta interactionMeta = (InteractionMeta) this.getEntityMeta();
        interactionMeta.setNotifyAboutChanges(false);
        this.setNoGravity(true);
        this.setInvisible(false);
        interactionMeta.setWidth(WIDTH);
        interactionMeta.setHeight(HEIGHT);
        interactionMeta.setNotifyAboutChanges(true);
        this.baseEntity = new Entity(EntityType.ARMOR_STAND);
        final ArmorStandMeta armorStandMeta = (ArmorStandMeta) this.baseEntity.getEntityMeta();
        armorStandMeta.setNotifyAboutChanges(false);
        this.baseEntity.setNoGravity(true);
        armorStandMeta.setSmall(true);
        armorStandMeta.setInvisible(true);
        armorStandMeta.setNotifyAboutChanges(true);

        this.setBoundingBox(new BoundingBox(WIDTH, HEIGHT, WIDTH));
    }

    @Override
    public void spawn() {
        super.spawn();
        this.baseEntity.setInstance(this.getInstance(), this.getPosition().sub(0, 1, 0));

        final Quaternionf rotation = RandomUtil.randomRotation(DebrisEntity.this.randomGenerator);
        final float[] arrayRotation = new float[4];
        arrayRotation[0] = rotation.x;
        arrayRotation[1] = rotation.y;
        arrayRotation[2] = rotation.z;
        arrayRotation[3] = rotation.w;

        final Pos currentPos = this.getPosition();
        Point translation = new Vec(0, 0.1, 0);
        Entity previous = this.baseEntity;
        for (int i = 0; i < DISPLAYS; i++) {
            final Direction direction = RandomUtil.randomElement(DebrisEntity.this.randomGenerator, POSSIBLE_DIRECTIONS);
            final Display display = new Display(direction, arrayRotation, translation);
            translation = translation.add(0, 0.2, 0);
            this.displays.add(display);
            previous.addPassenger(display);
            previous = display;
            display.setInstance(this.getInstance(), currentPos);
        }
    }

    @Override
    public void despawn() {
        for (final Display display : this.displays) {
            display.remove();
        }
        this.baseEntity.remove();
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
    }

    @Override
    public DSInstance getDSInstance() {
        return this.dsInstance;
    }

    @Override
    public void setVelocity(@NotNull Vec velocity) {
        super.setVelocity(velocity);
        this.baseEntity.setVelocity(velocity);
    }

    @Override
    public @NotNull Key key() {
        return EntityKeys.DEBRIS_ENTITY;
    }

    @Override
    public InstancePoint<Pos> getInstancePosition() {
        return new InstancePoint<>(this.getInstance(), this.getPosition());
    }

    private class Display extends Entity implements DSEntity {

        private static final Key ENTITY_KEY = Namespace.key("debris_entity", "display");

        public Display(Direction direction, float[] rotation, Point translation) {
            super(EntityType.BLOCK_DISPLAY);
            final BlockDisplayMeta blockMeta = (BlockDisplayMeta) this.getEntityMeta();
            final Block block = BlockBuilder.from(DebrisEntity.this.blocks.leafLitter())
                    .property(BlockProperties.FACING, direction)
                    .property(BlockProperties.SEGMENT_AMOUNT, 4)
                    .build();
            blockMeta.setNotifyAboutChanges(false);
            blockMeta.setTransformationInterpolationDuration(0);
            blockMeta.setPosRotInterpolationDuration(0);
            blockMeta.setBlockState(block);
            blockMeta.setLeftRotation(rotation);
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

        }

        @Override
        public void onPunch(DSEntity attacker) {

        }

        @Override
        public @NotNull Key key() {
            return ENTITY_KEY;
        }

        @Override
        public InstancePoint<Pos> getInstancePosition() {
            return new InstancePoint<>(DebrisEntity.this.getInstance(), this.getPosition());
        }
    }

}
