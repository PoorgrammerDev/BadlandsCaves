package me.fullpotato.badlandscaves.Other;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ActionbarRunnable extends BukkitRunnable {
    private final BadlandsCaves plugin;

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

            if (player.getWorld().equals(plugin.getServer().getWorld(plugin.getBackroomsWorldName()))) continue;

            int death_count = (int) PlayerScore.DEATHS.getScore(plugin, player);
            String separator = ChatColor.DARK_GRAY + ChatColor.BOLD.toString() + " | ";
            StringBuilder actionbarmsg = new StringBuilder((ChatColor.of("#c20c0c").toString()));
            actionbarmsg.append(ChatColor.BOLD).append("Deaths: ").append(ChatColor.of("#ff3b3b")).append(ChatColor.BOLD.toString()).append(death_count).append(separator);

            double thirst_count = (double) PlayerScore.THIRST.getScore(plugin, player);
            double tox_count = (double) PlayerScore.TOXICITY.getScore(plugin, player);

            if (thirst_count >= 10) {
                int thirst_count_int = (int) ((double) PlayerScore.THIRST.getScore(plugin, player));
                actionbarmsg.append(ChatColor.of("#2345eb")).append(ChatColor.BOLD.toString()).append("Thirst: ").append(ChatColor.of("#2990ff")).append(ChatColor.BOLD.toString()).append(thirst_count_int).append(ChatColor.BOLD.toString()).append("%").append(separator);
            }
            else {
                double thirst_count_rounded = Math.round(thirst_count * 10.0) / 10.0;
                actionbarmsg.append(ChatColor.of("#2345eb")).append(ChatColor.BOLD.toString()).append("Thirst: ").append(ChatColor.of("#2990ff")).append(ChatColor.BOLD.toString()).append(thirst_count_rounded).append(ChatColor.BOLD.toString()).append("%").append(separator);
            }


            if (tox_count >= 10) {
                int tox_count_int = (int) ((double) PlayerScore.TOXICITY.getScore(plugin, player));
                actionbarmsg.append(ChatColor.DARK_GREEN).append(ChatColor.BOLD.toString()).append("Toxicity: ").append(ChatColor.GREEN).append(ChatColor.BOLD.toString()).append(tox_count_int).append(ChatColor.BOLD.toString()).append("%");
            }
            else {
                double tox_count_rounded = Math.round(tox_count * 10.0) / 10.0;
                actionbarmsg.append(ChatColor.DARK_GREEN).append(ChatColor.BOLD.toString()).append("Toxicity: ").append(ChatColor.GREEN).append(ChatColor.BOLD.toString()).append(tox_count_rounded).append(ChatColor.BOLD.toString()).append("%");
            }

            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(actionbarmsg.toString()));
        }
    }
}
