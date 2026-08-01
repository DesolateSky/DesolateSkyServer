package net.desolatesky.block.behavior;

import net.desolatesky.block.behavior.serializer.BlockBehaviorSerializer;
import net.desolatesky.item.ItemFactory;
import net.desolatesky.logging.DSLogger;
import net.desolatesky.util.Namespace;
import net.desolatesky.world.DSWorld;
import net.kyori.adventure.key.Key;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface BlockDropBehavior extends BlockBehavior {

    final class Serializer extends BlockBehaviorSerializer<BlockDropBehavior> {

        public Serializer() {
            super(Namespace.key("block_drop"));
        }

        @Override
        public BlockDropBehavior deserialize(java.lang.reflect.Type type, ConfigurationNode node) {
            final Map<Object, ? extends ConfigurationNode> itemsMap = node.node("drops").childrenMap();
            final Map<Key, Integer> drops = new HashMap<>();
            for (final var entry : itemsMap.entrySet()) {
                if (!(entry.getKey() instanceof final String itemIdString)) {
                    DSLogger.getLogger().warn(entry.getKey() + " is an invalid item");
                    continue;
                }
                final Key itemKey = Key.key(itemIdString);
                final int amount = entry.getValue().getInt();
                drops.put(itemKey, amount);
            }
            return constantDrops(drops);
        }

        @Override
        public void serialize(java.lang.reflect.Type type, @org.jspecify.annotations.Nullable BlockDropBehavior obj, ConfigurationNode node) throws SerializationException {
        }

        @Override
        public Class<BlockDropBehavior> behaviorClass() {
            return BlockDropBehavior.class;
        }
    }

    Collection<ItemStack> getDrops(
            DSWorld world,
            Point pos,
            Block block,
            Key blockId,
            ItemFactory itemFactory,
            @Nullable ItemStack toolUsed
    );

    static BlockDropBehavior constantDrops(Map<Key, Integer> items) {
        return new BlockDropBehavior() {
            @Override
            public Collection<ItemStack> getDrops(DSWorld world, Point pos, Block block, Key blockId, ItemFactory itemFactory, @Nullable ItemStack toolUsed) {
                return items.entrySet().stream().map(entry -> {
                    final Key key = entry.getKey();
                    final Integer value = entry.getValue();
                    final ItemStack itemStack = itemFactory.getDefaultItem(key);
                    if (itemStack == null) {
                        return null;
                    }
                    return itemStack.withAmount(value);
                }).toList();
            }

            @Override
            public Collection<Type<?>> types() {
                return List.of(Type.BLOCK_DROP);
            }
        };
    }

}
