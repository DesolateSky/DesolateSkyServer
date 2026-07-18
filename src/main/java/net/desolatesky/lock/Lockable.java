package net.desolatesky.lock;

import net.desolatesky.util.SafeRunnable;

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

    default <T> T lockWrite(Supplier<T> supplier) {
        this.lock().writeLock().lock();
        try {
            return supplier.get();
        } finally {
            this.lock().writeLock().unlock();
        }
    }

    default <T> T lockRead(Supplier<T> supplier) {
        this.lock().readLock().lock();
        try {
            return supplier.get();
        } finally {
            this.lock().readLock().unlock();
        }
    }
}
