package net.desolatesky.config;

import net.desolatesky.util.ResourceLoader;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;

import java.io.File;
import java.nio.file.Path;
import java.util.function.Function;

public class ConfigFile {

    protected final Path filePath;
    protected final String resourcePath;
    protected ConfigNode rootNode;

    public static ConfigFile get(Path filePath, String resourcePath) {
        return get(filePath, resourcePath, Function.identity());
    }

    public static ConfigFile get(Path filePath, String resourcePath, Function<HoconConfigurationLoader.Builder, HoconConfigurationLoader.Builder> loaderBuilderFunction) {
        final ConfigFile configFile = new ConfigFile(filePath, resourcePath);
        configFile.load();
        final HoconConfigurationLoader loader = loaderBuilderFunction.apply(
                        HoconConfigurationLoader.builder()
                                .path(filePath)
                )
                .build();
        try {
            configFile.rootNode = new ConfigNode(loader.load());
        } catch (Exception e) {
            throw new RuntimeException("Failed to load configuration from " + filePath, e);
        }
        return configFile;
    }

    private ConfigFile(Path filePath, String resourcePath) {
        this.filePath = filePath;
        this.resourcePath = resourcePath;
    }

    private void load() {
        ResourceLoader.load(this.filePath, this.resourcePath, this.getClass());
    }

    public File getFile() {
        return this.filePath.toFile();
    }

    public ConfigNode rootNode() {
        return this.rootNode;
    }

}
