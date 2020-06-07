package me.fullpotato.badlandscaves.WorldGeneration;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.event.Listener;

public class PreventDragon implements Listener {
    public static void preventDragonSpawn (World world) {
        world.getBlockAt(0, 255, 0).setType(Material.BARRIER);
        world.getBlockAt(0, 254, 0).setType(Material.END_PORTAL);
        world.getBlockAt(0, 253, 0).setType(Material.BARRIER);
    }
}
