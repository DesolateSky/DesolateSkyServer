package net.desolatesky.block.handler;

import net.desolatesky.DesolateSkyServer;
import net.desolatesky.block.BlockTags;
import net.desolatesky.instance.DSInstance;
import net.desolatesky.instance.InstancePoint;
import net.desolatesky.item.DSItemRegistry;
import net.desolatesky.loot.table.LootTable;
import net.desolatesky.player.DSPlayer;
import net.desolatesky.util.InventoryUtil;
import net.desolatesky.util.PacketUtil;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockHandler;
import net.minestom.server.item.ItemStack;
import net.minestom.server.network.packet.server.play.WorldEventPacket;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public  abstract class DSBlockHandler implements BlockHandler {

    protected final DesolateSkyServer server;
    protected final Key key;
    protected final LootTable loot;
    protected final boolean stateless;

    public DSBlockHandler(DesolateSkyServer server, Key key, LootTable loot, boolean stateless) {
        this.server = server;
        this.key = key;
        this.loot = loot;
        this.stateless = stateless;
    }

    public DSBlockHandler(DesolateSkyServer server, Key key, boolean stateless) {
        this(server, key, LootTable.EMPTY, stateless);
    }

    @Override
    public void onPlace(@NotNull Placement placement) {
        if (!(placement.getInstance() instanceof final DSInstance instance)) {
            return;
        }
        this.onPlace(placement, instance);
    }

    public void onPlace(@NotNull Placement placement, DSInstance instance) {

    }

    @Override
    public void onDestroy(@NotNull Destroy destroy) {
        if (!(destroy.getInstance() instanceof final DSInstance instance)) {
            return;
        }
        this.onDestroy(destroy, instance);
    }

    public void onDestroy(@NotNull Destroy destroy, DSInstance instance) {
        if (destroy instanceof final PlayerDestroy playerDestroy) {
            final ItemStack toolUsed = playerDestroy.getPlayer().getItemInMainHand();
            final Point point = playerDestroy.getBlockPosition();
            final Block block = playerDestroy.getBlock();
            final Collection<ItemStack> drops = this.generateDrops(this.server.itemRegistry(), toolUsed, point, block);
            final WorldEventPacket packet = PacketUtil.blockBreakPacket(point, block);
            final DSPlayer player = (DSPlayer) playerDestroy.getPlayer();
            player.sendPacket(packet);
            if (!drops.isEmpty()) {
                final InstancePoint<Pos> instancePoint = new InstancePoint<>(instance, new Pos(point));
                drops.forEach(drop -> InventoryUtil.addItemToInventory(player, drop, instancePoint));
            }
        }
    }

    @Override
    public boolean onInteract(@NotNull Interaction interaction) {
        if (!(interaction.getInstance() instanceof final DSInstance instance)) {
            return BlockHandler.super.onInteract(interaction);
        }
        return this.onInteract(interaction, instance);
    }

    public boolean onInteract(@NotNull Interaction interaction, DSInstance instance) {
        return BlockHandler.super.onInteract(interaction);
    }

    @Override
    public void onTouch(@NotNull Touch touch) {
        if (!(touch.getInstance() instanceof final DSInstance instance)) {
            return;
        }
        this.onTouch(touch, instance);
    }

    public void onTouch(@NotNull Touch touch, DSInstance instance) {
        BlockHandler.super.onTouch(touch);
    }

    @Override
    public void tick(@NotNull Tick tick) {
        if (!(tick.getInstance() instanceof final DSInstance instance)) {
            return;
        }
        this.tick(tick, instance);
    }

    public void tick(@NotNull Tick tick, DSInstance instance) {
        BlockHandler.super.tick(tick);
    }

    public void randomTick(@NotNull Tick randomTick) {
        if (!(randomTick.getInstance() instanceof final DSInstance instance)) {
            return;
        }
        this.randomTick(randomTick, instance);
    }

    public void randomTick(@NotNull Tick randomTick, DSInstance instance) {
    }

    public Collection<ItemStack> generateDrops(DSItemRegistry itemRegistry, ItemStack toolUsed, Point point, Block block) {
        final Key blockId = block.getTag(BlockTags.BLOCK_ITEM);
        if (blockId == null) {
            return Collections.emptyList();
        }
        final ItemStack itemStack = itemRegistry.create(blockId);
        if (itemStack == null) {
            return Collections.emptyList();
        }
        return List.of(itemStack);
    }

    @Override
    public @NotNull Key getKey() {
        return this.key;
    }

    public LootTable loot() {
        return this.loot;
    }

    public boolean stateless() {
        return this.stateless;
    }


    public abstract void save(DSInstance instance, Point point, Block block);

    public abstract void load(CompoundBinaryTag tag, DSInstance instance, Point point, Block block);

}
