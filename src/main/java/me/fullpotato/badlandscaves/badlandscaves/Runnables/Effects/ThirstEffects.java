package me.fullpotato.badlandscaves.badlandscaves.Runnables.Effects;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

public class ThirstEffects {
    public HashMap<String, Integer> getThirstEffects(Player player) {
        final double thirst = player.getMetadata("Thirst").get(0).asDouble();
        int poison_lvl = 0;
        int hunger_lvl = 0;
        int walk_speed = 0;
        int mine_speed = 0;
        int blindness = 0;

        if (thirst <= 0) {
            player.setHealth(0);
        }
        else if (thirst <= 10) {
            blindness = 1;
            poison_lvl = 1;
            hunger_lvl = 2;
            walk_speed = -3;
            mine_speed = -3;
        }
        else if (thirst <= 30) {
            poison_lvl = 1;
            hunger_lvl = 2;
            walk_speed = -3;
            mine_speed = -3;
        }
        else if (thirst <= 40) {
            hunger_lvl = 1;
            walk_speed = -2;
            mine_speed = -2;
        }
        else if (thirst <= 50) {
            walk_speed = -1;
            mine_speed = -1;
        }

        HashMap<String, Integer> output = new HashMap<>();
        output.put("poison_lvl", poison_lvl);
        output.put("hunger_lvl", hunger_lvl);
        output.put("walk_speed", walk_speed);
        output.put("mine_speed", mine_speed);
        output.put("blindness", blindness);

        return output;
    }
}
