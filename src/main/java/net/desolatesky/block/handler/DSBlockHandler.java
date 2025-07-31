package net.desolatesky.block.handler;

import net.desolatesky.DesolateSkyServer;
import net.desolatesky.block.entity.BlockEntity;
import net.desolatesky.block.settings.BlockSettings;
import net.desolatesky.category.Category;
import net.desolatesky.instance.DSInstance;
import net.desolatesky.item.DSItemRegistry;
import net.desolatesky.item.handler.BasicItemHandler;
import net.desolatesky.item.handler.ItemHandler;
import net.desolatesky.item.handler.breaking.MiningLevel;
import net.desolatesky.loot.table.LootTable;
import net.desolatesky.player.DSPlayer;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;
import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.PlayerHand;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockFace;
import net.minestom.server.item.ItemStack;
import net.minestom.server.tag.Tag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class DSBlockHandler implements Keyed {

    public static final Duration UNBREAKABLE_BREAK_TIME = Duration.ofMillis(-1);

    protected final BlockSettings settings;

    public DSBlockHandler(BlockSettings blockSettings) {
        this.settings = blockSettings;
    }

    public DSBlockHandler(BlockSettings.Builder blockSettings) {
        this(blockSettings.build());
    }

    public void onPlace(
            DSInstance instance,
            Block block,
            Point blockPosition
    ) {
    }

    public void onPlayerPlace(
            DSPlayer player,
            DSInstance instance,
            Block block,
            Point blockPosition,
            PlayerHand hand,
            BlockFace face,
            Point cursorPosition
    ) {
    }

    public void onDestroy(
            DSInstance instance,
            Block block,
            Point blockPosition
    ) {
    }

    public void onPlayerDestroy(
            DSPlayer player,
            DSInstance instance,
            Block block,
            Point blockPosition
    ) {
    }

    public InteractionResult onPlayerInteract(
            Player player,
            DSInstance instance,
            Block block,
            Point blockPosition,
            PlayerHand hand,
            BlockFace face,
            Point cursorPosition
    ) {
        return InteractionResult.PASSTHROUGH;
    }

    public InteractionResult onBlockEntityInteract(
            BlockEntity<?> entity,
            DSInstance instance,
            Block block,
            Point blockPosition,
            BlockFace face
    ) {
        return InteractionResult.PASSTHROUGH;
    }

    public InteractionResult onPlayerPunch(
            Player player,
            DSInstance instance,
            Block block,
            Point blockPosition,
            BlockFace face,
            Point cursorPosition
    ) {
        return InteractionResult.PASSTHROUGH;
    }

    public InteractionResult onBlockEntityPunch(
            BlockEntity<?> entity,
            DSInstance instance,
            Block block,
            Point blockPosition,
            BlockFace face
    ) {
        return InteractionResult.PASSTHROUGH;
    }

    public void onTick(
            DSInstance instance,
            Block block,
            Point blockPosition
    ) {
    }

    public void onRandomTick(
            DSInstance instance,
            Block block,
            Point blockPosition
    ) {
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

    public Duration calculateBlockBreakTime(DesolateSkyServer server, DSPlayer player, Block block) {
        final ItemStack itemInHand = player.getItemInMainHand();
        if (itemInHand.isAir()) {
            return this.breakTime();
        }
        final ItemHandler itemHandler = server.itemRegistry().getItemHandler(itemInHand);
        if (itemHandler == null) {
            return this.breakTime();
        }
        return itemHandler.calculateBreakTime(server, itemInHand, block);
    }

    public boolean isCategory(Category category) {
        return this.settings.isCategory(category);
    }

    @Override
    public @NotNull Key key() {
        return this.settings.key();
    }

    public LootTable loot() {
        return this.settings.lootTable();
    }

    public Duration breakTime() {
        return this.settings.breakTime();
    }

    public boolean isUnbreakable() {
        return this.settings.breakTime().equals(BlockSettings.UNBREAKABLE_BREAK_TIME);
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

}
