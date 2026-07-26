package net.desolatesky.player;

import net.desolatesky.data.definition.DataTranslator;
import net.desolatesky.player.data.PlayerDataDefinitionV1;
import net.desolatesky.world.pos.WorldPosition;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.UUID;

public final class DSPlayerData {

    public static final DataTranslator<DSPlayerData> DATA_TRANSLATOR = new DataTranslator<>(List.of(
            new PlayerDataDefinitionV1()
    ));

    private final UUID uuid;
    private final @Nullable UUID islandId;
    private final @Unmodifiable List<ItemStack> inventory;
    private final @Nullable WorldPosition logoutPos;

    public DSPlayerData(
            UUID uuid,
            @Nullable UUID islandId,
            List<ItemStack> inventory,
            @Nullable WorldPosition logoutPos

    ) {
        this.uuid = uuid;
        this.islandId = islandId;
        this.inventory = List.copyOf(inventory);
        this.logoutPos = logoutPos;
    }

    public UUID uuid() {
        return this.uuid;
    }

    public @Nullable UUID islandId() {
        return this.islandId;
    }

    public @Unmodifiable List<ItemStack> inventory() {
        return this.inventory;
    }

    public @Nullable WorldPosition logoutPos() {
        return this.logoutPos;
    }
}
