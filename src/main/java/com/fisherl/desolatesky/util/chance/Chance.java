package com.fisherl.desolatesky.util.chance;

import java.util.random.RandomGenerator;

public final class Chance {

    private static final double MIN = 0;
    private static final double MAX = 100;

    public static boolean roll(RandomGenerator randomGenerator, double chance) {
        return randomGenerator.nextDouble(MIN, MAX) < chance;
    }

}
