package me.fullpotato.badlandscaves.badlandscaves.Runnables;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class playerEffectsRunnable extends BukkitRunnable {
    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            int death_slowmine = player.getMetadata("deaths_debuff_slowmine_lvl").get(0).asInt();
            int death_slow = player.getMetadata("deaths_debuff_slow_lvl").get(0).asInt();
            int death_hunger = player.getMetadata("deaths_debuff_hunger_lvl").get(0).asInt();
            int death_poison = player.getMetadata("deaths_debuff_poison_lvl").get(0).asInt();

            int tox_slowmine = player.getMetadata("tox_debuff_slowmine_lvl").get(0).asInt();
            int tox_slow = player.getMetadata("tox_debuff_slow_lvl").get(0).asInt();
            int tox_hunger = player.getMetadata("tox_debuff_hunger_lvl").get(0).asInt();
            int tox_poison = player.getMetadata("tox_debuff_poison_lvl").get(0).asInt();

            int thirst_slowmine = player.getMetadata("thirst_debuff_slowmine_lvl").get(0).asInt();
            int thirst_slow = player.getMetadata("thirst_debuff_slow_lvl").get(0).asInt();
            int thirst_hunger = player.getMetadata("thirst_debuff_hunger_lvl").get(0).asInt();
            int thirst_poison = player.getMetadata("thirst_debuff_poison_lvl").get(0).asInt();

            int total_slowmine = death_slowmine + tox_slowmine + thirst_slowmine;
            int total_slow = death_slow + tox_slow + thirst_slow;
            int total_hunger = death_hunger + tox_hunger + thirst_hunger;
            int total_poison = death_poison + tox_poison + thirst_poison;

            if (total_slowmine > 0) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 90, total_slowmine - 1));
            }

            if (total_slow > 0) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 90, total_slow - 1));
            }

            if (total_hunger > 0) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 90, total_hunger - 1));
            }

            if (total_poison > 0) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 90, total_poison - 1));
            }
        }
    }
}
