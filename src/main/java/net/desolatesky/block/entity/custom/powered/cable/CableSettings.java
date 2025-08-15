package net.desolatesky.block.entity.custom.powered.cable;

import net.minestom.server.instance.block.Block;

public record CableSettings(Block outputDisplayBlock, Block inputDisplayBlock, int maxPower, int transferRate) {
}
