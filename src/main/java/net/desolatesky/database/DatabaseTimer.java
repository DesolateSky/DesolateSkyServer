package net.desolatesky.database;

import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import net.desolatesky.instance.DSInstanceManager;
import net.desolatesky.instance.team.TeamInstance;
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
    private final DSInstanceManager instanceManager;
    private Task task;

    public DatabaseTimer(IslandTeamManager teamManager, DSPlayerManager playerManager, DSInstanceManager instanceManager) {
        this.teamManager = teamManager;
        this.playerManager = playerManager;
        this.instanceManager = instanceManager;
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
        this.saveAll(async, false);
    }

    private void saveAll(boolean async, boolean shutdown) {
        final Multimap<UUID, DSPlayer> islandsToPlayers = Multimaps.newSetMultimap(new HashMap<>(), HashSet::new);
        for (final Player element : MinecraftServer.getConnectionManager().getOnlinePlayers()) {
            final DSPlayer player = (DSPlayer) element;
            final UUID islandId = player.islandId();
            if (islandId == null) {
                save(() -> {
                    if (shutdown) {
                        this.playerManager.forceSave(player);
                    } else {
                        this.playerManager.queueSave(player);
                    }
                }, async);
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
                    if (shutdown) {
                        this.teamManager.forceSave(islandTeam);
                    } else {
                        this.teamManager.queueSave(islandTeam);
                    }
                }
                for (final DSPlayer player : players) {
                    if (!player.isOnline()) {
                        return;
                    }
                    LOGGER.info("Saving player {} with UUID {}", player.getUsername(), player.getUuid());
                    if (shutdown) {
                        this.playerManager.forceSave(player);
                    } else {
                        this.playerManager.queueSave(player);
                    }
                }
                final TeamInstance teamInstance = this.instanceManager.getIslandInstance(islandId);
                if (teamInstance != null) {
                    teamInstance.save();
                }
            }, async);
        }
    }

    public void shutdownAndSave() {
        this.stop();
        this.playerManager.shutdown();
        this.teamManager.shutdown();
        LOGGER.info("Saving all players and teams before shutdown...");
        this.saveAll(false, true);
    }

    private static void save(Runnable runnable, boolean async) {
        if (async) {
            Thread.startVirtualThread(runnable);
        } else {
            runnable.run();
        }
    }

}
