package net.desolatesky.util.functional;

import java.util.function.Consumer;

public interface SafeConsumer<I, E extends Throwable> {

    static <I, E extends Throwable> SafeConsumer<I, E> fromConsumer(Consumer<I> consumer) {
        return consumer::accept;
    }

    void accept(I input) throws E;

}
