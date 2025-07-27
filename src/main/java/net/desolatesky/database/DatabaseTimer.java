package net.desolatesky.database;

import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import net.desolatesky.player.DSPlayer;
import net.desolatesky.player.DSPlayerManager;
import net.desolatesky.player.database.PlayerDatabaseAccessor;
import net.desolatesky.team.IslandTeam;
import net.desolatesky.team.IslandTeamManager;
import net.desolatesky.team.database.IslandTeamDatabaseAccessor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.timer.Task;
import net.minestom.server.timer.TaskSchedule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

public final class DatabaseTimer {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseTimer.class);

    private final IslandTeamManager teamManager;
    private final DSPlayerManager playerManager;
    private Task task;

    public DatabaseTimer(IslandTeamManager teamManager, DSPlayerManager playerManager) {
        this.teamManager = teamManager;
        this.playerManager = playerManager;
    }

    public void start() {
        final Duration time = Duration.ofMinutes(1);
        this.task = MinecraftServer.getSchedulerManager().scheduleTask(() -> this.saveAll(true), TaskSchedule.duration(time), TaskSchedule.duration(time));
    }

    public void stop() {
        if (this.task != null) {
            this.task.cancel();
            this.task = null;
        }
    }

    public void saveAll(boolean async) {
        final Multimap<UUID, DSPlayer> islandsToPlayers = Multimaps.newSetMultimap(new HashMap<>(), HashSet::new);
        for (final Player element : MinecraftServer.getConnectionManager().getOnlinePlayers()) {
            final DSPlayer player = (DSPlayer) element;
            final UUID islandId = player.islandId();
            if (islandId == null) {
                save(() -> this.playerManager.savePlayer(player), async);
                continue;
            }
            islandsToPlayers.put(islandId, player);
        }
        for (final Map.Entry<UUID, Collection<DSPlayer>> entry : islandsToPlayers.asMap().entrySet()) {
            final UUID islandId = entry.getKey();
            final Collection<DSPlayer> players = entry.getValue();
            save(() -> {
                final IslandTeam islandTeam = this.teamManager.getTeam(islandId);
                if (islandTeam != null) {
                    this.teamManager.save(islandTeam);
                }
                for (final DSPlayer player : players) {
                    if (!player.isOnline()) {
                        return;
                    }
                    LOGGER.info("Saving player {} with UUID {}", player.getUsername(), player.getUuid());
                    this.playerManager.savePlayer(player);
                }
            }, async);
        }
    }

    private static void save(Runnable runnable, boolean async) {
        if (async) {
            Thread.startVirtualThread(runnable);
        } else {
            runnable.run();
        }
    }

}
