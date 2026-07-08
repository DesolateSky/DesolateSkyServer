package com.fisherl.desolatesky.entity.type;

import com.fisherl.desolatesky.entity.DSItemEntity;
import com.fisherl.desolatesky.entity.EntityIds;
import com.fisherl.desolatesky.island.Island;
import com.fisherl.desolatesky.item.ItemTags;
import com.fisherl.desolatesky.loot.LootTable;
import com.fisherl.desolatesky.recipe.RecipeFactory;
import com.fisherl.desolatesky.recipe.RecipeType;
import com.fisherl.desolatesky.recipe.type.CatalystRecipe;
import com.fisherl.desolatesky.util.ItemUtil;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import net.kyori.adventure.key.Key;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.ItemEntity;
import net.minestom.server.instance.Chunk;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.NotNullByDefault;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.function.Consumer;

@NotNullByDefault
public final class CraftingCatalystEntity extends DSItemEntity<CraftingCatalystEntity> {

    private @Nullable Key recipeKey;

    public CraftingCatalystEntity(
            Island island,
            Consumer<Entity> tagApplier
    ) {
        super(ItemStack.AIR, island, tagApplier);
    }

    @Override
    protected void initialize() {
    }

    @Override
    public void setItemStack(ItemStack itemStack) {
        super.setItemStack(itemStack);
        this.recipeKey = itemStack.getTag(ItemTags.RECIPE_ID);
    }

    @Override
    protected void onTick(long time) {
        if (this.recipeKey == null) {
            return;
        }
        if (this.getAliveTicks() % 10 != 0) {
            return;
        }
        final RecipeFactory recipeFactory = this.world().recipeFactory();
        final Chunk chunk = this.getChunk();
        if (chunk == null) {
            return;
        }
        final Collection<Entity> nearbyEntities = this.instance.getChunkEntities(chunk);
        final Multimap<Key, ItemEntity> itemEntities = Multimaps.newListMultimap(new HashMap<>(), ArrayList::new);
        for (final Entity other : nearbyEntities) {
            if (other == this) {
                continue;
            }
            if (!(other instanceof final ItemEntity item)) {
                continue;
            }
            if (other.getDistanceSquared(this) > 1) {
                continue;
            }
            final Key itemId = ItemUtil.getItemId(item.getItemStack());
            itemEntities.put(itemId, item);
        }
        recipeFactory.craft(RecipeType.CATALYST, this.recipeKey, new CatalystRecipe.Input(this, itemEntities))
                .ifPresent(result -> {
                    this.world().entityFactory().createEntity(EntityIds.ITEM, this.island, _ -> {
                            })
                            .map(ItemEntity.class::cast)
                            .ifPresent(e -> {
                                e.setItemStack(result.create(this.world().itemFactory()));
                                e.setInstance(this.getInstance(), this.getPosition());
                                this.remove();
                            });
                });
    }
}
