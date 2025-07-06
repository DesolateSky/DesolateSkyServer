package net.desolatesky.cooldown;

import net.desolatesky.config.ConfigFile;
import net.desolatesky.util.Namespace;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;

import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public final class CooldownConfig {

    private final ConfigFile config;
    private final Map<Key, Cooldown> cooldowns;

    private CooldownConfig(ConfigFile config, Map<Key, Cooldown> cooldowns) {
        this.config = config;
        this.cooldowns = cooldowns;
    }

    public @Nullable CooldownData createCooldown(Key key) {
        final Cooldown cooldown = this.cooldowns.get(key);
        if (cooldown == null) {
            return null;
        }
        return new CooldownData(cooldown, Instant.now());
    }

    public static CooldownConfig load(Path filePath, String resourcePath) {
        final Map<Key, Cooldown> cooldowns = new HashMap<>();
        final ConfigFile configFile = ConfigFile.get(filePath, resourcePath, Function.identity());
        final ConfigurationNode root = configFile.rootNode();
        for (final ConfigurationNode node : root.childrenMap().values()) {
            if (!(node.key() instanceof final String keyString)) {
                continue;
            }
            final ChronoUnit unit = ChronoUnit.valueOf(node.node("unit").getString("SECONDS"));
            final long duration = node.node("duration").getLong(0);
            final Key key = Namespace.key(keyString);
            cooldowns.put(key, Cooldown.create(key, Duration.of(duration, unit)));
        }
        return new CooldownConfig(configFile, cooldowns);
    }

}
