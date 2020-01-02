package me.fullpotato.badlandscaves.badlandscaves;

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
            int thirst_count = player.getMetadata("Thirst").get(0).asInt();
            int tox_count = player.getMetadata("Toxicity").get(0).asInt();

            String separator = ChatColor.DARK_GRAY + ChatColor.BOLD.toString() + " | ";
            String actionbarmsg = (
                    ChatColor.GOLD + ChatColor.BOLD.toString() + "Deaths: " + ChatColor.RED + ChatColor.BOLD.toString() + death_count + separator
                            + ChatColor.BLUE + ChatColor.BOLD.toString() + "Thirst: " + ChatColor.DARK_AQUA + ChatColor.BOLD.toString() + thirst_count + ChatColor.BOLD.toString() + "%" + separator
                            + ChatColor.DARK_GREEN + ChatColor.BOLD.toString() +"Toxicity: " + ChatColor.GREEN + ChatColor.BOLD.toString() + tox_count + ChatColor.BOLD.toString() + "%");

            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(actionbarmsg));
        }
    }
}
