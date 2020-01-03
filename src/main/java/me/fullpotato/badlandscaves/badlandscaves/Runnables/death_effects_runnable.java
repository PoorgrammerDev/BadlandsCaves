package me.fullpotato.badlandscaves.badlandscaves.Runnables;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class death_effects_runnable extends BukkitRunnable {

    private BadlandsCaves plugin;
    public death_effects_runnable ( BadlandsCaves bcav ) {
        plugin = bcav;
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            int deaths = player.getMetadata("Deaths").get(0).asInt();

            if (deaths >= 50) {
                //shadow realm time
            }
            else if (deaths >= 30) {
                //slowmine2 weak3 slow3 unluck3 hunger2 blind1 poison2 limHP:5hrts
                player.setMetadata("deaths_debuff_slowmine_lvl", new FixedMetadataValue(plugin, 2));
                player.setMetadata("deaths_debuff_slow_lvl", new FixedMetadataValue(plugin, 3));
                player.setMetadata("deaths_debuff_hunger_lvl", new FixedMetadataValue(plugin, 3));
                player.setMetadata("deaths_debuff_poison_lvl", new FixedMetadataValue(plugin, 2));

                player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 90, 2));
                player.addPotionEffect(new PotionEffect(PotionEffectType.UNLUCK, 90, 9));
                player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 90, 0));

                player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(1.0);
            }
            else if (deaths >= 15) {
                //slowmine2 weak2 slow2 unluck2 hunger1 blind1 limHP:5hrts
                player.setMetadata("deaths_debuff_slowmine_lvl", new FixedMetadataValue(plugin, 1));
                player.setMetadata("deaths_debuff_slow_lvl", new FixedMetadataValue(plugin, 2));
                player.setMetadata("deaths_debuff_hunger_lvl", new FixedMetadataValue(plugin, 2));
                player.setMetadata("deaths_debuff_poison_lvl", new FixedMetadataValue(plugin, 0));

                player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 90, 1));
                player.addPotionEffect(new PotionEffect(PotionEffectType.UNLUCK, 90, 4));
                player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 90, 0));

                player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(10.0);
            }
            else if (deaths >= 6) {
                //slowmine1 weak1 slow1 unluck1
                player.setMetadata("deaths_debuff_slow_lvl", new FixedMetadataValue(plugin, 1));
                player.setMetadata("deaths_debuff_hunger_lvl", new FixedMetadataValue(plugin, 1));
                player.setMetadata("deaths_debuff_poison_lvl", new FixedMetadataValue(plugin, 0));
                player.setMetadata("deaths_debuff_slowmine_lvl", new FixedMetadataValue(plugin, 0));

                player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 90, 0));
                player.addPotionEffect(new PotionEffect(PotionEffectType.UNLUCK, 90, 1));

                player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20.0);
            }
            else if (deaths == 5) {
                player.setMetadata("deaths_debuff_slowmine_lvl", new FixedMetadataValue(plugin, 0));
                player.setMetadata("deaths_debuff_slow_lvl", new FixedMetadataValue(plugin, 0));
                player.setMetadata("deaths_debuff_hunger_lvl", new FixedMetadataValue(plugin, 0));
                player.setMetadata("deaths_debuff_poison_lvl", new FixedMetadataValue(plugin, 0));
            }
            else if (deaths >= 1) {
                //haste1 speed1 resistance1
                player.setMetadata("deaths_debuff_slowmine_lvl", new FixedMetadataValue(plugin, 0));
                player.setMetadata("deaths_debuff_slow_lvl", new FixedMetadataValue(plugin, 0));
                player.setMetadata("deaths_debuff_hunger_lvl", new FixedMetadataValue(plugin, 0));
                player.setMetadata("deaths_debuff_poison_lvl", new FixedMetadataValue(plugin, 0));

                player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 90, 0));
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 90, 0));
                player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 90, 0));

                player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20.0);
            }
            else if (deaths == 0) {
                //haste2 strength1 speed2 jumpboost2 resistance2 luck2
                player.setMetadata("deaths_debuff_slowmine_lvl", new FixedMetadataValue(plugin, 0));
                player.setMetadata("deaths_debuff_slow_lvl", new FixedMetadataValue(plugin, 0));
                player.setMetadata("deaths_debuff_hunger_lvl", new FixedMetadataValue(plugin, 0));
                player.setMetadata("deaths_debuff_poison_lvl", new FixedMetadataValue(plugin, 0));

                player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 90, 1));
                player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 90, 0));
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 90, 1));
                player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 90, 1));
                player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 90, 1));
                player.addPotionEffect(new PotionEffect(PotionEffectType.LUCK, 90, 4));

                player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(40.0);
            }
        }
    }
}
