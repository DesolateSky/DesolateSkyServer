package net.desolatesky.config;

import net.desolatesky.config.serializer.BuiltInTypeSerializers;
import net.desolatesky.util.ResourceLoader;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;

import java.io.File;
import java.nio.file.Path;
import java.util.function.Function;

public class ConfigFile {

    protected final Path filePath;
    protected final String resourcePath;
    private final Function<HoconConfigurationLoader.Builder, HoconConfigurationLoader.Builder> loaderBuilderFunction;
    protected ConfigNode rootNode;

    public static ConfigFile get(Path filePath, String resourcePath) {
        return get(filePath, resourcePath, Function.identity());
    }

    public static ConfigFile get(Path filePath, String resourcePath, Function<HoconConfigurationLoader.Builder, HoconConfigurationLoader.Builder> loaderBuilderFunction) {
        final ConfigFile configFile = new ConfigFile(filePath, resourcePath, loaderBuilderFunction);
        configFile.load();
        final HoconConfigurationLoader.Builder builder = HoconConfigurationLoader.builder().path(filePath);
        builder.defaultOptions(opt -> opt.serializers(BuiltInTypeSerializers::registerToLoader));
        final HoconConfigurationLoader loader = loaderBuilderFunction.apply(builder)
                .build();
        try {
            configFile.rootNode = new ConfigNode(loader.load());
        } catch (Exception e) {
            throw new RuntimeException("Failed to load configuration from " + filePath, e);
        }
        return configFile;
    }

    private ConfigFile(Path filePath, String resourcePath, Function<HoconConfigurationLoader.Builder, HoconConfigurationLoader.Builder> loaderBuilderFunction) {
        this.filePath = filePath;
        this.resourcePath = resourcePath;
        this.loaderBuilderFunction = loaderBuilderFunction;
    }

    private void load() {
        ResourceLoader.load(this.filePath, this.resourcePath, this.getClass());
    }

    public void save() {
        final HoconConfigurationLoader loader = HoconConfigurationLoader.builder()
                .path(this.filePath)
                .build();
        try {
            loader.save(this.rootNode);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save configuration from " + this.filePath, e);
        }
    }

    public void reload() {
        this.load();
        final HoconConfigurationLoader loader = this.loaderBuilderFunction.apply(
                        HoconConfigurationLoader.builder()
                                .path(this.filePath)
                )
                .build();
        try {
            this.rootNode = new ConfigNode(loader.load());
        } catch (Exception e) {
            throw new RuntimeException("Failed to load configuration from " + this.filePath, e);
        }
    }

    public File getFile() {
        return this.filePath.toFile();
    }

    public ConfigNode rootNode() {
        return this.rootNode;
    }
}