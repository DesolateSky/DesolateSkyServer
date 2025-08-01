package net.desolatesky.menu.action;

public interface ClickAction {

    ClickAction CANCEL = unused -> Result.CANCEL;

    Result onClick(ClickData data);

    record Result(boolean cancel, boolean allowOtherActions) {

        public static final Result ALLOW = new Result(false, true);
        public static final Result CANCEL = new Result(true, false);
        public static final Result CANCEL_AND_ALLOW_OTHER_ACTIONS = new Result(true, true);
        public static final Result DISALLOW_OTHER_ACTIONS = new Result(false, false);

    }

}
