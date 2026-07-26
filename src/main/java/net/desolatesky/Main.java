package net.desolatesky;

import net.desolatesky.advancement.IslandAdvancementManager;
import net.desolatesky.block.BlockFactory;
import net.desolatesky.block.ConfiguredBlockFactory;
import net.desolatesky.data.FileDatabase;
import net.desolatesky.entity.EntityManager;
import net.desolatesky.entity.TypedEntityFactory;
import net.desolatesky.island.IslandManager;
import net.desolatesky.island.IslandSnapshot;
import net.desolatesky.item.ConfiguredItemFactory;
import net.desolatesky.item.ItemFactory;
import net.desolatesky.loot.LootFactory;
import net.desolatesky.message.MessageHandler;
import net.desolatesky.player.DSPlayerData;
import net.desolatesky.recipe.RecipeFactory;
import net.desolatesky.server.DSServer;
import net.desolatesky.world.WorldManager;
import net.minestom.server.Auth;
import net.minestom.server.MinecraftServer;

import java.nio.file.Path;

public final class Main {

    static void main(String[] ignoredArgs) {
        // Initialize the server
        final MinecraftServer minecraftServer = MinecraftServer.init(new Auth.Online());
        final FileDatabase<DSPlayerData> playerDatabase = new FileDatabase<>(Path.of("players"), DSPlayerData.DATA_TRANSLATOR);
        final FileDatabase<IslandSnapshot> islandDatabase = new FileDatabase<>(Path.of("islands"), IslandSnapshot.DATA_TRANSLATOR);
        final BlockFactory blockFactory = new ConfiguredBlockFactory();
        final ItemFactory itemFactory = new ConfiguredItemFactory();
        final EntityManager entityFactory = new TypedEntityFactory();
        final LootFactory lootFactory = new LootFactory();
        final RecipeFactory recipeFactory = new RecipeFactory(itemFactory);
        final IslandAdvancementManager islandAdvancementManager = new IslandAdvancementManager();
        final MessageHandler messageHandler = MessageHandler.create(Path.of("messages.conf"), "/messages.conf");
        final IslandManager islandManager = new IslandManager(islandDatabase, islandAdvancementManager, messageHandler);
        final WorldManager worldManager = new WorldManager(islandManager,
                blockFactory,
                itemFactory,
                entityFactory,
                lootFactory,
                recipeFactory);
        final DSServer server = new DSServer(minecraftServer,
                playerDatabase,
                islandDatabase,
                messageHandler,
                blockFactory,
                itemFactory,
                entityFactory,
                lootFactory,
                recipeFactory,
                islandManager,
                worldManager,
                islandAdvancementManager);
        server.init();
        server.start();
    }

}
