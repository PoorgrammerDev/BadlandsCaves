package me.fullpotato.badlandscaves.Extraterrestrial.Hazards;

import me.fullpotato.badlandscaves.BadlandsCaves;
import org.bukkit.GameRule;
import org.bukkit.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class EnvironmentalHazards {
    private final BadlandsCaves plugin;
    private final Random random = new Random();

    public EnvironmentalHazards(BadlandsCaves plugin) {
        this.plugin = plugin;
    }

    public enum Hazard {
        ACID_RAIN,
        TOXIC_WATER,
        SLOW_BREAK,
        METEOR_SHOWERS,
        BEWILDERMENT,
        NO_OXYGEN,
        LAVA_FLOOR,
        NO_FLOOR,
        NO_FOOD,
        PARANOIA,
        FREEZING,
    }

    public void addHazard(World world, @Nullable Hazard hazard) {
        if (hazard == null) hazard = Hazard.values()[random.nextInt(Hazard.values().length)];

        List<String> hazards = plugin.getConfig().getStringList("system.planet_stats." + world.getName() + ".hazards");
        hazards.add(hazard.name());

        plugin.getConfig().set("system.planet_stats." + world.getName() + ".hazards", hazards);
        plugin.saveConfig();

        if (hazard.equals(Hazard.ACID_RAIN)) {
            world.setStorm(true);
            world.setWeatherDuration(999);
            world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
        }
    }

    public boolean isPlanet (World world) {
        return (world.getName().startsWith(plugin.planetPrefixName));
    }

    public boolean hasHazard(World world, Hazard hazard) throws IllegalArgumentException {
        if (isPlanet(world)) {
            List<String> hazards = plugin.getConfig().getStringList("system.planet_stats." + world.getName() + ".hazards");
            return !hazards.isEmpty() && hazards.contains(hazard.name());
        }
        throw new IllegalArgumentException("World is not a planet.");
    }

}
