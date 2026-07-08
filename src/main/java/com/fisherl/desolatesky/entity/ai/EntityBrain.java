package com.fisherl.desolatesky.entity.ai;

import com.fisherl.desolatesky.entity.DSLivingEntity;
import com.fisherl.desolatesky.entity.ai.goal.AIGoal;

import java.util.List;

public final class EntityBrain<T extends DSLivingEntity> {

    private final List<AIGoal> goals;

    public EntityBrain(List<AIGoal> goals) {
        this.goals = goals;
    }

    public void tick(long time) {
        this.goals.forEach(goal -> {
            if (goal.canStart()) {
                goal.start();
            }
            goal.tick(time);
            if (goal.shouldEnd()) {
                goal.end();
            }
        });
    }
}
