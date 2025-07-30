package net.desolatesky.block.entity.custom;

import net.desolatesky.DesolateSkyServer;
import net.desolatesky.block.BlockKeys;
import net.desolatesky.block.BlockProperties;
import net.desolatesky.block.BlockTags;
import net.desolatesky.block.category.BlockCategories;
import net.desolatesky.block.entity.BlockEntity;
import net.desolatesky.block.handler.InteractionResult;
import net.desolatesky.block.handler.entity.BlockEntityHandler;
import net.desolatesky.block.settings.BlockSettings;
import net.desolatesky.block.settings.DSBlockSettings;
import net.desolatesky.instance.DSInstance;
import net.desolatesky.instance.InstancePoint;
import net.desolatesky.item.DSItem;
import net.desolatesky.item.DSItemRegistry;
import net.desolatesky.item.DSItems;
import net.desolatesky.item.ItemKeys;
import net.desolatesky.item.ItemTags;
import net.desolatesky.loot.LootContext;
import net.desolatesky.loot.generator.ItemStackLootGenerator;
import net.desolatesky.loot.generator.LootGenerator;
import net.desolatesky.loot.generator.LootGeneratorType;
import net.desolatesky.loot.table.LootTable;
import net.desolatesky.loot.type.ItemStackLoot;
import net.desolatesky.player.DSPlayer;
import net.desolatesky.util.InventoryUtil;
import net.desolatesky.util.Namespace;
import net.kyori.adventure.key.Key;
import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.PlayerHand;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockFace;
import net.minestom.server.item.ItemStack;
import net.minestom.server.tag.Tag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public final class ComposterBlockEntity extends BlockEntity<ComposterBlockEntity> {

    public static final LootGeneratorType LOOT_GENERATOR_TYPE = LootGeneratorType.create("composter");

    private static final Tag<Double> COMPOSTER_LEVEL_TAG = Tag.Double("composter_level");

    public static final BlockSettings SETTINGS = BlockSettings.builder(BlockKeys.COMPOSTER, DSItems.COMPOSTER.create())
            .breakTime(1_000)
            .blockItem(ItemKeys.COMPOSTER)
            .lootTable(LootTable.create(BlockKeys.COMPOSTER, Map.of(
                    ComposterBlockEntity.LOOT_GENERATOR_TYPE,
                    ItemStackLootGenerator.create(ComposterBlockEntity.LOOT_GENERATOR_TYPE, List.of(new ItemStackLoot(DSItems.DIRT, 1, 1, 1)), 1, 1)
            )))
            .categories(BlockCategories.AXE_MINEABLE).build();

    public static final BlockEntityHandler<ComposterBlockEntity> HANDLER = new Handler();

    public static final int MAX_LEVEL = 8;

    private final DSItemRegistry itemRegistry;

    public ComposterBlockEntity(DesolateSkyServer server) {
        super(server, HANDLER);
        this.itemRegistry = server.itemRegistry();
    }

    @Override
    public boolean isTickable() {
        return false;
    }

    @Override
    public @Nullable Block save(DSInstance instance, Point point, Block block) {
        return block;
    }

    @Override
    public void load(Placement placement, DSInstance instance) {

    }

    private static class Handler extends BlockEntityHandler<ComposterBlockEntity> {

        private Handler() {
            super(SETTINGS, ComposterBlockEntity.class);
        }

        @Override
        public InteractionResult onPlayerInteract(DSPlayer player, DSInstance instance, Block block, Point blockPosition, PlayerHand hand, BlockFace face, Point cursorPosition, ComposterBlockEntity entity) {
            if (player.isSneaking()) {
                return InteractionResult.PASSTHROUGH;
            }
            Double currentValue = block.getTag(BlockTags.COMPOSTER_LEVEL);
            if (currentValue == null) {
                currentValue = 0.0;
            }

            if (currentValue >= MAX_LEVEL) {
                final Block newBlock = BlockProperties.LEVEL.set(block, 0).withTag(BlockTags.COMPOSTER_LEVEL, 0.0);
                instance.setBlock(blockPosition, newBlock);
                final LootTable loot = this.loot();
                final LootGenerator lootGenerator = loot.getGenerator(LOOT_GENERATOR_TYPE);
                if (lootGenerator == null) {
                    return InteractionResult.PASSTHROUGH;
                }
                InventoryUtil.addItemsToInventory(player, lootGenerator.generateLoot(LootContext.create(loot.randomSource())), new InstancePoint<>(instance, blockPosition.add(0, 1, 0)));
                return InteractionResult.PASSTHROUGH;
            }

            final ItemStack mainHand = player.getItemInMainHand();
            final DSItem item = entity.itemRegistry.getItem(mainHand);
            if (item == null) {
                return InteractionResult.PASSTHROUGH;
            }
            final Double value = item.getTag(mainHand, ItemTags.COMPOSTER_VALUE);
            if (value == null || value <= 0) {
                return InteractionResult.PASSTHROUGH;
            }
            player.setItemInMainHand(mainHand.consume(1));
            currentValue += value;
            final Block newBlock = BlockProperties.LEVEL.set(block, currentValue.intValue()).withTag(BlockTags.COMPOSTER_LEVEL, currentValue);
            instance.setBlock(blockPosition, newBlock);
            return InteractionResult.CONSUME_INTERACTION;
        }

    }

}