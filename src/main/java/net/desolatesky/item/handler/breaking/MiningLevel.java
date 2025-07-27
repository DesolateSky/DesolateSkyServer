package net.desolatesky.item.handler.breaking;

public record MiningLevel(int level, double speedMultiplier) {

    public boolean isAtLeast(MiningLevel other) {
        return this.level >= other.level;
    }

    public boolean isAtLeast(int other) {
        return this.level >= other;
    }

}
