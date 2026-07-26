package net.desolatesky.server;

import me.lucko.luckperms.common.config.generic.adapter.EnvironmentVariableConfigAdapter;
import me.lucko.luckperms.common.config.generic.adapter.MultiConfigurationAdapter;
import me.lucko.luckperms.minestom.CommandRegistry;
import me.lucko.luckperms.minestom.LuckPermsMinestom;
import net.desolatesky.advancement.IslandAdvancementManager;
import net.desolatesky.advancement.listener.IslandAdvancementListener;
import net.desolatesky.block.BlockFactory;
import net.desolatesky.block.MaterialTags;
import net.desolatesky.block.behavior.listener.BlockClickListener;
import net.desolatesky.command.informational.AfkCommand;
import net.desolatesky.command.informational.DiscordCommand;
import net.desolatesky.command.SpawnCommand;
import net.desolatesky.command.admin.BanCommand;
import net.desolatesky.command.admin.ClearChatCommand;
import net.desolatesky.command.admin.ClearInventoryCommand;
import net.desolatesky.command.admin.FlyCommand;
import net.desolatesky.command.admin.GameModeCommand;
import net.desolatesky.command.admin.GiveCommand;
import net.desolatesky.command.admin.LogLevelCommand;
import net.desolatesky.command.admin.PlayerDataCommand;
import net.desolatesky.command.admin.ReloadMessagesCommand;
import net.desolatesky.command.admin.StopCommand;
import net.desolatesky.command.admin.TpCommand;
import net.desolatesky.command.admin.WhitelistCommand;
import net.desolatesky.command.console.ConsoleCommandHandler;
import net.desolatesky.command.informational.PluginsCommand;
import net.desolatesky.command.informational.RulesCommand;
import net.desolatesky.command.informational.StoryCommand;
import net.desolatesky.command.informational.TutorialCommand;
import net.desolatesky.command.island.IslandCommand;
import net.desolatesky.config.ConfigFile;
import net.desolatesky.crafting.listener.CraftingMenuListener;
import net.desolatesky.data.FileDatabase;
import net.desolatesky.entity.EntityManager;
import net.desolatesky.entity.listener.EntityDamageListener;
import net.desolatesky.entity.listener.ItemPickupListener;
import net.desolatesky.entity.listener.ItemThrowListener;
import net.desolatesky.island.Island;
import net.desolatesky.island.IslandManager;
import net.desolatesky.island.IslandSnapshot;
import net.desolatesky.item.ItemFactory;
import net.desolatesky.item.listener.BlockPlaceListener;
import net.desolatesky.item.listener.ItemClickListener;
import net.desolatesky.logging.DSLogger;
import net.desolatesky.loot.LootFactory;
import net.desolatesky.message.MessageHandler;
import net.desolatesky.player.DSPlayer;
import net.desolatesky.player.DSPlayerData;
import net.desolatesky.player.listener.PlayerChatListener;
import net.desolatesky.player.listener.PlayerEatListener;
import net.desolatesky.player.listener.PlayerJoinListener;
import net.desolatesky.player.listener.PlayerListPingListener;
import net.desolatesky.player.listener.PlayerTickListener;
import net.desolatesky.player.whitelist.PlayerWhitelist;
import net.desolatesky.recipe.RecipeFactory;
import net.desolatesky.server.player.PlayerBanDatabase;
import net.desolatesky.server.player.PlayerUUIDDatabase;
import net.desolatesky.teleport.TeleportManager;
import net.desolatesky.util.Constants;
import net.desolatesky.util.ResourceLoader;
import net.desolatesky.world.DSWorld;
import net.desolatesky.world.WorldManager;
import net.desolatesky.world.biome.Biomes;
import net.desolatesky.world.dimension.Dimensions;
import net.desolatesky.world.listener.ChunkLoadListener;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.luckperms.api.LuckPerms;
import net.minestom.server.MinecraftServer;
import net.minestom.server.adventure.audience.Audiences;
import net.minestom.server.color.TeamColor;
import net.minestom.server.command.CommandManager;
import net.minestom.server.entity.Player;
import net.minestom.server.event.EventFilter;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.server.ServerTickMonitorEvent;
import net.minestom.server.event.trait.InstanceEvent;
import net.minestom.server.event.trait.InventoryEvent;
import net.minestom.server.event.trait.PlayerEvent;
import net.minestom.server.monitoring.TickMonitor;
import net.minestom.server.network.packet.server.play.TeamsPacket;
import net.minestom.server.timer.TaskSchedule;
import net.minestom.server.utils.MathUtils;
import org.jetbrains.annotations.NotNullByDefault;
import org.jspecify.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

