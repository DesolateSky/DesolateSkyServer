package net.desolatesky.world.dimension;

import net.desolatesky.util.Constants;
import net.desolatesky.util.Namespace;
import net.kyori.adventure.key.Key;
import net.minestom.server.color.Color;
import net.minestom.server.world.DimensionType;
import net.minestom.server.world.attribute.EnvironmentAttribute;

public final class VoidDimension {

    private VoidDimension() {
        throw new UnsupportedOperationException();
    }

    public static final Key KEY = Namespace.key("void");

    public static final DimensionType INSTANCE = DimensionType.builder()
            .ambientLight(0.7f)
            .cardinalLight(DimensionType.CardinalLight.DEFAULT)
            .ceiling(false)
            .coordinateScale(1)
            .enderDragonFight(false)
            .fixedTime(true)
            .minY(Constants.WORLD_MIN_Y)
            .height(Constants.WORLD_MAX_Y - Constants.WORLD_MIN_Y + 1)
            .logicalHeight(Constants.WORLD_MAX_Y - Constants.WORLD_MIN_Y + 1)
            .skybox(DimensionType.Skybox.END)
            .skylight(true)
            .setAttribute(EnvironmentAttribute.SKY_COLOR, new Color(0, 0, 255))
            .build();
}
