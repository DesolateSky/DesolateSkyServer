package net.desolatesky.block.handler;

import com.google.common.base.Preconditions;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public final class BlockHandlerSupplier<T extends DSBlockHandler> implements Supplier<T>, Keyed {

    private final Key key;
    private final Supplier<T> supplier;
    private T cached;

    public BlockHandlerSupplier(Key key, Supplier<T> supplier) {
        this.key = key;
        this.supplier = supplier;
    }

    public BlockHandlerSupplier(T handler) {
        Preconditions.checkArgument(handler.stateless(), "Handler instance constructor can only be used for stateless block handlers");
        this.key = handler.getKey();
        this.supplier = () -> handler;
    }

    @Override
    public T get() {
        if (this.cached != null && this.cached.stateless()) {
            return this.cached;
        }
        this.cached = this.supplier.get();
        return this.cached;
    }

    @Override
    public @NotNull Key key() {
        return this.key;
    }
}
