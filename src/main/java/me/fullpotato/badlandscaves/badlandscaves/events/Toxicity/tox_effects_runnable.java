package me.fullpotato.badlandscaves.badlandscaves.events.Toxicity;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class tox_effects_runnable extends BukkitRunnable{

    private BadlandsCaves plugin;
    public tox_effects_runnable ( BadlandsCaves bcav ) {
        plugin = bcav;
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            double toxicity = player.getMetadata("Toxicity").get(0).asDouble();

            if (toxicity >= 100) {
                player.setHealth(0);
            }
            else if (toxicity >= 90) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 300, 0));
            }
            else if (toxicity >= 70) {
                player.setMetadata("tox_debuff_poison_lvl", new FixedMetadataValue(plugin, 1));
                player.setMetadata("tox_debuff_hunger_lvl", new FixedMetadataValue(plugin, 2));
                player.setMetadata("tox_debuff_slow_lvl", new FixedMetadataValue(plugin, 3));
                player.setMetadata("tox_debuff_slowmine_lvl", new FixedMetadataValue(plugin, 3));
            }
            else if (toxicity >= 60) {
                player.setMetadata("tox_debuff_hunger_lvl", new FixedMetadataValue(plugin, 1));
                player.setMetadata("tox_debuff_slow_lvl", new FixedMetadataValue(plugin, 2));
                player.setMetadata("tox_debuff_slowmine_lvl", new FixedMetadataValue(plugin, 2));
            }
            else if (toxicity >= 50) {
                player.setMetadata("tox_debuff_slow_lvl", new FixedMetadataValue(plugin, 1));
                player.setMetadata("tox_debuff_slowmine_lvl", new FixedMetadataValue(plugin, 1));
            }

        }
    }
}
