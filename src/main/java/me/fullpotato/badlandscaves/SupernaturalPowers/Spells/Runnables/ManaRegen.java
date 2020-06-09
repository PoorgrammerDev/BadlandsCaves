package me.fullpotato.badlandscaves.SupernaturalPowers.Spells.Runnables;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ManaRegen extends BukkitRunnable {
    private final BadlandsCaves plugin;
    public ManaRegen(BadlandsCaves bcav) {
        plugin = bcav;
    }

    @Override
    public void run() {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            final boolean has_powers = (byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == 1;
            if (!has_powers) return;

            double Mana = ((double) PlayerScore.MANA.getScore(plugin, player));
            double max_mana = ((double) PlayerScore.MAX_MANA.getScore(plugin, player));
            if (Mana >= max_mana) return;

            double thirst = (double) PlayerScore.THIRST.getScore(plugin, player);
            if (thirst < 30) return;

            int mana_regen_delay_timer = ((int) PlayerScore.MANA_REGEN_DELAY_TIMER.getScore(plugin, player));
            if (mana_regen_delay_timer > 0) {
                mana_regen_delay_timer--;
                PlayerScore.MANA_REGEN_DELAY_TIMER.setScore(plugin, player, mana_regen_delay_timer);
                return;
            }

            boolean isHardmode = plugin.getConfig().getBoolean("system.hardmode");
            double mana_regen_var = isHardmode ? plugin.getConfig().getInt("options.hardmode_values.mana_regen_var") : plugin.getConfig().getInt("options.pre_hardmode_values.mana_regen_var");
            double thirst_sys_var = (double) PlayerScore.THIRST_SYS_VAR.getScore(plugin, player);
            int in_descension = ((int) PlayerScore.IN_DESCENSION.getScore(plugin, player));

            if (thirst > 70) {
                Mana++;
            }
            else if (thirst > 50) {
                Mana += 0.5;
                mana_regen_var *= 0.5;
            }
            else {
                Mana += 0.25;
                mana_regen_var *= 0.25;
            }

            if (in_descension != 2) thirst_sys_var += mana_regen_var;


            PlayerScore.MANA.setScore(plugin, player, Mana);
            PlayerScore.THIRST_SYS_VAR.setScore(plugin, player, thirst_sys_var);
            PlayerScore.MANA_BAR_ACTIVE_TIMER.setScore(plugin, player, 60);
        }
    }
}
