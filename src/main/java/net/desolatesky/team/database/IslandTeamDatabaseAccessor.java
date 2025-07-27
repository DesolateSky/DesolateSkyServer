package net.desolatesky.team.database;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.ReplaceOptions;
import net.desolatesky.DesolateSkyServer;
import net.desolatesky.database.DatabaseAccessor;
import net.desolatesky.database.MongoConnection;
import net.desolatesky.player.DSPlayer;
import net.desolatesky.team.IslandTeam;
import net.desolatesky.team.IslandTeamManager;
import org.bson.Document;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public final class IslandTeamDatabaseAccessor implements DatabaseAccessor<IslandTeam.SaveData, IslandTeam, UUID> {

    private static final Logger LOGGER = LoggerFactory.getLogger(IslandTeamDatabaseAccessor.class.getName());

    private static final String COLLECTION_NAME = "islands";
    private static final String ID_FIELD = "_id";

    private final Map<UUID, ConcurrentLinkedDeque<IslandTeam.SaveData>> saveQueue = new ConcurrentHashMap<>();

    public static IslandTeamDatabaseAccessor create(DesolateSkyServer server, MongoConnection connection) {
        final IslandTeamDatabaseAccessor accessor = new IslandTeamDatabaseAccessor(server, connection);
        accessor.load();
        accessor.start();
        return accessor;
    }

    private final DesolateSkyServer server;
    private final MongoConnection connection;
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    private IslandTeamDatabaseAccessor(DesolateSkyServer server, MongoConnection connection) {
        this.server = server;
        this.connection = connection;
    }

    private void start() {
        this.executor.submit(() -> {
            while (!this.server.stopped()) {
                final Map<UUID, ConcurrentLinkedDeque<IslandTeam.SaveData>> copyMap = new ConcurrentHashMap<>(this.saveQueue);
                for (final Map.Entry<UUID, ConcurrentLinkedDeque<IslandTeam.SaveData>> entry : copyMap.entrySet()) {
                    final ConcurrentLinkedDeque<IslandTeam.SaveData> queue = entry.getValue();
                    Thread.startVirtualThread(() -> {
                        synchronized (queue) {
                            if (queue.isEmpty()) {
                                return;
                            }
                            final IslandTeam.SaveData data = queue.poll();
                            final IslandTeam team = data.team();
                            synchronized (team) {
                                if (team.state() == IslandTeam.State.DELETED) {
                                    return;
                                }
                                team.setState(IslandTeam.State.SAVING);
                                final UUID teamId = entry.getKey();
                                this.save(teamId, data);
                                team.setState(IslandTeam.State.LOADED);
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    public void shutdown() {
        this.executor.shutdown();
    }

    public CompletableFuture<IslandCreationResult> create(DSPlayer owner, String name) {
        if (name.length() > IslandTeamManager.MAX_TEAM_NAME_LENGTH) {
            return CompletableFuture.completedFuture(IslandCreationResult.INVALID_NAME);
        }
        if (owner.islandId() != null) {
            return CompletableFuture.completedFuture(IslandCreationResult.ALREADY_IN_TEAM);
        }
        return CompletableFuture.supplyAsync(() -> {
            if (this.teamExists(owner.islandId())) {
                return IslandCreationResult.ALREADY_IN_TEAM;
            }
            final UUID teamId = UUID.randomUUID();
            final IslandTeam islandTeam = IslandTeam.createNewTeam(this.server, teamId, owner.getUuid(), name, this.server.teamPermissionsRegistry());
            islandTeam.setState(IslandTeam.State.LOADED);
            this.queueSave(teamId, islandTeam.createSnapshot());
            LOGGER.info("Created new island team with ID: {}", teamId);
            return IslandCreationResult.success(islandTeam);
        }, Thread::startVirtualThread);
    }

    @Override
    public void save(UUID id, IslandTeam.SaveData data) {
        final IslandTeam islandTeam = data.team();
        synchronized (islandTeam) {
            if (islandTeam.state() == IslandTeam.State.DELETED) {
                return;
            }
            islandTeam.setState(IslandTeam.State.SAVING);
            final MongoCollection<Document> collection = this.getCollection();
            final Document document = new Document(ID_FIELD, id);
            IslandTeam.MONGO_CODEC.write(data, document);
            collection.replaceOne(new Document(ID_FIELD, id), document, new ReplaceOptions().upsert(true));
            islandTeam.setState(IslandTeam.State.LOADED);
        }
    }

    @Override
    public void queueSave(UUID uuid, IslandTeam.SaveData data) {
        final ConcurrentLinkedDeque<IslandTeam.SaveData> queue = this.saveQueue.computeIfAbsent(uuid, _ -> new ConcurrentLinkedDeque<>());
        queue.add(data);
    }

    @Override
    public CompletableFuture<Void> delete(UUID id, IslandTeam.SaveData data) {
        return CompletableFuture.runAsync(() -> {
            final ConcurrentLinkedDeque<IslandTeam.SaveData> queue = this.saveQueue.remove(id);
            synchronized (queue) {
                queue.clear();
                final IslandTeam islandTeam = data.team();
                synchronized (islandTeam) {
                    islandTeam.setState(IslandTeam.State.DELETING);
                    final MongoCollection<Document> collection = this.getCollection();
                    collection.deleteOne(new Document(ID_FIELD, id));
                    islandTeam.setState(IslandTeam.State.DELETED);
                }
            }
        }, Thread::startVirtualThread);
    }

    @Override
    public @Nullable IslandTeam load(UUID id) {
        final MongoCollection<Document> collection = this.getCollection();
        final Document document = collection.find(new Document(ID_FIELD, id)).first();
        if (document == null) {
            LOGGER.warn("Failed to load island team with ID: {}", id);
            return null;
        }
        final IslandTeam team = IslandTeam.MONGO_CODEC.read(document, new IslandTeam.MongoContext(this.server.teamPermissionsRegistry(), this.server.messageHandler()));
        team.setState(IslandTeam.State.LOADED);
        LOGGER.info("Loaded island team with ID: {}", id);
        return team;
    }

    @Override
    public CompletableFuture<@Nullable IslandTeam> loadAsync(UUID identifier) {
        return CompletableFuture.supplyAsync(() -> this.load(identifier), Thread::startVirtualThread);
    }

    private boolean teamExists(UUID id) {
        final MongoCollection<Document> collection = this.getCollection();
        return collection.countDocuments(new Document(ID_FIELD, id)) > 0;
    }

    private MongoCollection<Document> getCollection() {
        return this.connection.database().getCollection(COLLECTION_NAME);
    }

    private void load() {
        this.connection.database().createCollection(COLLECTION_NAME);
    }

}
