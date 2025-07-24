package net.desolatesky.database;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import net.desolatesky.database.codec.InstantCodec;
import org.bson.UuidRepresentation;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;

public final class MongoConnection {

    public static MongoConnection connect(MongoSettings settings) {
        final CodecRegistry codecRegistry = CodecRegistries.fromRegistries(
                CodecRegistries.fromCodecs(InstantCodec.INSTANT_CODEC),
                MongoClientSettings.getDefaultCodecRegistry()
        );

        final MongoClientSettings clientSettings = MongoClientSettings.builder()
                .codecRegistry(codecRegistry)
                .applyConnectionString(new ConnectionString(settings.url()))
                .uuidRepresentation(UuidRepresentation.STANDARD)
                .build();

        final MongoClient client = MongoClients.create(clientSettings);

        return new MongoConnection(settings, client, client.getDatabase(settings.databaseName()));
    }

    private final MongoSettings settings;
    private final MongoClient client;
    private MongoDatabase database;

    public MongoConnection(MongoSettings settings, MongoClient client, MongoDatabase mongoDatabase) {
        this.settings = settings;
        this.client = client;
        this.database = mongoDatabase;
    }

    public MongoSettings settings() {
        return this.settings;
    }

    public MongoClient client() {
        return this.client;
    }

    public MongoDatabase database() {
        return this.database;
    }

    public void shutdown() {
        this.client.close();
    }

}
