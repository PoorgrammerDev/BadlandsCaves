package me.fullpotato.badlandscaves.WorldGeneration;

import org.bukkit.Material;
import org.bukkit.World;

public class PreventDragon {
    public static void preventDragonSpawn (World world) {
        world.getBlockAt(0, 255, 0).setType(Material.BARRIER); //top
        world.getBlockAt(0, 254, 0).setType(Material.END_PORTAL);
        world.getBlockAt(0, 253, 0).setType(Material.BARRIER); //bottom

        world.getBlockAt(1, 254, 0).setType(Material.BARRIER); //+X
        world.getBlockAt(-1, 254, 0).setType(Material.BARRIER); //-X
        world.getBlockAt(0, 254, 1).setType(Material.BARRIER); //+Z
        world.getBlockAt(0, 254, -1).setType(Material.BARRIER); //-Z
    }
}
