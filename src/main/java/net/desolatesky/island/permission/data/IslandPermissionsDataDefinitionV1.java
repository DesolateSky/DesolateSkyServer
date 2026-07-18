package net.desolatesky.island.permission.data;

import net.desolatesky.data.definition.DataDefinition;
import net.desolatesky.data.reader.DataReader;
import net.desolatesky.data.writer.DataWriter;
import net.desolatesky.island.permission.IslandPermission;
import net.desolatesky.island.permission.IslandPermissions;
import net.desolatesky.island.role.IslandRole;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public final class IslandPermissionsDataDefinitionV1 extends DataDefinition<IslandPermissions> {

    public IslandPermissionsDataDefinitionV1() {
        super(1);
    }

    @Override
    public void write(DataWriter writer, IslandPermissions value) throws IOException {
        IslandRole.DATA.write(writer, value.role());
        IslandPermission.DATA.writeList(writer, new ArrayList<>(value.getAllowedPermissions()));
    }

    @Override
    public IslandPermissions read(DataReader reader) throws IOException {
        final IslandRole role = IslandRole.DATA.read(reader);
        final Set<IslandPermission> permissions = new HashSet<>(IslandPermission.DATA.readList(reader));
        return IslandPermissions.mutable(role, permissions);
    }
}
