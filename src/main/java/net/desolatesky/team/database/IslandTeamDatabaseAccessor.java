package net.desolatesky.team.database;

import com.google.common.base.Preconditions;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.ReplaceOptions;
import net.desolatesky.DesolateSkyServer;
import net.desolatesky.database.DatabaseAccessor;
import net.desolatesky.database.MongoConnection;
import net.desolatesky.player.DSPlayer;
import net.desolatesky.team.IslandTeam;
import net.desolatesky.team.IslandTeamManager;
import net.desolatesky.util.executor.WrappedScheduledExecutorService;
import org.bson.Document;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public final class IslandTeamDatabaseAccessor implements DatabaseAccessor<IslandTeam, IslandTeam, UUID> {

    private static final Logger LOGGER = LoggerFactory.getLogger(IslandTeamDatabaseAccessor.class.getName());

    private static final String COLLECTION_NAME = "islands";
    private static final String ID_FIELD = "_id";

    private final Map<UUID, IslandTeam> islandsToSave = new ConcurrentHashMap<>();

    public static IslandTeamDatabaseAccessor create(DesolateSkyServer server, MongoConnection connection) {
        final IslandTeamDatabaseAccessor accessor = new IslandTeamDatabaseAccessor(server, connection);
        accessor.load();
        accessor.start();
        return accessor;
    }

    private final DesolateSkyServer server;
    private final MongoConnection connection;
    private final ScheduledExecutorService executor;

    private IslandTeamDatabaseAccessor(DesolateSkyServer server, MongoConnection connection) {
        this.server = server;
        this.connection = connection;
        this.executor = new WrappedScheduledExecutorService(Executors.newSingleThreadScheduledExecutor(runnable -> new Thread(runnable, "IslandTeamDatabaseAccessorThread")));
    }

    private static final Map<UUID, AtomicInteger> saveCounts = new ConcurrentHashMap<>();

    private void start() {
        this.executor.scheduleAtFixedRate(() -> {
            if (!this.server.stopped()) {
                final Map<UUID, IslandTeam> copyMap = new ConcurrentHashMap<>(this.islandsToSave);
                for (final Map.Entry<UUID, IslandTeam> entry : copyMap.entrySet()) {
                    final IslandTeam team = entry.getValue();
                    LOGGER.info("Has team: " + team.id());
                    final IslandTeam.State state = team.state();
                    if (state.isSaving()) {
                        LOGGER.info("Saving team: " + team.id());
                        continue;
                    }
                    this.islandsToSave.remove(team.id());
                    if (state.isDeleted() || state.isDeleting()) {
                        LOGGER.info("Island already deleted");
                        continue;
                    }
                    team.setState(IslandTeam.State.SAVING);
                    LOGGER.info("Set to saving");
                    saveCounts.merge(team.id(), new AtomicInteger(1), (f, s) -> {
                        f.addAndGet(s.get());
                        return f;
                    });
                    Thread.startVirtualThread(() -> {
                        try {
                            LOGGER.info("Saving island, count: {}", saveCounts.get(team.id()));
                            final IslandTeam.SaveData data = team.createSnapshot();
                            if (team.state() == IslandTeam.State.DELETED) {
                                return;
                            }
                            team.setState(IslandTeam.State.SAVING);
                            final UUID teamId = entry.getKey();
                            this.save(teamId, data);
                        } finally {
                            team.setState(IslandTeam.State.LOADED);
                        }
                        LOGGER.info("Saved island, count: {}", saveCounts.get(team.id()));
                    });
                }
            }
        }, 1, 1, TimeUnit.SECONDS);
    }

    @Override
    public void shutdown() {
        try {
            LOGGER.info("Shutting down IslandTeamDatabaseAccessor executor service.");
            final long currentTime = System.currentTimeMillis();
            LOGGER.info("Shutting down");
            this.executor.shutdown();
            LOGGER.info("Shutdown");
            final boolean terminated = this.executor.awaitTermination(30, TimeUnit.SECONDS);
            final long elapsedTime = System.currentTimeMillis() - currentTime;
            if (!terminated) {
                LOGGER.warn("Executor service did not terminate in the expected time ({}), {} tasks remaining", elapsedTime, this.executor.shutdownNow().size());
            } else {
                LOGGER.info("Executor service terminated successfully after {} ms.", elapsedTime);
            }

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public CompletableFuture<IslandCreationResult> create(DSPlayer owner, String name) {
        if (name.length() > IslandTeamManager.MAX_TEAM_NAME_LENGTH) {
            return CompletableFuture.completedFuture(IslandCreationResult.INVALID_NAME);
        }
        if (owner.islandId() != null) {
            return CompletableFuture.completedFuture(IslandCreationResult.ALREADY_IN_TEAM);
        }
        if (this.teamExists(owner.islandId())) {
            return CompletableFuture.completedFuture(IslandCreationResult.ALREADY_IN_TEAM);
        }
        final UUID teamId = UUID.randomUUID();
        final IslandTeam islandTeam = IslandTeam.createNewTeam(this.server, teamId, owner.getUuid(), name, this.server.teamPermissionsRegistry());
        islandTeam.setState(IslandTeam.State.LOADED);
        this.queueSave(teamId, islandTeam);
        LOGGER.info("Created new island team with ID: {}", teamId);
        return CompletableFuture.completedFuture(IslandCreationResult.success(islandTeam));
    }

    private void save(UUID id, IslandTeam.SaveData data) {
        final MongoCollection<Document> collection = this.getCollection();
        final Document document = new Document(ID_FIELD, id);
        IslandTeam.MONGO_CODEC.write(data, document);
        collection.replaceOne(new Document(ID_FIELD, id), document, new ReplaceOptions().upsert(true));
    }

    @Override
    public void save(UUID id, IslandTeam islandTeam) {
        Preconditions.checkState(this.executor.isShutdown(), "Executor is not shutdown, you must use queueSave instead");
        if (islandTeam.state() == IslandTeam.State.DELETED) {
            return;
        }
        this.islandsToSave.remove(id);
        islandTeam.setState(IslandTeam.State.SAVING);
        final MongoCollection<Document> collection = this.getCollection();
        final Document document = new Document(ID_FIELD, id);
        IslandTeam.MONGO_CODEC.write(islandTeam.createSnapshot(), document);
        collection.replaceOne(new Document(ID_FIELD, id), document, new ReplaceOptions().upsert(true));
        islandTeam.setState(IslandTeam.State.LOADED);
    }

    @Override
    public void queueSave(UUID uuid, IslandTeam islandTeam) {
        this.islandsToSave.putIfAbsent(uuid, islandTeam);
    }

    @Override
    public CompletableFuture<Void> delete(UUID id, IslandTeam islandTeam) {
        return CompletableFuture.runAsync(() -> {
            islandTeam.setState(IslandTeam.State.DELETING);
            this.islandsToSave.remove(id);
            islandTeam.setState(IslandTeam.State.DELETING);
            final MongoCollection<Document> collection = this.getCollection();
            collection.deleteOne(new Document(ID_FIELD, id));
            islandTeam.setState(IslandTeam.State.DELETED);
        }, this.executor);
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
