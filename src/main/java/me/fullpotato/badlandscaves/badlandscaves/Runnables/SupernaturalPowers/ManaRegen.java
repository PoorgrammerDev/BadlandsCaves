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
        for (Player player : Bukkit.getOnlinePlayers()) {
            int has_powers = player.getMetadata("has_supernatural_powers").get(0).asInt();
            if (has_powers < 1.0) return;

            int Mana = player.getMetadata("Mana").get(0).asInt();
            int max_mana = player.getMetadata("max_mana").get(0).asInt();
            if (Mana >= max_mana) return;

            int Thirst = player.getMetadata("Thirst").get(0).asInt();
            if (Thirst < 80) return;

            int mana_regen_delay_timer = player.getMetadata("mana_regen_delay_timer").get(0).asInt();
            if (mana_regen_delay_timer > 0) {
                mana_regen_delay_timer--;
                player.setMetadata("mana_regen_delay_timer", new FixedMetadataValue(plugin, mana_regen_delay_timer));
                return;
            }

            boolean isHardmode = plugin.getConfig().getBoolean("game_values.hardmode");
            int mana_regen_var = isHardmode ? plugin.getConfig().getInt("game_values.hardmode_values.mana_regen_var") : plugin.getConfig().getInt("game_values.pre_hardmode_values.mana_regen_var");
            int thirst_sys_var = player.getMetadata("thirst_sys_var").get(0).asInt();
            int in_descension = player.getMetadata("in_descension").get(0).asInt();

            Mana++;
            if (in_descension != 2) thirst_sys_var += mana_regen_var;

            player.setMetadata("Mana", new FixedMetadataValue(plugin, Mana));
            player.setMetadata("thirst_sys_var", new FixedMetadataValue(plugin, thirst_sys_var));
            player.setMetadata("mana_bar_active_timer", new FixedMetadataValue(plugin, 60));
        }
    }
}
