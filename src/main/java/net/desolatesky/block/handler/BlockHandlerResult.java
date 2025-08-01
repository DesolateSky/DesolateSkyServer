package net.desolatesky.block.handler;

import net.desolatesky.listener.EventHandlerResult;
import net.minestom.server.instance.block.Block;

public interface BlockHandlerResult {

    BlockHandlerResult PASS_THROUGH = Default.PASS_THROUGH;
    BlockHandlerResult CANCEL = Default.CANCEL;
    BlockHandlerResult CONSUME = Default.CONSUME;
    BlockHandlerResult CONSUME_CANCEL = Default.CONSUME_CANCEL;

    boolean cancelEvent();

    boolean consumeEvent();

    static Place passthroughPlace(Block resultBlock) {
        return new Place(false, false, resultBlock);
    }

    static Place cancelPlace(Block resultBlock, boolean consumeEvent) {
        return new Place(true, consumeEvent, resultBlock);
    }

    static Place consumePlace(Block resultBlock, boolean cancelEvent) {
        return new Place(cancelEvent, true, resultBlock);
    }

    record Place(
            boolean cancelEvent,
            boolean consumeEvent,
            Block resultBlock
    ) implements BlockHandlerResult {

    }

    default EventHandlerResult toEventHandlerResult() {
        if (this.consumeEvent()) {
            return EventHandlerResult.CONSUME_EVENT;
        }
        return EventHandlerResult.CONTINUE_LISTENING;
    }

    enum Default implements BlockHandlerResult {
        PASS_THROUGH(false, false),
        CANCEL(true, false),
        CONSUME(false, true),
        CONSUME_CANCEL(true, true);

        private final boolean cancelEvent;
        private final boolean consumeEvent;

        Default(boolean cancelEvent, boolean consumeEvent) {
            this.cancelEvent = cancelEvent;
            this.consumeEvent = consumeEvent;
        }

        public boolean cancelEvent() {
            return this.cancelEvent;
        }

        public boolean consumeEvent() {
            return this.consumeEvent;
        }

    }

}
