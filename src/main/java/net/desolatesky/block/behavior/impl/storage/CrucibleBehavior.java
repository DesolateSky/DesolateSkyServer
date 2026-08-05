package net.desolatesky.block.behavior.impl.storage;

import net.desolatesky.block.behavior.ClickBehavior;
import net.desolatesky.block.behavior.RandomTickBehavior;
import net.desolatesky.block.behavior.listener.LoadBehavior;
import net.desolatesky.block.behavior.serializer.BlockBehaviorSerializer;
import net.desolatesky.block.handler.DSBlockHandler;
import net.desolatesky.item.ItemTags;
import net.desolatesky.measurement.FluidValue;
import net.desolatesky.measurement.TemperatureValue;
import net.desolatesky.player.DSPlayer;
import net.desolatesky.util.Namespace;
import net.desolatesky.world.DSWorld;
import net.kyori.adventure.key.Key;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.PlayerHand;
import net.minestom.server.entity.metadata.display.BlockDisplayMeta;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public final class CrucibleBehavior implements ClickBehavior, LoadBehavior, RandomTickBehavior {

    public static final class Serializer extends BlockBehaviorSerializer<CrucibleBehavior> {

        public Serializer() {
            super(Namespace.key("crucible"));
        }

        @Override
        public Class<CrucibleBehavior> behaviorClass() {
            return null;
        }

        @Override
        public CrucibleBehavior deserialize(java.lang.reflect.Type type, ConfigurationNode node) throws SerializationException {
            return null;
        }

        @Override
        public void serialize(java.lang.reflect.Type type, @Nullable CrucibleBehavior obj, ConfigurationNode node) throws SerializationException {

        }
    }

    private final boolean destroysOnCraft;

    public CrucibleBehavior(boolean destroysOnCraft) {
        this.destroysOnCraft = destroysOnCraft;
    }

    @Override
    public Result onRightClick(DSWorld world, DSPlayer player, PlayerHand hand, Point clickedPos, Block clickedBlock, ItemStack clickedWith) {
        final FluidValue fluidValue = clickedWith.getTag(ItemTags.FLUID_VALUE);
        if (fluidValue == null) {
            return Result.ALLOW;
        }

    }

    @Override
    public Result onLeftClick(DSWorld world, DSPlayer player, Point clickedPos, Block clickedBlock, ItemStack clickedWith) {
        return null;
    }

    @Override
    public void onRandomTick(DSWorld world, Point pos, Block block, Key blockId) {

    }

    @Override
    public void save(DSWorld world, Point blockPos, Block block) {

    }

    @Override
    public void onLoad(DSWorld world, Point blockPos, Block block) {

    }

    @Override
    public Collection<Type<?>> types() {
        return List.of(Type.CLICK, Type.RANDOM_TICK, Type.RANDOM_TICK);
    }

    private static class CrucibleDisplayEntity extends Entity {

        private static final Point TRANSLATION = new Vec(0.5, 0.5, 0.5);

        private final Block material;
        private final FluidValue fluidValue = new FluidValue(0);

        public CrucibleEntity(UUID uuid, Block material) {
            super(EntityType.BLOCK_DISPLAY, uuid);
            this.material = material;

            this.entityMeta.setNotifyAboutChanges(false);
            this.editEntityMeta(BlockDisplayMeta.class, meta -> {
                meta.setBlockState(this.material);
                meta.setTranslation(TRANSLATION);
            });
        }

        public void addFluid(FluidValue value) {

        }
    }

    private static class CrucibleBlockEntity extends DSBlockHandler {



    }
}
