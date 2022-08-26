package me.fullpotato.badlandscaves.NMS.LineOfSight;

import net.minecraft.server.v1_16_R3.EntityPig;
import net.minecraft.server.v1_16_R3.EntityTypes;
import net.minecraft.server.v1_16_R3.WorldServer;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.entity.Player;

public class LineOfSight_1_16_R3 implements LineOfSightNMS {
    public boolean hasLineOfSight (Player player, Location location) {
        WorldServer world = ((CraftWorld) player.getWorld()).getHandle();

        EntityPig pig = new EntityPig(EntityTypes.PIG, world);
        pig.setPositionRotation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());

        return player.hasLineOfSight(pig.getBukkitEntity());
    }
}
