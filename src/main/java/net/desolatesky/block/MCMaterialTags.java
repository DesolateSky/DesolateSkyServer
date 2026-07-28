package net.desolatesky.block;

import net.minestom.server.instance.block.Block;
import net.minestom.server.item.Material;
import net.minestom.server.registry.RegistryTag;
import net.minestom.server.registry.TagKey;

public final class MCMaterialTags {

    private MCMaterialTags() {}

    public static final RegistryTag<Material> MINECRAFT_PLANKS = Material.staticRegistry().getOrCreateTag(TagKey.ofHash("#minecraft:planks"));
    public static final RegistryTag<Material> PICKAXE_MINEABLE = Material.staticRegistry().getOrCreateTag(TagKey.ofHash("#minecraft:mineable/pickaxe"));
    public static final RegistryTag<Material> AXE_MINEABLE = Material.staticRegistry().getOrCreateTag(TagKey.ofHash("#minecraft:mineable/axe"));
    public static final RegistryTag<Material> SHOVEL_MINEABLE = Material.staticRegistry().getOrCreateTag(TagKey.ofHash("#minecraft:mineable/shovel"));
    public static final RegistryTag<Material> HOE_MINEABLE = Material.staticRegistry().getOrCreateTag(TagKey.ofHash("#minecraft:mineable/hoe"));
    public static final RegistryTag<Material> DIRT = Material.staticRegistry().getOrCreateTag(TagKey.ofHash("#dirt"));
    public static final RegistryTag<Material> GRASS_BLOCKS = Material.staticRegistry().getOrCreateTag(TagKey.ofHash("#grass_blocks"));


    public static boolean isDirtOrGrass(Block block) {
        final Material material = block.material();
        if (material == null) {
            return false;
        }
        return DIRT.contains(material)  || GRASS_BLOCKS.contains(material);
    }
}
