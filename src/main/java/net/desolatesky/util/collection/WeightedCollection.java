package net.desolatesky.util.collection;

import java.util.Collection;
import java.util.NavigableMap;
import java.util.SplittableRandom;
import java.util.TreeMap;
import java.util.random.RandomGenerator;

// https://stackoverflow.com/a/6409791
public final class WeightedCollection<E> {

    private final NavigableMap<Double, E> map = new TreeMap<>();
    private final RandomGenerator random;
    private double total = 0;

    public WeightedCollection() {
        this(new SplittableRandom());
    }

    public WeightedCollection(RandomGenerator random) {
        this.random = random;
    }

    public static <E> WeightedCollection<E> of(Collection<Pair<E, Double>> weights) {
        return of(new SplittableRandom(), weights);
    }

    public static <E> WeightedCollection<E> of(RandomGenerator random, Collection<Pair<E, Double>> weights) {
        final WeightedCollection<E> collection = new WeightedCollection<>(random);
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

    public E next() {
        final double value = this.random.nextDouble() * this.total;
        return this.map.higherEntry(value).getValue();
    }

}
