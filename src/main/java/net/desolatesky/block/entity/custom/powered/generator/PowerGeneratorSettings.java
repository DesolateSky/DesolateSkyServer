package net.desolatesky.block.entity.custom.powered.generator;

public record PowerGeneratorSettings(
        int maxPower,
        int generationRate,
        int transferRate,
        int tickInterval
) {
}
