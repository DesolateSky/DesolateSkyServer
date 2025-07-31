package net.desolatesky.util;

public enum PhysicalClickType {

    RIGHT_CLICK,
    LEFT_CLICK,
    MIDDLE_CLICK,
    SHIFT_RIGHT_CLICK,
    SHIFT_LEFT_CLICK,
    SHIFT_MIDDLE_CLICK,
    NONE;

    public boolean isShiftClick() {
        return this == SHIFT_RIGHT_CLICK || this == SHIFT_LEFT_CLICK || this == SHIFT_MIDDLE_CLICK;
    }

    public boolean isRightClick() {
        return this == RIGHT_CLICK || this == SHIFT_RIGHT_CLICK;
    }

    public boolean isLeftClick() {
        return this == LEFT_CLICK || this == SHIFT_LEFT_CLICK;
    }

    public boolean isMiddleClick() {
        return this == MIDDLE_CLICK || this == SHIFT_MIDDLE_CLICK;
    }

}
