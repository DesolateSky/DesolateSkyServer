package net.desolatesky.measurement;

import org.jspecify.annotations.NonNull;

public abstract class MeasurementValue<U extends Unit<U>, T extends MeasurementValue<U, T>> implements Comparable<T> {

    protected final U unit;
    protected final double value;

    public MeasurementValue(U unit, double value) {
        this.unit = unit;
        this.value = value;
    }

    public U unit() {
        return this.unit;
    }

    public double value() {
        return this.value;
    }

    public T add(U unit, double value) {
        return this.newValue(this.value + unit.convertTo(this.unit, value));
    }

    public T sub(U unit, double value) {
        return this.newValue(this.value - unit.convertTo(this.unit, value));
    }

    public T mul(U unit, double value) {
        return this.newValue(this.value * unit.convertTo(this.unit, value));
    }

    public T add(T measurement) {
        return this.newValue(this.value + measurement.unit.convertTo(this.unit, measurement.value));
    }

    public T sub(T measurement) {
        return this.newValue(this.value - measurement.unit.convertTo(this.unit, measurement.value));
    }

    public T mul(T measurement) {
        return this.newValue(this.value * measurement.unit.convertTo(this.unit, measurement.value));
    }

    public double convertTo(U unit) {
        return this.unit.convertTo(unit, this.value);
    }

    protected abstract T newValue(double value);

    @Override
    public int compareTo(@NonNull T o) {
        final double converted = o.convertTo(this.unit);
        return Double.compare(this.value, converted);
    }
}
