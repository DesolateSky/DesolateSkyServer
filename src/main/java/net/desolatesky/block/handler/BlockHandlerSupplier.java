package net.desolatesky.block.handler;

import com.google.common.base.Preconditions;
import net.desolatesky.DesolateSkyServer;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;
import java.util.function.Supplier;

public final class BlockHandlerSupplier<T extends DSBlockHandler> implements Keyed {

    private final Key key;
    private final Function<DesolateSkyServer, Supplier<T>> function;

    public BlockHandlerSupplier(Key key, Function<DesolateSkyServer, Supplier<T>> function) {
        this.key = key;
        this.function = function;
    }

    public BlockHandlerSupplier(T handler) {
        Preconditions.checkArgument(handler.stateless(), "Handler instance constructor can only be used for stateless block handlers");
        this.key = handler.getKey();
        this.function = _ -> () -> handler;
    }

    public Supplier<T> getSupplier(DesolateSkyServer server) {
        return new CachedSupplier(this.function.apply(server));
    }

    @Override
    public @NotNull Key key() {
        return this.key;
    }

    private class CachedSupplier implements Supplier<T> {

        private final Supplier<T> internal;
        private T cached;

        public CachedSupplier(Supplier<T> internal) {
            this.internal = internal;
        }

        @Override
        public T get() {
            if (this.cached != null && this.cached.stateless()) {
                return this.cached;
            }
            this.cached = this.internal.get();
            return this.cached;
        }

    }
}
