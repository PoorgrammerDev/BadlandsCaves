package me.fullpotato.badlandscaves.Other;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ActionbarRunnable extends BukkitRunnable {
    private BadlandsCaves plugin;

    public ActionbarRunnable(BadlandsCaves plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            int in_descension = ((int) PlayerScore.IN_DESCENSION.getScore(plugin, player));
            if (in_descension == 1 || in_descension == 2) continue;

            final boolean in_reflection = (PlayerScore.IN_REFLECTION.hasScore(plugin, player)) && ((byte) PlayerScore.IN_REFLECTION.getScore(plugin, player) == 1);
            if (in_reflection) continue;

            if (player.getWorld().equals(plugin.getServer().getWorld(plugin.backroomsWorldName))) continue;

            int death_count = (int) PlayerScore.DEATHS.getScore(plugin, player);
            String separator = ChatColor.DARK_GRAY + ChatColor.BOLD.toString() + " | ";
            String actionbarmsg = (ChatColor.GOLD + ChatColor.BOLD.toString() + "Deaths: " + ChatColor.RED + ChatColor.BOLD.toString() + death_count + separator);

            double thirst_count = (double) PlayerScore.THIRST.getScore(plugin, player);
            double tox_count = (double) PlayerScore.TOXICITY.getScore(plugin, player);

            if (thirst_count >= 10) {
                int thirst_count_int = (int) ((double) PlayerScore.THIRST.getScore(plugin, player));
                actionbarmsg += ChatColor.BLUE + ChatColor.BOLD.toString() + "Thirst: " + ChatColor.DARK_AQUA + ChatColor.BOLD.toString() + thirst_count_int + ChatColor.BOLD.toString() + "%" + separator;
            }
            else {
                double thirst_count_rounded = Math.round(thirst_count * 10.0) / 10.0;
                actionbarmsg += ChatColor.BLUE + ChatColor.BOLD.toString() + "Thirst: " + ChatColor.DARK_AQUA + ChatColor.BOLD.toString() + thirst_count_rounded + ChatColor.BOLD.toString() + "%" + separator;
            }


            if (tox_count >= 10) {
                int tox_count_int = (int) ((double) PlayerScore.TOXICITY.getScore(plugin, player));
                actionbarmsg += ChatColor.DARK_GREEN + ChatColor.BOLD.toString() + "Toxicity: " + ChatColor.GREEN + ChatColor.BOLD.toString() + tox_count_int + ChatColor.BOLD.toString() + "%";
            }
            else {
                double tox_count_rounded = Math.round(tox_count * 10.0) / 10.0;
                actionbarmsg += ChatColor.DARK_GREEN + ChatColor.BOLD.toString() + "Toxicity: " + ChatColor.GREEN + ChatColor.BOLD.toString() + tox_count_rounded + ChatColor.BOLD.toString() + "%";
            }

            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(actionbarmsg));
        }
    }
}
