package net.desolatesky.item;

import net.desolatesky.block.BlockAttributes;
import net.desolatesky.block.BlockIds;
import net.desolatesky.block.behavior.core.IslandCoreMobSpawnerIds;
import net.desolatesky.block.behavior.core.IslandCoreStormLoot;
import net.desolatesky.entity.EntityIds;
import net.desolatesky.item.behavior.BlockPlaceBehavior;
import net.desolatesky.item.behavior.ItemBehavior;
import net.desolatesky.item.behavior.impl.AttributeMiningSpeedBehavior;
import net.desolatesky.item.behavior.impl.CacheItemBehavior;
import net.desolatesky.item.behavior.impl.HoeBehavior;
import net.desolatesky.item.behavior.impl.WaterBottleBehavior;
import net.desolatesky.item.definition.ItemDefinition;
import net.desolatesky.recipe.RecipeIds;
import net.desolatesky.util.ComponentUtil;
import net.desolatesky.util.Constants;
import net.desolatesky.util.ItemUtil;
import net.desolatesky.util.Pair;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.component.DataComponents;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.item.component.ItemRarity;
import net.minestom.server.item.component.PotionContents;
import net.minestom.server.potion.Potion;
import net.minestom.server.potion.PotionType;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class ConfiguredItemFactory implements ItemFactory {

    private final Map<Key, ItemDefinition> items;

    public ConfiguredItemFactory() {
        this.items = new HashMap<>();
    }

    @Override
    public void initialize() {
        this.registerCaches();
        this.registerCrops();
        this.registerMobDrops();
        this.registerWood();
        this.registerInventoryBlocks();
        this.registerContainers();
        this.registerTools();

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

    private void registerCrops() {
        this.register(ItemDefinition.builder().key(ItemIds.DRY_GRASS_SEED)
                .defaultItem(ItemStack.of(Material.BEETROOT_SEEDS)
                        .withCustomName(ComponentUtil.noItalics("Dry Grass Seeds")))
                .defineBehavior(ItemBehavior.Type.BLOCK_PLACE, BlockPlaceBehavior.blockPlaceBehavior(BlockIds.DRY_GRASS_SEEDS))
                .build());
        this.register(ItemDefinition.builder().key(Material.CACTUS.key())
                .defaultItem(ItemStack.of(Material.CACTUS))
                .defineBehavior(ItemBehavior.Type.BLOCK_PLACE, BlockPlaceBehavior.blockPlaceBehavior(Block.CACTUS.key()))
                .build());
        this.register(ItemDefinition.builder().key(Material.CARROT.key())
                .defaultItem(ItemStack.of(Material.CARROT))
                .defineBehavior(ItemBehavior.Type.BLOCK_PLACE, BlockPlaceBehavior.blockPlaceBehavior(Block.CARROTS.key()))
                .build());
        this.register(ItemDefinition.builder().key(ItemIds.VOID_INFUSED_BUSH)
                .defaultItem(ItemStack.builder(Material.CLOSED_EYEBLOSSOM)
                        .customName(ComponentUtil.noItalics("Void Infused Bush"))
                        .lore(
                                ComponentUtil.noItalics(""),
                                ComponentUtil.noItalics("Entity Attractor (Rabbit)")
                        )
                        .set(DataComponents.RARITY, ItemRarity.EPIC)
                        .set(ItemTags.ISLAND_CORE_SPAWNER_KEY, IslandCoreMobSpawnerIds.RABBIT)
                        .build())
                .defineBehavior(ItemBehavior.Type.BLOCK_PLACE, BlockPlaceBehavior.BLOCKED)
                .build());
    }

    private void registerMobDrops() {
        this.register(ItemDefinition.builder().key(Material.RABBIT.key())
                .defaultItem(ItemStack.of(Material.RABBIT))
                .build());
        this.register(ItemDefinition.builder().key(Material.RABBIT_HIDE.key())
                .defaultItem(ItemStack.of(Material.RABBIT_HIDE))
                .build());
    }

    private void registerWood() {
        this.register(ItemDefinition.builder().key(ItemIds.THATCH)
                .defaultItem(ItemStack.of(Material.BAMBOO)
                        .withCustomName(ComponentUtil.noItalics("Thatch")))
                .build());
        this.register(ItemDefinition.builder().key(ItemIds.THATCH_PLANKS)
                .defaultItem(ItemStack.of(Material.BAMBOO_PLANKS)
                        .withCustomName(ComponentUtil.noItalics("Thatch Planks")))
                .defineBehavior(ItemBehavior.Type.BLOCK_PLACE, BlockPlaceBehavior.blockPlaceBehavior(BlockIds.THATCH_PLANKS))
                .build());
    }

    private void registerInventoryBlocks() {
        this.register(ItemDefinition.builder().key(Material.CRAFTING_TABLE.key())
                .defaultItem(ItemStack.of(Material.CRAFTING_TABLE))
                .defineBehavior(ItemBehavior.Type.BLOCK_PLACE, BlockPlaceBehavior.blockPlaceBehavior(Block.CRAFTING_TABLE.key()))
                .build());
    }

    private void registerContainers() {
        this.register(ItemDefinition.builder().key(Material.GLASS_BOTTLE.key())
                .defaultItem(ItemStack.of(Material.GLASS_BOTTLE))
                .build());
        this.register(ItemDefinition.builder().key(ItemIds.WATER_BOTTLE)
                .defaultItem(ItemStack.builder(Material.POTION)
                        .set(DataComponents.POTION_CONTENTS, new PotionContents(PotionType.WATER))
                        .build())
                .defineBehavior(ItemBehavior.Type.CLICK, new WaterBottleBehavior())
                .build());
    }

    private void registerCaches() {
        this.register(ItemDefinition.builder().key(ItemIds.STARTING_CACHE)
                .defaultItem(ItemStack.builder(Material.BEACON)
                        .set(ItemTags.CACHE_ITEMS_KEY, List.of(new Pair<>(Material.GLASS_BOTTLE.key(), 3), new Pair<>(Material.CACTUS.key(), 3)))
                        .customName(ComponentUtil.noItalics("Starting Cache"))
                        .lore(
                                ComponentUtil.noItalics(""),
                                ComponentUtil.noItalics("Collection of important starting items").color(NamedTextColor.GRAY)
                        )
                        .set(DataComponents.RARITY, ItemRarity.RARE)
                        .build())
                .defineBehavior(ItemBehavior.Type.CLICK, new CacheItemBehavior())
                .build());
    }

    private void registerTools() {
        this.register(ItemDefinition.builder().key(Material.STICK.key())
                .defaultItem(ItemStack.of(Material.STICK))
                .build());
        this.register(ItemDefinition.builder().key(ItemIds.THATCH_PICKAXE)
                .defaultItem(ItemStack.builder(Material.WOODEN_PICKAXE)
                        .set(ItemTags.PICKAXE_MINING_SPEED, 0.75)
                        .customName(ComponentUtil.noItalics("Thatch Pickaxe"))
                        .build())
                .defineBehavior(
                        ItemBehavior.Type.MINING_SPEED,
                        new AttributeMiningSpeedBehavior(Set.of(BlockAttributes.PICKAXE_MINEABLE), ItemTags.PICKAXE_MINING_SPEED)
                )
                .build());
        this.register(ItemDefinition.builder().key(ItemIds.THATCH_AXE)
                .defaultItem(ItemStack.builder(Material.WOODEN_AXE)
                        .set(ItemTags.AXE_MINING_SPEED, 0.75)
                        .customName(ComponentUtil.noItalics("Thatch Axe"))
                        .build())
                .defineBehavior(
                        ItemBehavior.Type.MINING_SPEED,
                        new AttributeMiningSpeedBehavior(Set.of(BlockAttributes.AXE_MINEABLE), ItemTags.AXE_MINING_SPEED)
                )
                .build());
        this.register(ItemDefinition.builder().key(ItemIds.THATCH_SHOVEL)
                .defaultItem(ItemStack.builder(Material.WOODEN_SHOVEL)
                        .set(ItemTags.SHOVEL_MINING_SPEED, 0.75)
                        .customName(ComponentUtil.noItalics("Thatch Shovel"))
                        .build())
                .defineBehavior(
                        ItemBehavior.Type.MINING_SPEED,
                        new AttributeMiningSpeedBehavior(Set.of(BlockAttributes.SHOVEL_MINEABLE), ItemTags.SHOVEL_MINING_SPEED)
                )
                .build());
        this.register(ItemDefinition.builder().key(ItemIds.THATCH_HOE)
                .defaultItem(ItemStack.builder(Material.WOODEN_HOE)
                        .set(ItemTags.HOE_MINING_SPEED, 0.75)
                        .customName(ComponentUtil.noItalics("Thatch Hoe"))
                        .build())
                .defineBehavior(
                        ItemBehavior.Type.MINING_SPEED,
                        new AttributeMiningSpeedBehavior(Set.of(BlockAttributes.HOE_MINEABLE), ItemTags.HOE_MINING_SPEED)
                )
                .defineBehavior(ItemBehavior.Type.CLICK, new HoeBehavior())
                .build());
        this.register(ItemDefinition.builder().key(ItemIds.THATCH_SWORD)
                .defaultItem(ItemStack.builder(Material.WOODEN_SWORD)
                        .set(ItemTags.SWORD_MINING_SPEED, 0.75)
                        .customName(ComponentUtil.noItalics("Thatch Sword"))
                        .build())
                .defineBehavior(
                        ItemBehavior.Type.MINING_SPEED,
                        new AttributeMiningSpeedBehavior(Set.of(BlockAttributes.SWORD_MINEABLE), ItemTags.SWORD_MINING_SPEED)
                )
                .build());
    }

    private void register(ItemDefinition definition) {
        this.items.put(definition.key(), definition);
    }

    @Override
    public @Nullable ItemDefinition getItemDefinition(Key id) {
        return this.items.get(id);
    }

    @Override
    public @Nullable ItemStack getDefaultItem(Key id) {
        final ItemDefinition itemDefinition = this.getItemDefinition(id);
        if (itemDefinition == null) {
            return null;
        }
        return itemDefinition.defaultItemStack();
    }

    @Override
    public @Nullable ItemDefinition getItemDefinition(ItemStack block) {
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
