package net.desolatesky.util.functional;

import java.util.function.Function;

public interface SafeFunction<I, R, E extends Throwable> {

    static <I, R, E extends Throwable> SafeFunction<I, R, E> fromFunction(Function<I, R> function) {
        return function::apply;
    }

    R apply(I input) throws E;

}
