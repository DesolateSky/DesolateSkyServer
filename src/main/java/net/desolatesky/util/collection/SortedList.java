package net.desolatesky.util.collection;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

public class SortedList<E> implements List<E> {

    private final List<E> internal;
    private final Comparator<E> comparator;

    public SortedList(Collection<E> collection, Comparator<E> comparator) {
        this.internal = new ArrayList<>(collection);
        this.comparator = comparator;
        this.internal.sort(comparator);
    }

    public SortedList(Comparator<E> comparator) {
        this.internal = new ArrayList<>();
        this.comparator = comparator;
    }

    public boolean add(E element) {
        if (this.internal.isEmpty()) {
            this.internal.add(element);
            return true;
        }
        for (int i = 0; i < this.internal.size(); i++) {
            if (this.comparator.compare(this.internal.get(i), element) > 0) {
                this.internal.add(i, element);
                return true;
            }
        }
        this.internal.add(element);
        return true;
    }

    @Override
    public int size() {
        return this.internal.size();
    }

    @Override
    public boolean isEmpty() {
        return this.internal.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return this.internal.contains(o);
    }

    @Override
    public @NotNull java.util.Iterator<E> iterator() {
        return new Iterator<>(this.internal.iterator());
    }

    @Override
    public @NotNull Object @NotNull [] toArray() {
        return this.internal.toArray();
    }

    @Override
    public @NotNull <T> T @NotNull [] toArray(@NotNull T @NotNull [] a) {
        return this.internal.toArray(a);
    }

    @Override
    public boolean remove(Object o) {
        return this.internal.remove(o);
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> c) {
        return new HashSet<>(this.internal).containsAll(c);
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends E> c) {
        final boolean modified = this.internal.addAll(c);
        if (modified) {
            this.internal.sort(this.comparator);
        }
        return modified;
    }

    @Override
    public boolean addAll(int index, @NotNull Collection<? extends E> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> c) {
        return this.internal.removeAll(c);
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        return this.internal.retainAll(c);
    }

    @Override
    public void clear() {
        this.internal.clear();
    }

    @Override
    public E get(int index) {
        return this.internal.get(index);
    }

    @Override
    public E set(int index, E element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(int index, E element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public E remove(int index) {
        return this.internal.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return this.internal.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return this.internal.lastIndexOf(o);
    }

    @Override
    public @NotNull java.util.ListIterator<E> listIterator() {
        return new ListIterator<>(this.internal.listIterator());
    }

    @Override
    public @NotNull java.util.ListIterator<E> listIterator(int index) {
        return new ListIterator<>(this.internal.listIterator(index));
    }

    @Override
    public @NotNull List<E> subList(int fromIndex, int toIndex) {
        return List.of();
    }

    private static class Iterator<E> implements java.util.Iterator<E> {

        private final java.util.Iterator<E> internalIterator;

        private Iterator(java.util.Iterator<E> internalIterator) {
            this.internalIterator = internalIterator;
        }

        @Override
        public boolean hasNext() {
            return this.internalIterator.hasNext();
        }

        @Override
        public E next() {
            return this.internalIterator.next();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

    }

    private static class ListIterator<E> implements java.util.ListIterator<E> {

        private final java.util.ListIterator<E> internalIterator;

        private ListIterator(java.util.ListIterator<E> internalIterator) {
            this.internalIterator = internalIterator;
        }

        @Override
        public boolean hasNext() {
            return this.internalIterator.hasNext();
        }

        @Override
        public E next() {
            return this.internalIterator.next();
        }

        @Override
        public boolean hasPrevious() {
            return this.internalIterator.hasPrevious();
        }

        @Override
        public E previous() {
            return this.internalIterator.previous();
        }

        @Override
        public int nextIndex() {
            return this.internalIterator.nextIndex();
        }

        @Override
        public int previousIndex() {
            return this.internalIterator.previousIndex();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void set(E e) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(E e) {
            throw new UnsupportedOperationException();
        }
    }
}