@NotNullByDefault
public final class DSServer {

    private final AtomicReference<@Nullable TickMonitor> lastTick = new AtomicReference<>();

    private final MinecraftServer server;
    private final FileDatabase<DSPlayerData> playerDatabase;
    private final FileDatabase<IslandSnapshot> islandDatabase;
    private final IslandManager islandManager;
    private final WorldManager worldManager;
    private final BlockFactory blockFactory;
    private final ItemFactory itemFactory;
    private final EntityManager entityFactory;
    private final LootFactory lootFactory;
    private final RecipeFactory recipeFactory;
    private final MessageHandler messageHandler;
    private final TeleportManager teleportManager;
    private final IslandAdvancementManager islandAdvancementManager;
    private final ConfigFile serverConfig;
    private final PlayerWhitelist whitelist;
    private final ServerDatabases serverDatabases;

    public DSServer(
            MinecraftServer server,
            FileDatabase<DSPlayerData> playerDatabase,
            FileDatabase<IslandSnapshot> islandDatabase,
            MessageHandler messageHandler,
            BlockFactory blockFactory,
            ItemFactory itemFactory,
            EntityManager entityFactory,
            LootFactory lootFactory,
            RecipeFactory recipeFactory,
            IslandManager islandManager,
            WorldManager worldManager,
            IslandAdvancementManager islandAdvancementManager
    ) {
        this.server = server;
        this.playerDatabase = playerDatabase;
        this.islandDatabase = islandDatabase;
        this.messageHandler = messageHandler;
        this.blockFactory = blockFactory;
        this.itemFactory = itemFactory;
        this.entityFactory = entityFactory;
        this.lootFactory = lootFactory;
        this.recipeFactory = recipeFactory;
        this.islandManager = islandManager;
        this.worldManager = worldManager;
        this.islandAdvancementManager = islandAdvancementManager;
        this.teleportManager = new TeleportManager(this.messageHandler, this.worldManager);
        this.whitelist = new PlayerWhitelist(Path.of("whitelist"), "/whitelist");

        this.serverConfig = ConfigFile.get(Path.of("server.conf"), "/server.conf");

        final Path databasesPath = Path.of("databases");
        this.serverDatabases = new ServerDatabases(
                new PlayerUUIDDatabase(databasesPath.resolve("player-uuids.db")),
                new PlayerBanDatabase(databasesPath.resolve("player-bans.db"))
        );
    }

    public void init() {
        // register material tags
        final String ignored = MaterialTags.class.getName();

        MinecraftServer.getConnectionManager().setPlayerProvider((conn, profile) -> {
            final DSPlayerData data = this.playerDatabase.loadDataNow(profile.uuid());
            return new DSPlayer(conn, profile, this, data);
        });
        MinecraftServer.setBrandName("DesolateSky");
        Dimensions.registerDimensions();
        Biomes.registerBiomes();

        boolean initialized = true;

        try {
            this.serverDatabases.initializeAll();
        } catch (IOException | SQLException e) {
            DSLogger.getLogger().severe(e);
            initialized = false;
            throw new RuntimeException(e);
        } finally {
            if (!initialized) {
                this.stop();
            }
        }

        this.blockFactory.initialize();
        this.itemFactory.initialize();
        this.entityFactory.initialize();
        this.lootFactory.initialize();
        this.recipeFactory.initialize();
        this.islandAdvancementManager.initialize(this);
        this.registerListeners();
        this.setupPermissions();
        this.registerCommands();

        MinecraftServer.getTeamManager().createBuilder(Constants.VOID_TEAM_ID)
                .collisionRule(TeamsPacket.CollisionRule.PUSH_OTHER_TEAMS)
                .prefix(Component.text("Void").color(NamedTextColor.DARK_PURPLE))
                .teamColor(TeamColor.DARK_PURPLE)
                .build();

        ConsoleCommandHandler.startConsoleCommandHandler();
    }

