package net.desolatesky.block.behavior.impl;

import net.desolatesky.block.behavior.BlockDropBehavior;
import net.desolatesky.block.behavior.MiningSpeedBehavior;
import net.desolatesky.block.behavior.serializer.BlockBehaviorSerializer;
import net.desolatesky.item.ItemFactory;
import net.desolatesky.item.definition.ItemDefinition;
import net.desolatesky.player.DSPlayer;
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
import java.util.Collections;
import java.util.List;

public final class WoodPlanksBehavior implements BlockDropBehavior {

    public static final class Serializer extends BlockBehaviorSerializer<WoodPlanksBehavior> {

        public Serializer() {
            super(Namespace.key("wood_planks"));
        }

        private static final String ITEM_KEY = "item";

        @Override
        public WoodPlanksBehavior deserialize(java.lang.reflect.Type type, ConfigurationNode node) throws SerializationException {
            final Key item = node.node(ITEM_KEY).get(Key.class);
            return new WoodPlanksBehavior(item);
        }

        @Override
        public void serialize(java.lang.reflect.Type type, @org.jspecify.annotations.Nullable WoodPlanksBehavior obj, ConfigurationNode node) throws SerializationException {
            if (obj == null) {
                return;
            }
            node.node(ITEM_KEY, obj.itemKey);
        }

        @Override
        public Class<WoodPlanksBehavior> behaviorClass() {
            return WoodPlanksBehavior.class;
        }
    }

    private final Key itemKey;

    public WoodPlanksBehavior(Key itemKey) {
        this.itemKey = itemKey;
    }

    @Override
    public Collection<ItemStack> getDrops(DSWorld world, Point pos, Block block, Key blockId, ItemFactory itemFactory, @Nullable ItemStack toolUsed) {
        final ItemDefinition itemDefinition = itemFactory.getItemDefinition(this.itemKey);
        if (itemDefinition == null) {
            return Collections.emptyList();
        }
        return List.of(itemDefinition.defaultItemStack());
    }

    @Override
    public Collection<Type<?>> types() {
        return List.of(Type.BLOCK_DROP);
    }
}
