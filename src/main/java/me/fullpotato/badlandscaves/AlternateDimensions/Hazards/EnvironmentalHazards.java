package me.fullpotato.badlandscaves.AlternateDimensions.Hazards;

import me.fullpotato.badlandscaves.BadlandsCaves;
import org.bukkit.GameRule;
import org.bukkit.World;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

public class EnvironmentalHazards {
    private final BadlandsCaves plugin;
    private final Random random;

    public EnvironmentalHazards(BadlandsCaves plugin, Random random) {
        this.plugin = plugin;
        this.random = random;
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

        List<String> hazards = plugin.getSystemConfig().getStringList("alternate_dimensions." + world.getName() + ".hazards");
        hazards.add(hazard.name());

        plugin.getSystemConfig().set("alternate_dimensions." + world.getName() + ".hazards", hazards);
        plugin.saveSystemConfig();

        if (hazard.equals(Hazard.ACID_RAIN)) {
            world.setStorm(true);
            world.setWeatherDuration(999);
            world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
        }
    }

    public boolean isDimension(World world) {
        return (world.getName().startsWith(plugin.getDimensionPrefixName()));
    }

    public Collection<Hazard> getHazards (World world) {
        if (isDimension(world)) {
            final List<String> hazards = plugin.getSystemConfig().getStringList("alternate_dimensions." + world.getName() + ".hazards");
            final Collection<Hazard> output = new HashSet<>();

            for (String hazard : hazards) {
                try {
                    output.add(Hazard.valueOf(hazard.toUpperCase()));
                }
                catch (IllegalArgumentException ignored){
                }
            }
            return output;
        }
        return null;
    }

    public boolean hasHazard(World world, Hazard hazard) {
        if (isDimension(world)) {
            return getHazards(world).contains(hazard);
        }
        return false;
    }

    public boolean hasHazards (World world) {
        if (isDimension(world)) {
            final List<String> hazards = plugin.getSystemConfig().getStringList("alternate_dimensions." + world.getName() + ".hazards");
            return !hazards.isEmpty();
        }
        return false;
    }

}
