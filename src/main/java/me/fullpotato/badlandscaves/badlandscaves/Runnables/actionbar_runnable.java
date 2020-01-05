package me.fullpotato.badlandscaves.badlandscaves.Runnables;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class actionbar_runnable extends BukkitRunnable {
    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            int death_count = player.getMetadata("Deaths").get(0).asInt();
            String separator = ChatColor.DARK_GRAY + ChatColor.BOLD.toString() + " | ";
            String actionbarmsg = (ChatColor.GOLD + ChatColor.BOLD.toString() + "Deaths: " + ChatColor.RED + ChatColor.BOLD.toString() + death_count + separator);

            double thirst_count = player.getMetadata("Thirst").get(0).asDouble();
            double tox_count = player.getMetadata("Toxicity").get(0).asDouble();

            if (thirst_count >= 10) {
                int thirst_count_int = player.getMetadata("Thirst").get(0).asInt();
                actionbarmsg += ChatColor.BLUE + ChatColor.BOLD.toString() + "Thirst: " + ChatColor.DARK_AQUA + ChatColor.BOLD.toString() + thirst_count_int + ChatColor.BOLD.toString() + "%" + separator;
            }
            else {
                double thirst_count_rounded = Math.round(thirst_count * 10.0) / 10.0;
                actionbarmsg += ChatColor.BLUE + ChatColor.BOLD.toString() + "Thirst: " + ChatColor.DARK_AQUA + ChatColor.BOLD.toString() + thirst_count_rounded + ChatColor.BOLD.toString() + "%" + separator;
            }


            if (tox_count >= 10) {
                int tox_count_int = player.getMetadata("Toxicity").get(0).asInt();
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
