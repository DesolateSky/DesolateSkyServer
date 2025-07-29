package net.desolatesky.util.executor;

import net.minestom.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public final class WrappedScheduledExecutorService implements ScheduledExecutorService {


    private final ScheduledExecutorService internal;

    public WrappedScheduledExecutorService(ScheduledExecutorService internal) {
        this.internal = internal;
    }

    @Override
    public void shutdown() {
        this.internal.shutdown();
    }

    @Override
    public @NotNull List<Runnable> shutdownNow() {
        return this.internal.shutdownNow();
    }

    @Override
    public boolean isShutdown() {
        return this.internal.isShutdown();
    }

    @Override
    public boolean isTerminated() {
        return this.internal.isTerminated();
    }

    @Override
    public boolean awaitTermination(long timeout, @NotNull TimeUnit unit) throws InterruptedException {
        return this.internal.awaitTermination(timeout, unit);
    }

    @Override
    public @NotNull <T> Future<T> submit(@NotNull Callable<T> task) {
        return this.internal.submit(wrap(task));
    }

    @Override
    public @NotNull <T> Future<T> submit(@NotNull Runnable task, T result) {
        return this.internal.submit(wrap(task), result);
    }

    @Override
    public @NotNull Future<?> submit(@NotNull Runnable task) {
        return this.internal.submit(wrap(task));
    }

    @Override
    public @NotNull <T> List<Future<T>> invokeAll(@NotNull Collection<? extends Callable<T>> tasks) throws InterruptedException {
        return this.internal.invokeAll(tasks.stream().map(WrappedScheduledExecutorService::wrap).toList());
    }

    @Override
    public @NotNull <T> List<Future<T>> invokeAll(@NotNull Collection<? extends Callable<T>> tasks, long timeout, @NotNull TimeUnit unit) throws InterruptedException {
        return this.internal.invokeAll(tasks.stream().map(WrappedScheduledExecutorService::wrap).toList(), timeout, unit);
    }

    @Override
    public @NotNull <T> T invokeAny(@NotNull Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        return this.internal.invokeAny(tasks.stream().map(WrappedScheduledExecutorService::wrap).toList());
    }

    @Override
    public <T> T invokeAny(@NotNull Collection<? extends Callable<T>> tasks, long timeout, @NotNull TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return this.internal.invokeAny(tasks.stream().map(WrappedScheduledExecutorService::wrap).toList(), timeout, unit);
    }

    @Override
    public void execute(@NotNull Runnable command) {
        this.internal.submit(wrap(command));
    }

    @Override
    public @NotNull ScheduledFuture<?> schedule(@NotNull Runnable command, long delay, @NotNull TimeUnit unit) {
        return this.internal.schedule(wrap(command), delay, unit);
    }

    @Override
    public @NotNull <V> ScheduledFuture<V> schedule(@NotNull Callable<V> callable, long delay, @NotNull TimeUnit unit) {
        return this.internal.schedule(wrap(callable), delay, unit);
    }

    @Override
    public @NotNull ScheduledFuture<?> scheduleAtFixedRate(@NotNull Runnable command, long initialDelay, long period, @NotNull TimeUnit unit) {
        return this.internal.scheduleAtFixedRate(wrap(command), initialDelay, period, unit);
    }

    @Override
    public @NotNull ScheduledFuture<?> scheduleWithFixedDelay(@NotNull Runnable command, long initialDelay, long delay, @NotNull TimeUnit unit) {
        return this.internal.scheduleWithFixedDelay(wrap(command), initialDelay, delay, unit);
    }

    private static Runnable wrap(Runnable runnable) {
        return () -> {
            try {
                runnable.run();
            } catch (Exception e) {
                MinecraftServer.getExceptionManager().handleException(e);
            }
        };
    }

    private static <V> Callable<V> wrap(Callable<V> callable) {
        return () -> {
            try {
                return callable.call();
            } catch (Exception e) {
                MinecraftServer.getExceptionManager().handleException(e);
                throw new RuntimeException(e);
            }
        };
    }

}
