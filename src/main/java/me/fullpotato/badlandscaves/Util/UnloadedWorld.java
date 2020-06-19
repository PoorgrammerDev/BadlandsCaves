package me.fullpotato.badlandscaves.Util;

import me.fullpotato.badlandscaves.BadlandsCaves;
import org.bukkit.WorldCreator;

import java.io.File;

public class UnloadedWorld {
    private final String worldName;

    public UnloadedWorld(String worldName) {
        this.worldName = worldName;
    }

    public boolean exists() {
        File worldFolder = new File(worldName);
        return (worldFolder != null && worldFolder.isDirectory());
    }

    public boolean load(BadlandsCaves plugin) {
        if (exists()) {
            plugin.getServer().createWorld(new WorldCreator(worldName));
            return true;
        }
        return false;
    }

}
