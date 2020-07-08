package me.fullpotato.badlandscaves.SupernaturalPowers.Spells.Runnables;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.KeyedBossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ManaBarManager extends BukkitRunnable {
    private final BadlandsCaves plugin;
    private final String title = ChatColor.DARK_AQUA + "Mana";
    private final BarColor defaultColor = BarColor.BLUE;
    public ManaBarManager(BadlandsCaves bcav) {
        plugin = bcav;
    }

    @Override
    public void run() {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            final boolean has_powers = (byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == 1;
            KeyedBossBar manaBar = getManaBar(player);
            if (has_powers) {
                double max_mana = ((double) PlayerScore.MAX_MANA.getScore(plugin, player));
                double mana = ((double) PlayerScore.MANA.getScore(plugin, player));
                double percentage;
                try {
                    percentage = Math.min(Math.max(mana / max_mana, 0.0), 1.0);
                }
                catch (ArithmeticException e) {
                    percentage = 0.0;
                }
                manaBar.setProgress(percentage);

                final int mana_bar_message_timer = (int) PlayerScore.MANA_BAR_MESSAGE_TIMER.getScore(plugin, player);
                final int mana_bar_color_change_timer = (int) PlayerScore.MANA_BAR_COLOR_CHANGE_TIMER.getScore(plugin, player);

                if (manaBar.isVisible()) {
                    if (mana_bar_message_timer > 0) {
                        PlayerScore.MANA_BAR_MESSAGE_TIMER.setScore(plugin, player, mana_bar_message_timer - 1);

                    }
                    else {
                        manaBar.setTitle(title);
                    }

                    if (mana_bar_color_change_timer > 0) {
                        PlayerScore.MANA_BAR_COLOR_CHANGE_TIMER.setScore(plugin, player, mana_bar_color_change_timer - 1);
                    }
                    else {
                        manaBar.setColor(defaultColor);
                    }
                }

                int active_timer = (int) PlayerScore.MANA_BAR_ACTIVE_TIMER.getScore(plugin, player);
                if (active_timer > 0) {
                    manaBar.setVisible(true);
                    active_timer--;
                    PlayerScore.MANA_BAR_ACTIVE_TIMER.setScore(plugin, player, active_timer);
                }
                else {
                    manaBar.setVisible(false);
                }
            }
            else {
                if (manaBar != null) {
                    manaBar.setVisible(false);
                    plugin.getServer().removeBossBar(manaBar.getKey());
                }
            }
        }
    }

    public KeyedBossBar getManaBar(Player player) {
        final boolean has_powers = (byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == 1;
        NamespacedKey key = new NamespacedKey(plugin, "mana_bar_" + player.getUniqueId());
        KeyedBossBar manaBar = plugin.getServer().getBossBar(key);
        if (manaBar == null && has_powers) {
            manaBar = plugin.getServer().createBossBar(key, title, defaultColor, BarStyle.SEGMENTED_10);
            manaBar.addPlayer(player);
        }
        return manaBar;
    }

    public void displayMessage (Player player, String message, int time, boolean force) {
        if ((byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) != 1) return;
        if (!force && (int) PlayerScore.MANA_BAR_MESSAGE_TIMER.getScore(plugin, player) > 0) return;

        KeyedBossBar bar = getManaBar(player);
        PlayerScore.MANA_BAR_ACTIVE_TIMER.setScore(plugin, player, time * 4);
        PlayerScore.MANA_BAR_MESSAGE_TIMER.setScore(plugin, player, time * 4);
        bar.setTitle(message);
    }

    public void clearMessage (Player player) {
        if ((byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) != 1) return;
        if ((int) PlayerScore.MANA_BAR_MESSAGE_TIMER.getScore(plugin, player) <= 0) return;

        PlayerScore.MANA_BAR_MESSAGE_TIMER.setScore(plugin, player, 0);
    }

    public void changeColor (Player player, BarColor barColor, int time, boolean force) {
        if ((byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) != 1) return;
        if (!force && (int) PlayerScore.MANA_BAR_COLOR_CHANGE_TIMER.getScore(plugin, player) > 0) return;

        KeyedBossBar bar = getManaBar(player);
        PlayerScore.MANA_BAR_ACTIVE_TIMER.setScore(plugin, player, time * 4);
        PlayerScore.MANA_BAR_COLOR_CHANGE_TIMER.setScore(plugin, player, time * 4);
        bar.setColor(barColor);
    }

    public void clearColor (Player player) {
        if ((byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) != 1) return;
        if ((int) PlayerScore.MANA_BAR_COLOR_CHANGE_TIMER.getScore(plugin, player) <= 0) return;

        PlayerScore.MANA_BAR_COLOR_CHANGE_TIMER.setScore(plugin, player, 0);
    }
}
