package net.desolatesky.entity.ai;

import net.desolatesky.entity.DSLivingEntity;
import net.minestom.server.entity.ai.EntityAIGroup;

import java.util.List;

public final class EntityBrain<T extends DSLivingEntity<T>> {

//    private final List<AIGoal> goals;
    private final List<EntityAIGroup> groups;
//    private @Nullable AIGoal currentGoal;

    public EntityBrain(List<EntityAIGroup> groups) {
        this.groups = groups;
    }

    public void tick(long time) {
        this.groups.forEach(g -> g.tick(time));
    }


//    public EntityBrain(List<AIGoal> goals) {
//        this.goals = goals;
//        this.currentGoal = null;
//    }
//
//    public void tick(long time) {
//        AIGoal nextGoal =  this.currentGoal;
//        for (final AIGoal goal : this.goals) {
//            if (goal == nextGoal) {
//                break;
//            }
//            if (goal.canStart()) {
//                if (nextGoal != null) {
//                    nextGoal.end();
//                }
//                nextGoal = goal;
//                nextGoal.start();
//            }
//            if (nextGoal == null) {
//                continue;
//            }
//            if (nextGoal.shouldEnd()) {
//                nextGoal.end();
//            }
//        }
//        if (nextGoal != null) {
//            nextGoal.tick(time);
//        }
//    }
}
