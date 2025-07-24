package net.desolatesky.database;

import net.desolatesky.config.ConfigNode;
import org.spongepowered.configurate.ConfigurateException;

public record MongoSettings(
        String url,
        String databaseName
) {

    public static MongoSettings fromConfig(ConfigNode config) throws ConfigurateException {
        final ConfigNode connectionNode = config.node("connection");
        final String url = connectionNode.node("url").getNonNull(String.class);
        final String databaseName = connectionNode.node("database").getNonNull(String.class);
        return new MongoSettings(url, databaseName);
    }
}
