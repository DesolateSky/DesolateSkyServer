package net.desolatesky.listener;

public enum EventHandlerResult {

    CONTINUE_LISTENING,
    CONSUME_EVENT;

    public boolean consumes() {
        return this == CONSUME_EVENT;
    }

}
