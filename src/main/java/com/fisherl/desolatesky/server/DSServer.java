package com.fisherl.desolatesky.server;

import com.fisherl.desolatesky.block.BlockFactory;
import com.fisherl.desolatesky.command.admin.StopCommand;
import com.fisherl.desolatesky.command.console.ConsoleCommandHandler;
import com.fisherl.desolatesky.player.DSPlayer;
import com.fisherl.desolatesky.player.listener.PlayerJoinListener;
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
import net.minestom.server.entity.Player;
import net.minestom.server.event.EventFilter;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.server.ServerTickMonitorEvent;
import net.minestom.server.event.trait.PlayerEvent;
import net.minestom.server.monitoring.TickMonitor;
import net.minestom.server.timer.TaskSchedule;
import net.minestom.server.utils.MathUtils;
import org.jetbrains.annotations.NotNullByDefault;

import java.nio.file.Path;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicReference;

@NotNullByDefault
public final class DSServer {

    private final AtomicReference<TickMonitor> lastTick = new AtomicReference<>();

    private final MinecraftServer server;
    private final WorldManager worldManager;
    private final BlockFactory blockFactory;

    public DSServer(
            MinecraftServer server,
            WorldManager worldManager,
            BlockFactory blockFactory
    ) {
        this.server = server;
        this.worldManager = worldManager;
        this.blockFactory = blockFactory;
    }

    public void init() {
        MinecraftServer.getConnectionManager().setPlayerProvider(DSPlayer::new);
        Biomes.registerBiomes();
        this.blockFactory.initialize();
        this.registerListeners();
        this.setupPermissions();
        this.registerCommands();

        ConsoleCommandHandler.startConsoleCommandHandler();
    }

    public void start() {
        this.server.start("0.0.0.0", 25565);

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

    public void stop() {
        LuckPermsMinestom.disable();
        MinecraftServer.stopCleanly();
    }

    private void registerListeners() {
        final GlobalEventHandler globalEventHandler = MinecraftServer.getGlobalEventHandler();
        final EventNode<PlayerEvent> playerEventNode = EventNode.type("player_configuration", EventFilter.PLAYER);
        globalEventHandler.addChild(playerEventNode);
        new PlayerJoinListener(this.worldManager).register(playerEventNode);

        globalEventHandler.addListener(ServerTickMonitorEvent.class, event -> this.lastTick.set(event.getTickMonitor()));
    }

    private void registerCommands() {
        MinecraftServer.getCommandManager().register(new StopCommand(this));
    }

    private void setupPermissions() {
        final Path directory = Path.of("luckperms");
        final LuckPerms luckPerms = LuckPermsMinestom.builder(directory)
                .commandRegistry(CommandRegistry.minestom())
                .configurationAdapter(plugin -> new MultiConfigurationAdapter(plugin, new EnvironmentVariableConfigAdapter(plugin)))
                .enable();
    }
}
