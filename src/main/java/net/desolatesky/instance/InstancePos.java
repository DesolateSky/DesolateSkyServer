package net.desolatesky.instance;

import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.Instance;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import java.util.function.DoubleUnaryOperator;

// Point is sealed for some reason
public record InstancePos(@UnknownNullability Instance instance, Pos pos) /*implements Point*/ {

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

    public @NotNull InstancePos withX(@NotNull final DoubleUnaryOperator operator) {
        final double newX = operator.applyAsDouble(this.pos.x());
        return new InstancePos(this.instance, this.pos.withX(newX));
    }

    public @NotNull InstancePos withX(final double x) {
        return new InstancePos(this.instance, this.pos.withX(x));
    }

    public @NotNull InstancePos withY(@NotNull final DoubleUnaryOperator operator) {
        final double newY = operator.applyAsDouble(this.pos.y());
        return new InstancePos(this.instance, this.pos.withY(newY));
    }

    public @NotNull InstancePos withY(final double y) {
        return new InstancePos(this.instance, this.pos.withY(y));
    }

    public @NotNull InstancePos withZ(@NotNull final DoubleUnaryOperator operator) {
        final double newZ = operator.applyAsDouble(this.pos.z());
        return new InstancePos(this.instance, this.pos.withZ(newZ));
    }

    public @NotNull InstancePos withZ(final double z) {
        return new InstancePos(this.instance, this.pos.withZ(z));
    }

    public @NotNull InstancePos add(final double x, final double y, final double z) {
        return new InstancePos(this.instance, this.pos.add(x, y, z));
    }

    public @NotNull InstancePos add(@NotNull final Point point) {
        return new InstancePos(this.instance, this.pos.add(point));
    }

    public @NotNull InstancePos add(final double value) {
        return new InstancePos(this.instance, this.pos.add(value));
    }

    public @NotNull InstancePos sub(final double x, final double y, final double z) {
        return new InstancePos(this.instance, this.pos.sub(x, y, z));
    }

    public @NotNull InstancePos sub(@NotNull final Point point) {
        return new InstancePos(this.instance, this.pos.sub(point));
    }

    public @NotNull InstancePos sub(final double value) {
        return new InstancePos(this.instance, this.pos.sub(value));
    }

    public @NotNull InstancePos mul(final double x, final double y, final double z) {
        return new InstancePos(this.instance, this.pos.mul(x, y, z));
    }

    public @NotNull InstancePos mul(@NotNull final Point point) {
        return new InstancePos(this.instance, this.pos.mul(point));
    }

    public @NotNull InstancePos mul(final double value) {
        return new InstancePos(this.instance, this.pos.mul(value));
    }

    public @NotNull InstancePos div(final double x, final double y, final double z) {
        return new InstancePos(this.instance, this.pos.div(x, y, z));
    }

    public @NotNull InstancePos div(@NotNull final Point point) {
        return new InstancePos(this.instance, this.pos.div(point));
    }

    public @NotNull InstancePos div(final double value) {
        return new InstancePos(this.instance, this.pos.div(value));
    }

}
