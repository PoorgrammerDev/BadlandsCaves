package me.fullpotato.badlandscaves.SupernaturalPowers.Spells.Runnables;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.Crafting.Voidmatter;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
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
            double thirst_penalty = isHardmode ? plugin.getConfig().getInt("options.hardmode_values.mana_regen_var") : plugin.getConfig().getInt("options.pre_hardmode_values.mana_regen_var");
            double thirst_sys_var = (double) PlayerScore.THIRST_SYS_VAR.getScore(plugin, player);
            int in_descension = ((int) PlayerScore.IN_DESCENSION.getScore(plugin, player));

            boolean fullSetVoidmatter = false;
            if (isHardmode) {
                EntityEquipment equipment = player.getEquipment();
                if (equipment != null) {
                    final ItemStack[] armorPieces = equipment.getArmorContents();
                    final Voidmatter voidmatterArmor = new Voidmatter(plugin);
                    fullSetVoidmatter = true;

                    for (ItemStack armor : armorPieces) {
                        if (armor == null || !voidmatterArmor.isVoidmatterArmor(armor)) {
                            fullSetVoidmatter = false;
                            break;
                        }
                    }
                }
            }

            double mana_regen_per_second;

            if (thirst > 70) {
                mana_regen_per_second = 1;
            }
            else if (thirst > 50) {
                mana_regen_per_second = 0.5;
                thirst_penalty *= 0.5;
            }
            else {
                mana_regen_per_second = 0.25;
                thirst_penalty *= 0.25;
            }

            if (fullSetVoidmatter) mana_regen_per_second *= 2;
            mana_regen_per_second /= 20.0;
            thirst_penalty /= 20.0;


            if (in_descension != 2) thirst_sys_var += thirst_penalty;

            PlayerScore.MANA.setScore(plugin, player, Math.min(Mana + mana_regen_per_second, max_mana));
            PlayerScore.THIRST_SYS_VAR.setScore(plugin, player, thirst_sys_var);
            PlayerScore.MANA_BAR_ACTIVE_TIMER.setScore(plugin, player, 60);
        }
    }
}
