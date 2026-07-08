package com.fisherl.desolatesky.item;

import com.fisherl.desolatesky.block.behavior.core.IslandCoreMobSpawnerIds;
import com.fisherl.desolatesky.block.behavior.core.IslandCoreStormLoot;
import com.fisherl.desolatesky.entity.EntityIds;
import com.fisherl.desolatesky.item.behavior.BlockPlaceBehavior;
import com.fisherl.desolatesky.item.behavior.ItemBehavior;
import com.fisherl.desolatesky.item.definition.ItemDefinition;
import com.fisherl.desolatesky.recipe.RecipeIds;
import com.fisherl.desolatesky.util.ComponentUtil;
import com.fisherl.desolatesky.util.Constants;
import com.fisherl.desolatesky.util.ItemUtil;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.component.DataComponents;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.item.component.ItemRarity;
import org.jetbrains.annotations.Unmodifiable;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class ConfiguredItemFactory implements ItemFactory {

    private final Map<Key, ItemDefinition> items;

    public ConfiguredItemFactory() {
        this.items = new HashMap<>();
    }

    @Override
    public void initialize() {
        this.register(ItemDefinition.builder().key(ItemIds.ITEM_CORE_STORM_CATALYST)
                .defaultItem(ItemStack.builder(Material.NETHERITE_SCRAP)
                        .set(ItemTags.ISLAND_CORE_STORM_LOOT, IslandCoreStormLoot.DUST_STORM.key())
                        .set(ItemTags.ISLAND_CORE_STORM_DURATION, Duration.ofMinutes(1))
                        .customName(ComponentUtil.noItalics("Dust Storm Catalyst").color(TextColor.color(Constants.PRIMARY_COLOR)))
                        .build())
                .build());
        this.register(ItemDefinition.builder().key(ItemIds.DUST)
                .defaultItem(ItemStack.builder(Material.SUGAR)
                        .customName(ComponentUtil.noItalics("Dust"))
                        .set(DataComponents.ITEM_MODEL, ItemIds.DUST.asString())
                        .build())
                .build());
        this.register(ItemDefinition.builder().key(ItemIds.SILVERFISH_CRAFTING_CATALYST)
                .defaultItem(ItemStack.builder(Material.SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE)
                        .customName(ComponentUtil.noItalics("Crafting Catalyst"))
                        .set(ItemTags.DROPPED_ITEM_ENTITY_KEY, EntityIds.CRAFTING_CATALYST)
                        .set(ItemTags.RECIPE_ID, RecipeIds.STONE_SLAB)
                        .set(DataComponents.RARITY, ItemRarity.EPIC)
                        .build())
                .build());
        this.register(ItemDefinition.builder().key(ItemIds.SILVERFISH_EYE)
                .defaultItem(ItemStack.builder(Material.IRON_NUGGET)
                        .customName(ComponentUtil.noItalics("Silverfish Eye"))
                        .build())
                .build());
        this.register(ItemDefinition.builder().key(ItemIds.PEBBLE)
                .defaultItem(ItemStack.builder(Material.STONE_BUTTON)
                        .customName(ComponentUtil.noItalics("Pebble"))
                        .build())
                .defineBehavior(ItemBehavior.Type.BLOCK_PLACE, BlockPlaceBehavior.BLOCKED)
                .build());
        this.register(ItemDefinition.builder().key(ItemIds.ENTITY_ATTRACTOR_SILVERFISH)
                .defaultItem(ItemStack.builder(Material.CRACKED_STONE_BRICKS)
                        .customName(ComponentUtil.noItalics("Entity Attractor (Silverfish)").color(NamedTextColor.GRAY))
                        .set(DataComponents.RARITY, ItemRarity.EPIC)
                        .set(ItemTags.ISLAND_CORE_SPAWNER_KEY, IslandCoreMobSpawnerIds.SILVERFISH)
                        .build()
                )
                .defineBehavior(ItemBehavior.Type.BLOCK_PLACE, BlockPlaceBehavior.BLOCKED)
                .build());
        this.register(ItemDefinition.builder().key(ItemIds.STONE_SLAB)
                .defaultItem(ItemStack.of(Material.STONE_SLAB))
                .defineBehavior(ItemBehavior.Type.BLOCK_PLACE, BlockPlaceBehavior.BLOCKED)
                .build());
    }

    private void register(ItemDefinition definition) {
        this.items.put(definition.key(), definition);
    }

    @Override
    public Optional<ItemDefinition> getItemDefinition(Key id) {
        return Optional.ofNullable(this.items.get(id));
    }

    @Override
    public Optional<ItemDefinition> getItemDefinition(ItemStack block) {
        return this.getItemDefinition(this.getItemId(block));
    }

    @Override
    public Key getItemId(ItemStack block) {
        return ItemUtil.getItemId(block);
    }

    @Override
    public @Unmodifiable Collection<Key> getALlItemIds() {
        return Collections.unmodifiableCollection(this.items.keySet());
    }
}
