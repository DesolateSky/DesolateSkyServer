package net.desolatesky.block;

import net.minestom.server.item.Material;
import net.minestom.server.registry.RegistryTag;
import net.minestom.server.registry.TagKey;

public final class MaterialTags {

    private MaterialTags() {}

    public static final RegistryTag<Material> MINECRAFT_PLANKS = Material.staticRegistry().getOrCreateTag(TagKey.ofHash("#minecraft:planks"));
    public static final RegistryTag<Material> PICKAXE_MINEABLE = Material.staticRegistry().getOrCreateTag(TagKey.ofHash("#minecraft:mineable/pickaxe"));
    public static final RegistryTag<Material> AXE_MINEABLE = Material.staticRegistry().getOrCreateTag(TagKey.ofHash("#minecraft:mineable/axe"));
    public static final RegistryTag<Material> SHOVEL_MINEABLE = Material.staticRegistry().getOrCreateTag(TagKey.ofHash("#minecraft:mineable/shovel"));
    public static final RegistryTag<Material> HOE_MINEABLE = Material.staticRegistry().getOrCreateTag(TagKey.ofHash("#minecraft:mineable/hoe"));
    public static final RegistryTag<Material> DIRT = Material.staticRegistry().getOrCreateTag(TagKey.ofHash("#dirt"));

}
