package net.desolatesky.item.tool.part;

import net.desolatesky.item.ItemKeys;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum ToolPartType {

    // TOOLS
    AXE_HEAD(0, ItemKeys.AXE),
    PICKAXE_HEAD(0, ItemKeys.PICKAXE),
    HOE_HEAD(0, ItemKeys.HOE),
    SHOVEL_HEAD(0, ItemKeys.SHOVEL),
    SICKLE_HEAD(0, ItemKeys.SICKLE),
    // larger tools
    SCYTHE_HEAD(0, ItemKeys.SCYTHE),
    BROAD_AXE_HEAD(0, ItemKeys.BROAD_AXE),
    MACE_HEAD(0, ItemKeys.MACE),
    HAMMER_HEAD(0, ItemKeys.HAMMER),

    // WEAPONS
    SWORD_BLADE(0, ItemKeys.SWORD),
    BOW_STRING(0, ItemKeys.BOW),
    BOW_LIMB(0),
    ARROW_HEAD(0, ItemKeys.ARROW),
    FLETCHING(1),

    // MISC
    HANDLE(2),
    BINDING(1),
    LARGE_BINDING(1),
    LARGE_HANDLE(2);

    /**
     * Used to determine display order on tool descriptions
     */
    private final int order;
    private final @Nullable Key craftsIntoItem;

    ToolPartType(int order, @Nullable Key craftsIntoItem) {
        this.order = order;
        this.craftsIntoItem = craftsIntoItem;
    }

    ToolPartType(int order) {
        this(order, null);
    }

    public @Nullable Key craftsIntoItem() {
        return this.craftsIntoItem;
    }

    private static @Unmodifiable Map<ToolPartType, Integer> requirements(ToolPartType... parts) {
        final Map<ToolPartType, Integer> requirements = new HashMap<>();
        for (final ToolPartType part : parts) {
            requirements.put(part, 1);
        }
        return Map.copyOf(requirements);
    }

    public static final @Unmodifiable Map<ToolPartType, Integer> AXE_HEAD_REQUIREMENTS = requirements(ToolPartType.HANDLE, ToolPartType.BINDING);
    public static final @Unmodifiable Map<ToolPartType, Integer> PICKAXE_HEAD_REQUIREMENTS = requirements(ToolPartType.HANDLE, ToolPartType.BINDING);
    public static final @Unmodifiable Map<ToolPartType, Integer> HOE_HEAD_REQUIREMENTS = requirements(ToolPartType.HANDLE, ToolPartType.BINDING);
    public static final @Unmodifiable Map<ToolPartType, Integer> SHOVEL_HEAD_REQUIREMENTS = requirements(ToolPartType.HANDLE, ToolPartType.BINDING);
    public static final @Unmodifiable Map<ToolPartType, Integer> SICKLE_HEAD_REQUIREMENTS = requirements(ToolPartType.HANDLE, ToolPartType.BINDING);
    public static final @Unmodifiable Map<ToolPartType, Integer> HAMMER_HEAD_REQUIREMENTS = requirements(ToolPartType.LARGE_HANDLE, ToolPartType.LARGE_BINDING);
    public static final @Unmodifiable Map<ToolPartType, Integer> SCYTHE_HEAD_REQUIREMENTS = requirements(ToolPartType.LARGE_HANDLE, ToolPartType.LARGE_BINDING);
    public static final @Unmodifiable Map<ToolPartType, Integer> BROAD_AXE_HEAD_REQUIREMENTS = requirements(ToolPartType.LARGE_HANDLE, ToolPartType.LARGE_BINDING);
    public static final @Unmodifiable Map<ToolPartType, Integer> MACE_HEAD_REQUIREMENTS = requirements(ToolPartType.LARGE_HANDLE, ToolPartType.LARGE_BINDING);
    public static final @Unmodifiable Map<ToolPartType, Integer> SWORD_BLADE_REQUIREMENTS = requirements(ToolPartType.HANDLE, ToolPartType.BINDING);
    public static final @Unmodifiable Map<ToolPartType, Integer> BOW_STRING_REQUIREMENTS = requirements(ToolPartType.BOW_LIMB);
    public static final @Unmodifiable Map<ToolPartType, Integer> ARROW_HEAD_REQUIREMENTS = requirements(ToolPartType.HANDLE, ToolPartType.FLETCHING);

    public @Unmodifiable Map<ToolPartType, Integer> getRequirements() {
        return switch (this) {
            case AXE_HEAD -> AXE_HEAD_REQUIREMENTS;
            case PICKAXE_HEAD -> PICKAXE_HEAD_REQUIREMENTS;
            case HOE_HEAD -> HOE_HEAD_REQUIREMENTS;
            case SHOVEL_HEAD -> SHOVEL_HEAD_REQUIREMENTS;
            case SICKLE_HEAD -> SICKLE_HEAD_REQUIREMENTS;
            case HAMMER_HEAD -> HAMMER_HEAD_REQUIREMENTS;
            case SCYTHE_HEAD -> SCYTHE_HEAD_REQUIREMENTS;
            case BROAD_AXE_HEAD -> BROAD_AXE_HEAD_REQUIREMENTS;
            case MACE_HEAD -> MACE_HEAD_REQUIREMENTS;
            case SWORD_BLADE -> SWORD_BLADE_REQUIREMENTS;
            case BOW_STRING -> BOW_STRING_REQUIREMENTS;
            case ARROW_HEAD -> ARROW_HEAD_REQUIREMENTS;
            default -> Collections.emptyMap();
        };
    }

    public boolean isHead() {
        return switch (this) {
            case AXE_HEAD, PICKAXE_HEAD, HOE_HEAD, SHOVEL_HEAD, SICKLE_HEAD,
                 SCYTHE_HEAD, BROAD_AXE_HEAD, MACE_HEAD, SWORD_BLADE, ARROW_HEAD -> true;
            default -> false;
        };
    }

    public boolean meetsRequirements(Map<ToolPartType, Integer> parts) {
        final Map<ToolPartType, Integer> requirements = this.getRequirements();
        if (requirements.isEmpty()) {
            return true;
        }
        if (parts.size() != requirements.size()) {
            return false;
        }
        for (final Map.Entry<ToolPartType, Integer> entry : requirements.entrySet()) {
            final Integer amount = parts.get(entry.getKey());
            if (amount == null || amount < entry.getValue()) {
                return false;
            }
        }
        return true;
    }

    public int order() {
        return this.order;
    }


}
