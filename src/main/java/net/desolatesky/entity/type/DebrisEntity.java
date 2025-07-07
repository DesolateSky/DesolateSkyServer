package net.desolatesky.entity.type;

import net.desolatesky.block.BlockBuilder;
import net.desolatesky.block.BlockProperties;
import net.desolatesky.block.DSBlocks;
import net.desolatesky.entity.DSEntity;
import net.desolatesky.instance.DSInstance;
import net.desolatesky.util.RandomUtil;
import net.desolatesky.util.collection.WeightedCollection;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.PlayerHand;
import net.minestom.server.entity.metadata.EntityMeta;
import net.minestom.server.entity.metadata.display.BlockDisplayMeta;
import net.minestom.server.entity.metadata.monster.zombie.ZombieMeta;
import net.minestom.server.entity.metadata.other.ArmorStandMeta;
import net.minestom.server.entity.metadata.other.InteractionMeta;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import net.minestom.server.utils.Direction;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.SplittableRandom;
import java.util.random.RandomGenerator;

public class DebrisEntity extends Entity implements DSEntity {

    private static final int DISPLAYS = 3;
    private static final Direction[] POSSIBLE_DIRECTIONS = new Direction[]{
            Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST
    };

    private final RandomGenerator randomGenerator = new SplittableRandom();
    private final DSInstance dsInstance;
    private final WeightedCollection<ItemStack> debrisItems;
    private final Collection<Display> displays = new ArrayList<>();
    private final Entity baseEntity;

    public DebrisEntity(DSInstance dsInstance, WeightedCollection<ItemStack> debrisItems) {
        super(EntityType.INTERACTION);
        this.dsInstance = dsInstance;
        this.debrisItems = debrisItems;
        final InteractionMeta interactionMeta = (InteractionMeta) this.getEntityMeta();
        interactionMeta.setNotifyAboutChanges(false);
        this.setNoGravity(true);
        this.setInvisible(false);
        interactionMeta.setWidth(1.5f);
        interactionMeta.setHeight(1.5f);
        interactionMeta.setNotifyAboutChanges(true);
        this.baseEntity = new Entity(EntityType.ARMOR_STAND);
        final ArmorStandMeta armorStandMeta = (ArmorStandMeta) this.baseEntity.getEntityMeta();
        armorStandMeta.setNotifyAboutChanges(false);
        this.baseEntity.setNoGravity(true);
        armorStandMeta.setSmall(true);
        armorStandMeta.setInvisible(true);
        armorStandMeta.setNotifyAboutChanges(true);
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
        if (!(clicker instanceof final Player player)) {
            return;
        }
        this.giveItem(player);
    }

    @Override
    public void onPunch(DSEntity attacker) {
        if (!(attacker instanceof final Player player)) {
            return;
        }
        this.giveItem(player);
    }

    private void giveItem(Player player) {
        final ItemStack randomItem = this.debrisItems.next();
        player.getInventory().addItemStack(randomItem);
        this.remove();
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

    private class Display extends Entity implements DSEntity {

        public Display(Direction direction, float[] rotation, Point translation) {
            super(EntityType.BLOCK_DISPLAY);
            final BlockDisplayMeta blockMeta = (BlockDisplayMeta) this.getEntityMeta();
            final Block block = BlockBuilder.of(DSBlocks.get(), Block.LEAF_LITTER)
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

    }

}
