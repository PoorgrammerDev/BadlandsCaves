package me.fullpotato.badlandscaves.badlandscaves.Runnables;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

public class agilityJumpRunnable extends BukkitRunnable {
    private BadlandsCaves plugin;
    private Player player;

    public agilityJumpRunnable (BadlandsCaves bcav, Player ply) {
        plugin = bcav;
        player = ply;
    }

    @Override
    public void run() {
        int has_powers = player.getMetadata("has_supernatural_powers").get(0).asInt();
        if (has_powers < 1.0) return;

        int agility_level = player.getMetadata("agility_level").get(0).asInt();
        if (agility_level < 1.0) return;

        int agility_timer = player.getMetadata("agility_jump_timer").get(0).asInt();
        if (agility_timer > 0) {
            player.sendMessage("" + agility_timer);
            player.setMetadata("agility_jump_timer", new FixedMetadataValue(plugin, agility_timer - 1));
        }
        else {
            player.setMetadata("agility_jump_id", new FixedMetadataValue(plugin, 0));
            player.setAllowFlight(false);
            Bukkit.getScheduler().cancelTask(this.getTaskId());
        }
    }
}
