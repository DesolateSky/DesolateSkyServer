package net.desolatesky.block.handler.custom;

import net.desolatesky.DesolateSkyServer;
import net.desolatesky.block.handler.DSBlockHandler;
import net.desolatesky.block.settings.DSBlockSettings;
import net.desolatesky.entity.type.SifterBlockEntity;
import net.desolatesky.instance.DSInstance;
import net.desolatesky.instance.InstancePoint;
import net.desolatesky.item.DSItems;
import net.desolatesky.item.ItemTags;
import net.desolatesky.loot.LootContext;
import net.desolatesky.loot.generator.LootGenerator;
import net.desolatesky.loot.generator.LootGeneratorType;
import net.desolatesky.loot.table.LootTable;
import net.desolatesky.player.DSPlayer;
import net.desolatesky.util.InventoryUtil;
import net.desolatesky.util.Namespace;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.ItemEntity;
import net.minestom.server.instance.block.Block;
import net.minestom.server.inventory.AbstractInventory;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;

public class SifterBlockHandler extends DSBlockHandler {

    public static final LootGeneratorType LOOT_GENERATOR_TYPE = LootGeneratorType.create("sifter");
    public static final Key KEY = Namespace.key("sifter");

    public static final Duration COOLDOWN = Duration.ofMillis(400);
    private Instant lastClick = Instant.now();

    private SifterBlockEntity entity;

    public SifterBlockHandler(DesolateSkyServer server) {
        super(server, DSBlockSettings.SIFTER);
    }

    @Override
    public boolean onInteract(@NotNull Interaction interaction, DSInstance instance) {
        final DSPlayer player = (DSPlayer) interaction.getPlayer();
        return this.click(player, instance, interaction.getBlockPosition(), true);
    }

    public boolean click(DSPlayer player, DSInstance instance, Point blockPosition, boolean clickNearby) {
        final ItemStack itemStack = player.getItemInMainHand();
        if (player.isSneaking()) {
            return false;
        }
        if (this.lastClick.plus(COOLDOWN).isAfter(Instant.now())) {
            return false;
        }
        if (this.entity != null && !this.entity.isRemoved()) {
            final boolean wasCompleted = this.entity.isComplete();
            this.entity.addStage();
            if (clickNearby) {
                this.clickNearbySifters(player, instance, blockPosition, 1);
            }
            if (!wasCompleted && this.entity.isComplete()) {
                final InstancePoint<Point> dropPosition = new InstancePoint<>(instance, blockPosition.add(0.5, 1, 0.5));
                this.dropLoot(player.getInventory(), dropPosition);
            }
            if (this.entity.isRemoved()) {
                this.entity = null;
            }
            return false;
        }
        if (!DSItems.DUST_BLOCK.is(itemStack)) {
            return false;
        }
        final Key blockKey = itemStack.getTag(ItemTags.BLOCK_ID);
        if (blockKey == null) {
            return false;
        }
        final Block block = this.server.blockRegistry().create(blockKey);
        if (block == null) {
            return false;
        }
        this.entity = new SifterBlockEntity(block, block, blockPosition, this);
        this.entity.setInstance(instance, blockPosition.add(0, 1, 0));
        this.entity.addStage();
        this.lastClick = Instant.now();
        player.setItemInMainHand(itemStack.consume(1));
        if (clickNearby) {
            this.clickNearbySifters(player, instance, blockPosition, 1);
        }
        return false;
    }

    private void clickNearbySifters(DSPlayer player, DSInstance instance, Point point, int radius) {
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                if (x == 0 && z == 0) {
                    continue;
                }
                final Point offsetPoint = point.add(x, 0, z);
                final Block block = instance.getBlock(offsetPoint);
                if (block.handler() instanceof SifterBlockHandler sifterHandler) {
                    sifterHandler.click(player, instance, offsetPoint, false);
                }
            }
        }
    }

    private void dropLoot(@Nullable AbstractInventory targetInventory, InstancePoint<? extends Point> dropPosition) {
        if (this.entity == null) {
            return;
        }
        if (!(this.entity.displayedBlock().handler() instanceof final DSBlockHandler displayBlockHandler)) {
            return;
        }
        final LootTable loot = displayBlockHandler.getLoot();
        final LootGenerator lootGenerator = loot.getGenerator(LOOT_GENERATOR_TYPE);
        if (lootGenerator != null) {
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
    }

    @Override
    public boolean isTickable() {
        return false;
    }

    @Override
    public void save(DSInstance instance, Point point, Block block) {
        // TODO
    }

    @Override
    public void load(CompoundBinaryTag tag, DSInstance instance, Point point, Block block) {
        // TODO
    }

}