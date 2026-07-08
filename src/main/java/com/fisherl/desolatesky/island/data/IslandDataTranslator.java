package com.fisherl.desolatesky.island.data;

import com.fisherl.desolatesky.data.definition.DataDefinition;
import com.fisherl.desolatesky.data.reader.DataReader;
import com.fisherl.desolatesky.data.writer.DataWriter;
import com.fisherl.desolatesky.island.Island;

public final class IslandDataTranslator {

    private IslandDataTranslator() {
        throw new UnsupportedOperationException();
    }

    public static final DataDefinition<Island> V_1 = new DataDefinition<>(1) {
        @Override
        public void write(DataWriter reader, Island value) {

        }

        @Override
        public Island read(DataReader reader) {
            return null;
        }
    };

}
