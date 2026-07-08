package com.fisherl.desolatesky.island;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

public record IslandInvite(UUID islandMember, UUID invited, Instant inviteTime, Duration inviteDuration) {

    public boolean isExpired() {
        return Instant.now().isAfter(this.inviteTime.plus(this.inviteDuration));
    }

}
