package net.desolatesky.item.listener;

import net.desolatesky.block.DSBlockRegistry;
import net.desolatesky.instance.DSInstance;
import net.desolatesky.inventory.InventoryHolder;
import net.desolatesky.item.DSItemRegistry;
import net.desolatesky.item.ItemTags;
import net.desolatesky.item.handler.ItemHandler;
import net.desolatesky.listener.DSListener;
import net.desolatesky.player.DSPlayer;
import net.desolatesky.registry.DSRegistries;
import net.kyori.adventure.key.Key;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.PlayerHand;
import net.minestom.server.entity.attribute.Attribute;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventFilter;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.entity.EntityAttackEvent;
import net.minestom.server.event.item.PickupItemEvent;
import net.minestom.server.event.player.PlayerBlockInteractEvent;
import net.minestom.server.event.player.PlayerBlockPlaceEvent;
import net.minestom.server.event.player.PlayerEntityInteractEvent;
import net.minestom.server.event.player.PlayerHandAnimationEvent;
import net.minestom.server.event.player.PlayerUseItemEvent;
import net.minestom.server.event.player.PlayerUseItemOnBlockEvent;
import net.minestom.server.event.trait.ItemEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockFace;
import net.minestom.server.instance.block.BlockHandler;
import net.minestom.server.item.ItemStack;

public final class ItemListeners implements DSListener {

    private final DSRegistries registries;
    private final DSItemRegistry itemRegistry;
    private final DSBlockRegistry blockRegistry;

    public ItemListeners(DSRegistries registries) {
        this.registries = registries;
        this.itemRegistry = registries.itemRegistry();
        this.blockRegistry = registries.blockRegistry();
    }

    @Override
    public void register(EventNode<Event> node) {
        node.addChild(this.itemNode())
                .addChild(this.clickNode())
                .addChild(this.punchNode());
    }

    private EventNode<ItemEvent> itemNode() {
        return EventNode.type("item", EventFilter.ITEM)
                .addListener(PickupItemEvent.class, event -> {
                    final Entity entity = event.getEntity();
                    if (!(entity instanceof final InventoryHolder inventoryHolder)) {
                        return;
                    }
                    inventoryHolder.getInventory().addItemStack(event.getItemStack());
                });
    }

    private EventNode<? extends Event> clickNode() {
        return EventNode.type("item-click", EventFilter.PLAYER, (event, player) -> player instanceof DSPlayer)
                .addListener(PlayerUseItemOnBlockEvent.class, event -> {
                    final DSPlayer player = (DSPlayer) event.getPlayer();
                    ItemStack itemStack = player.getItemInMainHand();
                    final Key blockId = itemStack.getTag(ItemTags.BLOCK_ID);
                    final Point clickedPoint = event.getPosition();
                    final DSInstance instance = player.getDSInstance();
                    final Block clickedBlock = instance.getBlock(clickedPoint);
                    final Point cursor = new Vec(0);
                    if (blockId != null) {
                        this.tryPlaceBlock(player, instance, event.getHand(), itemStack, clickedPoint, clickedBlock, cursor, event.getBlockFace(), blockId);
                        return;
                    }
                    final ItemHandler itemHandler = this.itemRegistry.getItemHandler(itemStack);
                    if (itemHandler == null) {
                        return;
                    }
                    itemStack = itemHandler.onInteractBlock(player, player.getDSInstance(), itemStack, event.getHand(), clickedPoint, clickedBlock, cursor, event.getBlockFace());
                    player.setItemInMainHand(itemStack);
                })
                .addListener(PlayerBlockPlaceEvent.class, event -> {
                    final DSPlayer player = (DSPlayer) event.getPlayer();
                    final ItemStack itemStack = player.getItemInMainHand();
                    final Key blockId = itemStack.getTag(ItemTags.BLOCK_ID);
                    if (blockId != null) {
                        final Block block = this.blockRegistry.create(blockId);
                        if (block == null) {
                            return;
                        }
                        event.setBlock(block);
                        return;
                    }
                    event.setCancelled(true);
                })
                .addListener(PlayerEntityInteractEvent.class, event -> {
                    final DSPlayer player = (DSPlayer) event.getPlayer();
                    ItemStack itemStack = player.getItemInMainHand();
                    final ItemHandler itemHandler = this.itemRegistry.getItemHandler(itemStack);
                    if (itemHandler == null) {
                        return;
                    }
                    itemStack = itemHandler.onInteractEntity(player, player.getDSInstance(), itemStack, event.getTarget());
                    player.setItemInMainHand(itemStack);
                })
                .addListener(PlayerUseItemEvent.class, event -> {
                    final DSPlayer player = (DSPlayer) event.getPlayer();
                    ItemStack itemStack = player.getItemInMainHand();
                    final ItemHandler itemHandler = this.itemRegistry.getItemHandler(itemStack);
                    if (itemHandler == null) {
                        return;
                    }
                    itemStack = itemHandler.onInteractAir(player, player.getDSInstance(), itemStack);
                    player.setItemInMainHand(itemStack);
                });
    }

    private void tryPlaceBlock(
            DSPlayer player,
            DSInstance instance,
            PlayerHand hand,
            ItemStack itemStack,
            Point clickedPoint,
            Block clickedBlock,
            Point cursor,
            BlockFace blockFace,
            Key blockId
    ) {
        final Block block = this.blockRegistry.create(blockId);
        if (block == null) {
            return;
        }
        final Point placePoint = clickedBlock.registry().isReplaceable() ? clickedPoint : clickedPoint.add(blockFace.toDirection().vec());
        final BlockHandler.Placement placement = new BlockHandler.PlayerPlacement(
                block,
                instance,
                placePoint,
                player,
                hand,
                blockFace,
                (float) cursor.x(),
                (float) cursor.y(),
                (float) cursor.z()
        );
        instance.placeBlock(placement, true);
        player.setItemInMainHand(itemStack.consume(1));
    }

    private EventNode<? extends Event> punchNode() {
        return EventNode.type("item-click", EventFilter.ALL)
                .addListener(EntityAttackEvent.class, event -> {
                    if (!(event.getEntity() instanceof final DSPlayer player)) {
                        return;
                    }
                    ItemStack itemStack = player.getItemInMainHand();
                    final ItemHandler itemHandler = this.itemRegistry.getItemHandler(itemStack);
                    if (itemHandler == null) {
                        return;
                    }
                    itemStack = itemHandler.onPunchEntity(player, player.getDSInstance(), itemStack, event.getTarget());
                    player.setItemInMainHand(itemStack);
                })
                .addListener(PlayerHandAnimationEvent.class, event -> {
                    if (!(event.getEntity() instanceof final DSPlayer player)) {
                        return;
                    }
                    ItemStack itemStack = player.getItemInMainHand();
                    final ItemHandler itemHandler = this.itemRegistry.getItemHandler(itemStack);
                    if (itemHandler == null) {
                        return;
                    }
                    final Point targeted = player.getTargetBlockPosition((int) player.getAttribute(Attribute.BLOCK_INTERACTION_RANGE).getValue());
                    final DSInstance instance = player.getDSInstance();
                    if (targeted == null) {
                        itemStack = itemHandler.onPunchAir(player, instance, itemStack);
                    } else {
                        itemStack = itemHandler.onPunchBlock(player, instance, itemStack, targeted, instance.getBlock(targeted));
                    }
                    player.setItemInMainHand(itemStack);
                });
    }

}
