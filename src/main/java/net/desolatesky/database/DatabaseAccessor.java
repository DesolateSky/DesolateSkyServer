package net.desolatesky.database;

import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public interface DatabaseAccessor<I, O, ID> {

    void queueSave(ID id, I data);

    void save(ID id, I data);

    CompletableFuture<Void> delete(ID id, I data);

    @Nullable O load(ID identifier);

    CompletableFuture<@Nullable O> loadAsync(ID identifier);

    void shutdown();

}
