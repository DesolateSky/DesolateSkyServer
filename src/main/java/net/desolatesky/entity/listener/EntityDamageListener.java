package net.desolatesky.entity.listener;

import net.desolatesky.Listener;
import net.desolatesky.player.DSPlayer;
import net.kyori.adventure.text.Component;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.LivingEntity;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.damage.Damage;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.entity.EntityAttackEvent;
import net.minestom.server.event.entity.EntityDamageEvent;
import net.minestom.server.event.entity.EntityDeathEvent;
import net.minestom.server.event.player.PlayerDeathEvent;
import org.jetbrains.annotations.NotNullByDefault;

@NotNullByDefault
public final class EntityDamageListener implements Listener<Event> {

    @Override
    public void register(EventNode<Event> node) {
        node.addListener(EntityAttackEvent.class, event -> {
            if (event.getEntity() instanceof Player && event.getTarget() instanceof Player) {
                return;
            }
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
           final double xKnockback = Math.sin(attacker.getPosition().yaw() * (Math.PI / 180));
           final double zKnockback = -Math.cos(attacker.getPosition().yaw() * (Math.PI / 180));
           entity.takeKnockback(0.4f, xKnockback, zKnockback);
        });
        node.addListener(PlayerDeathEvent.class, event -> {
            if (!(event.getPlayer() instanceof final DSPlayer player)) {
                return;
            }
            final Damage lastDamage = player.getLastDamageSource();
            if (lastDamage == null) {
                event.setDeathText(Component.text("You died."));
                event.setChatMessage(player.getDisplayName().append(Component.text(" died.")));
                return;
            }
            event.setChatMessage(lastDamage.buildDeathMessage(player));
            event.setDeathText(lastDamage.buildDeathScreenText(player));
        });
    }
}
