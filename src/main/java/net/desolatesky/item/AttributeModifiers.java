package net.desolatesky.item;

import net.desolatesky.util.Namespace;
import net.minestom.server.entity.attribute.AttributeModifier;
import net.minestom.server.entity.attribute.AttributeOperation;

public final class AttributeModifiers {

    private AttributeModifiers() {
    }

    public static AttributeModifier attackSpeed(double amount, AttributeOperation operation) {
        return new AttributeModifier(Namespace.minecraftKey("generic.attack_speed"), amount, operation);
    }

    public static AttributeModifier attackDamage(double amount, AttributeOperation operation) {
        return new AttributeModifier(Namespace.minecraftKey("generic.attack_damage"), amount, operation);
    }

}
