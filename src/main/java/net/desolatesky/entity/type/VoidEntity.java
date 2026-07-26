package net.desolatesky.entity.type;

import net.desolatesky.entity.DSLivingEntity;
import net.desolatesky.entity.ai.EntityBrain;
import net.desolatesky.entity.ai.goal.StealEntityAttractorGoal;
import net.desolatesky.entity.ai.navigation.MovementStrategy;
import net.desolatesky.island.Island;
import net.desolatesky.loot.LootTable;
import net.desolatesky.util.Constants;
import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.ItemEntity;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.ai.EntityAIGroup;
import net.minestom.server.entity.ai.EntityAIGroupBuilder;
import net.minestom.server.entity.ai.goal.CombinedAttackGoal;
import net.minestom.server.entity.ai.goal.MeleeAttackGoal;
import net.minestom.server.entity.ai.target.ClosestEntityTarget;
import net.minestom.server.item.ItemStack;
import net.minestom.server.scoreboard.Team;
import net.minestom.server.scoreboard.TeamManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.NotNullByDefault;
import org.jspecify.annotations.Nullable;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public abstract class VoidEntity<T extends VoidEntity<T>> extends DSLivingEntity<T> {

    private @Nullable ItemEntity stolenAttractor;

    public VoidEntity(
            EntityType entityType,
            LootTable drops,
            UUID uuid,
            Island island,
            MovementStrategy<T> movementStrategy,
            Consumer<Entity> entityConfigurer,
            Component entityTypeName
    ) {
        super(entityType, drops, uuid, island, movementStrategy, entityConfigurer, entityTypeName);
        this.setTeam();
    }

    public VoidEntity(
            EntityType entityType,
            LootTable drops,
            Island island,
            MovementStrategy<T> movementStrategy,
            Consumer<Entity> tagApplier,
            Component entityTypeName
    ) {
        super(entityType, drops, island, movementStrategy, tagApplier, entityTypeName);
        this.setTeam();
    }

    private void setTeam() {
        final Team team = MinecraftServer.getTeamManager().getTeam(Constants.VOID_TEAM_ID);
        if (team == null) {
            return;
        }
        this.setTeam(team);
    }


    @Override
    @SuppressWarnings("unchecked")
    protected EntityBrain<T> createBrain() {
        return new EntityBrain<>(List.of(
                new EntityAIGroupBuilder()
                        .addGoalSelector(new StealEntityAttractorGoal<>((T)this))
                        .addGoalSelector(new MeleeAttackGoal(this, 1, 1, ChronoUnit.SECONDS))
                        .addTargetSelector(new ClosestEntityTarget(this, 40, Player.class::isInstance))
                        .build()
        ));
    }

    @Override
    protected void dropItems() {
        super.dropItems();
        if (this.stolenAttractor == null) {
            return;
        }
        this.removePassenger(this.stolenAttractor);
        this.stolenAttractor.setPickable(true);
    }

    public void setStolenAttractor(ItemStack itemStack) {
        this.stolenAttractor = new ItemEntity(itemStack);
        this.stolenAttractor.setPickable(false);
        this.stolenAttractor.setInstance(this.instance, this.position);
        this.addPassenger(this.stolenAttractor);
    }
}
