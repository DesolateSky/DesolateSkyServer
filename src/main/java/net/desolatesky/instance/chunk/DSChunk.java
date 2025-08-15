package net.desolatesky.instance.chunk;

import net.desolatesky.block.DSBlockRegistry;
import net.desolatesky.instance.DSInstance;
import net.minestom.server.instance.LightingChunk;
import net.minestom.server.instance.Section;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class DSChunk extends LightingChunk {

    private final DSBlockRegistry blockRegistry;

    public DSChunk(DSBlockRegistry blockRegistry, @NotNull DSInstance instance, int chunkX, int chunkZ) {
        super(instance, chunkX, chunkZ);
        this.blockRegistry = blockRegistry;
    }

    public DSChunk(DSBlockRegistry blockRegistry, @NotNull DSInstance instance, int chunkX, int chunkZ, @NotNull List<Section> sections) {
        super(instance, chunkX, chunkZ, sections);
        this.blockRegistry = blockRegistry;
    }

    @Override
    public void setBlock(int x, int y, int z, @NotNull Block block, BlockHandler.@Nullable Placement placement, BlockHandler.@Nullable Destroy destroy) {
        super.setBlock(x, y, z, block, placement, destroy);
    }

    @Override
    public @NotNull DSInstance getInstance() {
        return (DSInstance) super.getInstance();
    }

}
