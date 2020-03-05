package me.fullpotato.badlandscaves.badlandscaves.NMS;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.v1_15_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_15_R1.CraftServer;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftZombie;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;

public class ReflectionWorldNMS {
    private Player player;
    private CraftPlayer ply;
    public static EntityPlayer clone;

    public ReflectionWorldNMS (Player player) {
        this.player = player;
        this.ply = (CraftPlayer) player;
    }

    public void disguiseAsPlayer(Zombie zomb) {
            Location location = zomb.getLocation();

            MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
            WorldServer world = ((CraftWorld) Bukkit.getServer().getWorlds().get(0)).getHandle();

            clone = new EntityPlayer(server, world, new GameProfile(player.getUniqueId(), "" + player.getName() + ""), new PlayerInteractManager(world));
            clone.setPositionRotation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());

            clone.setNoGravity(true);
            clone.setFlag(6, true);
            //clone.setInvulnerable(true);

            PacketPlayOutPlayerInfo info = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, clone);
            PacketPlayOutNamedEntitySpawn spawn = new PacketPlayOutNamedEntitySpawn(clone);

            ply.getHandle().playerConnection.sendPacket(info);
            ply.getHandle().playerConnection.sendPacket(spawn);
    }

    public void move (Zombie zomb) {
        if (clone != null) {
            Location location = zomb.getLocation();
            clone.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
            clone.setHeadRotation(location.getYaw());

            PacketPlayOutEntityTeleport tp = new PacketPlayOutEntityTeleport(clone);
            PacketPlayOutEntityHeadRotation rot = new PacketPlayOutEntityHeadRotation(clone, (byte) ((location.getYaw() * 256.0F) / 360.0F));

            ply.getHandle().playerConnection.sendPacket(tp);
            ply.getHandle().playerConnection.sendPacket(rot);
        }
    }
}
