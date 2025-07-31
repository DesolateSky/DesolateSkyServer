package net.desolatesky.item.tool.part;

import net.desolatesky.item.tool.ToolType;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public enum ToolPartType {

    // TOOLS
    AXE_HEAD(ToolType.TOOL),
    PICKAXE_HEAD(ToolType.TOOL),
    HOE_HEAD(ToolType.TOOL),
    SHOVEL_HEAD(ToolType.TOOL),
    HAMMER_HEAD(ToolType.TOOL),
    SICKLE_HEAD(ToolType.TOOL),
    // larger tools
    SCYTHE_HEAD(ToolType.TOOL),
    BROAD_AXE_HEAD(ToolType.TOOL),
    MACE_HEAD(ToolType.TOOL),

    // WEAPONS
    SWORD_BLADE(ToolType.TOOL),
    BOW_STRING(ToolType.BOW),
    BOW_LIMB(ToolType.BOW),
    FLETCHING(ToolType.AMMO),
    ARROW_HEAD(ToolType.AMMO),

    // MISC
    HANDLE(ToolType.TOOL, ToolType.BOW, ToolType.THROWABLE),
    BINDING(ToolType.TOOL, ToolType.BOW, ToolType.THROWABLE),;

    private final @Unmodifiable Set<ToolType> appliesTo;

    ToolPartType(ToolType first, ToolType... rest) {
        this.appliesTo = Collections.unmodifiableSet(EnumSet.of(first, rest));
    }

    ToolPartType(EnumSet<ToolType> appliesTo) {
        this.appliesTo = Collections.unmodifiableSet(appliesTo);
    }

    public boolean appliesTo(ToolType toolType) {
        return this.appliesTo.contains(toolType);
    }

    public @Unmodifiable Collection<ToolType> applies() {
        return this.appliesTo;
    }

}
