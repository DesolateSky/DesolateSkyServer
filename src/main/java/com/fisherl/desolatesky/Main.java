package com.fisherl.desolatesky;

import com.fisherl.desolatesky.block.BlockFactory;
import com.fisherl.desolatesky.block.ConfiguredBlockFactory;
import com.fisherl.desolatesky.entity.EntityFactory;
import com.fisherl.desolatesky.entity.TypedEntityFactory;
import com.fisherl.desolatesky.island.IslandManager;
import com.fisherl.desolatesky.item.ConfiguredItemFactory;
import com.fisherl.desolatesky.item.ItemFactory;
import com.fisherl.desolatesky.loot.LootFactory;
import com.fisherl.desolatesky.message.MessageHandler;
import com.fisherl.desolatesky.recipe.RecipeFactory;
import com.fisherl.desolatesky.server.DSServer;
import com.fisherl.desolatesky.world.WorldManager;
import net.minestom.server.Auth;
import net.minestom.server.MinecraftServer;

import java.nio.file.Path;

public final class Main {

    public static void main(String[] ignoredArgs) {
        // Initialize the server
        final MinecraftServer minecraftServer = MinecraftServer.init(new Auth.Online());
        final BlockFactory blockFactory = new ConfiguredBlockFactory();
        final ItemFactory itemFactory = new ConfiguredItemFactory();
        final EntityFactory entityFactory = new TypedEntityFactory();
        final LootFactory lootFactory = new LootFactory();
        final RecipeFactory recipeFactory = new RecipeFactory(itemFactory);
        final MessageHandler messageHandler = MessageHandler.create(Path.of("messages.conf"), "/messages.conf");
        final IslandManager islandManager = new IslandManager(messageHandler);
        final WorldManager worldManager = new WorldManager(islandManager,
                blockFactory,
                itemFactory,
                entityFactory,
                lootFactory,
                recipeFactory);
        final DSServer server = new DSServer(minecraftServer,
                messageHandler,
                blockFactory,
                itemFactory,
                entityFactory,
                lootFactory,
                recipeFactory,
                islandManager,
                worldManager
        );
        server.init();
        server.start();
    }

}
