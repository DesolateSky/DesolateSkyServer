package net.desolatesky.entity.ai.goal;

public interface AIGoal {

    boolean canStart();

    void start();

    void tick(long time);

    boolean shouldEnd();

    void end();

}
