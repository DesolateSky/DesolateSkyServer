package net.desolatesky.block.behavior.impl;

import net.desolatesky.block.BlockAttributes;
import net.desolatesky.block.behavior.ClickBehavior;
import net.desolatesky.block.behavior.PlaceRequirementsBehavior;
import net.desolatesky.block.behavior.RandomTickBehavior;
import net.desolatesky.block.behavior.serializer.BlockBehaviorSerializer;
import net.desolatesky.block.definition.BlockDefinition;
import net.desolatesky.block.property.BlockProperties;
import net.desolatesky.player.DSPlayer;
import net.desolatesky.util.Namespace;
import net.desolatesky.world.DSWorld;
import net.kyori.adventure.key.Key;
import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.PlayerHand;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import net.minestom.server.utils.Direction;
import org.jetbrains.annotations.NotNullByDefault;
import org.jspecify.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@NotNullByDefault
public final class FireBehavior implements RandomTickBehavior, ClickBehavior, PlaceRequirementsBehavior, PlaceBehavior {

    public static final class Serializer extends BlockBehaviorSerializer<FireBehavior> {

        public static final Key ID = Namespace.minecraftKey("fire");

        private static final String SPREAD_CHANCE_KEY = "spread-chance";
        private static final String EXTINGUISH_CHANCE_KEY = "extinguish-chance";
        private static final String SPREAD_OFFSETS_KEY = "spread-offsets";

        public Serializer() {
            super(ID);
        }

        @Override
        public FireBehavior deserialize(java.lang.reflect.Type type, ConfigurationNode node) throws SerializationException {
            final double spreadChance = node.node(SPREAD_CHANCE_KEY).getDouble();
            final double extinguishChance = node.node(EXTINGUISH_CHANCE_KEY).getDouble();
            final List<Point> spreadOffsets = node.node(SPREAD_OFFSETS_KEY).getList(Point.class, new ArrayList<>());
            return new FireBehavior(spreadChance, extinguishChance, spreadOffsets);
        }

        @Override
        public void serialize(java.lang.reflect.Type type, @Nullable FireBehavior obj, ConfigurationNode node) throws SerializationException {

        }

        @Override
        public Class<FireBehavior> behaviorClass() {
            return FireBehavior.class;
        }
    }

    private final double spreadChance;
    private final double extinguishChance;
    private final List<Point> spreadOffsets;

    public FireBehavior(double spreadChance, double extinguishChance, List<Point> spreadOffsets) {
        this.spreadChance = spreadChance;
        this.extinguishChance = extinguishChance;
        this.spreadOffsets = spreadOffsets;
    }

    @Override
    public ClickBehavior.Result onRightClick(DSWorld world, DSPlayer player, PlayerHand hand, Point clickedPos, Block clickedBlock, ItemStack clickedWith) {
        return ClickBehavior.Result.ALLOW;
    }

    @Override
    public ClickBehavior.Result onLeftClick(DSWorld world, DSPlayer player, Point clickedPos, Block clickedBlock, ItemStack clickedWith) {
        this.extinguish(world, clickedPos);
        // TODO - play fire extinguish sound
        return ClickBehavior.Result.ALLOW;
    }

    @Override
    public void onRandomTick(DSWorld world, Point pos, Block block, Key blockId) {
        if (world.rollChance(pos, this.extinguishChance)) {
            this.extinguish(world, pos);
            return;
        }
        this.spread(world, pos, blockId);
    }

    @Override
    public Block getBlockToPlace(DSWorld world, Point pos, Block block, Key blockId) {
        Block placedBlock = block;
        boolean changed = false;
        if (this.checkDirection(world, pos, Direction.NORTH)) {
            placedBlock = BlockProperties.NORTH.write(placedBlock, true);
            changed = true;
        }
        if (this.checkDirection(world, pos, Direction.EAST)) {
            placedBlock = BlockProperties.EAST.write(placedBlock, true);
            changed = true;
        }
        if (this.checkDirection(world, pos, Direction.SOUTH)) {
            placedBlock = BlockProperties.SOUTH.write(placedBlock, true);
            changed = true;
        }
        if (this.checkDirection(world, pos, Direction.WEST)) {
            placedBlock = BlockProperties.WEST.write(placedBlock, true);
            changed = true;
        }
        if (this.checkDirection(world, pos, Direction.UP)) {
            placedBlock = BlockProperties.UP.write(placedBlock, true);
            changed = true;
        }
        if (this.checkDirection(world, pos, Direction.DOWN)) {
            changed = true;
        }
        if (!changed) {
            return Block.AIR;
        }
        return placedBlock;
    }

    private void spread(DSWorld world, Point from, Key blockId) {
        final BlockDefinition definition = world.blockFactory().getBlockDefinition(blockId);
        if (definition == null) {
            return;
        }
        final Block initialBlock = definition.createBlock();
        for (final Point offset : this.spreadOffsets) {
            final Point spreadTo = from.add(offset);
            final Block block = world.getBlock(spreadTo);
            if (!(world.rollChance(from, this.spreadChance))) {
                continue;
            }
            if (!this.isValidForInitialPlace(world, spreadTo, block)) {
                continue;
            }
            final Block toPlace = this.getBlockToPlace(world, spreadTo, initialBlock, blockId);
            world.setBlock(spreadTo, toPlace, true);
        }
    }

    private void extinguish(DSWorld world, Point pos) {
        world.setBlock(pos, Block.AIR);

    }

    @Override
    public PlaceRequirementsBehavior.Result checkState(DSWorld world, Point pos, Block block) {
        if (!this.isValidForInitialPlace(world, pos, block)) {
            return PlaceRequirementsBehavior.Result.DESTROY;
        }
        return PlaceRequirementsBehavior.Result.GOOD;
    }

    @Override
    public boolean isValidForInitialPlace(DSWorld world, Point pos, Block block) {
        boolean valid = false;
        valid = valid || this.checkDirection(world, pos, Direction.NORTH);
        valid = valid || this.checkDirection(world, pos, Direction.EAST);
        valid = valid || this.checkDirection(world, pos, Direction.SOUTH);
        valid = valid || this.checkDirection(world, pos, Direction.WEST);
        valid = valid || this.checkDirection(world, pos, Direction.UP);
        valid = valid || this.checkDirection(world, pos, Direction.DOWN);
        valid = valid || this.checkFireReplaceable(world, pos);
        return valid;
    }

    private boolean checkDirection(DSWorld world, Point pos, Direction direction) {
        return this.checkSpreadable(world, pos.add(direction.vec()));
    }

    private boolean checkSpreadable(DSWorld world, Point pos) {
        final Block at = world.getBlock(pos);
        final BlockDefinition blockDefinition = world.blockFactory().getBlockDefinition(at);
        return blockDefinition != null && blockDefinition.hasAttribute(BlockAttributes.FLAMMABLE);
    }

    private boolean checkFireReplaceable(DSWorld world, Point pos) {
        final Block at = world.getBlock(pos);
        final BlockDefinition blockDefinition = world.blockFactory().getBlockDefinition(at);
        return blockDefinition != null && blockDefinition.hasAttribute(BlockAttributes.FIRE_STARTER);
    }

    @Override
    public Collection<Type<?>> types() {
        return List.of(Type.RANDOM_TICK, Type.CLICK, Type.PLACE_REQUIREMENTS, Type.PLACE);
    }
}
