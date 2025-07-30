package net.desolatesky.block.handler;

public enum InteractionResult {

    /**
     * Indicates that this interaction has been handled and no further processing is needed.
     */
    CONSUME_INTERACTION,
    /**
     * Indicates that this interaction has not been handled and further processing should continue.
     */
    PASSTHROUGH

}
