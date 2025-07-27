package net.desolatesky.item.handler.breaking.calculator;

public final class BreakTimeCalculators {

    private BreakTimeCalculators() {
        throw new UnsupportedOperationException();
    }

    public static final BreakTimeCalculator AXE = new CategoryBreakTimeCalculator(0.5);

}
