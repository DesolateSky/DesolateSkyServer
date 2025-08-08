package net.desolatesky.block.entity.custom;

import net.desolatesky.DesolateSkyServer;
import net.desolatesky.block.BlockKeys;
import net.desolatesky.block.DSBlock;
import net.desolatesky.block.DSBlockRegistry;
import net.desolatesky.block.entity.BlockEntities;
import net.desolatesky.block.entity.BlockEntity;
import net.desolatesky.block.handler.BlockHandlerResult;
import net.desolatesky.block.handler.DSBlockHandler;
import net.desolatesky.block.handler.entity.BlockEntityHandler;
import net.desolatesky.block.settings.BlockSettings;
import net.desolatesky.entity.type.SifterBlockDisplayEntity;
import net.desolatesky.instance.DSInstance;
import net.desolatesky.instance.InstancePoint;
import net.desolatesky.item.DSItems;
import net.desolatesky.item.ItemKeys;
import net.desolatesky.item.ItemTags;
import net.desolatesky.loot.LootContext;
import net.desolatesky.loot.generator.LootGenerator;
import net.desolatesky.loot.generator.LootGeneratorType;
import net.desolatesky.loot.generator.LootGeneratorTypes;
import net.desolatesky.loot.table.LootTable;
import net.desolatesky.player.DSPlayer;
import net.desolatesky.util.InventoryUtil;
import net.desolatesky.tag.Tags;
import net.kyori.adventure.key.Key;
import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.ItemEntity;
import net.minestom.server.entity.PlayerHand;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockFace;
import net.minestom.server.inventory.AbstractInventory;
import net.minestom.server.item.ItemStack;
import net.minestom.server.tag.Tag;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class SifterBlockEntity extends BlockEntity<SifterBlockEntity> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SifterBlockEntity.class);

    public static final BlockSettings SETTINGS = BlockSettings.builder(BlockKeys.SIFTER, DSItems.SIFTER.create())
            .breakTime(1_000)
            .blockItem(ItemKeys.SIFTER)
            .build();

    public static final BlockEntityHandler<SifterBlockEntity> HANDLER = new Handler();

    private static final Tag<Integer> STAGE_TAG = Tags.Integer("stage");
    private static final Tag<Key> SIFTING_BLOCK_TAG = Tags.Key("sifting_block");

    public static final int MAX_STAGE = 8;

    public static final Duration COOLDOWN = Duration.ofMillis(400);


    private Instant lastClick = Instant.now();
    private final DSBlockRegistry blockRegistry;
    private final BlockEntities blockEntities;
    private SifterBlockDisplayEntity entity;
    private int stage = 0;
    private Block siftingBlock;

    public SifterBlockEntity(DesolateSkyServer server) {
        super(BlockKeys.SIFTER, server);
        this.blockRegistry = server.blockRegistry();
        this.blockEntities = server.blockEntities();
    }

    private boolean isComplete() {
        return this.stage >= MAX_STAGE;
    }

    private void addStage() {
        this.stage = Math.min(MAX_STAGE, this.stage + 1);
    }

    private void spawnEntity(DSInstance instance, Point position) {
        final DSBlockHandler blockHandler = this.blockRegistry.getHandlerForBlock(this.siftingBlock);
        if (blockHandler == null) {
            LOGGER.warn("No block handler found for sifting block: {}", this.siftingBlock);
            return;
        }
        this.entity = new SifterBlockDisplayEntity(this.siftingBlock, this.siftingBlock, blockHandler, position, this);
        this.entity.setInstance(instance, position.add(0, 1, 0)).thenRun(() -> {
            this.entity.setStage(this.stage);
        });
    }

    public BlockHandlerResult.InteractBlock click(DSPlayer player, DSInstance instance, Point blockPosition, boolean clickNearby) {
        final ItemStack itemStack = player.getItemInMainHand();
        if (player.isSneaking()) {
            return BlockHandlerResult.passthroughInteractBlock();
        }
        if (this.lastClick.plus(COOLDOWN).isAfter(Instant.now())) {
            return BlockHandlerResult.consumeInteractBlock(null, true);
        }
        if (this.entity != null && !this.entity.isRemoved()) {
            final AtomicReference<BlockHandlerResult.InteractBlock> result = new AtomicReference<>(BlockHandlerResult.passthroughInteractBlock());
            this.entity.acquirable().sync(unused -> {
                final boolean wasCompleted = this.isComplete();
                this.addStage();
                this.entity.setStage(this.stage);
                if (clickNearby) {
                    this.clickNearbySifters(player, instance, blockPosition, 1);
                }
                if (!wasCompleted && this.isComplete()) {
                    final InstancePoint<Point> dropPosition = new InstancePoint<>(instance, blockPosition.add(0.5, 1, 0.5));
                    this.dropLoot(player.getInventory(), dropPosition);
                    this.stage = 0;
                    this.entity.remove();
                }
                if (this.entity.isRemoved()) {
                    this.entity = null;
                }
                result.set(BlockHandlerResult.consumeInteractBlock(null, true));
            });
            return result.get();
        }
        final Key blockKey = itemStack.getTag(ItemTags.BLOCK_ID);
        if (blockKey == null) {
            return BlockHandlerResult.passthroughInteractBlock();
        }
        final Block block = this.server.blockRegistry().create(blockKey);
        if (block == null) {
            return BlockHandlerResult.passthroughInteractBlock();
        }
        final LootGenerator lootGenerator = this.getLootGeneratorForBlock(block);
        if (lootGenerator == null) {
            return BlockHandlerResult.passthroughInteractBlock();
        }
        this.siftingBlock = block;
        this.addStage();
        this.spawnEntity(instance, blockPosition);
        this.lastClick = Instant.now();
        player.setItemInMainHand(itemStack.consume(1));
        if (clickNearby) {
            this.clickNearbySifters(player, instance, blockPosition, 1);
        }
        return BlockHandlerResult.passthroughInteractBlock();
    }

    private void clickNearbySifters(DSPlayer player, DSInstance instance, Point point, int radius) {
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                if (x == 0 && z == 0) {
                    continue;
                }
                final Point offsetPoint = point.add(x, 0, z);
                final Block block = instance.getBlock(offsetPoint);
                if (block.handler() instanceof SifterBlockEntity sifterHandler) {
                    sifterHandler.click(player, instance, offsetPoint, false);
                }
            }
        }
    }

    private void dropLoot(
            @Nullable AbstractInventory targetInventory, InstancePoint<? extends
                    Point> dropPosition
    ) {
        if (this.entity == null) {
            return;
        }
        final DSBlockHandler blockHandler = this.blockRegistry.getHandlerForBlock(this.siftingBlock);
        if (blockHandler == null) {
            return;
        }
        final LootTable loot = blockHandler.loot();
        final LootGenerator lootGenerator = loot.getGenerator(LootGeneratorTypes.SIFTER);
        if (lootGenerator == null) {
            return;
        }
        final Collection<ItemStack> generated = lootGenerator.generateLoot(LootContext.create(loot.randomSource()));
        if (targetInventory != null) {
            InventoryUtil.addItemsToInventory(targetInventory, generated, dropPosition);
        } else {
            for (final ItemStack itemStack : generated) {
                final ItemEntity itemEntity = new ItemEntity(itemStack);
                itemEntity.setInstance(dropPosition.instance(), dropPosition.pos());
            }
        }
    }

    private @Nullable LootGenerator getLootGeneratorForBlock(Block block) {
        return this.blockRegistry.getLootTableForBlock(block, LootTable.EMPTY).getGenerator(LootGeneratorTypes.SIFTER);
    }

    @Override
    public boolean isTickable() {
        return false;
    }

    @Override
    public @Nullable Block save(DSInstance instance, Point point, Block block) {
        Block newBlock = block.withTag(STAGE_TAG, this.stage);
        if (this.siftingBlock != null) {
            newBlock = newBlock.withTag(SIFTING_BLOCK_TAG, DSBlock.getIdFor(this.siftingBlock));
        }
        return newBlock;
    }

    @Override
    public void load(Placement placement, DSInstance instance) {
        final Block block = placement.getBlock();
        final Integer stage = block.getTag(STAGE_TAG);
        this.stage = Objects.requireNonNullElse(stage, 0);
        final Key blockKey = block.getTag(SIFTING_BLOCK_TAG);
        if (blockKey != null) {
            final DSBlock foundBlock = this.blockRegistry.getBlock(blockKey);
            if (foundBlock != null) {
                this.siftingBlock = foundBlock.create(this.blockEntities);
                this.spawnEntity(instance, placement.getBlockPosition());
            }
        } else {
            this.stage = 0;
        }
    }

    private static class Handler extends BlockEntityHandler<SifterBlockEntity> {

        private Handler() {
            super(SETTINGS, SifterBlockEntity.class);
        }

        @Override
        public BlockHandlerResult.InteractBlock onPlayerInteract(DSPlayer player, DSInstance instance, Block block, Point blockPosition, PlayerHand hand, BlockFace face, Point cursorPosition, SifterBlockEntity entity) {
            return entity.click(player, instance, blockPosition, true);
        }

        @Override
        public BlockHandlerResult onDestroy(DSInstance instance, Block block, Point blockPosition, SifterBlockEntity entity) {
            entity.entity.remove();
            return BlockHandlerResult.CONSUME;
        }

        @Override
        public BlockHandlerResult onPlayerDestroy(DSPlayer player, DSInstance instance, Block block, Point blockPosition, SifterBlockEntity entity) {
            if (entity.entity != null ) {
                entity.entity.remove();
            }
            return BlockHandlerResult.CONSUME;
        }

    }

}