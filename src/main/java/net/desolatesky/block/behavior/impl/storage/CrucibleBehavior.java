package net.desolatesky.block.behavior.impl.storage;

import net.desolatesky.block.behavior.ClickBehavior;
import net.desolatesky.block.behavior.RandomTickBehavior;
import net.desolatesky.block.behavior.TickBehavior;
import net.desolatesky.block.behavior.impl.BlockEntityBehavior;
import net.desolatesky.block.behavior.impl.heat.HeatSourceBehavior;
import net.desolatesky.block.behavior.listener.LoadBehavior;
import net.desolatesky.block.behavior.serializer.BlockBehaviorSerializer;
import net.desolatesky.block.definition.BlockDefinition;
import net.desolatesky.block.handler.DSBlockHandler;
import net.desolatesky.cooldown.Cooldown;
import net.desolatesky.cooldown.CooldownTemplate;
import net.desolatesky.cooldown.TickingCooldown;
import net.desolatesky.item.ItemFactory;
import net.desolatesky.item.ItemTags;
import net.desolatesky.measurement.FluidType;
import net.desolatesky.measurement.FluidUnit;
import net.desolatesky.measurement.FluidValue;
import net.desolatesky.measurement.TemperatureValue;
import net.desolatesky.player.DSPlayer;
import net.desolatesky.recipe.type.CrucibleRecipe;
import net.desolatesky.util.InventoryUtil;
import net.desolatesky.util.ItemUtil;
import net.desolatesky.util.Namespace;
import net.desolatesky.util.Tags;
import net.desolatesky.world.DSWorld;
import net.kyori.adventure.key.Key;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.PlayerHand;
import net.minestom.server.entity.metadata.display.BlockDisplayMeta;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockHandler;
import net.minestom.server.item.ItemStack;
import net.minestom.server.tag.Tag;
import net.minestom.server.utils.Direction;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.checker.units.qual.K;
import org.jetbrains.annotations.NotNullByDefault;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.Collection;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@NotNullByDefault
public final class CrucibleBehavior implements ClickBehavior, LoadBehavior, TickBehavior, BlockEntityBehavior {

    private static final Key ID = Namespace.key("crucible");

    public static final class Serializer extends BlockBehaviorSerializer<CrucibleBehavior> {

        private static final String DESTROYED_ON_CRAFT_KEY = "destroyed-on-craft";
        private static final String MAX_BUCKETS_KEY = "max-buckets";

        public Serializer() {
            super(ID);
        }

        @Override
        public Class<CrucibleBehavior> behaviorClass() {
            return CrucibleBehavior.class;
        }

        @Override
        public CrucibleBehavior deserialize(java.lang.reflect.Type type, ConfigurationNode node) throws SerializationException {
            final boolean destroyedOnCraft = node.node(DESTROYED_ON_CRAFT_KEY).getBoolean();
            final double maxBuckets = node.node(MAX_BUCKETS_KEY).getDouble();
            return new CrucibleBehavior(destroyedOnCraft, maxBuckets);
        }

        @Override
        public void serialize(java.lang.reflect.Type type, @Nullable CrucibleBehavior obj, ConfigurationNode node) throws SerializationException {

        }
    }

    private static final Tag<UUID> DISPLAY_ENTITY_ID = Tags.UUID("display-entity");
    private static final Point DISPLAY_OFFSET = new Vec(0, 0.2, 0);

    private @Nullable TickingCooldown tickingCooldown;
    private final boolean destroyedOnCraft;
    private final double maxBuckets;

    public CrucibleBehavior(boolean destroyedOnCraft, double maxBuckets) {
        this.destroyedOnCraft = destroyedOnCraft;
        this.maxBuckets = maxBuckets;
    }

