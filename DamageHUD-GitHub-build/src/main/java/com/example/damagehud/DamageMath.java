package com.example.damagehud;

import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.effect.MobEffects;

/** Client-side estimate. Server-side modifiers and plugins can make the actual hit differ. */
public final class DamageMath {
    private DamageMath() {}
    public record Result(double normal, boolean critical, double criticalDamage) {}

    public static Result estimate(Player attacker, Player target) {
        ItemStack weapon = attacker.getMainHandItem();
        double base = attacker.getAttributeValue(Attributes.ATTACK_DAMAGE);

        // Attribute values already include the held item's attack-damage modifiers.
        // Include the Strength/Weakness effects where their vanilla modifiers are available.
        if (attacker.hasEffect(MobEffects.STRENGTH)) {
            base += 3.0 * (attacker.getEffect(MobEffects.STRENGTH).getAmplifier() + 1);
        }
        if (attacker.hasEffect(MobEffects.WEAKNESS)) {
            base -= 4.0 * (attacker.getEffect(MobEffects.WEAKNESS).getAmplifier() + 1);
        }
        base = Math.max(0.0, base);

        // Approximate a fully charged attack. Actual damage scales with the attack cooldown.
        double normal = reduceByArmor(base, target);
        boolean crit = attacker.fallDistance > 0.0F && !attacker.onGround()
                && !attacker.isInWater() && !attacker.onClimbable()
                && !attacker.isPassenger() && !attacker.hasEffect(MobEffects.BLINDNESS);
        double critRaw = base * 1.5;
        double critical = reduceByArmor(critRaw, target);
        return new Result(normal, crit, critical);
    }

    private static double reduceByArmor(double damage, Player target) {
        double armor = target.getArmorValue();
        double toughness = target.getAttributeValue(Attributes.ARMOR_TOUGHNESS);
        double effectiveArmor = Math.min(20.0, Math.max(armor / 5.0,
                armor - damage / (2.0 + toughness / 4.0)));
        return damage * (1.0 - effectiveArmor / 25.0);
    }
}
