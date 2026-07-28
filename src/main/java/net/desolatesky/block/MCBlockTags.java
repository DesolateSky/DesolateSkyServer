package net.desolatesky.block;

import net.minestom.server.instance.block.Block;
import net.minestom.server.registry.RegistryTag;
import net.minestom.server.registry.TagKey;

public final class MCBlockTags {

    private MCBlockTags() {}

    public static final RegistryTag<Block> MINECRAFT_PLANKS = Block.staticRegistry().getOrCreateTag(TagKey.ofHash("#minecraft:planks"));
    public static final RegistryTag<Block> PICKAXE_MINEABLE = Block.staticRegistry().getOrCreateTag(TagKey.ofHash("#minecraft:mineable/pickaxe"));
    public static final RegistryTag<Block> AXE_MINEABLE = Block.staticRegistry().getOrCreateTag(TagKey.ofHash("#minecraft:mineable/axe"));
    public static final RegistryTag<Block> SHOVEL_MINEABLE = Block.staticRegistry().getOrCreateTag(TagKey.ofHash("#minecraft:mineable/shovel"));
    public static final RegistryTag<Block> HOE_MINEABLE = Block.staticRegistry().getOrCreateTag(TagKey.ofHash("#minecraft:mineable/hoe"));
    public static final RegistryTag<Block> DIRT = Block.staticRegistry().getOrCreateTag(TagKey.ofHash("#dirt"));
    public static final RegistryTag<Block> GRASS_BLOCKS = Block.staticRegistry().getOrCreateTag(TagKey.ofHash("#grass_blocks"));


    public static boolean isDirtOrGrass(Block block) {
        return DIRT.contains(block)  || GRASS_BLOCKS.contains(block);
    }
}
