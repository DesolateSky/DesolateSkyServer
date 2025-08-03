package net.desolatesky.command.player;

import net.desolatesky.command.DSCommand;
import net.desolatesky.item.DSItemRegistry;
import net.desolatesky.item.tool.menu.ToolMenu;
import net.desolatesky.item.tool.registry.ToolPartRegistry;
import net.desolatesky.menu.item.MenuButton;
import net.desolatesky.player.DSPlayer;
import net.desolatesky.util.ComponentUtil;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

public class ToolMenuCommand extends DSCommand {

    public static final String PERMISSION = "desolatesky.command.toolmenu";

    private final DSItemRegistry itemRegistry;
    private final ToolPartRegistry toolPartRegistry;

    public ToolMenuCommand(DSItemRegistry itemRegistry, ToolPartRegistry toolPartRegistry) {
        super(PERMISSION, "toolmenu");

        this.itemRegistry = itemRegistry;
        this.toolPartRegistry = toolPartRegistry;

        this.setDefaultExecutor((sender, context) -> {
            if (!(sender instanceof DSPlayer player)) {
                return;
            }
            this.toolMenu(player);
        });
    }

    private void toolMenu(DSPlayer player) {
        final ToolMenu menu = ToolMenu.createToolStationMenu(
                ItemStack.of(Material.GRAY_STAINED_GLASS_PANE).withCustomName(ComponentUtil.noItalics("")),
                this.itemRegistry,
                this.toolPartRegistry,
                player
        );
        menu.open();
    }

}
