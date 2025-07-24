package net.desolatesky.instance.weather;

import net.desolatesky.block.DSBlocks;
import net.desolatesky.entity.EntityKeys;
import net.desolatesky.entity.loot.EntityLootRegistry;
import net.desolatesky.entity.type.DebrisEntity;
import net.desolatesky.instance.DSInstance;
import net.desolatesky.item.DSItems;
import net.desolatesky.loot.table.LootTable;
import net.desolatesky.util.collection.Pair;
import net.desolatesky.util.collection.WeightedCollection;
import net.kyori.adventure.util.RGBLike;
import net.minestom.server.MinecraftServer;
import net.minestom.server.ServerFlag;
import net.minestom.server.adventure.MinestomAdventure;
import net.minestom.server.color.Color;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.attribute.Attribute;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.random.RandomGenerator;

public final class WeatherManager {

    private static final double WIND_CHANGE_DIRECTION_CHANCE = 25;
    private static final double WIND_CHANGE_MAX_ANGLE = 0.5;

    private static final double MIN_X_SPAWN_DISTANCE = 2.5;
    private static final double MAX_X_SPAWN_DISTANCE = 6.0;
    private static final double MIN_Y_SPAWN_DISTANCE = 0;
    private static final double MAX_Y_SPAWN_DISTANCE = 2.0;
    private static final double MIN_Z_SPAWN_DISTANCE = 2.5;
    private static final double MAX_Z_SPAWN_DISTANCE = 6.0;

    private static final double SPAWN_ITEM_CHANCE = 2;

    private static final double MAX_WIND_HEIGHT_SCALE = 1.50;
    private static final double MIN_WIND_HEIGHT_SCALE = 0.75;

    private static final double DESPAWN_DISTANCE = 15;
    private static final double DESPAWN_DISTANCE_SQUARED = DESPAWN_DISTANCE * DESPAWN_DISTANCE;

    private static final int DEFAULT_MAX_ITEMS = 15;

    private final DSBlocks blocks;
    private final EntityLootRegistry entityLootRegistry;
    private final DSInstance instance;
    private final RandomGenerator random;
    private final Map<UUID, PlayerDebris> playerItems = new HashMap<>();

    private Vec windVelocity;

    public WeatherManager(DSBlocks blocks, EntityLootRegistry entityLootRegistry, DSInstance instance, RandomGenerator random) {
        this.blocks = blocks;
        this.entityLootRegistry = entityLootRegistry;
        this.instance = instance;
        this.random = random;
        this.windVelocity = new Vec(0.1, 0, 0.25).normalize().mul(0.075);
    }

    public Vec getWindAtLocation(Point location) {
        final double y = location.y();
        final int maxHeight = this.instance.getCachedDimensionType().maxY();
        final int minHeight = this.instance.getCachedDimensionType().minY();
        if (y < minHeight || y > maxHeight) {
            return this.windVelocity;
        }
        final double scale;
        if (y >= 0) {
            scale = (y / maxHeight) * MAX_WIND_HEIGHT_SCALE;
        } else {
            scale = 1 - (y / minHeight) * MIN_WIND_HEIGHT_SCALE;
        }
        return this.windVelocity.mul(scale);
    }

    public void handlePlayerLeave(Player player) {
        final UUID playerId = player.getUuid();
        final PlayerDebris playerDebris = this.playerItems.remove(playerId);
        if (playerDebris == null) {
            return;
        }
        final Collection<DebrisEntity> itemsInWorld = playerDebris.items;
        if (itemsInWorld != null) {
            for (final DebrisEntity entity : itemsInWorld) {
                entity.remove();
            }
        }
    }

