package me.fullpotato.badlandscaves.badlandscaves.Runnables.SupernaturalPowers.DescensionStage;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

public class descensionFinish extends BukkitRunnable {
    private BadlandsCaves plugin;
    private World world = Bukkit.getWorld("world_descension");
    public descensionFinish (BadlandsCaves bcav) {
        plugin = bcav;
    }

    @Override
    public void run() {
        Location origin = new Location(world, 0, 80, 0);
        //TODO make central portal and bridges, kill all zombies, etc.
    }


}
