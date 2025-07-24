package net.desolatesky.instance;

import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.Instance;
import org.jetbrains.annotations.UnknownNullability;

public record InstancePoint<P extends Point>(@UnknownNullability Instance instance, P pos) {

    public double x() {
        return this.pos.x();
    }

    public double y() {
        return this.pos.y();
    }

    public double z() {
        return this.pos.z();
    }

    public int blockX() {
        return this.pos.blockX();
    }

    public int blockY() {
        return this.pos.blockY();
    }

    public int blockZ() {
        return this.pos.blockZ();
    }

}
