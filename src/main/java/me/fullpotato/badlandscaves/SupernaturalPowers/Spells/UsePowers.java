package me.fullpotato.badlandscaves.SupernaturalPowers.Spells;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.SupernaturalPowers.Spells.Runnables.ManaBarManager;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public abstract class UsePowers {
    protected BadlandsCaves plugin;

    protected UsePowers(BadlandsCaves plugin) {
        this.plugin = plugin;
    }

    public void notEnoughMana(Player player) {
        ManaBarManager manabar = new ManaBarManager(plugin);
        manabar.displayMessage(player, ChatColor.RED + "Not enough Mana!", 2, false);
    }

    public void preventDoubleClick (Player player) {
        PlayerScore.SPELL_COOLDOWN.setScore(plugin, player, 1);
        new BukkitRunnable() {
            @Override
            public void run() {
                PlayerScore.SPELL_COOLDOWN.setScore(plugin, player, 0);
            }
        }.runTaskLaterAsynchronously(plugin, 2);
    }
}
