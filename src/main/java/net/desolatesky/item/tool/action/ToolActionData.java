package net.desolatesky.item.tool.action;

import net.desolatesky.DesolateSkyServer;
import net.desolatesky.block.DSBlockRegistry;
import net.desolatesky.instance.DSInstance;
import net.desolatesky.item.DSItemRegistry;
import net.desolatesky.player.DSPlayer;
import net.minestom.server.item.ItemStack;

public abstract class ToolActionData {

    protected final DSPlayer player;
    protected final DSInstance instance;
    protected final ItemStack toolUsed;
    protected final int amountOfToolsUsed;
    protected boolean cancelled = false;

    /**
     * @param toolUsed          the {@link ItemStack} used to actually break the block.
     * @param amountOfToolsUsed A tool can have multiple of the same part, for example 2 diamond pickaxe heads.
     *                          This is the amount of tools used, so if you have 2 diamond pickaxe heads, this will be 2.
     */
    public ToolActionData(DSPlayer player, DSInstance instance, ItemStack toolUsed, int amountOfToolsUsed) {
        this.player = player;
        this.instance = instance;
        this.toolUsed = toolUsed;
        this.amountOfToolsUsed = amountOfToolsUsed;
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

    public int amountOfToolsUsed() {
        return this.amountOfToolsUsed;
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
