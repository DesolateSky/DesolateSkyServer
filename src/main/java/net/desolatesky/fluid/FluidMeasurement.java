package net.desolatesky.fluid;

public enum FluidMeasurement {

    BOTTLE(0.3),
    BUCKET(1);

    private final double buckets;

    FluidMeasurement(double buckets) {
        this.buckets = buckets;
    }

    public double buckets() {
        return this.buckets;
    }
}
