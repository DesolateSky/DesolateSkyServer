package net.desolatesky.island.invite;

import net.desolatesky.data.definition.DataTranslator;
import net.desolatesky.island.invite.data.IslandInviteDataDefinitionV1;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record IslandInvite(UUID islandMember, UUID invited, Instant inviteTime, Duration inviteDuration) {

    public static final DataTranslator<IslandInvite> DATA_TRANSLATOR = new DataTranslator<>(List.of(
       new IslandInviteDataDefinitionV1()
    ));

    public boolean isExpired() {
        return Instant.now().isAfter(this.inviteTime.plus(this.inviteDuration));
    }

}
