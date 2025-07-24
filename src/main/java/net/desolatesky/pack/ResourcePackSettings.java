package net.desolatesky.pack;

import net.desolatesky.config.ConfigNode;
import org.spongepowered.configurate.ConfigurateException;

import java.net.URI;
import java.util.UUID;

public record ResourcePackSettings(UUID id, URI uri, String hash) {

    public static ResourcePackSettings fromConfig(ConfigNode node) {
        try {
            final UUID uuid = node.node("id").getUUID();
            final URI url = URI.create(node.node("uri").getNonNull(String.class));
            final String hash = node.node("hash").getNonNull(String.class);
            return new ResourcePackSettings(uuid, url, hash);
        } catch (ConfigurateException e) {
            throw new RuntimeException();
        }
    }


}