    @Override
    public Result onRightClick(DSWorld world, DSPlayer player, PlayerHand hand, Point clickedPos, Block clickedBlock, ItemStack clickedWith) {
        final FluidValue fluidValue = clickedWith.getTag(ItemTags.FLUID_VALUE);
        if (fluidValue == null) {
            return Result.ALLOW;
        }
        if (!(clickedBlock.handler() instanceof final CrucibleBlockEntity blockEntity)) {
            return Result.BLOCK_INTERACTION;
        }
        final Map<Key, Integer> input = new HashMap<>(blockEntity.itemAmounts);
        input.merge(ItemUtil.getItemId(clickedWith), 1, Integer::sum);
        CrucibleRecipe recipe = null;
        for (final CrucibleRecipe crucibleRecipe : world.recipeFactory().getCrucibleRecipes()) {
            final CrucibleRecipe.Result result = crucibleRecipe.craft(world.itemFactory(), new CrucibleRecipe.Input(input));
            if (result != null) {
                recipe = crucibleRecipe;
                break;
            }
        }
        if (!blockEntity.addItem(clickedWith, fluidValue)) {
            return Result.BLOCK_INTERACTION;
        }
        InventoryUtil.subtractFromHeldItem(player, hand, 1);
        this.updateDisplay(world, clickedPos, clickedBlock, recipe);
        return Result.BLOCK_INTERACTION;
    }

    private void updateDisplay(DSWorld world, Point blockPos, Block clickedBlock, @Nullable CrucibleRecipe currentRecipe) {
        if (!(clickedBlock.handler() instanceof final CrucibleBlockEntity crucibleBlockEntity)) {
            return;
        }
        crucibleBlockEntity.setCurrentRecipe(currentRecipe);
        UUID displayEntityId = clickedBlock.getTag(DISPLAY_ENTITY_ID);
        if (displayEntityId == null) {
            displayEntityId = UUID.randomUUID();
        }
        final Entity possibleEntity = world.getEntityByUuid(displayEntityId);
        if (possibleEntity != null && !(possibleEntity instanceof CrucibleDisplayEntity)) {
            return;
        }
        final CrucibleDisplayEntity displayEntity;
        if (possibleEntity == null) {
            displayEntity = new CrucibleDisplayEntity(displayEntityId, Block.STONE);
            displayEntity.setInstance(world, blockPos.add(DISPLAY_OFFSET));
        } else {
            displayEntity = (CrucibleDisplayEntity) possibleEntity;
        }
        displayEntity.setFluidValue(new FluidValue(FluidUnit.BUCKET, crucibleBlockEntity.currentBuckets, FluidType.SOLID));
        world.setBlock(blockPos, clickedBlock.withTag(DISPLAY_ENTITY_ID, displayEntityId), false);
    }

    @Override
    public Result onLeftClick(DSWorld world, DSPlayer player, Point clickedPos, Block clickedBlock, ItemStack clickedWith) {
        return Result.ALLOW;
    }

    @Override
    public void onTick(DSWorld world, Point pos, Block block, Key blockId) {
        if (!this.checkTickCooldown()) {
            return;
        }
        final CrucibleBlockEntity blockEntity = (CrucibleBlockEntity) block.handler();
        if (blockEntity == null) {
            return;
        }
        final CrucibleRecipe recipe = blockEntity.currentRecipe;
        if (recipe == null) {
            return;
        }
        final CrucibleRecipe.Result result = blockEntity.getRecipeResult(world.itemFactory());
        if (result == null) {
            return;
        }
        final Point underPos = pos.add(Direction.DOWN.vec());
        final Block under = world.getBlock(underPos);
        final BlockDefinition underDefinition = world.blockFactory().getBlockDefinition(under);
        if (underDefinition == null) {
            return;
        }
        final HeatSourceBehavior heatSourceBehavior = underDefinition.getBehavior(Type.HEAT_SOURCE);
        if (heatSourceBehavior == null) {
            return;
        }
        if (heatSourceBehavior.getTemperature(world, underPos, under).compareTo(recipe.requiredTemperature()) < 0) {
            return;
        }
        blockEntity.incrementTicksHeated();
    }

    private boolean checkTickCooldown() {
        if (this.tickingCooldown == null || this.tickingCooldown.isComplete()) {
            this.tickingCooldown = new TickingCooldown(10);
            return true;
        }
        this.tickingCooldown.tick();
        return false;
    }

    @Override
    public void save(DSWorld world, Point blockPos, Block block) {
        if (!(block.handler() instanceof final CrucibleBlockEntity blockEntity)) {
            return;
        }
        world.setBlock(blockPos, blockEntity.saveBlock(block), false);
    }

