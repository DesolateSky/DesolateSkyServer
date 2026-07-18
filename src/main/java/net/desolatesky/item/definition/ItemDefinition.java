package net.desolatesky.item.definition;

import net.desolatesky.item.behavior.ItemBehavior;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public final class ItemDefinition implements Keyed {

    public static ItemDefinitionBuilder builder() {
        return new ItemDefinitionBuilder();
    }

    private final Key key;
    private final ItemStack defaultItemStack;
//    private final BlockSettings blockSettings;
    private final Map<ItemBehavior.Type<? extends ItemBehavior>, ItemBehavior> itemBehaviors;

    ItemDefinition(Key key, ItemStack defaultItemStack, Map<ItemBehavior.Type<? extends ItemBehavior>, ItemBehavior> itemBehaviors) {
        this.key = key;
        this.defaultItemStack = defaultItemStack;
        this.itemBehaviors = Collections.unmodifiableMap(itemBehaviors);
    }

    public <T extends ItemBehavior> @Nullable T getBehavior(ItemBehavior.Type<T> behaviorType) {
        final ItemBehavior blockBehavior = this.itemBehaviors.get(behaviorType);
        if (!behaviorType.itemBehaviorClass().isInstance(blockBehavior)) {
            return null;
        }
        return behaviorType.itemBehaviorClass().cast(blockBehavior);
    }

    public @Unmodifiable Collection<ItemBehavior> itemBehaviors() {
        return this.itemBehaviors.values();
    }

    @Override
    public @NotNull Key key() {
        return this.key;
    }

    public ItemStack defaultItemStack() {
        return this.defaultItemStack;
    }
}
