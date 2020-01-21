package me.fullpotato.badlandscaves.badlandscaves.Runnables;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class thirstEffectsRunnable extends BukkitRunnable {

    private BadlandsCaves plugin;
    public thirstEffectsRunnable(BadlandsCaves bcav ) {
        plugin = bcav;
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!player.getGameMode().equals(GameMode.SURVIVAL) && !player.getGameMode().equals(GameMode.ADVENTURE)) continue;

            double thirst = player.getMetadata("Thirst").get(0).asDouble();
            int poison_lvl = 0;
            int hunger_lvl = 0;
            int slow_lvl = 0;
            int slowmine_lvl = 0;

            if (thirst <= 0) {
                player.setHealth(0);
            }
            else if (thirst <= 10) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 90, 0, true, false));
                poison_lvl = 1;
                hunger_lvl = 2;
                slow_lvl = 3;
                slowmine_lvl = 3;
            }
            else if (thirst <= 30) {
                poison_lvl = 1;
                hunger_lvl = 2;
                slow_lvl = 3;
                slowmine_lvl = 3;
            }
            else if (thirst <= 40) {
                hunger_lvl = 1;
                slow_lvl = 2;
                slowmine_lvl = 2;
            }
            else if (thirst <= 50) {
                slow_lvl = 1;
                slowmine_lvl = 1;
            }

            player.setMetadata("thirst_debuff_poison_lvl", new FixedMetadataValue(plugin, poison_lvl));
            player.setMetadata("thirst_debuff_hunger_lvl", new FixedMetadataValue(plugin, hunger_lvl));
            player.setMetadata("thirst_debuff_slow_lvl", new FixedMetadataValue(plugin, slow_lvl));
            player.setMetadata("thirst_debuff_slowmine_lvl", new FixedMetadataValue(plugin, slowmine_lvl));
        }
    }
}
