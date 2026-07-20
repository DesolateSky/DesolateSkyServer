package net.desolatesky.advancement;

import net.desolatesky.advancement.impl.CreateIslandAdvancement;
import net.desolatesky.advancement.impl.RootAdvancement;
import net.desolatesky.config.ConfigFile;
import net.desolatesky.config.ConfigNode;
import net.desolatesky.util.ComponentUtil;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.minestom.server.advancements.FrameType;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.Nullable;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class IslandAdvancements {

    public static IslandAdvancements load(ConfigFile file) {
        try {
            final ConfigNode root = file.rootNode();
            final String name = Objects.requireNonNull(root.node("name").getString());
            if (!root.hasChild("index")) {
                throw new IllegalStateException();
            }
            final int index = root.node("index").getInt();
            final ConfigNode advancementsNode = root.node("advancements");
            final Map<Key, IslandAdvancement> advancementsMap = new HashMap<>();
            Key rootAdvancement = null;
            for (final Map.Entry<Object, ConfigNode> entry : advancementsNode.childrenMap().entrySet()) {
                if (!(entry.getKey() instanceof final String advancementString)) {
                    throw new IllegalStateException("Invalid advancement for " + name + ": " + entry.getKey());
                }
                final ConfigNode node = entry.getValue();
                final String advancementTypeString = node.node("type").getString();
                if (advancementTypeString == null) {
                    throw new IllegalStateException("Mo advancement type found for " + name);
                }
                final AdvancementType type = AdvancementType.valueOf(Objects.requireNonNull(node.node("type").getString()).toUpperCase());
                final IslandAdvancement islandAdvancement = loadAdvancement(type, Key.key(advancementString), node);
                advancementsMap.put(islandAdvancement.key(), islandAdvancement);
                if (type == AdvancementType.ROOT) {
                    rootAdvancement = islandAdvancement.key();
                }
            }
            if (rootAdvancement == null) {
                throw new IllegalStateException("All advancements require a root");
            }
            return new IslandAdvancements(Key.key(name), index, rootAdvancement, advancementsMap);
        } catch (SerializationException e) {
            throw new RuntimeException(e);
        }
    }

    private static IslandAdvancement loadAdvancement(AdvancementType type, Key advancementId, ConfigNode node) throws SerializationException {
        final List<Key> children = new ArrayList<>();
        final List<String> childrenStrings = node.node("children").getList(String.class);
        if (childrenStrings == null) {
            throw new IllegalStateException();
        }
        final Component title = ComponentUtil.parse(Objects.requireNonNull(node.node("title").getString()));
        final Component description = ComponentUtil.parse(Objects.requireNonNull(node.node("description").getString()));
        final Material icon = Objects.requireNonNull(Material.fromKey(Key.key(Objects.requireNonNull(node.node("icon").getString()).toLowerCase())));
        final FrameType frameType = FrameType.valueOf(Objects.requireNonNull(node.node("frame").getString()));
        final float x = node.node("x").getFloat();
        final float y = node.node("y").getFloat();
        for (final String string : childrenStrings) {
            children.add(Key.key(string));
        }
        return switch (type) {
            case ROOT -> {
                final String background = node.node("background").getString();
                yield new RootAdvancement(title, description, icon, frameType, x, y, background, advancementId, children);
            }
            case CREATE_ISLAND ->
                    new CreateIslandAdvancement(title, description, icon, frameType, x, y, advancementId, children);
        };
    }

    private final Key key;
    private final int index;
    private final Key rootAdvancement;
    private final @Unmodifiable Map<Key, IslandAdvancement> islandAdvancements;

    public IslandAdvancements(Key key, int index, Key rootAdvancement, Map<Key, IslandAdvancement> islandAdvancements) {
        this.key = key;
        this.index = index;
        this.rootAdvancement = rootAdvancement;
        this.islandAdvancements = Map.copyOf(islandAdvancements);
    }

    public Key key() {
        return this.key;
    }

    public int index() {
        return this.index;
    }

    public Key rootAdvancement() {
        return this.rootAdvancement;
    }

    public @Nullable IslandAdvancement getAdvancement(Key key) {
        return this.islandAdvancements.get(key);
    }
}
