package net.desolatesky.entity.listener;

import net.desolatesky.Listener;
import net.desolatesky.cooldown.CooldownHolder;
import net.desolatesky.item.ItemTags;
import net.desolatesky.player.DSPlayer;
import net.desolatesky.util.ItemUtil;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.minestom.server.component.DataComponent;
import net.minestom.server.component.DataComponents;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.LivingEntity;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.attribute.Attribute;
import net.minestom.server.entity.attribute.AttributeInstance;
import net.minestom.server.entity.damage.Damage;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.entity.EntityAttackEvent;
import net.minestom.server.event.entity.EntityDamageEvent;
import net.minestom.server.event.entity.EntityDeathEvent;
import net.minestom.server.event.player.PlayerDeathEvent;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.component.AttributeList;
import net.minestom.server.item.component.UseCooldown;
import net.minestom.server.item.component.Weapon;
import org.jetbrains.annotations.NotNullByDefault;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

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
            float damage = 1.0f;
            if (event.getEntity() instanceof final LivingEntity attacker) {
                final ItemStack inHand = attacker.getItemInMainHand();
                damage = this.getAttackDamage(attacker, inHand);
            }
            livingEntity.damage(Damage.fromEntity(event.getEntity(), damage));
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
//            if (lastDamage == null) {
                event.setDeathText(Component.text("You died."));
                event.setChatMessage(player.getDisplayName().append(Component.text(" died.")));
//                return;
//            }
//            event.setChatMessage(lastDamage.buildDeathMessage(player));
//            event.setDeathText(lastDamage.buildDeathScreenText(player));
        });
    }

    private float getAttackDamage(LivingEntity entity, ItemStack used) {
        final ItemStack inHand = entity.getItemInMainHand();
        final AttributeInstance damageAttribute = entity.getAttribute(Attribute.ATTACK_DAMAGE);
        final AttributeInstance cooldownAttribute = entity.getAttribute(Attribute.ATTACK_SPEED);
        final AttributeList attributeList = inHand.get(DataComponents.ATTRIBUTE_MODIFIERS);
        if (attributeList != null) {
            attributeList.modifiers().forEach(m -> {
                if (m.attribute().equals(damageAttribute.attribute())) {
                    damageAttribute.addModifier(m.modifier());
                } else if (m.attribute().equals(cooldownAttribute.attribute())) {
                    cooldownAttribute.addModifier(m.modifier());
                }
            });
        }
        float damage = (float) damageAttribute.getValue();
        if (entity instanceof final CooldownHolder cooldownHolder) {
            final Key key = ItemUtil.getItemUseCooldownKey(used);
            final double percentage = cooldownHolder.cooldowns().calculatePercentageCompleted(key);
            damage *= (float) (1.0 / percentage);
            cooldownHolder.cooldowns().setCooldown(key, Duration.ofMillis((long) (cooldownAttribute.getBaseValue() * 1000)));
        }
        return damage;
    }
}
