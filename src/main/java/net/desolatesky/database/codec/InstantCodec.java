package net.desolatesky.database.codec;

import org.bson.BsonDocument;
import org.bson.BsonDocumentReader;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.Document;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.conversions.Bson;

import java.time.Instant;

public final class InstantCodec implements Codec<Instant> {

    public static final InstantCodec INSTANT_CODEC = new InstantCodec();

    private InstantCodec() {

    }

    public static Instant decode(Bson document) {
        return INSTANT_CODEC.decode(new BsonDocumentReader(document.toBsonDocument()), DecoderContext.builder().build());
    }

    @Override
    public Instant decode(BsonReader reader, DecoderContext decoderContext) {
        reader.readStartDocument();
        final long epochSecond = reader.readInt64("epochSecond");
        final int nano = reader.readInt32("nano");
        reader.readEndDocument();
        return Instant.ofEpochSecond(epochSecond, nano);
    }

    @Override
    public void encode(BsonWriter writer, Instant value, EncoderContext encoderContext) {
        if (value == null) {
            writer.writeNull();
            return;
        }
        writer.writeStartDocument();
        writer.writeInt64("epochSecond", value.getEpochSecond());
        writer.writeInt32("nano", value.getNano());
        writer.writeEndDocument();
    }

    @Override
    public Class<Instant> getEncoderClass() {
        return Instant.class;
    }

}
