package net.desolatesky.item.behavior.impl;

import net.desolatesky.block.BlockAttributes;
import net.desolatesky.block.behavior.BlockBehavior;
import net.desolatesky.block.behavior.PlaceRequirementsBehavior;
import net.desolatesky.block.behavior.impl.PlaceBehavior;
import net.desolatesky.block.definition.BlockDefinition;
import net.desolatesky.item.behavior.ClickBehavior;
import net.desolatesky.player.DSPlayer;
import net.desolatesky.util.InventoryUtil;
import net.desolatesky.util.Namespace;
import net.desolatesky.world.DSWorld;
import net.kyori.adventure.key.Key;
import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.PlayerHand;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public final class FlintBehavior implements ClickBehavior {

    // temp until items are configurable
    private final Key fireBlockId = Namespace.minecraftKey("fire");

    @Override
    public void onRightClick(DSWorld world, DSPlayer player, PlayerHand hand, ItemStack clickedWith, @Nullable Point clickedPos, @Nullable Block clickedBlock) {
        if (clickedPos == null || clickedBlock == null) {
            return;
        }
        final BlockDefinition blockDefinition = world.blockFactory().getBlockDefinition(clickedBlock);
        if (blockDefinition == null) {
            return;
        }
        if (!blockDefinition.hasAttribute(BlockAttributes.FIRE_STARTER)) {
            return;
        }
        final BlockDefinition fireDefinition = world.blockFactory().getBlockDefinition(this.fireBlockId);
        if (fireDefinition == null) {
            return;
        }
        final PlaceRequirementsBehavior requirementsBehavior = fireDefinition.getBehavior(BlockBehavior.Type.PLACE_REQUIREMENTS);
        if (requirementsBehavior != null && !requirementsBehavior.isValidForInitialPlace(world, clickedPos, clickedBlock)) {
            return;
        }
        final PlaceBehavior placeBehavior = fireDefinition.getBehavior(BlockBehavior.Type.PLACE);
        Block toPlace = fireDefinition.createBlock();
        if (placeBehavior != null) {
            toPlace = placeBehavior.getBlockToPlace(world, clickedPos, toPlace, this.fireBlockId);
        }
        InventoryUtil.subtractFromHeldItem(player, hand, 1);
        world.setBlock(clickedPos, toPlace);
    }

    @Override
    public void onLeftClick(DSWorld world, DSPlayer player, PlayerHand hand, ItemStack clickedWith, @Nullable Point clickedPos, @Nullable Block clickedBlock) {

    }
}
