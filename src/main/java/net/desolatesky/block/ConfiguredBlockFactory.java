package net.desolatesky.block;

import net.desolatesky.block.behavior.BlockBehavior;
import net.desolatesky.block.behavior.BlockDropBehavior;
import net.desolatesky.block.behavior.MiningSpeedBehavior;
import net.desolatesky.block.behavior.core.VoidCoreBehavior;
import net.desolatesky.block.behavior.impl.BarrelBehavior;
import net.desolatesky.block.behavior.impl.BlockEntityBehavior;
import net.desolatesky.block.behavior.impl.CactusBehavior;
import net.desolatesky.block.behavior.impl.CactusFlowerBehavior;
import net.desolatesky.block.behavior.impl.ComposterBehavior;
import net.desolatesky.block.behavior.impl.CraftingTableBehavior;
import net.desolatesky.block.behavior.impl.CropBehavior;
import net.desolatesky.block.behavior.impl.DryGrassBehavior;
import net.desolatesky.block.behavior.impl.FireBehavior;
import net.desolatesky.block.behavior.impl.SupportedBlockBehavior;
import net.desolatesky.block.behavior.impl.WoodPlanksBehavior;
import net.desolatesky.block.behavior.serializer.BlockBehaviorSerializer;
import net.desolatesky.block.definition.BlockDefinition;
import net.desolatesky.block.definition.BlockDefinitionBuilder;
import net.desolatesky.config.ConfigFile;
import net.desolatesky.logging.DSLogger;
import net.desolatesky.util.BlockUtil;
import net.kyori.adventure.key.Key;
import net.minestom.server.MinecraftServer;
import net.minestom.server.instance.block.Block;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public final class ConfiguredBlockFactory implements BlockFactory {

    private static final List<Path> DEFAULT_CONFIG_FILES = List.of(
            Path.of("simple_blocks.conf"),
            Path.of("special_blocks.conf")
    );

    private final Map<Key, BlockDefinition> blocks;
    private final Map<Key, BlockBehaviorSerializer<? extends BlockBehavior>> blockBehaviorSerializers;

    private final Path folderPath;

    public ConfiguredBlockFactory(Path folderPath) {
        this.folderPath = folderPath;
        this.blocks = new HashMap<>();
        this.blockBehaviorSerializers = new HashMap<>();
    }

    public <T extends BlockBehavior> void registerBlockBehaviorSerializer(BlockBehaviorSerializer<T> serializer) {
        this.blockBehaviorSerializers.put(serializer.key(), serializer);
    }

    private void load() throws IOException {
        this.registerBlockBehaviorSerializer(new WoodPlanksBehavior.Serializer());
        this.registerBlockBehaviorSerializer(new SupportedBlockBehavior.Serializer());
        this.registerBlockBehaviorSerializer(new DryGrassBehavior.Serializer());
        this.registerBlockBehaviorSerializer(new CropBehavior.Serializer());
        this.registerBlockBehaviorSerializer(new CraftingTableBehavior.Serializer());
        this.registerBlockBehaviorSerializer(new ComposterBehavior.Serializer());
        this.registerBlockBehaviorSerializer(new CactusFlowerBehavior.Serializer());
        this.registerBlockBehaviorSerializer(new CactusBehavior.Serializer());
        this.registerBlockBehaviorSerializer(new VoidCoreBehavior.Serializer());
        this.registerBlockBehaviorSerializer(new MiningSpeedBehavior.Serializer());
        this.registerBlockBehaviorSerializer(new BarrelBehavior.Serializer());
        this.registerBlockBehaviorSerializer(new BlockDropBehavior.Serializer());
        this.registerBlockBehaviorSerializer(new FireBehavior.Serializer());

        final List<Path> files = new ArrayList<>();
        DEFAULT_CONFIG_FILES.forEach(p -> files.add(this.folderPath.resolve(p)));
        try (final Stream<Path> walked = Files.walk(this.folderPath).filter(Files::isRegularFile)) {
            files.addAll(walked.toList());
        }
        for (final Path path : files) {
            final ConfigFile config = ConfigFile.get(path, "", builder -> builder.defaultOptions(options -> options.serializers(b -> {
                for (final BlockBehaviorSerializer<?> serializer : this.blockBehaviorSerializers.values()) {
                    @SuppressWarnings("unchecked") final BlockBehaviorSerializer<BlockBehavior> behavior = (BlockBehaviorSerializer<BlockBehavior>) serializer;
                    b.register(behavior.behaviorClass(), behavior);
                }
            })));
            final Map<Object, ? extends ConfigurationNode> children = config.rootNode().childrenMap();
            try {
                for (final var entry : children.entrySet()) {
                    if (!(entry.getKey() instanceof final String blockIdString)) {
                        DSLogger.getLogger().warn(entry.getKey() + " is not a valid block id.");
                        continue;
                    }
                    final ConfigurationNode node = entry.getValue();
                    final Key key = Key.key(blockIdString);
                    Key blockKey = node.node("block").get(Key.class);
                    if (blockKey == null) {
                        blockKey = key;
                    }
                    final Block block = Block.fromKey(blockKey);
                    if (block == null) {
                        DSLogger.getLogger().warn("Block for " + key.asString() + " is null");
                        continue;
                    }
                    final Map<String, String> properties = new HashMap<>();
                    final Map<Object, ? extends ConfigurationNode> propertiesNode = node.node("properties").childrenMap();

                    for (final var propertyEntry : propertiesNode.entrySet()) {
                        if (!(propertyEntry.getKey() instanceof final String property)) {
                            DSLogger.getLogger().warn(propertyEntry.getKey() + " is not a valid property for " + blockKey.asString());
                            continue;
                        }
                        final String value = propertyEntry.getValue().getString();
                        if (value == null) {
                            DSLogger.getLogger().warn("Property " + property + " has no value for block " + blockKey.asString());
                            continue;
                        }
                        properties.put(property, value);
                    }
                    final Set<Key> blockAttributes = new HashSet<>(node.node("attributes").getList(Key.class, new ArrayList<>()));
                    final Map<Object, ? extends ConfigurationNode> behaviorsMap = node.node("behaviors").childrenMap();
                    final BlockDefinitionBuilder.BlockBehaviorsStep blockDefinition = BlockDefinition.builder()
                            .key(key)
                            .defaultBlock(block.withProperties(properties))
                            .attributes(blockAttributes);
                    for (final var serializerEntry : behaviorsMap.entrySet()) {
                        if (!(serializerEntry.getKey() instanceof final String type)) {
                            DSLogger.getLogger().warn(serializerEntry.getKey() + " is not a valid block behavior serialize for block " + blockKey.asString());
                            continue;
                        }
                        final ConfigurationNode serializerNode = serializerEntry.getValue();
                        final Key serializerKey = Key.key(type);
                        final BlockBehaviorSerializer<?> serializer = this.blockBehaviorSerializers.get(serializerKey);
                        if (serializer == null) {
                            DSLogger.getLogger().warn("No block serializer found for type " + type);
                            continue;
                        }
                        final BlockBehavior behavior = serializer.deserialize(serializer.behaviorClass(), serializerNode);
                        blockDefinition.defineBehaviors(behavior);
                    }
                    this.blocks.put(key, blockDefinition.build());
                }
            } catch (SerializationException e) {
                DSLogger.getLogger().severe(e);
            }
        }
    }

    @Override
    public void initialize() {
        try {
            this.load();
        } catch (IOException e) {
            DSLogger.getLogger().severe(e);
        }

        this.blocks.forEach((key, definition) -> {
             final BlockEntityBehavior blockEntityBehavior = definition.getBehavior(BlockBehavior.Type.BLOCK_ENTITY);
            if (blockEntityBehavior == null) {
                return;
            }
            MinecraftServer.getBlockManager().registerHandler(blockEntityBehavior.blockEntityId(), blockEntityBehavior::createBlockHandler);
        });
    }

    private void register(BlockDefinition definition) {
        this.blocks.put(definition.key(), definition);
    }

    @Override
    public @Nullable BlockDefinition getBlockDefinition(Key id) {
        return this.blocks.get(id);
    }

    @Override
    public @Nullable BlockDefinition getBlockDefinition(Block block) {
        return this.getBlockDefinition(this.getBlockId(block));
    }

    @Override
    public Key getBlockId(Block block) {
        return BlockUtil.getBlockId(block);
    }
}
