package net.desolatesky.block.handler;

import net.desolatesky.listener.EventHandlerResult;
import net.minestom.server.instance.block.Block;
import org.jetbrains.annotations.Nullable;

public interface BlockHandlerResult {

    BlockHandlerResult PASS_THROUGH = Default.PASS_THROUGH;
    BlockHandlerResult CANCEL = Default.CANCEL;
    BlockHandlerResult CONSUME = Default.CONSUME;
    BlockHandlerResult CONSUME_CANCEL = Default.CONSUME_CANCEL;

    boolean cancelEvent();

    boolean consumeEvent();

    static Place passthroughPlace() {
        return Place.PASSTHROUGH_NULL;
    }

    static Place passthroughPlace(@Nullable Block resultBlock) {
        return new Place(false, false, resultBlock);
    }

    static Place cancelPlace(@Nullable Block resultBlock, boolean consumeEvent) {
        return new Place(true, consumeEvent, resultBlock);
    }

    static Place consumePlace(@Nullable Block resultBlock, boolean cancelEvent) {
        return new Place(cancelEvent, true, resultBlock);
    }

    static InteractBlock passthroughInteractBlock() {
        return InteractBlock.PASSTHROUGH_NULL;
    }

    static InteractBlock passthroughInteractBlock(@Nullable Block resultBlock) {
        return new InteractBlock(false, false, resultBlock);
    }

    static InteractBlock cancelInteractBlock(@Nullable Block resultBlock, boolean consumeEvent) {
        return new InteractBlock(true, consumeEvent, resultBlock);
    }

    static InteractBlock consumeInteractBlock(@Nullable Block resultBlock, boolean cancelEvent) {
        return new InteractBlock(cancelEvent, true, resultBlock);
    }

    record Place(
            boolean cancelEvent,
            boolean consumeEvent,
            @Nullable Block resultBlock
    ) implements BlockHandlerResult {

        public static final Place PASSTHROUGH_NULL = new Place(false, false, null);

    }

    record InteractBlock(
            boolean cancelEvent,
            boolean consumeEvent,
            @Nullable Block resultBlock
    ) implements BlockHandlerResult {

        public static final InteractBlock PASSTHROUGH_NULL = new InteractBlock(false, false, null);

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
