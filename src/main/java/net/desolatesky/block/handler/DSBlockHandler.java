package net.desolatesky.block.handler;

import net.desolatesky.DesolateSkyServer;
import net.desolatesky.block.settings.BlockSettings;
import net.desolatesky.category.Category;
import net.desolatesky.instance.DSInstance;
import net.desolatesky.instance.InstancePoint;
import net.desolatesky.item.DSItemRegistry;
import net.desolatesky.item.handler.ItemHandler;
import net.desolatesky.item.handler.breaking.MiningLevel;
import net.desolatesky.loot.table.LootTable;
import net.desolatesky.player.DSPlayer;
import net.desolatesky.util.InventoryUtil;
import net.desolatesky.util.PacketUtil;
import net.kyori.adventure.key.Key;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockHandler;
import net.minestom.server.item.ItemStack;
import net.minestom.server.network.packet.server.play.WorldEventPacket;
import net.minestom.server.tag.Tag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class DSBlockHandler implements BlockHandler {

    public static final Duration UNBREAKABLE_BREAK_TIME = Duration.ofMillis(-1);

    protected final DesolateSkyServer server;
    protected final BlockSettings settings;
    protected final AtomicBoolean loaded = new AtomicBoolean(false);

    private final LootTable lootTable;

    public DSBlockHandler(DesolateSkyServer server, BlockSettings blockSettings) {
        this.server = server;
        this.settings = blockSettings;
        this.lootTable = this.server.blockLootRegistry().getLootTable(this.settings.key(), LootTable.EMPTY);
    }

    public DSBlockHandler(DesolateSkyServer server, BlockSettings.Builder blockSettings) {
        this(server, blockSettings.build());
    }

    @Override
    public void onPlace(@NotNull Placement placement) {
        if (!(placement.getInstance() instanceof final DSInstance instance)) {
            return;
        }
        instance.addBlockEntity(placement.getBlockPosition());
        final boolean loaded = this.loaded.getAndSet(true);
        if (placement.getClass().equals(Placement.class) && !loaded) {
            this.load(placement, instance);
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
        instance.removeBlockEntity(destroy.getBlockPosition());
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
        final Key itemId = this.settings.blockItemKey();
        if (itemId == null) {
            return Collections.emptyList();
        }
        final ItemStack itemStack = itemRegistry.create(itemId);
        if (itemStack == null) {
            return Collections.emptyList();
        }
        return List.of(itemStack);
    }

    public Duration calculateBlockBreakTime(DSPlayer player, Block block) {
        final ItemStack itemInHand = player.getItemInMainHand();
        if (itemInHand.isAir()) {
            return Duration.ofMillis(this.breakTime());
        }
        final ItemHandler itemHandler = this.server.itemRegistry().getItemHandler(itemInHand);
        if (itemHandler == null) {
            return Duration.ofMillis(this.breakTime());
        }
        return itemHandler.calculateBreakTime(itemInHand, block);
    }

    public boolean isCategory(Category category) {
        return this.settings.isCategory(category);
    }

    @Override
    public @NotNull Key getKey() {
        return this.settings.key();
    }

    public LootTable loot() {
        return this.lootTable;
    }

    public boolean stateless() {
        return this.settings.stateless();
    }

    public int breakTime() {
        return this.settings.breakTime();
    }

    public boolean isUnbreakable() {
        return this.settings.breakTime() < 0;
    }

    public @Nullable Key blockItemKey() {
        return this.settings.blockItemKey();
    }

    public MiningLevel miningLevel() {
        return this.settings.miningLevel();
    }

    public ItemStack menuItem() {
        return this.settings.menuItem();
    }

    public <T> T getSetting(Tag<T> tag) {
        return this.settings.getSetting(tag);
    }

    public BlockSettings settings() {
        return this.settings;
    }

    public abstract @Nullable Block save(DSInstance instance, Point point, Block block);

    public abstract void load(Placement placement, DSInstance instance);

}
