package net.desolatesky.item.tool.action;

import net.desolatesky.DesolateSkyServer;
import net.desolatesky.block.DSBlockRegistry;
import net.desolatesky.instance.DSInstance;
import net.desolatesky.item.DSItemRegistry;
import net.desolatesky.player.DSPlayer;
import net.minestom.server.entity.PlayerHand;
import net.minestom.server.item.ItemStack;

public abstract class ToolActionData {

    protected final DSPlayer player;
    protected final DSInstance instance;
    protected final ItemStack toolUsed;
    protected final PlayerHand hand;
    protected boolean cancelled = false;

    /**
     * @param toolUsed          the {@link ItemStack} used to actually break the block.
     */
    public ToolActionData(DSPlayer player, DSInstance instance, ItemStack toolUsed, PlayerHand hand) {
        this.player = player;
        this.instance = instance;
        this.toolUsed = toolUsed;
        this.hand = hand;
    }

    public DSPlayer player() {
        return this.player;
    }

    public DSInstance instance() {
        return this.instance;
    }

    public ItemStack toolUsed() {
        return this.toolUsed;
    }

    public PlayerHand hand() {
        return this.hand;
    }

    public boolean cancelled() {
        return this.cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public DesolateSkyServer getServer() {
        return this.player.desolateSkyServer();
    }

    public DSBlockRegistry getBlockRegistry() {
        return this.getServer().blockRegistry();
    }

    public DSItemRegistry getItemRegistry() {
        return this.getServer().itemRegistry();
    }

    public static final class Result {

        public static final Result CONSUME_CANCEL = new Result(true, true);
        public static final Result CONSUME_NOT_CANCEL = new Result(true, false);
        public static final Result NOT_CONSUME_CANCEL = new Result(false, true);
        public static final Result NOT_CONSUME_NOT_CANCEL = new Result(false, false);

        public static Result create(boolean consumeInteraction, boolean cancelEvent) {
            if (consumeInteraction && cancelEvent) {
                return CONSUME_CANCEL;
            } else if (consumeInteraction) {
                return CONSUME_NOT_CANCEL;
            } else if (cancelEvent) {
                return NOT_CONSUME_CANCEL;
            } else {
                return NOT_CONSUME_NOT_CANCEL;
            }
        }

        private final boolean consumeInteraction;
        private final boolean cancelEvent;

        private Result(boolean consumeInteraction, boolean cancelEvent) {
            this.consumeInteraction = consumeInteraction;
            this.cancelEvent = cancelEvent;
        }

        public boolean consumeInteraction() {
            return this.consumeInteraction;
        }

        public boolean cancelEvent() {
            return this.cancelEvent;
        }

    }

}
