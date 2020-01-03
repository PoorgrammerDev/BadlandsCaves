package me.fullpotato.badlandscaves.badlandscaves.Runnables;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class thirst_effects_runnable extends BukkitRunnable {

    private BadlandsCaves plugin;
    public thirst_effects_runnable ( BadlandsCaves bcav ) {
        plugin = bcav;
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            double thirst = player.getMetadata("Thirst").get(0).asDouble();

            if (thirst <= 0) {
                player.setHealth(0);
            }
            else if (thirst <= 10) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 90, 0));
                player.setMetadata("thirst_debuff_poison_lvl", new FixedMetadataValue(plugin, 1));
                player.setMetadata("thirst_debuff_hunger_lvl", new FixedMetadataValue(plugin, 2));
                player.setMetadata("thirst_debuff_slow_lvl", new FixedMetadataValue(plugin, 3));
                player.setMetadata("thirst_debuff_slowmine_lvl", new FixedMetadataValue(plugin, 3));
            }
            else if (thirst <= 30) {
                player.setMetadata("thirst_debuff_poison_lvl", new FixedMetadataValue(plugin, 1));
                player.setMetadata("thirst_debuff_hunger_lvl", new FixedMetadataValue(plugin, 2));
                player.setMetadata("thirst_debuff_slow_lvl", new FixedMetadataValue(plugin, 3));
                player.setMetadata("thirst_debuff_slowmine_lvl", new FixedMetadataValue(plugin, 3));
            }
            else if (thirst <= 40) {
                player.setMetadata("thirst_debuff_hunger_lvl", new FixedMetadataValue(plugin, 1));
                player.setMetadata("thirst_debuff_slow_lvl", new FixedMetadataValue(plugin, 2));
                player.setMetadata("thirst_debuff_slowmine_lvl", new FixedMetadataValue(plugin, 2));
                player.setMetadata("thirst_debuff_poison_lvl", new FixedMetadataValue(plugin, 0));
            }
            else if (thirst <= 50) {
                player.setMetadata("thirst_debuff_slow_lvl", new FixedMetadataValue(plugin, 1));
                player.setMetadata("thirst_debuff_slowmine_lvl", new FixedMetadataValue(plugin, 1));
                player.setMetadata("thirst_debuff_poison_lvl", new FixedMetadataValue(plugin, 0));
                player.setMetadata("thirst_debuff_hunger_lvl", new FixedMetadataValue(plugin, 0));
            }
            else {
                player.setMetadata("thirst_debuff_slow_lvl", new FixedMetadataValue(plugin, 0));
                player.setMetadata("thirst_debuff_slowmine_lvl", new FixedMetadataValue(plugin, 0));
                player.setMetadata("thirst_debuff_poison_lvl", new FixedMetadataValue(plugin, 0));
                player.setMetadata("thirst_debuff_hunger_lvl", new FixedMetadataValue(plugin, 0));
            }
        }
    }
}
