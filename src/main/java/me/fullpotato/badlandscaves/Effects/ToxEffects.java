package me.fullpotato.badlandscaves.Effects;

import org.bukkit.entity.Player;

import java.util.HashMap;

public class ToxEffects {
    public HashMap<String, Integer> getToxValues(Player player) {
        double toxicity = player.getMetadata("Toxicity").get(0).asDouble();
        int poison_lvl = 0;
        int hunger_lvl = 0;
        int walk_speed = 0;
        int mine_speed = 0;
        int blindness = 0;

        if (toxicity >= 100) {
            player.setHealth(0);
        }
        else if (toxicity >= 90) {
            blindness = 1;
            poison_lvl = 3;
            hunger_lvl = 3;
            walk_speed = -3;
            mine_speed = -3;
        }
        else if (toxicity >= 70) {
            poison_lvl = 1;
            hunger_lvl = 2;
            walk_speed = -3;
            mine_speed = -3;
        }
        else if (toxicity >= 60) {
            hunger_lvl = 1;
            walk_speed = -2;
            mine_speed = -2;
        }
        else if (toxicity >= 50) {
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
