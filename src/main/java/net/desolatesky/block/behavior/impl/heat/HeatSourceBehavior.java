package net.desolatesky.block.behavior.impl.heat;

import net.desolatesky.block.behavior.BlockBehavior;
import net.desolatesky.measurement.TemperatureValue;
import net.desolatesky.world.DSWorld;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.block.Block;

public interface HeatSourceBehavior extends BlockBehavior {

    TemperatureValue getTemperature(DSWorld world, Point blockPos, Block block);

}
