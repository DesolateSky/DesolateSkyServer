package net.desolatesky.util;

import net.desolatesky.cooldown.CooldownHolder;
import net.kyori.adventure.key.Key;
import net.minestom.server.ServerFlag;
import net.minestom.server.entity.LivingEntity;
import net.minestom.server.entity.attribute.Attribute;
import net.minestom.server.entity.attribute.AttributeInstance;

public final class MinecraftUtil {

    private MinecraftUtil() {
    }

    public static float getAttackDamage(LivingEntity entity) {
        final AttributeInstance damageAttribute = entity.getAttribute(Attribute.ATTACK_DAMAGE);
        final AttributeInstance attackSpeedAttribute = entity.getAttribute(Attribute.ATTACK_SPEED);
        float damage = (float) damageAttribute.getValue();
        if (entity instanceof final CooldownHolder cooldownHolder) {
            final Key key = ItemUtil.getItemUseCooldownKey(entity.getItemInMainHand());
            final double attackSpeed = attackSpeedAttribute.getValue();
            final double cooldownTicks = ServerFlag.SERVER_TICKS_PER_SECOND / attackSpeed;
            final float cooldownProgress = (float) Math.min(1, cooldownHolder.cooldowns().calculatePercentageCompleted(key));
            final float multiplier = 0.2f + (float) Math.pow(cooldownProgress, 2) * 0.8f;
            damage *= multiplier;
            cooldownHolder.cooldowns().setCooldown(key, TimeUtil.ticksToDuration((long) cooldownTicks));
        }
        return damage;
    }

    public static <T extends LivingEntity & CooldownHolder> void resetHeldItemCooldown(T entity) {
        final AttributeInstance attackSpeedAttribute = entity.getAttribute(Attribute.ATTACK_SPEED);
        final Key key = ItemUtil.getItemUseCooldownKey(entity.getItemInMainHand());
        final double attackSpeed = attackSpeedAttribute.getValue();
        final double cooldownTicks = ServerFlag.SERVER_TICKS_PER_SECOND / attackSpeed;
        entity.cooldowns().setCooldown(key, TimeUtil.ticksToDuration((long) cooldownTicks));
    }
}
