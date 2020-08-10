package me.fullpotato.badlandscaves.Util;

import me.fullpotato.badlandscaves.BadlandsCaves;
import org.bukkit.Location;
import org.bukkit.World;

public class LocationSave {
    private final BadlandsCaves plugin;

    public LocationSave(BadlandsCaves plugin) {
        this.plugin = plugin;
    }

    public String getStringFromLocation (Location location) {
        return location.getWorld().getName() + ";" + location.getX() + ";" + location.getY() + ";" + location.getZ() + ";" + location.getYaw() + ";" + location.getPitch();
    }

    public Location getLocationFromString (String string) {
        final String[] parts = string.split(";");
        final World world = plugin.getServer().getWorld(parts[0]);
        try {
            final double x = Double.parseDouble(parts[1]);
            final double y = Double.parseDouble(parts[2]);
            final double z = Double.parseDouble(parts[3]);
            final float yaw = Float.parseFloat(parts[4]);
            final float pitch = Float.parseFloat(parts[5]);

            return new Location(world, x, y, z, yaw, pitch);
        }
        catch (NumberFormatException | ArrayIndexOutOfBoundsException ignored) {
        }
        return null;
    }
}
