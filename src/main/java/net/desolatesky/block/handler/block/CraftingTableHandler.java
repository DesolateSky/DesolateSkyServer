package net.desolatesky.block.handler.block;

import net.desolatesky.block.handler.BlockHandlerResult;
import net.desolatesky.block.handler.DSBlockHandler;
import net.desolatesky.block.settings.DSBlockSettings;
import net.desolatesky.crafting.menu.CraftingMenu;
import net.desolatesky.instance.DSInstance;
import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.PlayerHand;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockFace;

public class CraftingTableHandler extends DSBlockHandler {

    public CraftingTableHandler() {
        super(DSBlockSettings.CRAFTING_TABLE);
    }

    @Override
    public BlockHandlerResult onPlayerInteract(Player player, DSInstance instance, Block block, Point blockPosition, PlayerHand hand, BlockFace face, Point cursorPosition) {
        if (player.isSneaking()) {
            return BlockHandlerResult.PASS_THROUGH;
        }
        final CraftingMenu craftingMenu = new CraftingMenu();
        player.openInventory(craftingMenu);
        return BlockHandlerResult.CONSUME_CANCEL;
    }

}
