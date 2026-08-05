package net.desolatesky.measurement;

public enum FluidUnit implements Unit<FluidUnit> {

    BOTTLE(0.3),
    BUCKET(1);

    private final double buckets;

    FluidUnit(double buckets) {
        this.buckets = buckets;
    }

    public double buckets() {
        return this.buckets;
    }

    public double getBucketValue(double value) {
        return value * this.buckets;
    }

    @Override
    public double convertTo(FluidUnit unit, double value) {
        final double buckets = this.getBucketValue(value);
        return value / buckets;
    }
}
