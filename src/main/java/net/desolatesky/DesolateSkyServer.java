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
import net.desolatesky.config.ConfigNode;
import net.desolatesky.cooldown.CooldownConfig;
import net.desolatesky.crafting.CraftingManager;
import net.desolatesky.crafting.menu.listener.CraftingMenuListener;
import net.desolatesky.crafting.recipe.Recipes;
import net.desolatesky.database.DatabaseTimer;
import net.desolatesky.database.MongoConnection;
import net.desolatesky.database.MongoSettings;
import net.desolatesky.entity.EntityListener;
import net.desolatesky.entity.loot.EntityLootRegistry;
import net.desolatesky.instance.DSInstanceManager;
import net.desolatesky.instance.biome.Biomes;
import net.desolatesky.instance.listener.InstanceListener;
import net.desolatesky.item.DSItemRegistry;
import net.desolatesky.item.listener.ItemListeners;
import net.desolatesky.item.loot.ItemLootRegistry;
import net.desolatesky.menu.listener.MenuListener;
import net.desolatesky.message.MessageHandler;
import net.desolatesky.pack.ResourcePackSettings;
import net.desolatesky.player.DSPlayer;
import net.desolatesky.player.DSPlayerManager;
import net.desolatesky.player.database.PlayerDatabaseAccessor;
import net.desolatesky.player.database.PlayerLoader;
import net.desolatesky.player.listener.PlayerListener;
import net.desolatesky.registry.DSRegistries;
import net.desolatesky.team.IslandTeam;
import net.desolatesky.team.IslandTeamManager;
import net.desolatesky.team.database.IslandTeamDatabaseAccessor;
import net.desolatesky.team.role.RolePermissionsRegistry;
import net.desolatesky.teleport.TeleportConfig;
import net.desolatesky.teleport.TeleportManager;
import net.desolatesky.util.ResourceLoader;
import net.luckperms.api.LuckPerms;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.extras.MojangAuth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.configurate.ConfigurateException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

