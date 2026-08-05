package net.desolatesky.measurement;

public final class TemperatureValue extends MeasurementValue<TemperatureUnit, TemperatureValue> {

    public TemperatureValue(TemperatureUnit unit, double value) {
        super(unit, value);
    }

    @Override
    protected TemperatureValue newValue(double value) {
        return new TemperatureValue(this.unit, value);
    }
}
