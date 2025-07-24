package net.desolatesky.command.player;

import net.desolatesky.command.DSCommand;
import net.desolatesky.crafting.menu.CraftingMenu;
import net.desolatesky.player.DSPlayer;

public final class CraftCommand extends DSCommand {

    public static final String PERMISSION = "desolatesky.command.craft";

    public CraftCommand() {
        super(PERMISSION, "craft");

        this.setDefaultExecutor((sender, context) -> {
            if (!(sender instanceof DSPlayer player)) {
                return;
            }
            this.craft(player);
        });
    }

    private void craft(DSPlayer player) {
        final CraftingMenu menu = new CraftingMenu();
        player.openInventory(menu);
    }

}
