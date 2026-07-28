package net.desolatesky.util;

import net.desolatesky.cooldown.CooldownHolder;
import net.desolatesky.world.DSWorld;
import net.kyori.adventure.key.Key;
import net.minestom.server.ServerFlag;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.ItemEntity;
import net.minestom.server.entity.LivingEntity;
import net.minestom.server.entity.attribute.Attribute;
import net.minestom.server.entity.attribute.AttributeInstance;
import net.minestom.server.item.ItemStack;

import java.util.Collection;
import java.util.random.RandomGenerator;

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

    public static void spawnDroppedBlockItems(DSWorld world, Point blockPos, Collection<ItemStack> itemStacks) {
        final RandomGenerator randomGenerator = world.getRandomGenerator(blockPos);
        for (final ItemStack itemStack : itemStacks) {
            final double x = blockPos.blockX() + 0.5 + randomGenerator.nextDouble(-0.25, 0.25);
            final double y = blockPos.blockY() + 0.5 + randomGenerator.nextDouble(-0.25, 0.25);
            final double z = blockPos.blockZ() + 0.5 + randomGenerator.nextDouble(-0.25, 0.25);
            final double velocityX = randomGenerator.nextDouble(-0.5, 0.5) ;
            final double velocityY = randomGenerator.nextDouble(-0.5, 0.5) + 4;
            final double velocityZ = randomGenerator.nextDouble(-0.5, 0.5);
            final ItemEntity itemEntity = new ItemEntity(itemStack);
            itemEntity.setPickupDelay(TimeUtil.ticksToDuration(ServerFlag.SERVER_TICKS_PER_SECOND / 4));
            itemEntity.setInstance(world, new Vec(x, y, z));
            itemEntity.setVelocity(new Vec(velocityX, velocityY, velocityZ));
        }
    }
}
