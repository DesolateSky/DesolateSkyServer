package net.desolatesky.block.handler.custom;

import net.desolatesky.DesolateSkyServer;
import net.desolatesky.block.BlockProperties;
import net.desolatesky.block.BlockTags;
import net.desolatesky.block.handler.DSBlockHandler;
import net.desolatesky.block.settings.DSBlockSettings;
import net.desolatesky.instance.DSInstance;
import net.desolatesky.instance.InstancePoint;
import net.desolatesky.item.DSItem;
import net.desolatesky.item.DSItemRegistry;
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
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public final class ComposterBlockHandler extends DSBlockHandler {

    public static final int MAX_LEVEL = 8;

    public static final LootGeneratorType LOOT_GENERATOR_TYPE = LootGeneratorType.create("composter");

    public static final Key KEY = Namespace.key("composter");

    private final DSItemRegistry itemRegistry;

    public ComposterBlockHandler(DesolateSkyServer server) {
        super(server, DSBlockSettings.COMPOSTER);
        this.itemRegistry = server.itemRegistry();
    }

    @Override
    public boolean onInteract(@NotNull Interaction interaction, DSInstance instance) {
        final DSPlayer player = (DSPlayer) interaction.getPlayer();
        if (player.isSneaking()) {
            return false;
        }
        final Block block = interaction.getBlock();
        Double currentValue = block.getTag(BlockTags.COMPOSTER_LEVEL);
        if (currentValue == null) {
            currentValue = 0.0;
        }

        final Point blockPosition = interaction.getBlockPosition();
        if (currentValue >= MAX_LEVEL) {
            final Block newBlock = BlockProperties.LEVEL.set(block, 0).withTag(BlockTags.COMPOSTER_LEVEL, 0.0);
            instance.setBlock(blockPosition, newBlock);
            final LootTable loot = this.loot();
            final LootGenerator lootGenerator = loot.getGenerator(LOOT_GENERATOR_TYPE);
            if (lootGenerator == null) {
                return false;
            }
            InventoryUtil.addItemsToInventory(player, lootGenerator.generateLoot(LootContext.create(loot.randomSource())), new InstancePoint<>(instance, blockPosition.add(0, 1, 0)));
            return true;
        }

        final ItemStack mainHand = player.getItemInMainHand();
        final DSItem item = this.itemRegistry.getItem(mainHand);
        if (item == null) {
            return false;
        }
        final Double value = item.getTag(mainHand, ItemTags.COMPOSTER_VALUE);
        if (value == null || value <= 0) {
            return false;
        }
        player.setItemInMainHand(mainHand.consume(1));
        currentValue += value;
        final Block newBlock = BlockProperties.LEVEL.set(block, currentValue.intValue()).withTag(BlockTags.COMPOSTER_LEVEL, currentValue);
        instance.setBlock(interaction.getBlockPosition(), newBlock);
        return true;
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