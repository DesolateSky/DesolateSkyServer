package net.desolatesky.island.data;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import net.desolatesky.data.definition.DataDefinition;
import net.desolatesky.data.reader.DataReader;
import net.desolatesky.data.type.Data;
import net.desolatesky.data.writer.DataWriter;
import net.desolatesky.island.IslandSnapshot;
import net.desolatesky.island.invite.IslandInvite;
import net.desolatesky.island.permission.IslandPermissions;
import net.desolatesky.island.role.IslandRole;
import net.desolatesky.world.WorldType;
import net.desolatesky.world.region.Region;
import net.desolatesky.world.region.SquareRegion;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class IslandDataDefinitionV2 extends DataDefinition<IslandSnapshot> {

    public IslandDataDefinitionV2() {
        super(2);
    }

    @Override
    public void write(DataWriter writer, IslandSnapshot island) throws IOException {
        Data.UUID.write(writer, island.islandId());
        WorldType.DATA.writeKeyMap(writer, Data.UUID, island.worldIds());
        IslandInvite.DATA_TRANSLATOR.writeList(writer, island.islandInvites());
        Data.UUID.writeKeyMap(writer, IslandRole.DATA, island.members());
        IslandRole.DATA.writeKeyMap(writer, IslandPermissions.DATA_TRANSLATOR, island.permissions());
        Region.DATA_TRANSLATOR.write(writer, island.islandRegion());
        Data.COMPONENT.write(writer, island.displayName());
        final Multimap<Key, Key> advancements = island.advancementsProgress();
        Data.INTEGER.write(writer, advancements.size());
        for (final Key key : advancements.keys()) {
            Data.KEY.write(writer, key);
            Data.KEY.writeList(writer, advancements.get(key).stream().toList());
        }
    }

    @Override
    public IslandSnapshot read(DataReader reader) throws IOException {
        final UUID islandId = Data.UUID.read(reader);
        final Map<WorldType, UUID> worldIds = new EnumMap<>(WorldType.DATA.readKeyMap(reader, Data.UUID));
        final List<IslandInvite> islandInvites = IslandInvite.DATA_TRANSLATOR.readList(reader);
        final Map<UUID, IslandRole> members = Data.UUID.readKeyMap(reader, IslandRole.DATA);
        final Map<IslandRole, IslandPermissions> permissions = IslandRole.DATA.readKeyMap(reader, IslandPermissions.DATA_TRANSLATOR);
        final SquareRegion region = (SquareRegion) Region.DATA_TRANSLATOR.read(reader);
        final Component displayName = Data.COMPONENT.read(reader);
        final int advancementsSize = Data.INTEGER.read(reader);
        final ListMultimap<Key, Key> advancements = Multimaps.newListMultimap(new HashMap<>(), ArrayList::new);
        for (int i = 0; i < advancementsSize; i++) {
            final Key key = Data.KEY.read(reader);
            final List<Key> list = Data.KEY.readList(reader);
            advancements.putAll(key, list);
        }

        return IslandSnapshot.create(islandId, worldIds, islandInvites, members, permissions, advancements, region, displayName);
    }
}
