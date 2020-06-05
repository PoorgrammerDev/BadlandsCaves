package me.fullpotato.badlandscaves.badlandscaves.Runnables.SupernaturalPowers;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

public class ManaRegen extends BukkitRunnable {
    private BadlandsCaves plugin;
    public ManaRegen(BadlandsCaves bcav) {
        plugin = bcav;
    }

    @Override
    public void run() {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            final boolean has_powers = player.getMetadata("has_supernatural_powers").get(0).asBoolean();
            if (!has_powers) return;

            double Mana = player.getMetadata("Mana").get(0).asDouble();
            int max_mana = player.getMetadata("max_mana").get(0).asInt();
            if (Mana >= max_mana) return;

            int thirst = player.getMetadata("Thirst").get(0).asInt();
            if (thirst < 30) return;

            int mana_regen_delay_timer = player.getMetadata("mana_regen_delay_timer").get(0).asInt();
            if (mana_regen_delay_timer > 0) {
                mana_regen_delay_timer--;
                player.setMetadata("mana_regen_delay_timer", new FixedMetadataValue(plugin, mana_regen_delay_timer));
                return;
            }

            boolean isHardmode = plugin.getConfig().getBoolean("game_values.hardmode");
            double mana_regen_var = isHardmode ? plugin.getConfig().getInt("game_values.hardmode_values.mana_regen_var") : plugin.getConfig().getInt("game_values.pre_hardmode_values.mana_regen_var");
            double thirst_sys_var = player.getMetadata("thirst_sys_var").get(0).asDouble();
            int in_descension = player.getMetadata("in_descension").get(0).asInt();

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


            player.setMetadata("Mana", new FixedMetadataValue(plugin, Mana));
            player.setMetadata("thirst_sys_var", new FixedMetadataValue(plugin, thirst_sys_var));
            player.setMetadata("mana_bar_active_timer", new FixedMetadataValue(plugin, 60));
        }
    }
}
