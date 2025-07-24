package net.desolatesky;

import me.lucko.luckperms.common.config.generic.adapter.EnvironmentVariableConfigAdapter;
import me.lucko.luckperms.common.config.generic.adapter.MultiConfigurationAdapter;
import me.lucko.luckperms.minestom.CommandRegistry;
import me.lucko.luckperms.minestom.LuckPermsMinestom;
import net.desolatesky.block.DSBlockRegistry;
import net.desolatesky.block.DSBlocks;
import net.desolatesky.block.handler.BlockHandlers;
import net.desolatesky.block.loot.BlockLootRegistry;
import net.desolatesky.command.Commands;
import net.desolatesky.command.console.ConsoleCommandHandler;
import net.desolatesky.config.ConfigFile;
import net.desolatesky.cooldown.CooldownConfig;
import net.desolatesky.crafting.CraftingManager;
import net.desolatesky.crafting.menu.listener.CraftingMenuListener;
import net.desolatesky.crafting.recipe.Recipes;
import net.desolatesky.database.MongoConnection;
import net.desolatesky.database.MongoSettings;
import net.desolatesky.entity.EntityListener;
import net.desolatesky.entity.loot.EntityLootRegistry;
import net.desolatesky.instance.DSInstanceManager;
import net.desolatesky.instance.biome.Biomes;
import net.desolatesky.instance.listener.InstanceListener;
import net.desolatesky.item.DSItemRegistry;
import net.desolatesky.item.listener.ItemListeners;
import net.desolatesky.message.MessageHandler;
import net.desolatesky.player.DSPlayer;
import net.desolatesky.player.DSPlayerManager;
import net.desolatesky.player.database.PlayerData;
import net.desolatesky.player.database.PlayerDatabaseAccessor;
import net.desolatesky.player.database.PlayerLoader;
import net.desolatesky.player.listener.PlayerListener;
import net.desolatesky.registry.DSRegistries;
import net.desolatesky.teleport.TeleportConfig;
import net.desolatesky.teleport.TeleportManager;
import net.luckperms.api.LuckPerms;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.extras.MojangAuth;
import org.spongepowered.configurate.ConfigurateException;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

public final class DesolateSkyServer {

    private MongoConnection mongoConnection;
    private PlayerDatabaseAccessor playerDatabase;
    private DSInstanceManager instanceManager;
    private DSPlayerManager playerManager;
    private MessageHandler messageHandler;
    private TeleportManager teleportManager;
    private CooldownConfig cooldownConfig;
    private DSRegistries registries;
    private CraftingManager craftingManager;

    private DesolateSkyServer() {
    }

    public DSInstanceManager instanceManager() {
        return this.instanceManager;
    }

    public DSPlayerManager playerManager() {
        return this.playerManager;
    }

    public MessageHandler messageHandler() {
        return this.messageHandler;
    }

    public TeleportManager teleportManager() {
        return this.teleportManager;
    }

    public CooldownConfig cooldownConfig() {
        return this.cooldownConfig;
    }

    public DSRegistries registries() {
        return this.registries;
    }

    public DSBlockRegistry blockRegistry() {
        return this.registries.blockRegistry();
    }

    public DSItemRegistry itemRegistry() {
        return this.registries.itemRegistry();
    }

    public BlockLootRegistry blockLootRegistry() {
        return this.registries.blockLootRegistry();
    }

    public EntityLootRegistry entityLootRegistry() {
        return this.registries.entityLootRegistry();
    }

    public CraftingManager craftingManager() {
        return this.craftingManager;
    }

    public DSBlocks blocks() {
        return this.blockRegistry().blocks();
    }

    public BlockHandlers blockHandlers() {
        return this.blockRegistry().blockHandlers();
    }

