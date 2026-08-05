package net.desolatesky.measurement;

public enum TemperatureUnit implements Unit<TemperatureUnit> {

    CELSIUS;

    @Override
    public double convertTo(TemperatureUnit unit, double value) {
        return value;
    }

}
