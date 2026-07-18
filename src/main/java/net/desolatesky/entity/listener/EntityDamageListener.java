package net.desolatesky.entity.listener;

import net.desolatesky.Listener;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.LivingEntity;
import net.minestom.server.entity.damage.Damage;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.entity.EntityAttackEvent;
import net.minestom.server.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.NotNullByDefault;

@NotNullByDefault
public final class EntityDamageListener implements Listener<Event> {

    @Override
    public void register(EventNode<Event> node) {
        node.addListener(EntityAttackEvent.class, event -> {
            if (!(event.getTarget() instanceof final LivingEntity livingEntity)) {
                return;
            }
            livingEntity.damage(Damage.fromEntity(event.getEntity(), 2));
        });
        node.addListener(EntityDamageEvent.class, event -> {
           final Entity entity = event.getEntity();
           final Entity attacker = event.getDamage().getSource();
           if (attacker == null) {
               return;
           }
           final Vec currentVelocity = entity.getVelocity();
           entity.setVelocity(currentVelocity.add(entity.getPosition().sub(attacker.getPosition()).normalize().mul(5).add(0, 2, 0)));
        });
    }
}