    @Override
    public void onLoad(DSWorld world, Point blockPos, Block block) {
        if (!(block.handler() instanceof final CrucibleBlockEntity blockEntity)) {
            return;
        }
        blockEntity.loadFromBlock(world, block);
        this.updateDisplay(world, blockPos, block, blockEntity.currentRecipe);
    }

    @Override
    public BlockHandler createBlockHandler() {
        return new CrucibleBlockEntity();
    }

    @Override
    public Key blockEntityId() {
        return ID;
    }

    @Override
    public Collection<Type<?>> types() {
        return List.of(Type.CLICK, Type.TICK, Type.LOAD, Type.BLOCK_ENTITY);
    }

    private class CrucibleDisplayEntity extends Entity {

        private static final Point TRANSLATION = new Vec(0.1, 0.1, 0.1);
        private static final Point SCALE = new Vec(0.8, 0.65, 0.8);

        private final Block material;

        public CrucibleDisplayEntity(UUID uuid, Block material) {
            super(EntityType.BLOCK_DISPLAY, uuid);
            this.material = material;

            this.entityMeta.setNotifyAboutChanges(false);
            this.editEntityMeta(BlockDisplayMeta.class, meta -> {
                meta.setBlockState(this.material);
                meta.setTranslation(TRANSLATION);
                meta.setHasNoGravity(true);
            });
        }

        public void setFluidValue(FluidValue value) {
            final double heightScale = value.convertTo(FluidUnit.BUCKET) / CrucibleBehavior.this.maxBuckets;
            final Vec scale = SCALE.mul(1, heightScale, 1).asVec();
            final Point translation = TRANSLATION.add(0); // TODO
            this.editEntityMeta(BlockDisplayMeta.class, meta -> {
                meta.setBlockState(this.material);
                meta.setScale(scale);
                meta.setTranslation(translation);
            });
        }
    }

    private class CrucibleBlockEntity extends DSBlockHandler {

        private static final Tag<Map<Key, Integer>> ITEMS_TAG = Tags.Map("items", Tags.Key("id"), Tags.Integer("amount"), HashMap::new);
        private static final Tag<Double> BUCKETS = Tags.Double("buckets");
        private static final Tag<Key> CRUCIBLE_RECIPE = Tags.Key("recipe");

        private final Map<Key, Integer> itemAmounts = new HashMap<>();
        private double currentBuckets = 0;
        private @Nullable CrucibleRecipe currentRecipe;
        private int ticksHeated = 0;

        public CrucibleBlockEntity() {
            super(ID, true);
        }

        public Block saveBlock(Block block) {
            final Block result = block.withTag(ITEMS_TAG, this.itemAmounts)
                    .withTag(BUCKETS, this.currentBuckets);
            if (this.currentRecipe == null) {
                return result;
            }
            return result.withTag(CRUCIBLE_RECIPE, this.currentRecipe.key());
        }

        public void loadFromBlock(DSWorld world, Block block) {
            final Map<Key, Integer> storedItems = block.getTag(ITEMS_TAG);
            if (storedItems != null) {
                this.itemAmounts.putAll(storedItems);
            }
            final Double storedBuckets = block.getTag(BUCKETS);
            if (storedBuckets != null) {
                this.currentBuckets = storedBuckets;
            }
            final Key recipe = block.getTag(CRUCIBLE_RECIPE);
            if (recipe != null) {
                this.currentRecipe = world.recipeFactory().getCrucibleRecipe(recipe);
            }
        }

        public void setCurrentRecipe(@Nullable CrucibleRecipe currentRecipe) {
            this.currentRecipe = currentRecipe;
        }

        public CrucibleRecipe.@Nullable Result getRecipeResult(ItemFactory itemFactory) {
            if (this.currentRecipe == null) {
                return null;
            }
            return this.currentRecipe.craft(itemFactory, new CrucibleRecipe.Input(this.itemAmounts));
        }

        public boolean addItem(ItemStack itemStack, FluidValue fluidValue) {
            final double nextValue = this.currentBuckets + fluidValue.convertTo(FluidUnit.BUCKET);
            if (nextValue > CrucibleBehavior.this.maxBuckets) {
                return false;
            }
            this.currentBuckets = nextValue;
            return true;
        }

        public void resetTicksHeated() {
            this.ticksHeated = 0;
        }

        public void incrementTicksHeated() {
            this.ticksHeated++;
        }
    }
}
