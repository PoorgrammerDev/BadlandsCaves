package me.fullpotato.badlandscaves.Effects;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class ThirstEffects {
    private final BadlandsCaves plugin;

    public ThirstEffects(BadlandsCaves plugin) {
        this.plugin = plugin;
    }

    public HashMap<String, Integer> getThirstEffects(Player player) {
        final double thirst = (double) PlayerScore.THIRST.getScore(plugin, player);
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
