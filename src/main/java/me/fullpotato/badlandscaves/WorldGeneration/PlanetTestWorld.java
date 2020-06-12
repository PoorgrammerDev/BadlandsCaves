package me.fullpotato.badlandscaves.WorldGeneration;

import me.fullpotato.badlandscaves.BadlandsCaves;
import org.bukkit.WorldCreator;

public class PlanetTestWorld {
    private final BadlandsCaves plugin;

    public PlanetTestWorld(BadlandsCaves plugin) {
        this.plugin = plugin;
    }

    public void generate() {
        WorldCreator creator = new WorldCreator("test_planet");
        creator.generator(new PlanetsGeneration());
        creator.createWorld();
    }
}
