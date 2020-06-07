package me.fullpotato.badlandscaves.NMS;

import net.minecraft.server.v1_15_R1.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.entity.Player;

public class LineOfSight {
    public static boolean hasLineOfSight (Player player, Location location) {
        WorldServer world = ((CraftWorld) player.getWorld()).getHandle();

        EntityPig pig = new EntityPig(EntityTypes.PIG, world);
        pig.setPositionRotation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());

        return player.hasLineOfSight(pig.getBukkitEntity());
    }
}
