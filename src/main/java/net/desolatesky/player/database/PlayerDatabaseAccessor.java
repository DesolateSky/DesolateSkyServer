package net.desolatesky.player.database;


import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import net.desolatesky.DesolateSkyServer;
import net.desolatesky.database.DatabaseAccessor;
import net.desolatesky.database.MongoConnection;
import net.desolatesky.player.DSPlayer;
import org.bson.Document;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public final class PlayerDatabaseAccessor implements DatabaseAccessor<DSPlayer, PlayerData, UUID> {

    private static final Logger LOGGER = Logger.getLogger(PlayerDatabaseAccessor.class.getName());

    private static final String COLLECTION_NAME = "players";

    private static final String ID_FIELD = "_id";
    private static final String INVENTORY_FIELD = "inventory";

    public static PlayerDatabaseAccessor create(DesolateSkyServer server, MongoConnection connection) {
        final PlayerDatabaseAccessor accessor = new PlayerDatabaseAccessor(server, connection);
        accessor.load();
        return accessor;
    }

    private final DesolateSkyServer server;
    private final MongoConnection connection;
    private final ExecutorService executorService;

    private PlayerDatabaseAccessor(DesolateSkyServer server, MongoConnection connection) {
        this.server = server;
        this.connection = connection;
        this.executorService = Executors.newSingleThreadExecutor();
    }

    @Override
    public void save(UUID id, DSPlayer player) {
        final MongoCollection<Document> collection = this.getCollection();
        final PlayerData playerData = player.playerData();
        playerData.setLastLogoutInstanceId(player.getInstance().getUuid());
        playerData.setLastLogoutPos(player.getPosition());
        final Document playerDocument = new Document();
        playerDocument.append(ID_FIELD, id);
        DSPlayer.MONGO_CODEC.write(player, playerDocument);
        collection.replaceOne(Filters.eq(ID_FIELD, id), playerDocument, new ReplaceOptions().upsert(true));
        LOGGER.info("Saved player data for " + id + " in MongoDB.");
    }

    @Override
    public CompletableFuture<Void> delete(UUID id, DSPlayer data) {
        return CompletableFuture.runAsync(() -> {
            final MongoCollection<Document> collection = this.getCollection();
            collection.deleteOne(new Document(ID_FIELD, id.toString()));
        }, Thread::startVirtualThread);
    }

    @Override
    public @Nullable PlayerData load(UUID id) {
        final MongoCollection<Document> collection = this.getCollection();
        final Document playerDocument = collection.find(new Document(ID_FIELD, id)).first();
        if (playerDocument == null) {
            return null;
        }
        return DSPlayer.MONGO_CODEC.read(playerDocument, new DSPlayer.MongoContext(this.server.cooldownConfig()));
    }

    @Override
    public void queueSave(UUID uuid, DSPlayer data) {
        this.executorService.submit(() -> this.save(uuid, data));
    }

    @Override
    public CompletableFuture<@Nullable PlayerData> loadAsync(UUID identifier) {
        return CompletableFuture.supplyAsync(() -> this.load(identifier), Thread::startVirtualThread);
    }

    @Override
    public void shutdown() {

    }

    private MongoCollection<Document> getCollection() {
        return this.connection.database().getCollection(COLLECTION_NAME);
    }

    private void load() {
        this.connection.database().createCollection(COLLECTION_NAME);
    }
}
