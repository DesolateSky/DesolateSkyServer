package net.desolatesky.instance.weather;

import net.desolatesky.entity.type.DebrisEntity;
import net.desolatesky.instance.DSInstance;
import net.desolatesky.item.DSItems;
import net.desolatesky.util.collection.Pair;
import net.desolatesky.util.collection.WeightedCollection;
import net.minestom.server.ServerFlag;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;
import net.minestom.server.item.ItemStack;

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

    private final Instance instance;
    private final DSInstance dsInstance;
    private final RandomGenerator random;
    private final Map<UUID, Collection<DebrisEntity>> playerItems = new HashMap<>();

    private Vec windVelocity;

    public WeatherManager(Instance instance, RandomGenerator random) {
        this.dsInstance = (DSInstance) instance;
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
        final Collection<DebrisEntity> itemsInWorld = this.playerItems.remove(playerId);
        if (itemsInWorld != null) {
            for (final DebrisEntity entity : itemsInWorld) {
                entity.remove();
            }
        }
    }

    public void tick() {
        try {
            for (final Player player : this.instance.getPlayers()) {
                final WeightedCollection<ItemStack> items = this.getPlayerItems(player);
                final UUID playerId = player.getUuid();
                Collection<DebrisEntity> itemsInWorld = this.playerItems.get(playerId);
                if (itemsInWorld != null) {
                    itemsInWorld.removeIf(DebrisEntity::isRemoved);
                }
                if (itemsInWorld != null) {
                    for (final DebrisEntity entity : itemsInWorld) {
                        entity.setVelocity(this.windVelocity.mul(ServerFlag.SERVER_TICKS_PER_SECOND));
                    }
                }
                final int maxItems = this.getMaxItems(player);
                if (itemsInWorld != null && itemsInWorld.size() >= maxItems) {
                    continue;
                }
                if (itemsInWorld == null) {
                    itemsInWorld = Collections.newSetFromMap(new ConcurrentHashMap<>());
                    this.playerItems.put(playerId, itemsInWorld);
                }
                if (this.random.nextDouble(100.0) <= SPAWN_ITEM_CHANCE) {
                    final DebrisEntity debrisEntity = new DebrisEntity(this.dsInstance, items);
                    debrisEntity.setInstance(this.instance, this.getRandomPosition(player.getPosition()));
                    itemsInWorld.add(debrisEntity);
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

    private WeightedCollection<ItemStack> getPlayerItems(Player player) {
        final DSItems items = DSItems.get();
        final ItemStack dust = items.dustItem();
        final ItemStack stick = items.stickItem();
        return WeightedCollection.of(
                List.of(
                        Pair.of(dust, 1.0),
                        Pair.of(stick, 1.0)
                )
        );
    }

    private int getMaxItems(Player player) {
        final int playerCount = this.instance.getPlayers().size();
        return Math.ceilDiv(15, playerCount);
    }

}
