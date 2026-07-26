package net.desolatesky.island.data;

import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;
import net.desolatesky.data.definition.DataDefinition;
import net.desolatesky.data.reader.DataReader;
import net.desolatesky.data.type.Data;
import net.desolatesky.data.writer.DataWriter;
import net.desolatesky.island.IslandSnapshot;
import net.desolatesky.island.invite.IslandInvite;
import net.desolatesky.island.permission.IslandPermissions;
import net.desolatesky.island.role.IslandRole;
import net.desolatesky.world.region.Region;
import net.desolatesky.world.region.SquareRegion;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class IslandDataDefinitionV1 extends DataDefinition<IslandSnapshot> {

    public IslandDataDefinitionV1() {
        super(1);
    }

    @Override
    public void write(DataWriter writer, IslandSnapshot island) throws IOException {
        Data.UUID.write(writer, island.islandId());
        IslandInvite.DATA_TRANSLATOR.writeList(writer, island.islandInvites());
        Data.UUID.writeKeyMap(writer, IslandRole.DATA, island.members());
        IslandRole.DATA.writeKeyMap(writer, IslandPermissions.DATA_TRANSLATOR, island.permissions());
        Region.DATA_TRANSLATOR.write(writer, island.islandRegion());
        Data.COMPONENT.write(writer, island.displayName());
        final Multimap<Key, Key> advancements = island.advancementsProgress();
        Data.INTEGER.write(writer, advancements.keySet().size());
        for (final Key key : advancements.keySet()) {
            Data.KEY.write(writer, key);
            Data.KEY.writeList(writer, advancements.get(key).stream().toList());
        }
        final Multimap<Key, Key> completedAdvancements = island.completedAdvancements();
        Data.INTEGER.write(writer, completedAdvancements.keySet().size());
        for (final Key key : completedAdvancements.keySet()) {
            Data.KEY.write(writer, key);
            Data.KEY.writeList(writer, completedAdvancements.get(key).stream().toList());
        }
    }

    @Override
    public IslandSnapshot read(DataReader reader) throws IOException {
        final UUID islandId = Data.UUID.read(reader);
        final List<IslandInvite> islandInvites = IslandInvite.DATA_TRANSLATOR.readList(reader);
        final Map<UUID, IslandRole> members = Data.UUID.readKeyMap(reader, IslandRole.DATA);
        final Map<IslandRole, IslandPermissions> permissions = IslandRole.DATA.readKeyMap(reader, IslandPermissions.DATA_TRANSLATOR);
        final SquareRegion region = (SquareRegion) Region.DATA_TRANSLATOR.read(reader);
        final Component displayName = Data.COMPONENT.read(reader);
        final int advancementsSize = Data.INTEGER.read(reader);
        final SetMultimap<Key, Key> advancements = Multimaps.newSetMultimap(new HashMap<>(), HashSet::new);
        for (int i = 0; i < advancementsSize; i++) {
            final Key key = Data.KEY.read(reader);
            final List<Key> list = Data.KEY.readList(reader);
            advancements.putAll(key, list);
        }
        final int completedAdvancementsSize = Data.INTEGER.read(reader);
        final SetMultimap<Key, Key> completedAdvancements = Multimaps.newSetMultimap(new HashMap<>(), HashSet::new);
        for (int i = 0; i < completedAdvancementsSize; i++) {
            final Key key = Data.KEY.read(reader);
            final List<Key> list = Data.KEY.readList(reader);
            completedAdvancements.putAll(key, list);
        }
        return IslandSnapshot.create(islandId, islandInvites, members, permissions, advancements, completedAdvancements, region, displayName);
    }
}
