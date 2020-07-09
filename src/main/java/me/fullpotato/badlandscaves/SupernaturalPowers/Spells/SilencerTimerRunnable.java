package me.fullpotato.badlandscaves.SupernaturalPowers.Spells;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.scheduler.BukkitRunnable;

public class SilencerTimerRunnable extends BukkitRunnable {
    private final BadlandsCaves plugin;

    public SilencerTimerRunnable(BadlandsCaves plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        if (plugin.getSystemConfig().getBoolean("hardmode")) {
            plugin.getServer().getOnlinePlayers().forEach(player -> {
                if ((byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == (byte) 1) {
                    final int silence = (int) PlayerScore.SPELLS_SILENCED_TIMER.getScore(plugin, player);
                    if (silence > 0) {
                        PlayerScore.SPELLS_SILENCED_TIMER.setScore(plugin, player, silence - 1);
                        if (silence == 40) {
                            player.playSound(player.getLocation(), Sound.BLOCK_BELL_RESONATE, SoundCategory.PLAYERS, 0.5F, 1);
                        }
                        else if (silence == 1) {
                            player.stopSound("custom.darkrooms_ambience", SoundCategory.BLOCKS);
                        }
                    }
                }
            });
        }
    }
}
