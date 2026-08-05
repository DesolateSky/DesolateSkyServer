package net.desolatesky.measurement;

public final class FluidValue extends MeasurementValue<FluidUnit, FluidValue> {

    private final FluidType fluidType;

    public FluidValue(FluidUnit unit, double value, FluidType fluidType) {
        super(unit, value);
        this.fluidType = fluidType;
    }

    @Override
    protected FluidValue newValue(double value) {
        return new FluidValue(this.unit, value, this.fluidType);
    }

    public FluidType fluidType() {
        return this.fluidType;
    }
}
