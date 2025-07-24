package net.desolatesky.block;

import net.minestom.server.item.Material;
import net.minestom.server.registry.RegistryTag;
import net.minestom.server.registry.TagKey;

public final class MaterialTags {

    private MaterialTags() {
        throw new IllegalArgumentException();
    }

    public static final RegistryTag<Material> MINECRAFT_PLANKS = Material.staticRegistry().getOrCreateTag(TagKey.ofHash("#minecraft:planks"));


}
