package me.fullpotato.badlandscaves.Effects;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.Util.AddPotionEffect;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;

public class DeathEffects {
    private final BadlandsCaves plugin;

    public DeathEffects(BadlandsCaves plugin) {
        this.plugin = plugin;
    }

    public HashMap<String, Integer> getDeathEffects(Player player) {
        final int deaths = (int) PlayerScore.DEATHS.getScore(plugin, player);
        final boolean supernatural = ((PlayerScore.HAS_SUPERNATURAL_POWERS.hasScore(plugin, player)) && (byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == 1);
        int walk_speed = 0;
        int mine_speed = 0;
        int weakness_lvl = 0;
        int unluck_lvl = 0;
        int blindness = 0;
        int health = 20;

        if (deaths >= 50) {
            walk_speed = supernatural ? -3 : -4;
            mine_speed = supernatural ? -1 : -2;
            weakness_lvl = 3;
            unluck_lvl = supernatural ? 5 : 6;
            blindness = 1;

            health = supernatural ? 12 : 10;
        }

        //40+ DEATHS----------------------------------------------
        else if (deaths >= 40) {
            walk_speed = supernatural ? -2 : -3;
            mine_speed = -1;
            weakness_lvl = supernatural ? 2 : 3;
            unluck_lvl = supernatural ? 4 : 5;

            health = supernatural ? 16 : 12;
        }

        //30+ DEATHS----------------------------------------------
        else if (deaths >= 30) {
            walk_speed = supernatural ? -2 : -3;
            mine_speed = supernatural ? 0 : -1;
            weakness_lvl = supernatural ? 1 : 2;
            unluck_lvl = supernatural ? 2 : 3;

            health = supernatural ? 20 : 16;
        }

        //20+ DEATHS----------------------------------------------
        else if (deaths >= 20) {
            walk_speed = supernatural ? -1 : -2;
            weakness_lvl = 1;
            unluck_lvl = supernatural ? 1 : 2;
        }

        //10+ DEATHS----------------------------------------------
        else if (deaths >= 10) {
            walk_speed = supernatural ? -1 : -2;
            unluck_lvl = supernatural ? 1 : 2;
        }

        //6+ DEATHS-----------------------------------------------
        else if (deaths >= 6) {
            if (!supernatural) {
                walk_speed = -1;
                unluck_lvl = 1;
            }
        }

        //5+ DEATHS-----------------------------------------------
        else if (deaths == 5) {
            if (supernatural) {
                walk_speed = 1;
            }
        }

        //1+ DEATHS-----------------------------------------------
        else if (deaths >= 1) {
            walk_speed = 1;
            mine_speed = 1;
            AddPotionEffect.addPotionEffect(player, new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 0, true, false));
            AddPotionEffect.addPotionEffect(player, new PotionEffect(PotionEffectType.LUCK, Integer.MAX_VALUE, 0, true, false));
        }

        //NO DEATHS-----------------------------------------------
        else if (deaths == 0) {
            walk_speed = 2;
            mine_speed = 1;
            AddPotionEffect.addPotionEffect(player, new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 0, true, false));
            AddPotionEffect.addPotionEffect(player, new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 1, true, false));
            AddPotionEffect.addPotionEffect(player, new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1, true, false));
            AddPotionEffect.addPotionEffect(player, new PotionEffect(PotionEffectType.LUCK, Integer.MAX_VALUE, 1, true, false));

            health = 40;
        }

        if (player.getAttribute(Attribute.GENERIC_MAX_HEALTH) != null && player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue() != health) {
            player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(health);
        }

        if (unluck_lvl > 0) {
            AddPotionEffect.addPotionEffect(player, new PotionEffect(PotionEffectType.UNLUCK, Integer.MAX_VALUE, unluck_lvl - 1, true, false));
        }

        if (weakness_lvl > 0) {
            AddPotionEffect.addPotionEffect(player, new PotionEffect(PotionEffectType.WEAKNESS, Integer.MAX_VALUE, weakness_lvl - 1, true, false));
        }

        HashMap<String, Integer> output = new HashMap<>();
        output.put("walk_speed", walk_speed);
        output.put("mine_speed", mine_speed);
        output.put("blindness", blindness);

        return output;
    }
}
