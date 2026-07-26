package net.desolatesky.player.data;

import net.desolatesky.data.definition.DataDefinition;
import net.desolatesky.data.reader.DataReader;
import net.desolatesky.data.type.Data;
import net.desolatesky.data.writer.DataWriter;
import net.desolatesky.logging.DSLogger;
import net.desolatesky.player.DSPlayerData;
import net.kyori.adventure.nbt.TagStringIO;
import net.minestom.server.codec.Transcoder;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.NotNullByDefault;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@NotNullByDefault
public final class PlayerDataDefinitionV1 extends DataDefinition<DSPlayerData> {

    public PlayerDataDefinitionV1() {
        super(1);
    }

    @Override
    public void write(DataWriter writer, DSPlayerData data) throws IOException {
        Data.UUID.write(writer, data.uuid());
        if (data.islandId() == null) {
            Data.BOOLEAN.write(writer, false);
        } else {
            Data.BOOLEAN.write(writer, true);
            Data.UUID.write(writer, data.islandId());
        }
        final List<String> items = data.inventory()
                .stream()
                .map(itemStack -> ItemStack.CODEC.encode(Transcoder.NBT, itemStack).orElse(null))
                .filter(Objects::nonNull)
                .map(tag -> {
                    try {
                        return TagStringIO.tagStringIO().asString(tag);
                    } catch (IOException e) {
                        DSLogger.getLogger().severe(e);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toList();
        Data.STRING.writeList(writer, items);
    }

    @Override
    public DSPlayerData read(DataReader reader) throws IOException {
        final UUID id = Data.UUID.read(reader);
        final @Nullable UUID islandId;
        if (reader.readBoolean()) {
            islandId = Data.UUID.read(reader);
        } else {
            islandId = null;
        }
        final List<ItemStack> inventory = Data.STRING.readList(reader).
                stream()
                .map(itemString -> {
                    try {
                        return TagStringIO.tagStringIO().asTag(itemString);
                    } catch (IOException e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .map(tag -> ItemStack.CODEC.decode(Transcoder.NBT, tag).orElse(null))
                .filter(Objects::nonNull)
                .toList();
        return new DSPlayerData(id, null /* use v2 */, inventory, null);
    }
}
