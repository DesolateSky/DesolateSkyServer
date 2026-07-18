package net.desolatesky.island.invite.data;

import net.desolatesky.data.definition.DataDefinition;
import net.desolatesky.data.reader.DataReader;
import net.desolatesky.data.type.Data;
import net.desolatesky.data.writer.DataWriter;
import net.desolatesky.island.invite.IslandInvite;
import org.jetbrains.annotations.NotNullByDefault;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@NotNullByDefault
public final class IslandInviteDataDefinitionV1 extends DataDefinition<IslandInvite> {

    public IslandInviteDataDefinitionV1() {
        super(1);
    }

    @Override
    public void write(DataWriter writer, IslandInvite invite) throws IOException {
        Data.UUID.write(writer, invite.islandMember());
        Data.UUID.write(writer, invite.invited());
        Data.INSTANT.write(writer, invite.inviteTime());
        Data.DURATION.write(writer, invite.inviteDuration());
    }

    @Override
    public IslandInvite read(DataReader reader) throws IOException {
        final UUID islandMember = Data.UUID.read(reader);
        final UUID invited = Data.UUID.read(reader);
        final Instant inviteTime = Data.INSTANT.read(reader);
        final Duration inviteDuration = Data.DURATION.read(reader);
        return new IslandInvite(islandMember, invited, inviteTime, inviteDuration);
    }
}