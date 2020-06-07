package me.fullpotato.badlandscaves.SupernaturalPowers.DescensionStage;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

public class DescensionTimeLimit extends BukkitRunnable {
    private BadlandsCaves plugin;
    public DescensionTimeLimit(BadlandsCaves bcav) {
        plugin = bcav;
    }

    @Override
    public void run() {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            if (player.getGameMode().equals(GameMode.SURVIVAL) || player.getGameMode().equals(GameMode.ADVENTURE)) {
                int in_descension = ((int) PlayerScore.IN_DESCENSION.getScore(plugin, player));
                if (in_descension == 2) {
                    int descension_timer = (int) PlayerScore.DESCENSION_TIMER.getScore(plugin, player);
                    if (descension_timer > 0) {
                        descension_timer--;
                        PlayerScore.DESCENSION_TIMER.setScore(plugin, player, descension_timer);
                        sendActionBarMsg(player, descension_timer);
                    }
                    else {
                        player.setHealth(0);
                    }
                }
            }
        }
    }

    public void sendActionBarMsg (Player player, int timer_value) {
        int max_bars = 20;
        int starting_time = plugin.getConfig().getInt("game_values.descension_time_limit");
        int dividing_num = starting_time / max_bars;
        int bar_value = Math.min(Math.max((int) Math.ceil(1.0 * timer_value / dividing_num), 0), max_bars);
        double bar_perc = 100.0 * timer_value / starting_time;

        StringBuilder builder = new StringBuilder();
        builder.append(ChatColor.GRAY)
                .append("Time Remaining")
                .append(ChatColor.DARK_GRAY)
                .append(": ")
                .append(statusColor(bar_perc))
                .append(timer_value)
                .append(ChatColor.DARK_GRAY)
                .append(" [")
                .append(statusColor(bar_perc));

        for (int a = 0; a < bar_value; a++) {
            builder.append("=");
        }

        builder.append(ChatColor.GRAY);
        for (int a = 0; a < max_bars - bar_value; a++) {
            builder.append("-");
        }

        builder.append(ChatColor.DARK_GRAY)
                .append("]");

        String output = builder.toString();
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(output));
    }


    public ChatColor statusColor (double bar_perc) {
        if (bar_perc > 75) {
            return ChatColor.GREEN;
        }
        if (bar_perc > 50) {
            return ChatColor.YELLOW;
        }
        if (bar_perc > 25) {
            return ChatColor.GOLD;
        }
        if (bar_perc > 10) {
            return ChatColor.RED;
        }
        return ChatColor.DARK_RED;
    }

}
