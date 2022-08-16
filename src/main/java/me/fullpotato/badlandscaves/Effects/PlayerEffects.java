package me.fullpotato.badlandscaves.Effects;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

public class PlayerEffects implements Listener {
    private final BadlandsCaves plugin;
    private final DeathEffects deathEffects;
    private final ThirstEffects thirstEffects;
    private final ToxEffects toxEffects;

    public PlayerEffects(BadlandsCaves plugin) {
        this.plugin = plugin;
        deathEffects = new DeathEffects(plugin);
        thirstEffects = new ThirstEffects(plugin);
        toxEffects = new ToxEffects(plugin);
    }

    @EventHandler
    public void playerRespawn (PlayerRespawnEvent event) {
        new BukkitRunnable() {
            @Override
            public void run() {
                applyEffects(event.getPlayer(), false);
            }
        }.runTaskLater(plugin, 1);
    }

    public void applyEffects (Player player, boolean removeOld) {
        if (removeOld) {
            for (PotionEffectType value : PotionEffectType.values()) {
                final PotionEffect potionEffect = player.getPotionEffect(value);
                if (potionEffect != null && potionEffect.getDuration() > 999999) {
                    player.removePotionEffect(value);
                }
            }
        }
        if (!player.getGameMode().equals(GameMode.SURVIVAL) && !player.getGameMode().equals(GameMode.ADVENTURE)) return;

        final int agility_speed = ((int) PlayerScore.AGILITY_LEVEL.getScore(plugin, player));
        final HashMap<String, Integer> deathValues = deathEffects.getDeathEffects(player);
        final HashMap<String, Integer> thirstValues = thirstEffects.getThirstEffects(player);
        final HashMap<String, Integer> toxValues = toxEffects.getToxValues(player);

        final int total_walk_speed = deathValues.get("walk_speed") + thirstValues.get("walk_speed") + toxValues.get("walk_speed") + agility_speed;
        final int total_mine_speed = deathValues.get("mine_speed") + thirstValues.get("mine_speed") + toxValues.get("mine_speed");
        final int total_hunger = thirstValues.get("hunger_lvl") + toxValues.get("hunger_lvl");
        final int total_poison = thirstValues.get("poison_lvl") + toxValues.get("poison_lvl");
        final boolean blindness = deathValues.get("blindness") > 0 || thirstValues.get("blindness") > 0 || toxValues.get("blindness") > 0;

        if (total_walk_speed > 0) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, total_walk_speed - 1, true, false));
        }
        else if (total_walk_speed < 0) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, Math.abs(total_walk_speed) - 1, true, false));
        }

        if (total_mine_speed > 0) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, Integer.MAX_VALUE, total_mine_speed - 1, true, false));
        }
        else if (total_mine_speed < 0) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, Integer.MAX_VALUE, Math.abs(total_mine_speed) - 1, true, false));
        }

        if (total_hunger > 0) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, Integer.MAX_VALUE, total_hunger - 1, true, false));
        }

        if (total_poison > 0 && (!player.hasPotionEffect(PotionEffectType.POISON) || player.getPotionEffect(PotionEffectType.POISON).getDuration() <= 2)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, Integer.MAX_VALUE, total_poison - 1, true, false));
        }

        if (blindness && !player.hasPotionEffect(PotionEffectType.BLINDNESS)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE, 0, true, false));
        }
    }
}