    public void start() {
        final String ip = this.serverConfig.rootNode().node("ip").getString("0.0.0.0");
        final int port = this.serverConfig.rootNode().node("port").getInt(25565);
        DSLogger.getLogger().info("Running on %s:%d".formatted(ip, port));
        this.server.start(ip, port);

        MinecraftServer.getSchedulerManager().scheduleTask(this.teleportManager::tick, TaskSchedule.tick(1), TaskSchedule.tick(1));

        MinecraftServer.getSchedulerManager().scheduleTask(() -> {
            final Collection<Player> players = MinecraftServer.getConnectionManager().getOnlinePlayers();
            if (players.isEmpty()) return;

            final Runtime runtime = Runtime.getRuntime();
            final TickMonitor tickMonitor = this.lastTick.get();
            final long ramUsage = (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024;

            final Component header = Component.newline()
                    .append(Component.text("RAM USAGE: " + ramUsage + " MB", NamedTextColor.GRAY).append(Component.newline())
                            .append(Component.text("TICK TIME: " + MathUtils.round(tickMonitor.getTickTime(), 2) + "ms", NamedTextColor.GRAY))).append(Component.newline());

            Audiences.players().sendPlayerListHeaderAndFooter(header, Component.empty());
        }, TaskSchedule.tick(10), TaskSchedule.tick(10));

        MinecraftServer.getSchedulerManager().scheduleTask(() -> {
            DSLogger.getLogger().info("Beginning save of data");
            for (final Player player : MinecraftServer.getConnectionManager().getOnlinePlayers()) {
                this.playerDatabase.saveData(player.getUuid(), ((DSPlayer) player).createSnapshot());
            }
            for (final Island island : this.islandManager.getAll()) {
                this.islandDatabase.saveData(island.islandId(), island.createSnapshot());
            }
            DSLogger.getLogger().info("Ending save of data");
        }, TaskSchedule.duration(Duration.ofMinutes(3)), TaskSchedule.duration(Duration.ofMinutes(3)));
    }

    public IslandManager islandManager() {
        return this.islandManager;
    }

    public WorldManager worldManager() {
        return this.worldManager;
    }

    public MessageHandler messageHandler() {
        return this.messageHandler;
    }

    public TeleportManager teleportManager() {
        return this.teleportManager;
    }

    public BlockFactory blockFactory() {
        return this.blockFactory;
    }

    public ItemFactory itemFactory() {
        return this.itemFactory;
    }

    public EntityManager entityFactory() {
        return this.entityFactory;
    }

    public LootFactory lootFactory() {
        return this.lootFactory;
    }

    public RecipeFactory recipeManager() {
        return this.recipeFactory;
    }

    public IslandAdvancementManager islandAdvancementManager() {
        return this.islandAdvancementManager;
    }

    public PlayerWhitelist playerWhitelist() {
        return this.whitelist;
    }

    public ConfigFile serverConfig() {
        return this.serverConfig;
    }

    public ServerDatabases serverDatabases() {
        return this.serverDatabases;
    }

    public void stop() {
        for (final Player player : MinecraftServer.getConnectionManager().getOnlinePlayers()) {
            player.kick(Component.text("Server is restarting").color(NamedTextColor.RED));
        }
        this.islandManager.getAll().forEach(i -> this.islandDatabase.saveDataNow(i.islandId(), i.createSnapshot()));
        DSLogger.getLogger().info("Disabling LuckPerms");
        LuckPermsMinestom.disable();
        DSLogger.getLogger().info("Saving instances");
        CompletableFuture.allOf(
                MinecraftServer.getInstanceManager().getInstances().stream()
                        .filter(DSWorld.class::isInstance)
                        .map(DSWorld.class::cast)
                        .map(DSWorld::save)
                        .toArray(CompletableFuture[]::new)
        ).join();
        try {
            this.serverDatabases.shutdownAll();
        } catch (SQLException e) {
            DSLogger.getLogger().severe(e);
        }
        DSLogger.getLogger().info("Stopping server");
        MinecraftServer.getSchedulerManager().scheduleEndOfTick(MinecraftServer::stopCleanly);
    }

    private void registerListeners() {
        final GlobalEventHandler globalEventHandler = MinecraftServer.getGlobalEventHandler();
        final EventNode<PlayerEvent> playerEventNode = EventNode.type("player_configuration", EventFilter.PLAYER);
        final EventNode<InventoryEvent> inventoryEventNode = EventNode.type("inventory_events", EventFilter.INVENTORY);
        final EventNode<InstanceEvent> instanceEventNode = EventNode.type("instance_events", EventFilter.INSTANCE);

        globalEventHandler.addChild(playerEventNode);
        globalEventHandler.addChild(inventoryEventNode);
        globalEventHandler.addChild(instanceEventNode);

        new PlayerListPingListener(this.serverConfig).register(globalEventHandler);
        new PlayerJoinListener(this.serverDatabases, this.playerDatabase, this.islandDatabase, this.worldManager, this.islandManager, this.itemFactory, this.messageHandler, this.serverConfig).register(playerEventNode);
        new PlayerTickListener(this.islandManager, this.worldManager).register(playerEventNode);
        new PlayerChatListener(loadBadWords()).register(playerEventNode);
        new PlayerEatListener().register(playerEventNode);

        new CraftingMenuListener(this.recipeFactory, this.itemFactory).register(inventoryEventNode);

        new ChunkLoadListener(this.worldManager).register(globalEventHandler);
        new ItemPickupListener().register(globalEventHandler);
        new ItemThrowListener().register(globalEventHandler);
        new EntityDamageListener().register(globalEventHandler);
        new BlockClickListener(this.blockFactory).register(globalEventHandler);
        new BlockPlaceListener(this.itemFactory, this.blockFactory).register(globalEventHandler);
        new ItemClickListener(this.itemFactory).register(globalEventHandler);
        new IslandAdvancementListener(this.islandAdvancementManager).register(globalEventHandler);

        globalEventHandler.addListener(ServerTickMonitorEvent.class, event -> this.lastTick.set(event.getTickMonitor()));
    }

    private static List<String> loadBadWords() {
        try {
            final File file = ResourceLoader.load(Path.of("badwords.txt"), "/badwords.txt");
            return Files.readAllLines(file.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void registerCommands() {
        final CommandManager commandManager = MinecraftServer.getCommandManager();
        commandManager.register(new StopCommand(this));
        commandManager.register(new IslandCommand(this.teleportManager, this.messageHandler, this.worldManager, this.islandManager, this.playerDatabase, this.serverDatabases));
        commandManager.register(new GiveCommand(this.itemFactory));
        commandManager.register(new LogLevelCommand());
        commandManager.register(new ReloadMessagesCommand(this.messageHandler));
        commandManager.register(new ClearInventoryCommand(this.serverDatabases, this.playerDatabase));
        commandManager.register(new PlayerDataCommand(this.serverDatabases, this.playerDatabase));
        commandManager.register(new ClearChatCommand());
        commandManager.register(new BanCommand(this.serverConfig, this.serverDatabases));
        commandManager.register(new WhitelistCommand(this.whitelist));
        commandManager.register(new SpawnCommand(this.teleportManager));
        commandManager.register(new DiscordCommand(this.serverConfig));
        commandManager.register(new GameModeCommand());
        commandManager.register(new FlyCommand());
        commandManager.register(new TpCommand());
        commandManager.register(new TutorialCommand(this.messageHandler));
        commandManager.register(new StoryCommand(this.messageHandler));
        commandManager.register(new RulesCommand(this.messageHandler));
        commandManager.register(new AfkCommand());
        commandManager.register(new PluginsCommand(this.messageHandler));
    }

    private void setupPermissions() {
        final Path directory = Path.of("luckperms");
        final LuckPerms luckPerms = LuckPermsMinestom.builder(directory)
                .commandRegistry(CommandRegistry.minestom())
                .configurationAdapter(plugin -> new MultiConfigurationAdapter(plugin, new EnvironmentVariableConfigAdapter(plugin)))
                .enable();
    }
}
