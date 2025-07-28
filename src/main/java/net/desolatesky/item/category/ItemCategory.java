package net.desolatesky.item.category;

import net.desolatesky.block.handler.DSBlockHandler;
import net.desolatesky.category.Category;
import net.kyori.adventure.key.Key;

import java.util.Collection;
import java.util.Collections;

public record ItemCategory(Key key, Collection<Category> appliesTo) implements Category {

    public ItemCategory(Key key) {
        this(key , Collections.emptySet());
    }

    public boolean isApplicableTo(Category category) {
        return this.appliesTo.contains(category);
    }

    public boolean appliesTo(DSBlockHandler blockHandler) {
        for (final Category category : this.appliesTo) {
            if (blockHandler.isCategory(category)) {
                return true;
            }
        }
        return false;
    }

}
