package net.desolatesky.block;

import net.desolatesky.block.handler.BlockHandlers;
import net.desolatesky.block.property.NoteBlockInstrument;
import net.desolatesky.item.ItemKeys;
import net.desolatesky.util.Namespace;
import net.kyori.adventure.key.Key;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;

import java.util.HashMap;
import java.util.Map;

public final class DSBlocks {

    public static DSBlocks load(BlockHandlers blockHandlers) {
        final DSBlocks blocks = new DSBlocks(blockHandlers);
        blocks.initialize();
        return blocks;
    }

    private final Map<Key, DSBlock> defaultBlocks = new HashMap<>();

    private final BlockHandlers blockHandlers;

    private DSBlocks(BlockHandlers blockHandlers) {
        this.blockHandlers = blockHandlers;
    }

    // VANILLA
    private DSBlock leafLitter;
    private DSBlock waxedExposedCutCopperSlab;
    private DSBlock waxedExposedCopperTrapdoor;
    private DSBlock craftingTable;

    // CUSTOM
    private DSBlock debrisCatcher;
    private DSBlock dustBlock;
    private DSBlock sifter;

    private void initialize() {
        // VANILLA
        this.leafLitter = this.addDefault(DSBlock.create(Block.LEAF_LITTER));
        this.waxedExposedCutCopperSlab = this.addDefault(DSBlock.create(Block.WAXED_EXPOSED_CUT_COPPER_SLAB));
        this.waxedExposedCopperTrapdoor = this.addDefault(DSBlock.create(Block.WAXED_EXPOSED_COPPER_TRAPDOOR, this.blockHandlers.trapdoor()));
        this.craftingTable = this.addDefault(DSBlock.create(Block.CRAFTING_TABLE, this.blockHandlers.craftingTable()));

        // CUSTOM
        this.debrisCatcher = this.addDefault(DSBlock.create(BlockKeys.DEBRIS_CATCHER, blockBuilder(Block.COBWEB).breakTime(1_000).blockItem(ItemKeys.DEBRIS_CATCHER).build(), this.blockHandlers.debrisCatcher()));
        this.dustBlock = this.addDefault(DSBlock.create(BlockKeys.DUST_BLOCK, blockBuilder(Block.NOTE_BLOCK).property(BlockProperties.INSTRUMENT, NoteBlockInstrument.HARP).property(BlockProperties.NOTE, 1).breakTime(1_000).blockItem(ItemKeys.DUST_BLOCK).build(), this.blockHandlers.dustBlock()));
        this.sifter = this.addDefault(DSBlock.create(BlockKeys.SIFTER, blockBuilder(Block.SCAFFOLDING).breakTime(1_000).blockItem(ItemKeys.SIFTER).build(), this.blockHandlers.sifter()));
    }

    public DSBlock leafLitter() {
        return this.leafLitter;
    }

    public DSBlock waxedExposedCutCopperSlab() {
        return this.waxedExposedCutCopperSlab;
    }

    public DSBlock waxedExposedCopperTrapdoor() {
        return this.waxedExposedCopperTrapdoor;
    }

    public DSBlock debrisCatcher() {
        return this.debrisCatcher;
    }

    public DSBlock dustBlock() {
        return this.dustBlock;
    }

    public DSBlock sifter() {
        return this.sifter;
    }

    public void register(DSBlockRegistry registry) {
        this.defaultBlocks.values().forEach(registry::register);
    }

    private static BlockBuilder blockBuilder(Block block) {
        return BlockBuilder.from(block);
    }

    private DSBlock addDefault(DSBlock dsBlock) {
        this.defaultBlocks.putIfAbsent(dsBlock.key(), dsBlock);
        return dsBlock;
    }

}
