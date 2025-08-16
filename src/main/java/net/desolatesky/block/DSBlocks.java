package net.desolatesky.block;

import net.desolatesky.block.entity.custom.ComposterBlockEntity;
import net.desolatesky.block.entity.custom.DebrisCatcherBlockEntity;
import net.desolatesky.block.entity.custom.SifterBlockEntity;
import net.desolatesky.block.entity.custom.crop.CropBlockEntity;
import net.desolatesky.block.entity.custom.powered.cable.CableBlockEntity;
import net.desolatesky.block.entity.custom.powered.generator.SolarPanelBlockEntity;
import net.desolatesky.block.entity.custom.powered.machine.CobblestoneGeneratorBlockEntity;
import net.desolatesky.block.handler.DSBlockHandler;
import net.desolatesky.block.handler.block.CraftingTableHandler;
import net.desolatesky.block.handler.block.TrapDoorHandler;
import net.desolatesky.block.property.NoteBlockInstrument;
import net.desolatesky.block.settings.BlockSettings;
import net.desolatesky.block.settings.DSBlockSettings;
import net.kyori.adventure.key.Key;
import net.minestom.server.instance.block.Block;

import java.util.HashMap;
import java.util.Map;

public final class DSBlocks {

    public static DSBlocks load() {
        return new DSBlocks();
    }

    private static final Map<Key, DSBlock> DEFAULT_BLOCKS = new HashMap<>();

    private DSBlocks() {
    }

    // VANILLA
    public static final DSBlock WAXED_EXPOSED_CUT_COPPER_SLAB = create(Block.WAXED_EXPOSED_CUT_COPPER_SLAB, DSBlockSettings.WAXED_EXPOSED_CUT_COPPER_SLAB);
    public static final DSBlock UNBREAKABLE_WAXED_EXPOSED_CUT_COPPER_SLAB = create(Block.WAXED_EXPOSED_CUT_COPPER_SLAB, DSBlockSettings.UNBREAKABLE_WAXED_EXPOSED_CUT_COPPER_SLAB);
    public static final DSBlock WAXED_EXPOSED_COPPER_TRAPDOOR = create(Block.WAXED_EXPOSED_COPPER_TRAPDOOR, new TrapDoorHandler(DSBlockSettings.WAXED_EXPOSED_COPPER_TRAPDOOR));
    public static final DSBlock DIRT = create(Block.DIRT, DSBlockSettings.DIRT);
    public static final DSBlock SAND = create(Block.SAND, DSBlockSettings.SAND);
    public static final DSBlock GRAVEL = create(Block.GRAVEL, DSBlockSettings.GRAVEL);
    public static final DSBlock CRAFTING_TABLE = create(Block.CRAFTING_TABLE, new CraftingTableHandler());
    public static final DSBlock WHEAT = create(Block.WHEAT, CropBlockEntity.createHandler(DSBlockSettings.WHEAT));
    public static final DSBlock COBBLESTONE = create(Block.COBBLESTONE, DSBlockSettings.COBBLESTONE);

    public static final DSBlock DUST_BLOCK = create(blockBuilder(Block.NOTE_BLOCK).property(BlockProperties.INSTRUMENT, NoteBlockInstrument.HARP).property(BlockProperties.NOTE, 1).build(), DSBlockSettings.DUST_BLOCK);
    public static final DSBlock DEBRIS_CATCHER = create(Block.COBWEB, DebrisCatcherBlockEntity.HANDLER);
    public static final DSBlock SIFTER = create(blockBuilder(Block.SCAFFOLDING).build(), SifterBlockEntity.HANDLER);
    public static final DSBlock COMPOSTER = create(blockBuilder(Block.COMPOSTER).build(), ComposterBlockEntity.HANDLER);

    // PETRIFIED WOOD
    public static final DSBlock PETRIFIED_SAPLING = create(Block.PALE_OAK_SAPLING, CropBlockEntity.createHandler(DSBlockSettings.PETRIFIED_SAPLING));
    public static final DSBlock PETRIFIED_LOG = create(Block.PALE_OAK_LOG, DSBlockSettings.PETRIFIED_LOG);
public static final DSBlock STRIPPED_PETRIFIED_LOG = create(Block.STRIPPED_PALE_OAK_LOG, DSBlockSettings.STRIPPED_PETRIFIED_LOG);
    public static final DSBlock PETRIFIED_LEAVES = create(Block.PALE_OAK_LEAVES, DSBlockSettings.PETRIFIED_LEAVES);
    public static final DSBlock PETRIFIED_PLANKS = create(Block.PALE_OAK_PLANKS, DSBlockSettings.PETRIFIED_PLANKS);
    public static final DSBlock PETRIFIED_SLAB = create(Block.PALE_OAK_SLAB, DSBlockSettings.PETRIFIED_SLAB);

    // POWER
    public static final DSBlock CABLE = create(Block.BARRIER, CableBlockEntity.createHandler(DSBlockSettings.CABLE));
    public static final DSBlock SOLAR_PANEL = create(Block.DAYLIGHT_DETECTOR, SolarPanelBlockEntity.createHandler(DSBlockSettings.SOLAR_PANEL));
    public static final DSBlock COBBLESTONE_GENERATOR = create(Block.BEACON, CobblestoneGeneratorBlockEntity.createHandler(DSBlockSettings.COBBLESTONE_GENERATOR));

    public void register(DSBlockRegistry registry) {
        DEFAULT_BLOCKS.values().forEach(registry::register);
    }

    private static DSBlock addDefault(DSBlock dsBlock) {
        DEFAULT_BLOCKS.putIfAbsent(dsBlock.key(), dsBlock);
        return dsBlock;
    }

    private static BlockBuilder blockBuilder(Block block) {
        return BlockBuilder.blockBuilder(block);
    }

    private static DSBlock create(Block block, DSBlockHandler blockHandler) {
        return addDefault(DSBlock.create(blockHandler.key(), block, blockHandler));
    }

    private static DSBlock create(Block block, BlockSettings blockSettings) {
        return addDefault(DSBlock.create(blockSettings.key(), block, new DSBlockHandler(blockSettings)));
    }


}
