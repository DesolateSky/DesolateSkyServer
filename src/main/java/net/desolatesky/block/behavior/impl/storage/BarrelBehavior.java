package net.desolatesky.block.behavior.impl.storage;

import net.desolatesky.block.BlockTags;
import net.desolatesky.block.behavior.BlockBehavior;
import net.desolatesky.block.behavior.BlockDropBehavior;
import net.desolatesky.block.behavior.ClickBehavior;
import net.desolatesky.block.behavior.impl.BlockEntityBehavior;
import net.desolatesky.block.behavior.listener.LoadBehavior;
import net.desolatesky.block.behavior.serializer.BlockBehaviorSerializer;
import net.desolatesky.island.permission.IslandPermission;
import net.desolatesky.item.ItemFactory;
import net.desolatesky.player.DSPlayer;
import net.desolatesky.util.Namespace;
import net.desolatesky.world.DSWorld;
import net.desolatesky.world.IslandWorld;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.PlayerHand;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockHandler;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.NotNullByDefault;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@NotNullByDefault
public final class BarrelBehavior implements BlockBehavior, BlockDropBehavior, LoadBehavior, ClickBehavior, BlockEntityBehavior {

    public static final Key ID = Namespace.minecraftKey("barrel");

    public static final class Serializer extends BlockBehaviorSerializer<BarrelBehavior> {

        private static final String BLOCK_ITEM_KEY = "block_item";
        private static final String INVENTORY_SIZE_KEY = "inventory_size";

        public Serializer() {
            super(ID);
        }

        @Override
        public BarrelBehavior deserialize(java.lang.reflect.Type type, ConfigurationNode node) throws SerializationException {
            final Key itemId = Objects.requireNonNull(node.node(BLOCK_ITEM_KEY).get(Key.class), "item entity cannot be null");
            final int inventorySize = node.node(INVENTORY_SIZE_KEY).getInt();
            return new BarrelBehavior(itemId, inventorySize);
        }

        @Override
        public void serialize(java.lang.reflect.Type type, @org.jspecify.annotations.Nullable BarrelBehavior obj, ConfigurationNode node) throws SerializationException {
        }

        @Override
        public Class<BarrelBehavior> behaviorClass() {
            return BarrelBehavior.class;
        }
    }

    private final Key blockItemKey;
    private final int inventorySize;

    public BarrelBehavior(Key blockItemKey, int inventorySize) {
        this.blockItemKey = blockItemKey;
        this.inventorySize = inventorySize;
    }

    @Override
    public Result onRightClick(DSWorld world, DSPlayer player, PlayerHand hand, Point clickedPos, Block clickedBlock, ItemStack clickedWith) {
        if (world instanceof final IslandWorld islandWorld && !islandWorld.island().hasPermission(player.getUuid(), IslandPermission.USE_INVENTORY)) {
            return Result.BLOCK_INTERACTION;
        }
        if (!(clickedBlock.handler() instanceof final BarrelEntity barrelEntity)) {
            return Result.ALLOW;
        }
        if (player.isSneaking()) {
            return Result.ALLOW;
        }
        player.openInventory(barrelEntity.inventory);
        return Result.ALLOW;
    }

    @Override
    public Result onLeftClick(DSWorld world, DSPlayer player, Point clickedPos, Block clickedBlock, ItemStack clickedWith) {
        return Result.ALLOW;
    }

    @Override
    public Collection<ItemStack> getDrops(DSWorld world, Point pos, Block block, Key blockId, ItemFactory itemFactory, @Nullable ItemStack toolUsed) {
        if (!(block.handler() instanceof final BarrelEntity barrelEntity)) {
            return Collections.emptyList();
        }
        final List<ItemStack> drops = new ArrayList<>();
        for (final ItemStack itemStack : barrelEntity.inventory.getItemStacks()) {
            if (itemStack.isAir()) {
                continue;
            }
            drops.add(itemStack);
        }
        final ItemStack barrel = itemFactory.getDefaultItem(this.blockItemKey);
        if (barrel == null) {
            return drops;
        }
        drops.add(barrel);
        return drops;
    }

    private Map<Integer, ItemStack> buildInventoryMap(Block block) {
        if (!(block.handler() instanceof final BarrelEntity barrelEntity)) {
            return Collections.emptyMap();
        }
        final Map<Integer, ItemStack> items = new HashMap<>();
        for (int slot = 0; slot < barrelEntity.inventory.getSize(); slot++) {
            final ItemStack itemStack = barrelEntity.inventory.getItemStack(slot);
            if (itemStack.isAir()) {
                continue;
            }
            items.put(slot, itemStack);
        }
        return items;
    }

    @Override
    public void save(DSWorld world, Point blockPos, Block block) {
        world.setBlock(blockPos, block.withTag(BlockTags.INVENTORY, this.buildInventoryMap(block)), false);
    }

    @Override
    public void onLoad(DSWorld world, Point blockPos, Block block) {
        final Map<Integer, ItemStack> items = block.getTag(BlockTags.INVENTORY);
        if (items == null) {
            return;
        }
        if (!(block.handler() instanceof final BarrelEntity barrelEntity)) {
            return;
        }
        items.forEach(barrelEntity.inventory::setItemStack);
    }

    @Override
    public BlockHandler createBlockHandler() {
        return new BarrelEntity(this.inventorySize);
    }

    @Override
    public Key blockEntityId() {
        return ID;
    }

    @Override
    public Collection<Type<?>> types() {
        return List.of(Type.BLOCK_DROP, Type.LOAD, Type.CLICK, Type.BLOCK_ENTITY);
    }

    private static final class BarrelEntity implements BlockHandler {

        private final Inventory inventory;

        public BarrelEntity(int size) {
            final InventoryType inventoryType = switch (size) {
                case 9 -> InventoryType.CHEST_1_ROW;
                case 18 -> InventoryType.CHEST_2_ROW;
                case 27 -> InventoryType.CHEST_3_ROW;
                case 36 -> InventoryType.CHEST_4_ROW;
                case 45 -> InventoryType.CHEST_5_ROW;
                case 54 -> InventoryType.CHEST_6_ROW;
                default -> throw new IllegalArgumentException("Invalid size " + size);
            };
            this.inventory = new Inventory(inventoryType, Component.text("Barrel"));
        }

        @Override
        public Key getKey() {
            return ID;
        }
    }
}
