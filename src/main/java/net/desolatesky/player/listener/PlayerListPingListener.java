package net.desolatesky.player.listener;

import net.desolatesky.Listener;
import net.desolatesky.config.ConfigFile;
import net.desolatesky.config.ConfigNode;
import net.desolatesky.util.ComponentUtil;
import net.desolatesky.util.ResourceLoader;
import net.kyori.adventure.text.Component;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.server.ServerListPingEvent;
import net.minestom.server.ping.Status;
import org.jetbrains.annotations.NotNullByDefault;
import org.spongepowered.configurate.serialize.SerializationException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.file.Path;
import java.util.Objects;

@NotNullByDefault
public class PlayerListPingListener implements Listener<Event> {

    private final ConfigFile serverConfig;

    public PlayerListPingListener(ConfigFile serverConfig) {
        this.serverConfig = serverConfig;
    }

    @Override
    public void register(EventNode<Event> node) {
        this.registerServerListPing(node);
    }

    private void registerServerListPing(EventNode<Event> node) {
        try {
            final ConfigNode config = this.serverConfig.rootNode();
            final Component motd = Objects.requireNonNull(config.node("motd").getList(String.class))
                    .stream()
                    .map(ComponentUtil::parse)
                    .reduce(null, (f, s) -> {
                        if (f == null) {
                            return s;
                        }
                        return f.appendNewline().append(s);
                    });
            final byte[] favicon = loadFavicon();
            node.addListener(ServerListPingEvent.class, e -> {
                final Status status = new Status(motd,
                        favicon,
                        Status.VersionInfo.DEFAULT,
                        Status.PlayerInfo.online(10),
                        false
                );
                e.setStatus(status);
            });
        } catch (SerializationException e) {
            throw new RuntimeException(e);
        }
    }

    private static byte[] loadFavicon() {
        final File file = ResourceLoader.load(Path.of("server-icon.png"), "/server-icon.png");
        try {
            final BufferedImage image = ImageIO.read(file);
            if (image == null) {
                throw new IllegalStateException("Failed to read server icon image");
            }
            final Raster raster = image.getData();
            final int width = raster.getWidth();
            final int height = raster.getHeight();
            if (width != 64 || height != 64) {
                throw new IllegalStateException("Server icon must be 64x64 pixels");
            }
            final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(image, "PNG", outputStream);
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to load favicon", e);
        }
    }
}