    public static void start(String[] args) {
        final MinecraftServer minecraftServer = MinecraftServer.init();
        MinecraftServer.getSchedulerManager().buildShutdownTask(() -> {
            MinecraftServer.getInstanceManager().getInstances().stream()
                    .map(instance -> instance.saveChunksToStorage()
                            .whenComplete((result, error) -> CompletableFuture.allOf(instance.saveChunksToStorage(), instance.saveInstance()))
                    )
                    .forEach(CompletableFuture::join);
            LuckPermsMinestom.disable();
        });
        final DesolateSkyServer server = new DesolateSkyServer();
        server.initialize();
        server.registerStuff();

        MinecraftServer.getConnectionManager().setPlayerProvider(new PlayerLoader(server, server.playerManager));
        MinecraftServer.setBrandName("DesolateSky");
        ConsoleCommandHandler.startConsoleCommandHandler();
        MojangAuth.init();
        minecraftServer.start("0.0.0.0", 25565);
    }

    private void initialize() {
        final Path worldsFolderPath = Path.of("worlds");

        this.mongoConnection = createMongoConnection();
        this.playerDatabase = PlayerDatabaseAccessor.create(this, this.mongoConnection);

        this.cooldownConfig = CooldownConfig.load(Path.of("cooldowns.conf"), "/cooldowns.conf");
        this.registries = this.loadRegistries();

        this.messageHandler = MessageHandler.DEFAULT_INSTANCE;
        this.craftingManager = CraftingManager.load();

        this.teleportManager = new TeleportManager(TeleportConfig.load(Path.of("teleport-times.conf"), "/teleport-times.conf"), MessageHandler.DEFAULT_INSTANCE);
        this.instanceManager = new DSInstanceManager(this, worldsFolderPath, MinecraftServer.getInstanceManager());

        this.playerManager = DSPlayerManager.create(this.playerDatabase);

        this.instanceManager.createLobbyInstance();
    }

    private void registerStuff() {
        Commands.register(MinecraftServer.getCommandManager(), this);
        Biomes.registerBiomes();

        Recipes.registerCrafting(this.craftingManager);

        this.registerListeners();

        this.setupPermissions();
    }

    private void registerListeners() {
        final GlobalEventHandler globalEventHandler = MinecraftServer.getGlobalEventHandler();
        InstanceListener.register(globalEventHandler);
        EntityListener.register(globalEventHandler);

        new PlayerListener(this).register(globalEventHandler);
        new ItemListeners(this.registries).register(globalEventHandler);
        new CraftingMenuListener(this.craftingManager).register(globalEventHandler);
    }

    private void setupPermissions() {
        final Path directory = Path.of("luckperms");
        final LuckPerms luckPerms = LuckPermsMinestom.builder(directory)
                .commandRegistry(CommandRegistry.minestom())
                .configurationAdapter(plugin ->
                        new MultiConfigurationAdapter(plugin,
                                new EnvironmentVariableConfigAdapter(plugin)
                        ))
                .dependencyManager(true)
                .enable();
    }

    private DSRegistries loadRegistries() {
        final BlockLootRegistry blockLootRegistry = BlockLootRegistry.create();
        final DSBlockRegistry blockRegistry = this.loadBlockRegistry(blockLootRegistry);
        final DSItemRegistry itemRegistry = DSItemRegistry.create(new HashMap<>());
        final EntityLootRegistry entityLootRegistry = EntityLootRegistry.create();
        return new DSRegistries(blockRegistry, itemRegistry, blockLootRegistry, entityLootRegistry);
    }

    private DSBlockRegistry loadBlockRegistry(BlockLootRegistry blockLootRegistry) {
        final BlockHandlers blockHandlers = BlockHandlers.load(this, blockLootRegistry);
        final DSBlocks blocks = DSBlocks.load(blockHandlers);
        return DSBlockRegistry.create(new HashMap<>(), blockHandlers, blocks, blockLootRegistry);
    }

    private static MongoConnection createMongoConnection() {
        final ConfigFile configFile = ConfigFile.get(Path.of("database.conf"), "/database.conf");
        try {
            final MongoSettings settings = MongoSettings.fromConfig(configFile.rootNode());
            return MongoConnection.connect(settings);
        } catch (ConfigurateException e) {
            throw new RuntimeException(e);
        }
    }

}
