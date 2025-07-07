package net.desolatesky.player.database;

import net.desolatesky.instance.InstancePos;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public record PlayerData(UUID playerId, @Nullable UUID islandId, @Nullable InstancePos logoutPosition) {

}
