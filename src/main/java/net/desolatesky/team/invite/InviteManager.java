package net.desolatesky.team.invite;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalCause;
import com.github.benmanes.caffeine.cache.Scheduler;
import net.desolatesky.message.MessageHandler;
import net.desolatesky.message.Messages;
import net.desolatesky.player.DSPlayer;
import net.desolatesky.player.PlayerProfile;
import net.minestom.server.MinecraftServer;
import net.minestom.server.network.ConnectionManager;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public final class InviteManager {

    public static final Duration INVITE_EXPIRATION_DURATION = Duration.ofMinutes(1);

    private final Cache<UUID, Invite> invites;

    public InviteManager(MessageHandler messageHandler) {
        this.invites = Caffeine.newBuilder()
                .expireAfterWrite(INVITE_EXPIRATION_DURATION)
                .<UUID, Invite>evictionListener((key, value, cause) -> {
                    if (value == null) {
                        return;
                    }
                    if (key == null) {
                        return;
                    }
                    if (cause != RemovalCause.EXPIRED) {
                        return;
                    }
                    final ConnectionManager connectionManager = MinecraftServer.getConnectionManager();
                    final DSPlayer invitedPlayer = (DSPlayer) connectionManager.getOnlinePlayerByUuid(key);
                    final DSPlayer inviter = (DSPlayer) connectionManager.getOnlinePlayerByUuid(value.inviter.id());
                    if (invitedPlayer != null) {
                        messageHandler.sendMessage(invitedPlayer, Messages.RECEIVED_INVITE_EXPIRED, Map.of("player", value.inviter.name(), "island", value.islandName()));
                    }
                    if (inviter != null) {
                        messageHandler.sendMessage(inviter, Messages.SENT_INVITE_EXPIRED, Map.of("player", value.invitedPlayer.name(), "island", value.islandName()));
                    }
                })
                .scheduler(Scheduler.systemScheduler())
                .build();
    }

    public void addInvite(PlayerProfile inviter, PlayerProfile invitedPlayer, String islandName) {
        this.invites.put(invitedPlayer.id(), new Invite(inviter, invitedPlayer, islandName, Instant.now()));
    }

    public void removeInvite(UUID inviteId) {
        this.invites.invalidate(inviteId);
    }

    public boolean isInvited(UUID inviteId) {
        final Invite invite = this.invites.getIfPresent(inviteId);
        if (invite == null) {
            return false;
        }
        if (invite.isExpired()) {
            this.invites.invalidate(inviteId);
            return false;
        }
        return true;
    }

    private record Invite(PlayerProfile inviter, PlayerProfile invitedPlayer, String islandName, Instant timestamp) {

        public boolean isExpired() {
            return Instant.now().isAfter(this.timestamp.plus(InviteManager.INVITE_EXPIRATION_DURATION));
        }

    }

}