public final class DesolateSkyServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(DesolateSkyServer.class.getName());

    private DatabaseTimer databaseTimer;
    private MongoConnection mongoConnection;
    private PlayerDatabaseAccessor playerDatabase;
    private IslandTeamDatabaseAccessor islandDatabase;
    private DSInstanceManager instanceManager;
    private DSPlayerManager playerManager;
    private MessageHandler messageHandler;
    private TeleportManager teleportManager;
    private CooldownConfig cooldownConfig;
    private DSRegistries registries;
    private CraftingManager craftingManager;
    private RolePermissionsRegistry rolePermissionsRegistry;
    private IslandTeamManager islandTeamManager;
    private final AtomicBoolean stopped;

    private DesolateSkyServer() {
        this.stopped = new AtomicBoolean(false);
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

    public ItemLootRegistry itemLootRegistry() {
        return this.registries.itemLootRegistry();
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

    public RolePermissionsRegistry teamPermissionsRegistry() {
        return this.rolePermissionsRegistry;
    }

    public IslandTeamManager islandTeamManager() {
        return this.islandTeamManager;
    }

    public boolean stopped() {
        return this.stopped.get();
    }

    public static void start(String[] args) {
        final MinecraftServer minecraftServer = MinecraftServer.init();
        final DesolateSkyServer server = new DesolateSkyServer();
        server.initialize();
        server.registerStuff();

        MinecraftServer.getConnectionManager().setPlayerProvider(new PlayerLoader(server, server.playerManager));
        MinecraftServer.setBrandName("DesolateSky");
        ConsoleCommandHandler.startConsoleCommandHandler();
        MojangAuth.init();
        startServer(minecraftServer);
    }

    public void stop() {
        this.stopped.set(true);
        this.islandDatabase.shutdown();
        this.databaseTimer.stop();
        MinecraftServer.getInstanceManager().getInstances().stream()
                .map(instance -> instance.saveChunksToStorage()
                        .thenApply(_ -> CompletableFuture.allOf(instance.saveChunksToStorage(), instance.saveInstance()))
                )
                .forEach(CompletableFuture::join);
        LuckPermsMinestom.disable();
        this.databaseTimer.saveAll(false);
        final Map<Thread, StackTraceElement[]> allThreads = Thread.getAllStackTraces();
        for (final Thread thread : allThreads.keySet()) {
            System.out.println("Thread: " + thread.getName() + " | State: " + thread.getState());
        }
        LOGGER.info("Server shutdown complete.");
        MinecraftServer.stopCleanly();
    }

    private static void startServer(MinecraftServer minecraftServer) {
        try {
            final ConfigFile settingsFile = ConfigFile.get(Path.of("server-settings.conf"), "/server-settings.conf");
            final ConfigNode root = settingsFile.rootNode();
            final String address = root.node("address").getNonNull(String.class);
            final int port = root.node("port").getNonNull(Integer.class);
            minecraftServer.start(address, port);
        } catch (ConfigurateException e) {
            throw new RuntimeException("Failed to load server settings", e);
        }
    }

    private void initialize() {
        final Path worldsFolderPath = Path.of("worlds");

        this.mongoConnection = createMongoConnection();
        this.playerDatabase = PlayerDatabaseAccessor.create(this, this.mongoConnection);
        this.islandDatabase = IslandTeamDatabaseAccessor.create(this, this.mongoConnection);

        this.cooldownConfig = CooldownConfig.load(Path.of("cooldowns.conf"), "/cooldowns.conf");
        this.registries = this.loadRegistries();

        this.messageHandler = MessageHandler.DEFAULT_INSTANCE;
        this.craftingManager = CraftingManager.load();

        this.teleportManager = new TeleportManager(TeleportConfig.load(Path.of("teleport-times.conf"), "/teleport-times.conf"), MessageHandler.DEFAULT_INSTANCE);
        this.instanceManager = new DSInstanceManager(this, worldsFolderPath, MinecraftServer.getInstanceManager());

        this.playerManager = DSPlayerManager.create(this.playerDatabase);
        this.islandTeamManager = new IslandTeamManager(this, this.islandDatabase);

        this.rolePermissionsRegistry = RolePermissionsRegistry.load(this.blockRegistry(), this.itemRegistry());

        this.instanceManager.createLobbyInstance();

        this.databaseTimer = new DatabaseTimer(this.islandTeamManager, this.playerManager);
        this.databaseTimer.start();
    }

    private void registerStuff() {
        Commands.register(MinecraftServer.getCommandManager(), this);
        Biomes.registerBiomes();
        this.registries.registerAll(this);
        this.rolePermissionsRegistry.initialize();

        Recipes.registerCrafting(this.craftingManager, this.itemRegistry());


        this.registerListeners();

        this.setupPermissions();
    }

    private void registerListeners() {
        final GlobalEventHandler globalEventHandler = MinecraftServer.getGlobalEventHandler();
        InstanceListener.register(globalEventHandler);
        EntityListener.register(globalEventHandler);

        final ConfigFile resourcePackConfig = ConfigFile.get(Path.of("resource-pack.conf"), "/resource-pack.conf");
        new PlayerListener(this, ResourcePackSettings.fromConfig(resourcePackConfig.rootNode()), loadFavicon()).register(globalEventHandler);
        new ItemListeners(this.registries).register(globalEventHandler);
        new CraftingMenuListener(this.craftingManager).register(globalEventHandler);
        new MenuListener().register(globalEventHandler);
    }

    private static byte[] loadFavicon() {
        final File file = ResourceLoader.load(Path.of("server-icon.png"), "/server-icon.png");
        try {
            final BufferedImage image = ImageIO.read(file);
            if (image == null) {
                throw new IllegalStateException("Failed to read server icon image");
            }
            final Raster raster = image.getData();
            final int width = raster.getWidth();
            final int height = raster.getHeight();
            if (width != 64 || height != 64) {
                throw new IllegalStateException("Server icon must be 64x64 pixels");
            }
            final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(image, "PNG", outputStream);
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to load favicon", e);
        }
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
        final ItemLootRegistry itemLootRegistry = ItemLootRegistry.create();
        final EntityLootRegistry entityLootRegistry = EntityLootRegistry.create();
        return new DSRegistries(blockRegistry, itemRegistry, blockLootRegistry, itemLootRegistry, entityLootRegistry);
    }

    private DSBlockRegistry loadBlockRegistry(BlockLootRegistry blockLootRegistry) {
        final BlockHandlers blockHandlers = BlockHandlers.load(this, blockLootRegistry);
        final DSBlocks blocks = DSBlocks.load();
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
