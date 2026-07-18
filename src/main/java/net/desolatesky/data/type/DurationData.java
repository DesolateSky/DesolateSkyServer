package net.desolatesky.data.type;

import net.desolatesky.data.reader.DataReader;
import net.desolatesky.data.writer.DataWriter;

import java.io.IOException;
import java.time.Duration;

public final class DurationData implements Data<Duration> {

    @Override
    public void write(DataWriter writer, Duration value) throws IOException {
        writer.write(value.toMillis());
    }

    @Override
    public Duration read(DataReader reader) throws IOException {
        return Duration.ofMillis(reader.readLong());
    }
}
