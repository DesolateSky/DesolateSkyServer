package net.desolatesky.database;

import org.jetbrains.annotations.UnknownNullability;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public interface Database {

    void executeWrite(Runnable runnable);

    <T> CompletableFuture<@UnknownNullability T> executeRead(Supplier<@UnknownNullability T> runnable);

    <T> @UnknownNullability T read(Supplier<@UnknownNullability T> runnable);

    void init();

    void shutdown();

}
