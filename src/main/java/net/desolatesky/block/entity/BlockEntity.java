package net.desolatesky.block.entity;

import com.google.common.base.Preconditions;
import net.desolatesky.DesolateSkyServer;
import net.desolatesky.block.handler.InteractionResult;
import net.desolatesky.block.handler.entity.BlockEntityHandler;
import net.desolatesky.block.settings.BlockSettings;
import net.desolatesky.category.Category;
import net.desolatesky.instance.DSInstance;
import net.desolatesky.item.DSItemRegistry;
import net.desolatesky.item.handler.ItemHandler;
import net.desolatesky.item.handler.breaking.MiningLevel;
import net.desolatesky.loot.table.LootTable;
import net.desolatesky.player.DSPlayer;
import net.kyori.adventure.key.Key;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockHandler;
import net.minestom.server.item.ItemStack;
import net.minestom.server.tag.Tag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class BlockEntity<E extends BlockEntity<E>> implements BlockHandler {

    public static final Duration UNBREAKABLE_BREAK_TIME = Duration.ofMillis(-1);

    protected final DesolateSkyServer server;
    protected final AtomicBoolean loaded = new AtomicBoolean(false);
    protected final BlockEntityHandler<E> handler;

    public BlockEntity(DesolateSkyServer server, BlockEntityHandler<E> handler) {
        Preconditions.checkArgument(this.getClass().isAssignableFrom(handler.entityClass()));
        this.server = server;
        this.handler = handler;
    }

    @Override
    public final void onPlace(@NotNull Placement placement) {
        if (!(placement.getInstance() instanceof final DSInstance instance)) {
            return;
        }
        instance.addBlockEntity(placement.getBlockPosition());
        final boolean loaded = this.loaded.getAndSet(true);
        if (!loaded) {
            this.load(placement, instance);
        }
//        @SuppressWarnings("unchecked") final E entity = (E) this;
//        if (placement instanceof final PlayerPlacement playerPlacement) {
//            final Point cursor = new Vec(playerPlacement.getCursorX(), playerPlacement.getCursorY(), playerPlacement.getCursorZ());
//            this.handler.onPlayerPlace(
//                    (DSPlayer) playerPlacement.getPlayer(),
//                    instance,
//                    playerPlacement.getBlock(),
//                    playerPlacement.getBlockPosition(),
//                    playerPlacement.getHand(),
//                    playerPlacement.getBlockFace(),
//                    cursor,
//                    entity
//            );
//            return;
//        }
//        this.handler.onPlace(instance, placement.getBlock(), placement.getBlockPosition(), entity);
    }

    @Override
    public final void onDestroy(@NotNull Destroy destroy) {
        if (!(destroy.getInstance() instanceof final DSInstance instance)) {
            return;
        }
        instance.removeBlockEntity(destroy.getBlockPosition());
//        @SuppressWarnings("unchecked") final E entity = (E) this;
//        if (destroy instanceof final PlayerDestroy playerDestroy) {
//            this.handler.onPlayerDestroy((DSPlayer) playerDestroy.getPlayer(), instance, playerDestroy.getBlock(), playerDestroy.getBlockPosition(), entity);
//            return;
//        }
//        this.handler.onDestroy(instance, destroy.getBlock(), destroy.getBlockPosition());
    }

//    public void onDestroy(@NotNull Destroy destroy, DSInstance instance) {
//        if (destroy instanceof final PlayerDestroy playerDestroy) {
//            final ItemStack toolUsed = playerDestroy.getPlayer().getItemInMainHand();
//            final Point point = playerDestroy.getBlockPosition();
//            final Block block = playerDestroy.getBlock();
//            final Collection<ItemStack> drops = this.generateDrops(this.server.itemRegistry(), toolUsed, point, block);
//            final WorldEventPacket packet = PacketUtil.blockBreakPacket(point, block);
//            final DSPlayer player = (DSPlayer) playerDestroy.getPlayer();
//            player.sendPacket(packet);
//            if (!drops.isEmpty()) {
//                final InstancePoint<Pos> instancePoint = new InstancePoint<>(instance, new Pos(point));
//                drops.forEach(drop -> InventoryUtil.addItemToInventory(player, drop, instancePoint));
//            }
//        }
//    }

    @Override
    public final boolean onInteract(@NotNull Interaction interaction) {
        if (!(interaction.getInstance() instanceof final DSInstance instance)) {
            return BlockHandler.super.onInteract(interaction);
        }
//        @SuppressWarnings("unchecked") final E entity = (E) this;
//        final InteractionResult result = this.handler.onPlayerInteract(
//                (DSPlayer) interaction.getPlayer(),
//                instance,
//                interaction.getBlock(),
//                interaction.getBlockPosition(),
//                interaction.getHand(),
//                interaction.getBlockFace(),
//                interaction.getCursorPosition(),
//                entity
//        );
//        return switch (result) {
//            case PASSTHROUGH -> true;
//            case CONSUME_INTERACTION -> false;
//        };
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
        return this.handler.key();
    }

    public abstract @Nullable Block save(DSInstance instance, Point point, Block block);

    public abstract void load(Placement placement, DSInstance instance);

}
