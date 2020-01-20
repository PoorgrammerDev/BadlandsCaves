package me.fullpotato.badlandscaves.badlandscaves.Runnables;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class manaBarRunnable extends BukkitRunnable {
    private BadlandsCaves plugin;
    private Player player;
    public manaBarRunnable (BadlandsCaves bcav, Player ply) {
        plugin = bcav;
        player = ply;
    }

    @Override
    public void run() {
        int has_powers = player.getMetadata("has_supernatural_powers").get(0).asInt();
        if (has_powers < 1.0) return;

        String title = "mana_bar_" + player.getUniqueId();
        BossBar manaBar = Bukkit.createBossBar(title, BarColor.BLUE, BarStyle.SOLID);

        //TODO a lot of stuff
    }
}
