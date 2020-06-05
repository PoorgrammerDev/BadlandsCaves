package me.fullpotato.badlandscaves.badlandscaves.Runnables.Effects;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.badlandscaves.Util.AddPotionEffect;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

public class PlayerEffectsRunnable extends BukkitRunnable {
    private BadlandsCaves plugin;

    public PlayerEffectsRunnable(BadlandsCaves plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            if (!player.getGameMode().equals(GameMode.SURVIVAL) && !player.getGameMode().equals(GameMode.ADVENTURE)) continue;

            final int in_descension = player.hasMetadata("in_descension") ? player.getMetadata("in_descension").get(0).asInt() : 0;
            if (in_descension == 1 || in_descension == 2) continue;

            final boolean in_reflection = player.hasMetadata("in_reflection") && player.getMetadata("in_reflection").get(0).asBoolean();
            if (in_reflection) continue;

            if (player.getWorld().equals(plugin.getServer().getWorld(plugin.backroomsWorldName))) continue;

            final int agility_speed = player.getMetadata("agility_buff_speed_lvl").get(0).asInt();

            DeathEffects deathEffects = new DeathEffects();
            HashMap<String, Integer> deathValues = deathEffects.getDeathEffects(player);

            ThirstEffects thirstEffects = new ThirstEffects();
            HashMap<String, Integer> thirstValues = thirstEffects.getThirstEffects(player);

            ToxEffects toxEffects = new ToxEffects();
            HashMap<String, Integer> toxValues = toxEffects.getToxValues(player);


            int total_walk_speed = deathValues.get("walk_speed") + thirstValues.get("walk_speed") + toxValues.get("walk_speed") + agility_speed;
            int total_mine_speed = deathValues.get("mine_speed") + thirstValues.get("mine_speed") + toxValues.get("mine_speed");
            int total_hunger = deathValues.get("hunger_lvl") + thirstValues.get("hunger_lvl") + toxValues.get("hunger_lvl");
            int total_poison = thirstValues.get("poison_lvl") + toxValues.get("poison_lvl");
            boolean blindness = deathValues.get("blindness") > 0 || thirstValues.get("blindness") > 0 || toxValues.get("blindness") > 0;

            if (total_walk_speed > 0) {
                AddPotionEffect.addPotionEffect(player, new PotionEffect(PotionEffectType.SPEED, 90, total_walk_speed - 1, true, false));
            }
            else if (total_walk_speed < 0) {
                AddPotionEffect.addPotionEffect(player, new PotionEffect(PotionEffectType.SLOW, 90, Math.abs(total_walk_speed) - 1, true, false));
            }

            if (total_mine_speed > 0) {
                AddPotionEffect.addPotionEffect(player, new PotionEffect(PotionEffectType.FAST_DIGGING, 90, total_mine_speed - 1, true, false));
            }
            else if (total_mine_speed < 0) {
                AddPotionEffect.addPotionEffect(player, new PotionEffect(PotionEffectType.SLOW_DIGGING, 90, Math.abs(total_mine_speed) - 1, true, false));
            }

            if (total_hunger > 0 && (!player.hasPotionEffect(PotionEffectType.HUNGER) || player.getPotionEffect(PotionEffectType.HUNGER).getDuration() <= 2)) {
                if (player.getFoodLevel() > 6) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 90, total_hunger - 1, true, false));
                }
            }

            if (total_poison > 0 && (!player.hasPotionEffect(PotionEffectType.POISON) || player.getPotionEffect(PotionEffectType.POISON).getDuration() <= 2)) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 90, total_poison - 1, true, false));
            }

            if (blindness && !player.hasPotionEffect(PotionEffectType.BLINDNESS)) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 90, 0, true, false));
            }
        }
    }
}
