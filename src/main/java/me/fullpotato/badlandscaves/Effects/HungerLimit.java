package me.fullpotato.badlandscaves.Effects;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class HungerLimit implements Listener {
    @EventHandler
    public void hungerTick (FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player) {
            final Player player = (Player) event.getEntity();
            if (event.getFoodLevel() < 6 && event.getFoodLevel() < player.getFoodLevel()) {
                final PotionEffect hunger = player.getPotionEffect(PotionEffectType.HUNGER);
                if (hunger != null && hunger.getDuration() > 999999) {
                    player.removePotionEffect(PotionEffectType.HUNGER);
                }
            }
        }
    }
}
