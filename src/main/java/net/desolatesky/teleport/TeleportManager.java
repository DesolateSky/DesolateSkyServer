package net.desolatesky.teleport;

import net.desolatesky.instance.InstancePoint;
import net.desolatesky.message.MessageHandler;
import net.desolatesky.message.MessageKey;
import net.desolatesky.message.Messages;
import net.desolatesky.player.DSPlayer;
import net.desolatesky.util.TimeUtil;
import net.kyori.adventure.key.Key;
import net.minestom.server.timer.Task;
import net.minestom.server.timer.TaskSchedule;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public final class TeleportManager {

    private final TeleportConfig teleportConfig;
    private final MessageHandler messageHandler;
    private final Map<UUID, Task> teleportationTasks;

    public TeleportManager(TeleportConfig teleportConfig, MessageHandler messageHandler) {
        this.teleportConfig = teleportConfig;
        this.messageHandler = messageHandler;
        this.teleportationTasks = new ConcurrentHashMap<>();
    }

    public TeleportDataBuilder builder(Key locationKey, DSPlayer player, int totalTicks, InstancePoint target) {
        return new TeleportDataBuilder(
                locationKey,
                totalTicks,
                target,
                player,
                true
        );
    }

    public void queue(Key locationKey, DSPlayer player, int ticks, InstancePoint target) {
        this.builder(locationKey, player, ticks, target).queue();
    }

    public void queue(Key locationKey, DSPlayer player, InstancePoint target) {
        final int totalTicks = this.teleportConfig.getTeleportTicks(locationKey);
        this.queue(locationKey, player, totalTicks, target);
    }

    private void sendSuccessMessage(TeleportData teleportData, MessageKey messageKey) {
        final InstancePoint target = teleportData.destination();
        this.messageHandler.sendMessage(
                teleportData.player(),
                messageKey,
                Map.of("x", target.blockX(),
                        "y", target.blockY(),
                        "z", target.blockZ()
                )
        );
    }

    private void start(TeleportData data) {
        final DSPlayer player = data.player();
        final UUID playerId = player.getUuid();
        final Task currentTask = this.teleportationTasks.get(playerId);
        if (currentTask != null) {
            currentTask.cancel();
        }
        this.teleportationTasks.put(playerId, player.scheduler().scheduleTask(() -> {
            if (!player.isOnline()) {
                return TaskSchedule.stop();
            }
            if (!data.checkMovement()) {
                data.onCancel();
                return TaskSchedule.stop();
            }
            if (!data.isComplete()) {
                data.onTick();
                data.incrementTicks();
                return TaskSchedule.tick(1);
            }
            data.onPreSuccess();
            final InstancePoint instancePoint = data.destination();
            player.teleport(instancePoint).whenComplete((result, error) -> {
                if (error != null) {
                    data.onCancel();
                    return;
                }
                data.onPostSuccess();
            });
            return TaskSchedule.stop();
        }, TaskSchedule.tick(1)));
    }

    public class TeleportDataBuilder {

        private final Key locationKey;
        private final int totalTicks;
        private final InstancePoint destination;
        private final DSPlayer player;
        private final boolean cancelOnMove;

        private @Nullable Consumer<TeleportData> tickCallback = data -> {
            final int ticksRemaining = data.ticksRemaining();
            if (data.ticksPassed() % 20 == 0 && ticksRemaining > 0) {
                final int secondsLeft = (int) (TimeUtil.ticksToDuration(ticksRemaining)).getSeconds();
                TeleportManager.this.messageHandler.sendMessage(TeleportDataBuilder.this.player, Messages.TELEPORT_INTERVAL, Map.of("seconds-left", secondsLeft));
            }
        };
        private @Nullable Consumer<TeleportData> cancellationCallback = data -> TeleportManager.this.messageHandler.sendMessage(TeleportDataBuilder.this.player, Messages.TELEPORT_CANCELLED);
        private @Nullable Consumer<TeleportData> preSuccessCallback = data -> {
        };
        private @Nullable Consumer<TeleportData> postSuccessCallback = data -> TeleportManager.this.sendSuccessMessage(data, Messages.TELEPORT_SUCCESS);

        public TeleportDataBuilder(Key locationKey, int totalTicks, InstancePoint destination, DSPlayer player, boolean cancelOnMove) {
            this.locationKey = locationKey;
            this.totalTicks = totalTicks;
            this.destination = destination;
            this.player = player;
            this.cancelOnMove = cancelOnMove;
        }

        public TeleportDataBuilder setTickCallback(@Nullable Consumer<TeleportData> tickCallback) {
            this.tickCallback = tickCallback;
            return this;
        }

        public TeleportDataBuilder appendTickCallback(Consumer<TeleportData> tickCallback) {
            this.tickCallback = this.tickCallback == null ? tickCallback : this.tickCallback.andThen(tickCallback);
            return this;
        }

        public TeleportDataBuilder setCancellationCallback(@Nullable Consumer<TeleportData> cancellationCallback) {
            this.cancellationCallback = cancellationCallback;
            return this;
        }

        public TeleportDataBuilder appendCancellationCallback(Consumer<TeleportData> cancellationCallback) {
            this.cancellationCallback = this.cancellationCallback == null ? cancellationCallback : this.cancellationCallback.andThen(cancellationCallback);
            return this;
        }

        public TeleportDataBuilder setPreSuccessCallback(@Nullable Consumer<TeleportData> preSuccessCallback) {
            this.preSuccessCallback = preSuccessCallback;
            return this;
        }

        public TeleportDataBuilder appendPreSuccessCallback(Consumer<TeleportData> preSuccessCallback) {
            this.preSuccessCallback = this.preSuccessCallback == null ? preSuccessCallback : this.preSuccessCallback.andThen(preSuccessCallback);
            return this;
        }

        public TeleportDataBuilder setPostSuccessCallback(@Nullable Consumer<TeleportData> postSuccessCallback) {
            this.postSuccessCallback = postSuccessCallback;
            return this;
        }

        public TeleportDataBuilder appendPostSuccessCallback(Consumer<TeleportData> postSuccessCallback) {
            this.postSuccessCallback = this.postSuccessCallback == null ? postSuccessCallback : this.postSuccessCallback.andThen(postSuccessCallback);
            return this;
        }

        public void queue() {
            final TeleportData teleportData = new TeleportData(
                    this.locationKey,
                    this.totalTicks,
                    this.destination,
                    this.player,
                    this.cancelOnMove,
                    this.tickCallback,
                    this.cancellationCallback,
                    this.preSuccessCallback,
                    this.postSuccessCallback
            );
            TeleportManager.this.start(teleportData);
        }

    }

}
