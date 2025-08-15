package net.desolatesky.block.handler;

import net.desolatesky.DesolateSkyServer;
import net.desolatesky.block.DSBlockRegistry;
import net.desolatesky.block.entity.BlockEntity;
import net.desolatesky.block.settings.BlockSettings;
import net.desolatesky.category.Category;
import net.desolatesky.instance.DSInstance;
import net.desolatesky.instance.InstancePoint;
import net.desolatesky.item.DSItemRegistry;
import net.desolatesky.item.handler.ItemHandler;
import net.desolatesky.item.handler.ItemInteractionResult;
import net.desolatesky.item.handler.breaking.MiningLevel;
import net.desolatesky.loot.LootContext;
import net.desolatesky.loot.context.BlockBreakContext;
import net.desolatesky.loot.generator.LootGenerator;
import net.desolatesky.loot.generator.LootGeneratorTypes;
import net.desolatesky.loot.table.LootTable;
import net.desolatesky.player.DSPlayer;
import net.desolatesky.util.InventoryUtil;
import net.desolatesky.util.PacketUtil;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;
import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.PlayerHand;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockFace;
import net.minestom.server.item.ItemStack;
import net.minestom.server.tag.Tag;
import net.minestom.server.utils.Direction;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;

public class DSBlockHandler implements Keyed {

    public static final Duration UNBREAKABLE_BREAK_TIME = Duration.ofMillis(-1);

    protected final BlockSettings settings;

    public DSBlockHandler(BlockSettings blockSettings) {
        this.settings = blockSettings;
    }

    public DSBlockHandler(BlockSettings.Builder blockSettings) {
        this(blockSettings.build());
    }

    public BlockHandlerResult.Place onPlace(
            DSInstance instance,
            Block block,
            Point blockPosition
    ) {
        return BlockHandlerResult.passthroughPlace(block);
    }

    public BlockHandlerResult.Place onPlayerPlace(
            DSPlayer player,
            DSInstance instance,
            Block block,
            Point blockPosition,
            PlayerHand hand,
            BlockFace face,
            Point cursorPosition
    ) {
        return BlockHandlerResult.passthroughPlace(block);
    }

    public BlockHandlerResult onDestroy(
            DSInstance instance,
            Block block,
            Point blockPosition
    ) {
        return BlockHandlerResult.PASS_THROUGH;
    }

    public final BlockHandlerResult playerDestroyBlock(
            DSItemRegistry itemRegistry,
            DSPlayer player,
            DSInstance instance,
            Block block,
            Point blockPosition
    ) {
        final ItemStack inHand = player.getItemInMainHand();
        final ItemHandler itemHandler = itemRegistry.getItemHandler(inHand);
        boolean passthrough = true;
        if (itemHandler != null) {
            final ItemInteractionResult itemHandlerResult = itemHandler.onBreakBlock(player, instance, inHand, block, blockPosition);
            final ItemStack newItem = itemHandlerResult.newItem();
            if (newItem != null) {
                player.setItemInMainHand(newItem);
            }
            if (itemHandlerResult.cancel()) {
                return BlockHandlerResult.CANCEL;
            }
            passthrough = false;
        }
        final BlockHandlerResult blockHandlerResult = this.onPlayerDestroy(player, instance, block, blockPosition);
        if (blockHandlerResult.cancelEvent()) {
            return BlockHandlerResult.CANCEL;
        }
        PacketUtil.sendBlockBreak(instance, this, blockPosition, block);
        instance.setBlock(blockPosition, Block.AIR);
        if (blockHandlerResult.consumeEvent()) {
            return BlockHandlerResult.CONSUME;
        }
        final Collection<ItemStack> drops = this.generateDrops(instance, inHand, blockPosition, block);
        final InstancePoint<? extends Point> instancePoint = new InstancePoint<>(instance, blockPosition);
        InventoryUtil.addItemsToInventory(player, drops, instancePoint);
        if (passthrough) {
            return BlockHandlerResult.PASS_THROUGH;
        } else {
            return BlockHandlerResult.CONSUME;
        }
    }

    public BlockHandlerResult onPlayerDestroy(
            DSPlayer player,
            DSInstance instance,
            Block block,
            Point blockPosition
    ) {
        return BlockHandlerResult.PASS_THROUGH;
    }

    public BlockHandlerResult.InteractBlock onPlayerInteract(
            Player player,
            DSInstance instance,
            Block block,
            Point blockPosition,
            PlayerHand hand,
            BlockFace face,
            Point cursorPosition
    ) {
        return BlockHandlerResult.passthroughInteractBlock();
    }

    public BlockHandlerResult.InteractBlock onBlockEntityInteract(
            BlockEntity<?> entity,
            DSInstance instance,
            Block block,
            Point blockPosition,
            BlockFace face
    ) {
        return BlockHandlerResult.passthroughInteractBlock();
    }

    public BlockHandlerResult onPlayerPunch(
            Player player,
            DSInstance instance,
            Block block,
            Point blockPosition,
            BlockFace face,
            Point cursorPosition
    ) {
        return BlockHandlerResult.PASS_THROUGH;
    }

    public BlockHandlerResult onBlockEntityPunch(
            BlockEntity<?> entity,
            DSInstance instance,
            Block block,
            Point blockPosition,
            BlockFace face
    ) {
        return BlockHandlerResult.PASS_THROUGH;
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

    public BlockHandlerResult onUpdate(DSInstance instance, Point sourcePoint, Block sourceBlock, Point updatedPoint, Block updatedBlock) {
        return BlockHandlerResult.PASS_THROUGH;
    }

    public Collection<ItemStack> generateDrops(DSInstance instance, ItemStack toolUsed, Point point, Block block) {
        final LootTable lootTable = this.settings.lootTable();
        final LootGenerator lootGenerator = lootTable.getGenerator(LootGeneratorTypes.BLOCK_BREAK);
        if (lootGenerator == null) {
            return Collections.emptyList();
        }
        return lootGenerator.generateLoot(BlockBreakContext.createBlockBreakContext(instance, toolUsed, this, block, point));
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
