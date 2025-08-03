package net.desolatesky.block.entity;

import com.google.common.base.Preconditions;
import net.desolatesky.DesolateSkyServer;
import net.desolatesky.block.DSBlock;
import net.desolatesky.block.DSBlockRegistry;
import net.desolatesky.block.handler.DSBlockHandler;
import net.desolatesky.block.handler.entity.BlockEntityHandler;
import net.desolatesky.category.Category;
import net.desolatesky.instance.DSInstance;
import net.kyori.adventure.key.Key;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class BlockEntity<E extends BlockEntity<E>> implements BlockHandler {

    public static final Duration UNBREAKABLE_BREAK_TIME = Duration.ofMillis(-1);

    protected final Key key;
    protected final DesolateSkyServer server;
    protected final DSBlockRegistry blockRegistry;
    protected final AtomicBoolean loaded = new AtomicBoolean(false);
    protected final BlockEntityHandler<E> handler;

    @SuppressWarnings("unchecked")
    public BlockEntity(Key key, DesolateSkyServer server) {
        this.key = key;
        this.blockRegistry = server.blockRegistry();
        final DSBlock block = this.blockRegistry.getBlock(key);
        Preconditions.checkArgument(block != null, "Block with key %s does not exist in the registry", key);
        final DSBlockHandler blockHandler = block.handler();
        if (!(blockHandler instanceof BlockEntityHandler<?> entityHandler)) {
            throw new IllegalArgumentException("Block with key " + key + " does not have a BlockEntityHandler");
        }
        Preconditions.checkArgument(entityHandler.entityClass().isAssignableFrom(this.getClass()), this.getClass().getName() + " is not assignable from " + entityHandler.entityClass().getName());
        this.server = server;
        this.handler = (BlockEntityHandler<E>) entityHandler;
    }

    @Override
    public final void onPlace(@NotNull Placement placement) {
        // player placements are handled separately by listeners
        if (placement instanceof PlayerPlacement) {
            return;
        }
        if (!(placement.getInstance() instanceof final DSInstance instance)) {
            return;
        }
        final boolean loaded = this.loaded.getAndSet(true);
        if (!loaded) {
            this.load(placement, instance);
            instance.addBlockEntity(placement.getBlockPosition());
        }
    }

    @Override
    public final void onDestroy(@NotNull Destroy destroy) {
        // player destroy is handled separately by listeners
        if (destroy instanceof PlayerDestroy) {
            return;
        }
        if (!(destroy.getInstance() instanceof final DSInstance instance)) {
            return;
        }
        instance.removeBlockEntity(destroy.getBlockPosition());
    }

    @Override
    public final boolean onInteract(@NotNull Interaction interaction) {
        if (!(interaction.getInstance() instanceof final DSInstance instance)) {
            return BlockHandler.super.onInteract(interaction);
        }
        return false;
    }

    @Override
    public final void onTouch(@NotNull Touch touch) {
    }

    @Override
    public final void tick(@NotNull Tick tick) {
        if (!(tick.getInstance() instanceof final DSInstance instance)) {
            return;

        }
        @SuppressWarnings("unchecked") final E entity = (E) this;
        this.handler.onTick(instance, tick.getBlock(), tick.getBlockPosition(), entity);
    }

    public final void randomTick(@NotNull Tick randomTick) {
        if (!(randomTick.getInstance() instanceof final DSInstance instance)) {
            return;
        }
        @SuppressWarnings("unchecked") final E entity = (E) this;
        this.handler.onRandomTick(instance, randomTick.getBlock(), randomTick.getBlockPosition(), entity);
    }

    public boolean isCategory(Category category) {
        return this.handler.isCategory(category);
    }

    @Override
    public @NotNull Key getKey() {
        return this.key;
    }

    public abstract @Nullable Block save(DSInstance instance, Point point, Block block);

    public abstract void load(Placement placement, DSInstance instance);

    public DSBlockHandler blockHandler() {
        return this.handler;
    }

}
