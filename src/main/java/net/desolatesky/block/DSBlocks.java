package net.desolatesky.block;

import net.desolatesky.block.handler.BlockHandlers;
import net.desolatesky.block.property.NoteBlockInstrument;
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
    public static final DSBlock WAXED_EXPOSED_CUT_COPPER_SLAB = create(Block.WAXED_EXPOSED_CUT_COPPER_SLAB);
    public static final DSBlock UNBREAKABLE_WAXED_EXPOSED_CUT_COPPER_SLAB = create(Block.WAXED_EXPOSED_CUT_COPPER_SLAB);
    public static final DSBlock WAXED_EXPOSED_COPPER_TRAPDOOR = create(Block.WAXED_EXPOSED_COPPER_TRAPDOOR);
    public static final DSBlock DIRT = create(Block.DIRT);
    public static final DSBlock CRAFTING_TABLE = create(Block.CRAFTING_TABLE);

    public static final DSBlock PETRIFIED_PLANKS = create(BlockKeys.PETRIFIED_PLANKS, blockBuilder(Block.PALE_OAK_PLANKS).build());
    public static final DSBlock PETRIFIED_SLAB = create(BlockKeys.PETRIFIED_SLAB, blockBuilder(Block.PALE_OAK_SLAB).build());
    public static final DSBlock DEBRIS_CATCHER = create(BlockKeys.DEBRIS_CATCHER, blockBuilder(Block.COBWEB).build());
    public static final DSBlock DUST_BLOCK = create(BlockKeys.DUST_BLOCK, blockBuilder(Block.NOTE_BLOCK).property(BlockProperties.INSTRUMENT, NoteBlockInstrument.HARP).property(BlockProperties.NOTE, 1).build());
    public static final DSBlock SIFTER = create(BlockKeys.SIFTER, blockBuilder(Block.SCAFFOLDING).build());
    public static final DSBlock COMPOSTER = create(BlockKeys.COMPOSTER, blockBuilder(Block.COMPOSTER).build());

    public void register(DSBlockRegistry registry) {
        DEFAULT_BLOCKS.values().forEach(registry::register);
    }

    private static BlockBuilder blockBuilder(Block block) {
        return BlockBuilder.from(block);
    }

    private static DSBlock addDefault(DSBlock dsBlock) {
        DEFAULT_BLOCKS.putIfAbsent(dsBlock.key(), dsBlock);
        return dsBlock;
    }

    private static DSBlock create(Key key, Block block) {
        return addDefault(DSBlock.create(key, block));
    }

    private static DSBlock create(Block block) {
        return addDefault(DSBlock.create(block));
    }


}
