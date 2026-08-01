package net.desolatesky.block.behavior.impl;

import net.desolatesky.block.behavior.BlockDropBehavior;
import net.desolatesky.block.behavior.ClickBehavior;
import net.desolatesky.block.behavior.MiningSpeedBehavior;
import net.desolatesky.block.behavior.serializer.BlockBehaviorSerializer;
import net.desolatesky.crafting.CraftingInventory;
import net.desolatesky.item.ItemFactory;
import net.desolatesky.item.definition.ItemDefinition;
import net.desolatesky.player.DSPlayer;
import net.desolatesky.util.Namespace;
import net.desolatesky.world.DSWorld;
import net.kyori.adventure.key.Key;
import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.PlayerHand;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public final class CraftingTableBehavior implements ClickBehavior {

    public static final class Serializer extends BlockBehaviorSerializer<CraftingTableBehavior> {

        public Serializer() {
            super(Namespace.minecraftKey("crafting_table"));
        }

        @Override
        public CraftingTableBehavior deserialize(java.lang.reflect.Type type, ConfigurationNode node) {
            return new CraftingTableBehavior();
        }

        @Override
        public void serialize(java.lang.reflect.Type type, @org.jspecify.annotations.Nullable CraftingTableBehavior obj, ConfigurationNode node) throws SerializationException {
        }

        @Override
        public Class<CraftingTableBehavior> behaviorClass() {
            return CraftingTableBehavior.class;
        }
    }

    @Override
    public Result onRightClick(DSWorld world, DSPlayer player, PlayerHand hand, Point clickedPos, Block clickedBlock, ItemStack clickedWith) {
        final CraftingInventory craftingInventory = new CraftingInventory(world, clickedPos);
        player.openInventory(craftingInventory);
        return Result.BLOCK_INTERACTION;
    }

    @Override
    public Result onLeftClick(DSWorld world, DSPlayer player, Point clickedPos, Block clickedBlock, ItemStack clickedWith) {
        return Result.ALLOW;
    }

    @Override
    public Collection<Type<?>> types() {
        return List.of(Type.CLICK);
    }
}
