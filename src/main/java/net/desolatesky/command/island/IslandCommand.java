package net.desolatesky.command.island;

import net.desolatesky.data.FileDatabase;
import net.desolatesky.island.IslandManager;
import net.desolatesky.island.IslandSnapshot;
import net.desolatesky.island.role.IslandRole;
import net.desolatesky.item.ItemIds;
import net.desolatesky.item.definition.ItemDefinition;
import net.desolatesky.logging.DSLogger;
import net.desolatesky.message.MessageHandler;
import net.desolatesky.message.Messages;
import net.desolatesky.player.DSPlayer;
import net.desolatesky.player.DSPlayerData;
import net.desolatesky.server.ServerDatabases;
import net.desolatesky.teleport.TeleportLocation;
import net.desolatesky.teleport.TeleportManager;
import net.desolatesky.util.InventoryUtil;
import net.desolatesky.world.IslandWorld;
import net.desolatesky.world.TeleportUtil;
import net.desolatesky.world.WorldManager;
import net.desolatesky.world.WorldType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.Argument;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.suggestion.SuggestionEntry;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.Chunk;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public final class IslandCommand extends Command {

    private final Argument<String> playerArgument = ArgumentType.String("player");

    private final TeleportManager teleportManager;
    private final MessageHandler messageHandler;
    private final WorldManager worldManager;
    private final IslandManager islandManager;
    private final FileDatabase<DSPlayerData> playerDatabase;
    private final ServerDatabases serverDatabases;

    public IslandCommand(
            TeleportManager teleportManager,
            MessageHandler messageHandler,
            WorldManager worldManager,
            IslandManager islandManager,
            FileDatabase<DSPlayerData> playerDatabase,
            ServerDatabases serverDatabases
    ) {
        super("island", "is");
        this.teleportManager = teleportManager;
        this.messageHandler = messageHandler;
        this.worldManager = worldManager;
        this.islandManager = islandManager;
        this.playerDatabase = playerDatabase;
        this.serverDatabases = serverDatabases;

        this.playerArgument.setSuggestionCallback((s, _, suggestion) -> {
            MinecraftServer.getConnectionManager().getOnlinePlayers().stream()
                    .filter(p -> !s.equals(p))
                    .forEach(p -> suggestion.addEntry(new SuggestionEntry(p.getUsername())));
        });

        this.addSyntax(this::create, ArgumentType.Literal("create"));
        this.addSyntax(this::go, ArgumentType.Literal("go"));
        this.addSyntax(this::invite, ArgumentType.Literal("invite"), this.playerArgument);
        this.addSyntax(this::kick, ArgumentType.Literal("kick"), this.playerArgument);
        this.addSyntax(this::deleteIsland, ArgumentType.Literal("delete"));
        this.addSyntax(this::deleteIsland, ArgumentType.Literal("delete"), ArgumentType.Literal("confirm"));
        this.addSyntax(this::join, ArgumentType.Literal("join"), this.playerArgument);
        this.addSyntax(this::leave, ArgumentType.Literal("leave"));
    }

    private void create(CommandSender sender, CommandContext context) {
        if (!(sender instanceof final DSPlayer player)) {
            return;
        }
        this.islandManager.createIsland(player).
                thenCompose(island -> {
                    if (island == null) {
                        return CompletableFuture.completedFuture(null);
                    }
                    return this.worldManager.loadWorld(island.islandId(), WorldType.ISLAND);
                })
                .thenCompose(world -> {
                    if (world == null) {
                        return CompletableFuture.completedFuture(null);
                    }
                    if (!(world instanceof final IslandWorld islandWorld)) {
                        this.messageHandler.sendMessage(player, Messages.ISLAND_CREATION_FAILED, Map.of("error-code", "world-missing"));
                        return CompletableFuture.completedFuture(null);
                    }
                    final ItemDefinition itemDefinition = world.itemFactory().getItemDefinition(ItemIds.STARTING_CACHE);
                    if (itemDefinition != null) {
                        InventoryUtil.addItemToInventory(player, itemDefinition.defaultItemStack(), player.getInstance(), player.getPosition());
                    }
                    CompletableFuture<Chunk> previous = null;
                    final Point spawn = world.getSpawnPointFor(player);
                    for (int x = -1; x <= 1; x++) {
                        for (int z = -1; z <= 1; z++) {
                            final Point chunkPos = spawn.add(x, 0, z);
                            if (previous == null) {
                                previous = world.loadChunk(chunkPos);
                            } else {
                                previous = previous.thenCompose(_ -> world.loadChunk(chunkPos));
                            }
                        }
                    }

                    world.asInstance().loadChunk(world.getSpawnPointFor(player));
                    return previous.thenApply(_ -> islandWorld.island());
                })
                .whenComplete((island, exception) -> {
                    if (exception != null) {
                        DSLogger.getLogger().severe(exception);
                        this.messageHandler.sendMessage(player, Messages.ISLAND_CREATION_FAILED, Map.of("error-code", "world-missing"));
                    }
                    this.teleportManager.teleport(player, TeleportLocation.Type.ISLAND, island.islandId(), island.getWorldId(WorldType.ISLAND), island.getSpawnPosition(), WorldType.ISLAND);
                });
    }

    private void go(CommandSender sender, CommandContext context) {
        if (!(sender instanceof final DSPlayer player)) {
            return;
        }
        final UUID islandId = player.getIslandId();
        if (islandId == null) {
            this.messageHandler.sendMessage(player, Messages.HAS_NO_ISLAND);
            return;
        }
        this.islandManager.loadOrGet(islandId).
                thenAccept(island -> {
                    if (island == null) {
                        this.messageHandler.sendMessage(player, Messages.ISLAND_LOAD_FAILED, Map.of("error-code", "island-missing"));
                        return;
                    }
                    this.worldManager.loadWorld(island.islandId(), WorldType.ISLAND)
                            .whenComplete((world, exception) -> {
                                if (world == null || exception != null) {
                                    this.messageHandler.sendMessage(player, Messages.ISLAND_LOAD_FAILED, Map.of("error-code", "world-missing"));
                                    return;
                                }
                                world.asInstance().loadChunk(world.getSpawnPointFor(player));
                                this.teleportManager.teleport(player, TeleportLocation.Type.ISLAND, island.islandId(), island.getWorldId(WorldType.ISLAND), island.getSpawnPosition(), WorldType.ISLAND);
                            });
                }).whenComplete((_, error) -> {
                    if (error != null) {
                        this.messageHandler.sendMessage(player, Messages.ISLAND_LOAD_FAILED, Map.of("error-code", "exception-thrown"));
                        if (error instanceof final CompletionException completionException) {
                            DSLogger.getLogger().severe(completionException.getCause());
                        } else {
                            DSLogger.getLogger().severe(error);
                        }
                    }
                });
    }

    private void invite(CommandSender sender, CommandContext context) {
        if (!(sender instanceof final DSPlayer player)) {
            return;
        }
        final UUID islandId = player.getIslandId();
        if (islandId == null) {
            this.messageHandler.sendMessage(player, Messages.HAS_NO_ISLAND);
            return;
        }
        final String invitedName = context.get(this.playerArgument);
        if (player.getUsername().equalsIgnoreCase(invitedName)) {
            this.messageHandler.sendMessage(player, Messages.INVITE_ALREADY_MEMBER, Map.of("player", invitedName));
            return;
        }
        final DSPlayer invited = (DSPlayer) MinecraftServer.getConnectionManager().getOnlinePlayerByUsername(invitedName);
        if (invited == null) {
            this.messageHandler.sendMessage(player, Messages.PLAYER_NOT_FOUND, Map.of("player", invitedName));
            return;
        }
        if (islandId.equals(invited.getIslandId())) {
            this.messageHandler.sendMessage(player, Messages.INVITE_ALREADY_MEMBER, Map.of("player", invitedName));
            return;
        }
        this.islandManager.loadOrGet(islandId).
                thenAccept(island -> {
                    if (island == null) {
                        this.messageHandler.sendMessage(player, Messages.ISLAND_LOAD_FAILED, Map.of("error-code", "island-missing"));
                        return;
                    }
                    final boolean inviteSuccessful = island.invite(player.getUuid(), invited.getUuid());
                    if (!inviteSuccessful) {
                        this.messageHandler.sendMessage(player, Messages.INVITE_ALREADY_EXISTS, Map.of("player", invitedName));
                        return;
                    }
                    this.messageHandler.sendMessage(player, Messages.INVITE_SENT, Map.of("player", invitedName));
                    this.messageHandler.sendMessage(invited, Messages.INVITE_RECEIVED, Map.of("island", island.displayName(), "player", player.getDisplayName()));
                });
    }

    private void join(CommandSender sender, CommandContext context) {
        if (!(sender instanceof final DSPlayer player)) {
            return;
        }
        if (player.hasIsland()) {
            this.messageHandler.sendMessage(player, Messages.ALREADY_HAS_ISLAND);
            return;
        }
        final String ownerName = context.get(this.playerArgument);
        final DSPlayer owner = (DSPlayer) MinecraftServer.getConnectionManager().getOnlinePlayerByUsername(ownerName);
        if (owner == null) {
            this.messageHandler.sendMessage(player, Messages.PLAYER_NOT_FOUND, Map.of("player", ownerName));
            return;
        }
        final UUID islandId = owner.getIslandId();
        if (!owner.hasIsland() || islandId == null) {
            this.messageHandler.sendMessage(player, Messages.INVITE_NOT_FOUND, Map.of("player", ownerName));
            return;
        }
        this.islandManager.loadOrGet(islandId).
                thenAccept(island -> {
                    if (island == null) {
                        this.messageHandler.sendMessage(player, Messages.ISLAND_LOAD_FAILED, Map.of("error-code", "island-missing"));
                        return;
                    }
                    if (!island.isInvited(player.getUuid())) {
                        this.messageHandler.sendMessage(player, Messages.INVITE_NOT_FOUND, Map.of("island", island.displayName()));
                        return;
                    }
                    if (!island.acceptInvite(player.getUuid())) {
                        this.messageHandler.sendMessage(player, Messages.INVITE_NOT_FOUND, Map.of("island", island.displayName()));
                        return;
                    }
                    player.setIslandId(islandId);
                    this.messageHandler.sendMessage(player, Messages.RECEIVED_INVITE_ACCEPTED, Map.of("player", owner.getDisplayName(), "island", island.displayName()));
                    this.messageHandler.sendMessage(owner, Messages.SENT_INVITE_ACCEPTED, Map.of("player", player.getDisplayName(), "island", island.displayName()));
                })
                .whenComplete((_, e) -> {
                    if (e != null) {
                        DSLogger.getLogger().severe(e);
                    }
                });
    }

    private void leave(CommandSender sender, CommandContext context) {
        if (!(sender instanceof final DSPlayer player)) {
            return;
        }
        final UUID islandId = player.getIslandId();
        if (!player.hasIsland() || islandId == null) {
            this.messageHandler.sendMessage(player, Messages.ISLAND_NOT_FOUND);
            return;
        }
        this.islandManager.loadOrGet(islandId).
                thenAccept(island -> {
                    if (island == null) {
                        this.messageHandler.sendMessage(player, Messages.ISLAND_LOAD_FAILED, Map.of("error-code", "island-missing"));
                        return;
                    }
                    if (island.getIslandRole(player.getUuid()) == IslandRole.OWNER) {
                        this.messageHandler.sendMessage(player, Messages.OWNER_CANNOT_LEAVE);
                        return;
                    }
                    final boolean left = island.leaveIsland(player.getUuid());
                    if (!left) {
                        player.sendMessage(Component.text("There was an error leaving your island, please contact a server staff member.").color(NamedTextColor.RED));
                        return;
                    }
                    player.setIslandId(null);
                    this.worldManager.getLobbyWorld().thenAccept(world -> TeleportUtil.teleportToSpawn(player, world));
                    this.playerDatabase.saveData(player.getUuid(), player.createSnapshot());
                    this.messageHandler.sendMessage(player, Messages.SELF_LEFT_ISLAND);
                    island.getMembers().stream().map(MinecraftServer.getConnectionManager()::getOnlinePlayerByUuid)
                            .filter(Objects::nonNull)
                            .forEach(p -> this.messageHandler.sendMessage(p, Messages.MEMBER_LEFT_ISLAND, Map.of("player", player.getUsername())));
                });
    }

    private void kick(CommandSender sender, CommandContext context) {
        if (!(sender instanceof final DSPlayer player)) {
            return;
        }
        final UUID islandId = player.getIslandId();
        if (!player.hasIsland() || islandId == null) {
            this.messageHandler.sendMessage(player, Messages.ISLAND_NOT_FOUND);
            return;
        }
        final String kickedName = context.get(this.playerArgument);
        final UUID kickedUUID = this.serverDatabases.playerUUIDDatabase().getPlayerUUID(kickedName).join();
        if (kickedUUID == null) {
            this.messageHandler.sendMessage(player, Messages.PLAYER_NOT_FOUND, Map.of("player", kickedName));
            return;
        }
        this.islandManager.loadOrGet(islandId).
                thenAccept(island -> {
                    if (island == null) {
                        this.messageHandler.sendMessage(player, Messages.ISLAND_LOAD_FAILED, Map.of("error-code", "island-missing"));
                        return;
                    }
                    final IslandRole kickedRole = island.getIslandRole(kickedUUID);
                    final IslandRole memberRole = island.getIslandRole(player.getUuid());
                    if (!memberRole.isHigherThan(kickedRole)) {
                        this.messageHandler.sendMessage(player, Messages.NO_PERMISSION_TO_KICK_MEMBER, Map.of("player", kickedName));
                        return;
                    }
                    final boolean left = island.leaveIsland(kickedUUID);
                    if (!left) {
                        if (island.removeInvite(player.getUuid(), kickedUUID)) {
                            this.messageHandler.sendMessage(player, Messages.SENT_INVITE_CANCELLED);
                            final DSPlayer onlinePlayer = (DSPlayer) MinecraftServer.getConnectionManager().getOnlinePlayerByUuid(kickedUUID);
                            if (onlinePlayer != null) {
                                this.messageHandler.sendMessage(onlinePlayer, Messages.RECEIVED_INVITE_CANCELLED);
                            }
                            return;
                        }
                        this.messageHandler.sendMessage(player, Messages.NOT_ISLAND_MEMBER, Map.of("player", kickedName));
                        return;
                    }
                    final DSPlayer onlinePlayer = (DSPlayer) MinecraftServer.getConnectionManager().getOnlinePlayerByUuid(kickedUUID);
                    if (onlinePlayer != null) {
                        onlinePlayer.setIslandId(null);
                        this.playerDatabase.saveData(kickedUUID, onlinePlayer.createSnapshot());
                        this.messageHandler.sendMessage(onlinePlayer, Messages.RECEIVED_KICK_FROM_ISLAND, Map.of("player", player.getUsername(), "island", island.displayName()));
                        this.worldManager.getLobbyWorld().thenAccept(world -> TeleportUtil.teleportToSpawn(onlinePlayer, world));
                    } else {
                        final DSPlayerData playerData = this.playerDatabase.loadDataNow(kickedUUID);
                        if (playerData == null) {
                            this.messageHandler.sendMessage(player, Messages.NOT_ISLAND_MEMBER, Map.of("player", kickedName));
                            return;
                        }
                        final DSPlayerData newData = new DSPlayerData(playerData.uuid(), null, playerData.inventory(), playerData.logoutPos());
                        this.playerDatabase.saveDataNow(kickedUUID, newData);
                    }
                    this.messageHandler.sendMessage(player, Messages.SENT_KICK_FROM_ISLAND, Map.of("player", kickedName));
                });
    }

    private void deleteIsland(CommandSender sender, CommandContext context) {
        if (!(sender instanceof final DSPlayer player)) {
            return;
        }
        final UUID islandId = player.getIslandId();
        if (!player.hasIsland() || islandId == null) {
            this.messageHandler.sendMessage(player, Messages.ISLAND_NOT_FOUND);
            return;
        }
        this.islandManager.loadOrGet(islandId).
                thenAccept(island -> {
                    if (island == null) {
                        this.messageHandler.sendMessage(player, Messages.ISLAND_LOAD_FAILED, Map.of("error-code", "island-missing"));
                        return;
                    }
                    final IslandRole role = island.getIslandRole(player.getUuid());
                    if (role != IslandRole.OWNER) {
                        this.messageHandler.sendMessage(player, Messages.CANNOT_DELETE_ISLAND);
                        return;
                    }
                    if (!context.has("confirm")) {
                        this.messageHandler.sendMessage(player, Messages.CONFIRM_DELETE_ISLAND);
                        return;
                    }
                    final Collection<UUID> members = island.getMembers();
                    final boolean deleted = this.islandManager.deleteIsland(island);
                    if (!deleted) {
                        player.sendMessage(Component.text("There was an error deleting your island, please contact an administrator.").color(NamedTextColor.RED));
                        return;
                    }
                    members.forEach(id -> {
                        final DSPlayer online = (DSPlayer) MinecraftServer.getConnectionManager().getOnlinePlayerByUuid(id);
                        if (online != null) {
                            online.setIslandId(null);
                            this.playerDatabase.saveDataNow(online.getUuid(), online.createSnapshot());
                            this.messageHandler.sendMessage(online, Messages.DELETED_ISLAND);
                        } else {
                            final DSPlayerData data = this.playerDatabase.loadDataNow(id);
                            if (data != null) {
                                this.playerDatabase.saveDataNow(id, new DSPlayerData(data.uuid(), null, data.inventory(), data.logoutPos()));
                            }
                        }
                    });
                    for (final WorldType worldType : WorldType.values()) {
                        if (worldType.hubWorld()) {
                            continue;
                        }
                        this.worldManager.deleteWorld(island.getWorldId(worldType));
                    }
                });
    }
}
