package net.desolatesky.item.listener;

import net.desolatesky.block.DSBlock;
import net.desolatesky.block.DSBlockRegistry;
import net.desolatesky.block.handler.DSBlockHandler;
import net.desolatesky.block.handler.InteractionResult;
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
import net.minestom.server.event.player.PlayerStartDiggingEvent;
import net.minestom.server.event.player.PlayerUseItemEvent;
import net.minestom.server.event.player.PlayerUseItemOnBlockEvent;
import net.minestom.server.event.trait.ItemEvent;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockFace;
import net.minestom.server.item.ItemStack;
import net.minestom.server.network.packet.server.play.BlockChangePacket;

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
        return EventNode.type("item-click", EventFilter.PLAYER, (_, player) -> player instanceof DSPlayer)
                .addListener(PlayerBlockInteractEvent.class, event -> {
                    final DSPlayer player = (DSPlayer) event.getPlayer();
                    final Block block = event.getBlock();
                    final DSBlockHandler blockHandler = this.blockRegistry.getHandlerForBlock(block);
                    player.sendMessage("Block handler for " + block.id() + ": " + blockHandler);
                    if (blockHandler == null) {
                        return;
                    }
                    final Point placementPosition = event.getBlockPosition();
                    final InteractionResult result = blockHandler.onPlayerInteract(
                            player,
                            player.getDSInstance(),
                            block,
                            placementPosition,
                            event.getHand(),
                            event.getBlockFace(),
                            event.getCursorPosition()
                    );
                    player.sendMessage("Block interaction event: " + result);
                    switch (result) {
                        case PASSTHROUGH -> {}
                        case CONSUME_INTERACTION -> {
                            event.setBlockingItemUse(true);
                            event.setCancelled(true);
                        }
                    }
                })
                .addListener(PlayerUseItemOnBlockEvent.class, event -> {
                    final DSPlayer player = (DSPlayer) event.getPlayer();
                    player.sendMessage("Use item on block event");
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
                    if (itemHandler != null) {
                        itemStack = itemHandler.onInteractBlock(player, player.getDSInstance(), itemStack, event.getHand(), clickedPoint, clickedBlock, cursor, event.getBlockFace());
                        player.setItemInMainHand(itemStack);
                    }
                    final DSBlockHandler blockHandler = this.blockRegistry.getHandlerForBlock(clickedBlock);
                    if (blockHandler != null) {
                        blockHandler.onPlayerInteract(
                                player,
                                instance,
                                clickedBlock,
                                clickedPoint,
                                event.getHand(),
                                event.getBlockFace(),
                                cursor
                        );
                    }
                })
                .addListener(PlayerBlockPlaceEvent.class, event -> {
                    final DSPlayer player = (DSPlayer) event.getPlayer();
                    player.sendMessage("Block place event");
                    final ItemStack itemStack = player.getItemInMainHand();
                    final Key blockId = itemStack.getTag(ItemTags.BLOCK_ID);
                    if (blockId != null) {
                        final Block block = this.blockRegistry.create(blockId);
                        if (block == null) {
                            return;
                        }
                        event.setBlock(block);
                        final DSBlockHandler blockHandler = this.blockRegistry.getHandlerForBlock(block);
                        if (blockHandler != null) {
                            final DSInstance instance = player.getDSInstance();
                            final Point cursor = new Vec(0);
                            final BlockFace blockFace = event.getBlockFace();
                            blockHandler.onPlayerPlace(player, instance, block, event.getBlockPosition(), event.getHand(), blockFace, cursor);
                        }
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
                    player.sendMessage("Player use item event");
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
        instance.setBlock(placePoint, block, true);
        final DSBlockHandler blockHandler = this.blockRegistry.getHandlerForBlock(block);
        if (blockHandler != null) {
            blockHandler.onPlayerPlace(
                    player,
                    instance,
                    block,
                    placePoint,
                    hand,
                    blockFace,
                    cursor
            );
        }
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
                    if (itemHandler != null) {
                        itemStack = itemHandler.onPunchEntity(player, player.getDSInstance(), itemStack, event.getTarget());
                        player.setItemInMainHand(itemStack);
                    }
                })
                .addListener(PlayerHandAnimationEvent.class, event -> {
                    if (!(event.getEntity() instanceof final DSPlayer player)) {
                        return;
                    }
                    player.sendMessage("Player hand animation event");
                    ItemStack itemStack = player.getItemInMainHand();
                    final ItemHandler itemHandler = this.itemRegistry.getItemHandler(itemStack);
                    if (itemHandler == null) {
                        return;
                    }
                    final Point targeted = player.getTargetBlockPosition((int) player.getAttribute(Attribute.BLOCK_INTERACTION_RANGE).getValue());
                    final DSInstance instance = player.getDSInstance();
                    if (targeted == null) {
                        itemStack = itemHandler.onPunchAir(player, instance, itemStack);
                        player.setItemInMainHand(itemStack);
                    }
                }).addListener(PlayerStartDiggingEvent.class, event -> {
                    if (!(event.getEntity() instanceof final DSPlayer player)) {
                        return;
                    }
                    player.sendMessage("Started digging");
                    ItemStack itemStack = player.getItemInMainHand();
                    final ItemHandler itemHandler = this.itemRegistry.getItemHandler(itemStack);

                    final Point targeted = event.getBlockPosition();
                    final Block block = event.getBlock();
                    final DSInstance instance = player.getDSInstance();
                    if (itemHandler != null) {
                        itemStack = itemHandler.onPunchAir(player, instance, itemStack);
                        player.setItemInMainHand(itemStack);
                    }
                    final DSBlockHandler blockHandler = this.blockRegistry.getHandlerForBlock(block);
                    player.sendMessage("Block handler for " + DSBlock.getIdFor(block) + ": " + blockHandler + " blockEntity: " + block.handler());
                    if (blockHandler != null) {
                        blockHandler.onPlayerPunch(
                                player,
                                instance,
                                block,
                                targeted,
                                event.getBlockFace(),
                                targeted.add(0, 1, 0)
                        );
                        player.sendMessage("Handled punch");
                    }
                });
    }

}
