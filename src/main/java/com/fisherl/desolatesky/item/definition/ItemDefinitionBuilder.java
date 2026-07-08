package com.fisherl.desolatesky.item.definition;

import com.fisherl.desolatesky.item.ItemTags;
import com.fisherl.desolatesky.item.behavior.ItemBehavior;
import net.kyori.adventure.key.Key;
import net.minestom.server.item.ItemStack;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public final class ItemDefinitionBuilder {

    ItemDefinitionBuilder() {
    }

    public DefaultItemStep key(Key key) {
        return new DefaultItemStep(key);
    }

    public static final class DefaultItemStep {

        private final Key key;

        private DefaultItemStep(Key key) {
            this.key = key;
        }

        public ItemBehaviorsStep defaultItem(ItemStack defaultItem) {
            if (!this.key.namespace().equals(Key.MINECRAFT_NAMESPACE)) {
                return new ItemBehaviorsStep(this.key, defaultItem.withTag(ItemTags.ID, this.key));
            }
            return new ItemBehaviorsStep(this.key, defaultItem);
        }
    }

    public static final class ItemBehaviorsStep {

        private final Key key;
        private final ItemStack defaultItem;
        private final Map<ItemBehavior.Type<?>, ItemBehavior> blockBehaviors;

        private ItemBehaviorsStep(Key key, ItemStack defaultItem) {
            this.key = key;
            this.defaultItem = defaultItem;
            this.blockBehaviors = new HashMap<>();
        }

        public <T extends ItemBehavior> ItemBehaviorsStep defineBehavior(ItemBehavior.Type<? extends T> type, T blockBehavior) {
            if (this.blockBehaviors.containsKey(type)) {
                throw new IllegalArgumentException("Item behavior of type " + type.itemBehaviorClass().getName() + " is already defined.");
            }
            this.blockBehaviors.put(type, blockBehavior);
            return this;
        }

        public <T extends ItemBehavior> ItemBehaviorsStep defineBehaviors(Collection<ItemBehavior.Type<? extends T>> types, T blockBehavior) {
            types.forEach(type -> this.defineBehavior(type, blockBehavior));
            return this;
        }


        public ItemDefinition build() {
            return new ItemDefinition(this.key, this.defaultItem, this.blockBehaviors);
        }
    }
}
