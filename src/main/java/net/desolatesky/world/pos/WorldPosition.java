package net.desolatesky.world.pos;

import net.desolatesky.data.definition.DataDefinition;
import net.desolatesky.data.definition.DataTranslator;
import net.desolatesky.data.reader.DataReader;
import net.desolatesky.data.type.Data;
import net.desolatesky.data.writer.DataWriter;
import net.desolatesky.world.WorldType;
import net.minestom.server.coordinate.Point;
import org.jetbrains.annotations.UnknownNullability;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public record WorldPosition(@UnknownNullability UUID islandId, UUID worldId, Point pos, WorldType worldType) {

    public static final DataTranslator<WorldPosition> DATA_TRANSLATOR = new DataTranslator<>(List.of(
            new DataDefinitionV1()
    ));

    private static class DataDefinitionV1 extends DataDefinition<WorldPosition> {

        public DataDefinitionV1() {
            super(1);
        }

        @Override
        public void write(DataWriter writer, WorldPosition value) throws IOException {
            Data.UUID.writeNullable(writer, value.islandId());
            Data.UUID.write(writer, value.worldId());
            Data.POINT.write(writer, value.pos());
            WorldType.DATA.write(writer, value.worldType());
        }

        @Override
        public WorldPosition read(DataReader reader) throws IOException {
            final UUID islandId = Data.UUID.readNullable(reader);
            final UUID worldId = Data.UUID.read(reader);
            final Point pos = Data.POINT.read(reader);
            final WorldType worldType = WorldType.DATA.read(reader);
            return new WorldPosition(islandId, worldId, pos, worldType);
        }
    }
}
