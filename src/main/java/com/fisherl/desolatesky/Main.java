package com.fisherl.desolatesky;

import com.fisherl.desolatesky.block.BlockFactory;
import com.fisherl.desolatesky.block.ConfiguredBlockFactory;
import com.fisherl.desolatesky.server.DSServer;
import com.fisherl.desolatesky.world.WorldManager;
import net.minestom.server.Auth;
import net.minestom.server.MinecraftServer;

public final class Main {

    public static void main(String[] ignoredArgs) {
        // Initialize the server
        final MinecraftServer minecraftServer = MinecraftServer.init(new Auth.Online());
        final BlockFactory blockFactory = new ConfiguredBlockFactory();
        final DSServer server = new DSServer(minecraftServer, new WorldManager(blockFactory), blockFactory);
        server.init();
        server.start();
    }

}
