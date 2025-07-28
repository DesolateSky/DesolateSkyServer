package net.desolatesky.block.handler.vanilla;

import net.desolatesky.DesolateSkyServer;
import net.desolatesky.block.BlockKeys;
import net.desolatesky.block.handler.TransientBlockHandler;
import net.desolatesky.block.settings.DSBlockSettings;
import net.desolatesky.crafting.menu.CraftingMenu;
import net.desolatesky.instance.DSInstance;
import net.desolatesky.player.DSPlayer;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;

public class CraftingTableHandler extends TransientBlockHandler {

    public static final Key KEY = BlockKeys.CRAFTING_TABLE;

    public CraftingTableHandler(DesolateSkyServer server) {
        super(server, DSBlockSettings.CRAFTING_TABLE);
    }

    @Override
    public boolean onInteract(@NotNull Interaction interaction, DSInstance instance) {
        final DSPlayer player = (DSPlayer) interaction.getPlayer();
        if (player.isSneaking()) {
            return false;
        }
        final CraftingMenu craftingMenu = new CraftingMenu();
        player.openInventory(craftingMenu);
        return true;
    }

}