    public void tick() {
        try {
            for (final Player player : this.instance.getPlayers()) {
                final UUID playerId = player.getUuid();
                PlayerDebris playerDebris = this.playerItems.get(playerId);
                if (playerDebris != null) {
                    playerDebris.items.removeIf(entity -> {
                        if (entity.isRemoved()) {
                            return true;
                        }
                        if (entity.getDistanceSquared(player.getPosition()) > DESPAWN_DISTANCE_SQUARED) {
                            entity.remove();
                            return true;
                        }
                        return false;
                    });
                }
                if (playerDebris != null) {
                    for (final DebrisEntity entity : playerDebris.items) {
                        entity.setVelocity(this.windVelocity.mul(ServerFlag.SERVER_TICKS_PER_SECOND));
                    }
                    final DebrisEntity targetedEntity = (DebrisEntity) player.getLineOfSightEntity(player.getAttributeValue(Attribute.ENTITY_INTERACTION_RANGE), e -> e instanceof DebrisEntity);
                    playerDebris.trackEntity(targetedEntity);
                }

                final int maxItems = this.getMaxItems(player);
                if (playerDebris != null && playerDebris.items.size() >= maxItems) {
                    continue;
                }
                if (playerDebris == null) {
                    playerDebris = new PlayerDebris(Collections.newSetFromMap(new ConcurrentHashMap<>()));
                    this.playerItems.put(playerId, playerDebris);
                }
                if (this.random.nextDouble(100.0) <= SPAWN_ITEM_CHANCE) {
                    final LootTable lootTable = this.entityLootRegistry.getLootTable(EntityKeys.DEBRIS_ENTITY);
                    if (lootTable == null) {
                        continue;
                    }
                    final DebrisEntity debrisEntity = new DebrisEntity(this.blocks, this.instance, lootTable);
                    debrisEntity.setInstance(this.instance, this.getRandomPosition(player.getPosition()));
                    playerDebris.items.add(debrisEntity);
                }
            }
            this.changeWind();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Pos getRandomPosition(Point playerPosition) {
        double xOffset = this.random.nextDouble(MIN_X_SPAWN_DISTANCE, MAX_X_SPAWN_DISTANCE);
        final double yOffset = this.random.nextDouble(MIN_Y_SPAWN_DISTANCE, MAX_Y_SPAWN_DISTANCE);
        double zOffset = this.random.nextDouble(MIN_Z_SPAWN_DISTANCE, MAX_Z_SPAWN_DISTANCE);
        final boolean negativeX = this.random.nextBoolean();
        final boolean negativeZ = this.random.nextBoolean();
        if (negativeX) {
            xOffset = -xOffset;
        }
        if (negativeZ) {
            zOffset = -zOffset;
        }
        return new Pos(
                playerPosition.x() + xOffset,
                playerPosition.y() + yOffset,
                playerPosition.z() + zOffset
        );
    }

    private void changeWind() {
        if (this.random.nextDouble(100.0) > WIND_CHANGE_DIRECTION_CHANCE) {
            return;
        }
        final double angle = this.random.nextDouble(-WIND_CHANGE_MAX_ANGLE, WIND_CHANGE_MAX_ANGLE);
        this.windVelocity = this.windVelocity.rotateAroundAxis(new Vec(0, 1, 0), angle);
    }

    private int getMaxItems(Player player) {
        final int playerCount = this.instance.getPlayers().size();
        return Math.ceilDiv(DEFAULT_MAX_ITEMS, playerCount);
    }

    private static class PlayerDebris {

        private static final RGBLike GLOW_COLOR = Color.WHITE;

        private final Collection<DebrisEntity> items;
        private @Nullable DebrisEntity targetedEntity;

        public PlayerDebris(Collection<DebrisEntity> items) {
            this.items = items;
        }

        private void trackEntity(@Nullable DebrisEntity entity) {
            if (entity == null) {
                if (this.targetedEntity != null) {
                    this.targetedEntity.setGlowing(false);
                }
                this.targetedEntity = null;
                return;
            }
            if (this.targetedEntity == null) {
                this.targetedEntity = entity;
                this.glowEntity(entity);
                return;
            }
            if (this.targetedEntity == entity) {
                return;
            }
            if (this.targetedEntity.isRemoved()) {
                this.targetedEntity = entity;
                this.glowEntity(entity);
                return;
            }
            this.targetedEntity.setGlowing(false);
            this.targetedEntity = entity;
            this.glowEntity(entity);
        }

        private void glowEntity(DebrisEntity entity) {
            entity.setGlowing(GLOW_COLOR);
        }

        private void removeGlow(DebrisEntity entity) {
            entity.setGlowing(false);
        }

    }

}
