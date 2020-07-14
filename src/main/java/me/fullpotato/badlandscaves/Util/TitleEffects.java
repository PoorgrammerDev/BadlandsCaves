package me.fullpotato.badlandscaves.Util;

import me.fullpotato.badlandscaves.BadlandsCaves;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class TitleEffects {
    private final BadlandsCaves plugin;

    public TitleEffects(BadlandsCaves plugin) {
        this.plugin = plugin;
    }

    public void sendDecodingTitle (Player player, String title, String titleColorMod, String subtitle, String subtitleColorMod, int fadeIn, int stay, int fadeOut, int delay, boolean subtitleDecode) {
        final String titleClone = ChatColor.stripColor(title);
        final String subtitleClone = ChatColor.stripColor(subtitle);

        final String[] titles = new String[Math.max(titleClone.length() + 1, 0)];
        for (int i = 0; i < titleClone.length() + 1; i++) {
            titles[i] = titleClone.substring(0, i) + ChatColor.MAGIC + titleClone.substring(i);
        }

        final String[] subtitles = new String[Math.max(subtitleClone.length() + 1, 0)];
        if (subtitleDecode) {
            for (int i = 0; i < subtitleClone.length() + 1; i++) {
                subtitles[i] = subtitleClone.substring(0, i) + ChatColor.MAGIC + subtitleClone.substring(i);
            }
        }

        final int totalTimes = subtitleDecode ? Math.max(titles.length, subtitles.length) : titles.length;
        int[] times = {0};

        new BukkitRunnable() {
            @Override
            public void run() {
                if (times[0] > totalTimes) {
                    this.cancel();
                    return;
                }
                String sendTitle = titleColorMod + titles[Math.min(times[0], titles.length - 1)];
                String sendSubtitle = subtitleDecode ? subtitleColorMod + subtitles[Math.min(times[0], subtitles.length - 1)] : subtitleColorMod + subtitleClone;
                player.sendTitle(sendTitle, sendSubtitle, fadeIn, stay, fadeOut);
                times[0]++;
            }
        }.runTaskTimerAsynchronously(plugin, 0, delay);
    }
}
