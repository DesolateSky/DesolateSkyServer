package net.desolatesky.block.handler;

import net.desolatesky.instance.team.TeamInstance;
import net.kyori.adventure.key.Key;
import net.minestom.server.instance.block.BlockHandler;
import org.jetbrains.annotations.NotNull;

public class DSBlockHandler implements BlockHandler {

    private final Key key;

    public DSBlockHandler(Key key) {
        this.key = key;
    }

    @Override
    public void onPlace(@NotNull Placement placement) {
        if (!(placement.getInstance() instanceof final TeamInstance instance)) {
            return;
        }
        this.onPlace(placement, instance);
    }

    public void onPlace(@NotNull Placement placement, TeamInstance instance) {

    }

    @Override
    public void onDestroy(@NotNull Destroy destroy) {
        if (!(destroy.getInstance() instanceof final TeamInstance instance)) {
            return;
        }
        this.onDestroy(destroy, instance);
    }

    public void onDestroy(@NotNull Destroy destroy, TeamInstance instance) {

    }

    @Override
    public boolean onInteract(@NotNull Interaction interaction) {
        if (!(interaction.getInstance() instanceof final TeamInstance instance)) {
            return BlockHandler.super.onInteract(interaction);
        }
        return this.onInteract(interaction, instance);
    }

    public boolean onInteract(@NotNull Interaction interaction, TeamInstance instance) {
        return BlockHandler.super.onInteract(interaction);
    }

    @Override
    public void onTouch(@NotNull Touch touch) {
        if (!(touch.getInstance() instanceof final TeamInstance instance)) {
            return;
        }
        this.onTouch(touch, instance);
    }

    public void onTouch(@NotNull Touch touch, TeamInstance instance) {
        BlockHandler.super.onTouch(touch);
    }

    @Override
    public void tick(@NotNull Tick tick) {
        if (!(tick.getInstance() instanceof final TeamInstance instance)) {
            return;
        }
        this.tick(tick, instance);
    }

    public void tick(@NotNull Tick tick, TeamInstance instance) {
        BlockHandler.super.tick(tick);
    }

    public void randomTick(@NotNull Tick randomTick) {
        if (!(randomTick.getInstance() instanceof final TeamInstance instance)) {
            return;
        }
        this.randomTick(randomTick, instance);
    }

    public void randomTick(@NotNull Tick randomTick, TeamInstance instance) {
    }

    @Override
    public @NotNull Key getKey() {
        return this.key;
    }

}
