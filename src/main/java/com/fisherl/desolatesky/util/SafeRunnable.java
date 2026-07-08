package com.fisherl.desolatesky.util;

public interface SafeRunnable<E extends Throwable> {

    static <E extends Throwable> SafeRunnable<E> fromRunnable(Runnable runnable) {
        return runnable::run;
    }

    void run() throws E;
}
