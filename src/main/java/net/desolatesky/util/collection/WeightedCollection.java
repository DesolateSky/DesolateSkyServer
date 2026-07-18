package net.desolatesky.util.collection;

import net.desolatesky.util.Pair;

import java.util.Collection;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.random.RandomGenerator;

// https://stackoverflow.com/a/6409791
public final class WeightedCollection<E> {

    private final NavigableMap<Double, E> map = new TreeMap<>();
    private double total = 0;

    public WeightedCollection() {
    }

    public static <E> WeightedCollection<E> of(Collection<Pair<E, Double>> weights) {
        final WeightedCollection<E> collection = new WeightedCollection<>();
        for (Pair<E, Double> pair : weights) {
            collection.add(pair.second(), pair.first());
        }
        return collection;
    }

    public WeightedCollection<E> add(double weight, E result) {
        if (weight <= 0) {
            throw new IllegalArgumentException("Weight must be greater than 0");
        }
        this.total += weight;
        this.map.put(this.total, result);
        return this;
    }

    public E next(RandomGenerator random) {
        final double value = random.nextDouble() * this.total;
        return this.map.higherEntry(value).getValue();
    }

    public Pair<E, WeightedCollection<E>> nextWithRemaining(RandomGenerator random) {
        final double value = random.nextDouble() * this.total;
        final var entry = this.map.higherEntry(value);
        if (entry == null) {
            throw new IllegalStateException("No entry found for the generated value: " + value);
        }
        final E result = entry.getValue();
        final WeightedCollection<E> copy = this.copy();
        copy.map.remove(entry.getKey());
        copy.total -= entry.getKey();
        return new Pair<>(result, copy);
    }

    public WeightedCollection<E> copy() {
        final WeightedCollection<E> copy = new WeightedCollection<>();
        for (final var entry : this.map.entrySet()) {
            copy.add(entry.getKey(), entry.getValue());
        }
        return copy;
    }
}