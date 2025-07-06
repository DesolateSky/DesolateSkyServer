package net.desolatesky.block.handler;

import net.desolatesky.block.handler.vanilla.TrapdoorHandler;
import net.minestom.server.MinecraftServer;

import java.util.Collection;
import java.util.HashSet;

public final class BlockHandlers {

    private static final Collection<DSBlockHandler> blockHandlers = new HashSet<>();

    private  BlockHandlers() {
        throw new UnsupportedOperationException();
    }

    public static final TrapdoorHandler TRAPDOOR_HANDLER = register(new TrapdoorHandler());

    private static <T extends DSBlockHandler> T register(T handler) {
        blockHandlers.add(handler);
        return handler;
    }

    public static void registerAll(){
        for (DSBlockHandler blockHandler : blockHandlers) {
            MinecraftServer.getBlockManager().registerHandler(blockHandler.getKey(), () -> blockHandler);
        }
    }

}
