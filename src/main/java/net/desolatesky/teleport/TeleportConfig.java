package net.desolatesky.teleport;

import net.desolatesky.config.ConfigFile;
import net.desolatesky.util.Namespace;
import net.kyori.adventure.key.Key;
import org.spongepowered.configurate.ConfigurationNode;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public final class TeleportConfig {

    private final ConfigFile config;
    private final Map<Key, Integer> teleportTimes;

    private TeleportConfig(ConfigFile config, Map<Key, Integer> teleportTimes) {
        this.config = config;
        this.teleportTimes = teleportTimes;
    }

    public int getTeleportTicks(Key key, int defaultTicks) {
        return this.teleportTimes.getOrDefault(key, defaultTicks);
    }

    public int getTeleportTicks(Key key) {
        return this.getTeleportTicks(key, 0);
    }

    public static TeleportConfig load(Path filePath, String resourcePath) {
        final Map<Key, Integer> teleportTimes = new HashMap<>();
        final ConfigFile configFile = ConfigFile.get(filePath, resourcePath, Function.identity());
        final ConfigurationNode root = configFile.rootNode();
        for (final ConfigurationNode node : root.childrenMap().values()) {
            if (!(node.key() instanceof final String keyString)) {
                continue;
            }
            final int ticks = node.getInt();
            final Key key = Namespace.key(keyString);
            teleportTimes.put(key, ticks);
        }
        return new TeleportConfig(configFile, teleportTimes);
    }

}
