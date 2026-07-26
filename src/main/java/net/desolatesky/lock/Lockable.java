package net.desolatesky.lock;

import net.desolatesky.util.functional.SafeRunnable;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.function.Supplier;

public interface Lockable {

    ReadWriteLock lock();

    default void lockWrite(Runnable runnable) {
        this.lock().writeLock().lock();
        try {
            runnable.run();
        } finally {
            this.lock().writeLock().unlock();
        }
    }

    default <E extends Throwable> void lockWriteSafely(SafeRunnable<E> runnable) throws E {
        this.lock().writeLock().lock();
        try {
            runnable.run();
        } finally {
            this.lock().writeLock().unlock();
        }
    }

    default <T> @UnknownNullability T lockWrite(Supplier<@UnknownNullability T> supplier) {
        this.lock().writeLock().lock();
        try {
            return supplier.get();
        } finally {
            this.lock().writeLock().unlock();
        }
    }

    default <T> @UnknownNullability T lockRead(Supplier<@UnknownNullability T> supplier) {
        this.lock().readLock().lock();
        try {
            return supplier.get();
        } finally {
            this.lock().readLock().unlock();
        }
    }
}
