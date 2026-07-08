package com.fisherl.desolatesky.server;

import com.fisherl.desolatesky.block.BlockFactory;
import com.fisherl.desolatesky.block.behavior.listener.BlockClickListener;
import com.fisherl.desolatesky.command.admin.GiveCommand;
import com.fisherl.desolatesky.command.admin.StopCommand;
import com.fisherl.desolatesky.command.console.ConsoleCommandHandler;
import com.fisherl.desolatesky.command.island.IslandCommand;
import com.fisherl.desolatesky.entity.EntityFactory;
import com.fisherl.desolatesky.entity.listener.EntityDamageListener;
import com.fisherl.desolatesky.entity.listener.ItemPickupListener;
import com.fisherl.desolatesky.entity.listener.ItemThrowListener;
import com.fisherl.desolatesky.island.IslandManager;
import com.fisherl.desolatesky.item.ItemFactory;
import com.fisherl.desolatesky.item.listener.BlockPlaceListener;
import com.fisherl.desolatesky.loot.LootFactory;
import com.fisherl.desolatesky.message.MessageHandler;
import com.fisherl.desolatesky.player.DSPlayer;
import com.fisherl.desolatesky.player.listener.PlayerJoinListener;
import com.fisherl.desolatesky.recipe.RecipeFactory;
import com.fisherl.desolatesky.teleport.TeleportManager;
import com.fisherl.desolatesky.world.WorldManager;
import com.fisherl.desolatesky.world.biome.Biomes;
import me.lucko.luckperms.common.config.generic.adapter.EnvironmentVariableConfigAdapter;
import me.lucko.luckperms.common.config.generic.adapter.MultiConfigurationAdapter;
import me.lucko.luckperms.minestom.CommandRegistry;
import me.lucko.luckperms.minestom.LuckPermsMinestom;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.luckperms.api.LuckPerms;
import net.minestom.server.MinecraftServer;
import net.minestom.server.adventure.audience.Audiences;
import net.minestom.server.command.CommandManager;
import net.minestom.server.entity.Player;
import net.minestom.server.event.EventFilter;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.server.ServerTickMonitorEvent;
import net.minestom.server.event.trait.PlayerEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.monitoring.TickMonitor;
import net.minestom.server.timer.TaskSchedule;
import net.minestom.server.utils.MathUtils;
import org.jetbrains.annotations.NotNullByDefault;

import java.nio.file.Path;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

@NotNullByDefault
public final class DSServer {

    private final AtomicReference<TickMonitor> lastTick = new AtomicReference<>();

    private final MinecraftServer server;
    private final IslandManager islandManager;
    private final WorldManager worldManager;
    private final BlockFactory blockFactory;
    private final ItemFactory itemFactory;
    private final EntityFactory entityFactory;
    private final LootFactory lootFactory;
    private final RecipeFactory recipeFactory;
    private final MessageHandler messageHandler;
    private final TeleportManager teleportManager;

    public DSServer(
            MinecraftServer server,
            MessageHandler messageHandler,
            BlockFactory blockFactory,
            ItemFactory itemFactory,
            EntityFactory entityFactory,
            LootFactory lootFactory,
            RecipeFactory recipeFactory,
            IslandManager islandManager,
            WorldManager worldManager
    ) {
        this.server = server;
        this.messageHandler = messageHandler;
        this.blockFactory = blockFactory;
        this.itemFactory = itemFactory;
        this.entityFactory = entityFactory;
        this.lootFactory = lootFactory;
        this.recipeFactory = recipeFactory;
        this.islandManager = islandManager;
        this.worldManager = worldManager;
        this.teleportManager = new TeleportManager(this.messageHandler, this.worldManager);
    }

    public void init() {
        MinecraftServer.getConnectionManager().setPlayerProvider(DSPlayer::new);
        MinecraftServer.setBrandName("DesolateSky");
        Biomes.registerBiomes();
        this.blockFactory.initialize();
        this.itemFactory.initialize();
        this.entityFactory.initialize();
        this.lootFactory.initialize();
        this.recipeFactory.initialize();
        this.registerListeners();
        this.setupPermissions();
        this.registerCommands();

        ConsoleCommandHandler.startConsoleCommandHandler();
    }

    public void start() {
        this.server.start("0.0.0.0", 25565);

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

    public EntityFactory entityFactory() {
        return this.entityFactory;
    }

    public LootFactory lootFactory() {
        return this.lootFactory;
    }

    public RecipeFactory recipeManager() {
        return this.recipeFactory;
    }

    public void stop() {
        LuckPermsMinestom.disable();
        CompletableFuture.allOf(
                MinecraftServer.getInstanceManager().getInstances().stream()
                        .map(Instance::saveInstance)
                        .toArray(CompletableFuture[]::new)
        ).join();
        MinecraftServer.stopCleanly();
    }

    private void registerListeners() {
        final GlobalEventHandler globalEventHandler = MinecraftServer.getGlobalEventHandler();
        final EventNode<PlayerEvent> playerEventNode = EventNode.type("player_configuration", EventFilter.PLAYER);
        globalEventHandler.addChild(playerEventNode);

        new PlayerJoinListener(this.worldManager).register(playerEventNode);
        new ItemPickupListener().register(globalEventHandler);
        new ItemThrowListener().register(globalEventHandler);
        new EntityDamageListener().register(globalEventHandler);
        new BlockClickListener(this.blockFactory).register(globalEventHandler);
        new BlockPlaceListener(this.itemFactory).register(globalEventHandler);

        globalEventHandler.addListener(ServerTickMonitorEvent.class, event -> this.lastTick.set(event.getTickMonitor()));
    }

    private void registerCommands() {
        final CommandManager commandManager = MinecraftServer.getCommandManager();
        commandManager.register(new StopCommand(this));
        commandManager.register(new IslandCommand(this.teleportManager, this.messageHandler, this.worldManager, this.islandManager));
        commandManager.register(new GiveCommand(this.itemFactory));
    }

    private void setupPermissions() {
        final Path directory = Path.of("luckperms");
        final LuckPerms luckPerms = LuckPermsMinestom.builder(directory)
                .commandRegistry(CommandRegistry.minestom())
                .configurationAdapter(plugin -> new MultiConfigurationAdapter(plugin, new EnvironmentVariableConfigAdapter(plugin)))
                .enable();
    }
}
