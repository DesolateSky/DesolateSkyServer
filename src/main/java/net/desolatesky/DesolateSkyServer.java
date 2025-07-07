package net.desolatesky;

import me.lucko.luckperms.common.config.generic.adapter.EnvironmentVariableConfigAdapter;
import me.lucko.luckperms.common.config.generic.adapter.MultiConfigurationAdapter;
import me.lucko.luckperms.minestom.CommandRegistry;
import me.lucko.luckperms.minestom.LuckPermsMinestom;
import net.desolatesky.block.handler.BlockHandlers;
import net.desolatesky.command.Commands;
import net.desolatesky.command.console.ConsoleCommandHandler;
import net.desolatesky.cooldown.CooldownConfig;
import net.desolatesky.entity.EntityListener;
import net.desolatesky.instance.DSInstanceManager;
import net.desolatesky.instance.biome.Biomes;
import net.desolatesky.instance.listener.InstanceListener;
import net.desolatesky.message.MessageHandler;
import net.desolatesky.player.DSPlayer;
import net.desolatesky.player.DSPlayerManager;
import net.desolatesky.player.listener.PlayerListener;
import net.desolatesky.teleport.TeleportConfig;
import net.desolatesky.teleport.TeleportManager;
import net.luckperms.api.LuckPerms;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.extras.MojangAuth;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

public final class DesolateSkyServer {

    private static DesolateSkyServer instance;

    public static DesolateSkyServer get() {
        if (instance == null) {
            throw new IllegalStateException("DesolateSkyServer has not been started yet.");
        }
        return instance;
    }

    private final DSInstanceManager instanceManager;
    private final DSPlayerManager playerManager;
    private final MessageHandler messageHandler;
    private final TeleportManager teleportManager;
    private final CooldownConfig cooldownConfig;

    private DesolateSkyServer(
            DSInstanceManager instanceManager,
            DSPlayerManager playerManager,
            MessageHandler messageHandler,
            TeleportManager teleportManager,
            CooldownConfig cooldownConfig
    ) {
        this.instanceManager = instanceManager;
        this.playerManager = playerManager;
        this.messageHandler = messageHandler;
        this.teleportManager = teleportManager;
        this.cooldownConfig = cooldownConfig;
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
        MinecraftServer.getConnectionManager().setPlayerProvider(DSPlayer::new);

        final Path worldsFolderPath = Path.of("worlds");
        final DSInstanceManager instanceManager = new DSInstanceManager(worldsFolderPath, MinecraftServer.getInstanceManager());
        instanceManager.createLobbyInstance();
        final DSPlayerManager playerManager = DSPlayerManager.create(Path.of("players").resolve("players.db"));
        final CooldownConfig cooldownConfig = CooldownConfig.load(Path.of("cooldowns.conf"), "/cooldowns.conf");
        final TeleportManager teleportManager = new TeleportManager(TeleportConfig.load(Path.of("teleport-times.conf"), "/teleport-times.conf"), MessageHandler.DEFAULT_INSTANCE);
        instance = new DesolateSkyServer(instanceManager, playerManager, MessageHandler.DEFAULT_INSTANCE, teleportManager, cooldownConfig);

        MinecraftServer.setBrandName("DesolateSky");

        ConsoleCommandHandler.startConsoleCommandHandler();

        MojangAuth.init();

        instance.registerStuff();

        minecraftServer.start("0.0.0.0", 25565);
    }

    private void registerStuff() {
        Commands.register(MinecraftServer.getCommandManager());
        BlockHandlers.registerAll();
        Biomes.registerBiomes();

        this.registerListeners();

        this.setupPermissions();
    }

    private void registerListeners() {
        final GlobalEventHandler globalEventHandler = MinecraftServer.getGlobalEventHandler();
        PlayerListener.register(globalEventHandler);
        InstanceListener.register(globalEventHandler);
        EntityListener.register(globalEventHandler);
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

}
