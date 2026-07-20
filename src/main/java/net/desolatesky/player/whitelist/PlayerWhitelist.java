package net.desolatesky.player.whitelist;

import net.desolatesky.config.ConfigFile;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import org.spongepowered.configurate.serialize.SerializationException;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public final class PlayerWhitelist {

    private final Path filePath;
    private final String resourcePath;
    private boolean enabled;
    private final Set<UUID> whitelistedPlayers = new HashSet<>();

    public PlayerWhitelist(Path filePath, String resourcePath) {
        this.filePath = filePath;
        this.resourcePath = resourcePath;
    }

    public void load() throws SerializationException {
        final ConfigFile configFile = ConfigFile.get(this.filePath, this.resourcePath);
        this.enabled = configFile.rootNode().node("enabled").getBoolean();
        final List<String> list = configFile.rootNode().node("players").getList(String.class);
        if (list == null) {
            return;
        }
        for (final String string : list) {
            this.whitelistedPlayers.add(UUID.fromString(string));
        }
    }

    public void save() throws SerializationException {
        final ConfigFile configFile = ConfigFile.get(this.filePath, this.resourcePath);
        configFile.rootNode().node("enabled").set(this.enabled);
        configFile.rootNode().node("players").setList(String.class, this.whitelistedPlayers
                .stream()
                .map(UUID::toString)
                .toList());
        configFile.save();
    }

    public boolean addPlayer(UUID playerId) {
        final boolean added = this.whitelistedPlayers.add(playerId);
        if (added) {
            try {
                this.save();
            } catch (SerializationException e) {
                throw new RuntimeException(e);
            }
        }
        return added;
    }

    public boolean removePlayer(UUID playerId) {
        final boolean removed = this.whitelistedPlayers.remove(playerId);
        if (removed) {
            try {
                this.save();
            } catch (SerializationException e) {
                throw new RuntimeException(e);
            }
        }
        return removed;
    }

    public List<String> getWhitelistedPlayers() {
        return this.whitelistedPlayers.stream()
                .map(id -> {
                    final Player player = MinecraftServer.getConnectionManager().getOnlinePlayerByUuid(id);
                    if (player == null) {
                        return id.toString();
                    }
                    return player.getUsername();
                })
                .toList();
    }
}
