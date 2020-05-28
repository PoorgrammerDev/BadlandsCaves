package me.fullpotato.badlandscaves.badlandscaves.Events.SupernaturalPowers;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.badlandscaves.Runnables.SupernaturalPowers.ManaBarManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
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
        player.setMetadata("spell_cooldown", new FixedMetadataValue(plugin, true));
        new BukkitRunnable() {
            @Override
            public void run() {
                player.setMetadata("spell_cooldown", new FixedMetadataValue(plugin, false));
            }
        }.runTaskLaterAsynchronously(plugin, 2);
    }
}
