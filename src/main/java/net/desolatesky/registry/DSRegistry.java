package net.desolatesky.registry;

import net.kyori.adventure.key.Key;
import net.minestom.server.instance.block.Block;
import net.minestom.server.tag.Tag;

import java.util.HashMap;
import java.util.Map;

public abstract class DSRegistry<E> {

    protected final Tag<Key> idTag;
    protected final Map<Key, E> elements = new HashMap<>();

    public DSRegistry(Tag<Key> idTag) {
        this.idTag = idTag;
    }

    public E register(Key id, E element) {
        final E actual = this.withTag(element, this.idTag, id);
        this.elements.put(id, actual);
        return actual;
    }

    public E register(E element) {
        final Key key = this.getId(element);
        return this.register(key, element);
    }

    public E get(Key key) {
        return this.elements.getOrDefault(key, this.getDefault(key));
    }

    public E get(E element) {
        return this.get(this.getId(element));
    }

    protected abstract <T> E withTag(E element, Tag<T> tag, T value);

    protected abstract <T> T getTag(E element, Tag<T> tag);

    protected abstract E getDefault(Key key);

    protected abstract Key getId(E element);

}
