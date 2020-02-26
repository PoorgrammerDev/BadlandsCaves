package me.fullpotato.badlandscaves.badlandscaves.Util;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class AddPotionEffect {

    public static void addPotionEffect (Player player, PotionEffect effect, boolean force) {
        PotionEffectType type = effect.getType();
        int amplifier = effect.getAmplifier();

        for (PotionEffect active_effect : player.getActivePotionEffects()) {
            PotionEffectType active_type = active_effect.getType();
            int active_amplifier = active_effect.getAmplifier();
            if (active_type.equals(type) && (active_amplifier > amplifier)) {
                return;
            }
        }

        player.addPotionEffect(effect, force);
    }


}
