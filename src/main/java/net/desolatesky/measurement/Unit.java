package net.desolatesky.measurement;

public interface Unit<U extends Unit<U>> {

    double convertTo(U unit, double value);

}
