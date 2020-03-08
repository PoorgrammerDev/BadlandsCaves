package me.fullpotato.badlandscaves.badlandscaves.NMS;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.v1_15_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_15_R1.CraftServer;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftZombie;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;

public class ReflectionWorldNMS {
    private Player player;
    private World reflection_world = Bukkit.getWorld("world_reflection");
    public static EntityPlayer clone;

    public ReflectionWorldNMS (Player player) {
        this.player = player;
    }

    public void disguiseAsPlayer(Zombie zombie) {
            Location location = zombie.getLocation();

            MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
            WorldServer world = ((CraftWorld) Bukkit.getServer().getWorlds().get(0)).getHandle();

            clone = new EntityPlayer(server, world, new GameProfile(player.getUniqueId(), "" + player.getName() + ""), new PlayerInteractManager(world));
            clone.setPositionRotation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());

            clone.setNoGravity(true);
            clone.setFlag(6, true);

            //clone.setInvulnerable(true);

            PacketPlayOutPlayerInfo info = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, clone);
            PacketPlayOutNamedEntitySpawn spawn = new PacketPlayOutNamedEntitySpawn(clone);

            //TODO see if sending to all is actually viable or not (test it)
            sendToAll(info, spawn);
    }

    public void move (Zombie zombie) {
        if (clone != null) {
            Location location = zombie.getLocation();
            clone.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
            clone.setHeadRotation(location.getYaw());

            PacketPlayOutEntityTeleport tp = new PacketPlayOutEntityTeleport(clone);
            PacketPlayOutEntityHeadRotation rot = new PacketPlayOutEntityHeadRotation(clone, (byte) ((location.getYaw() * 256.0F) / 360.0F));

            sendToAll(tp, rot);
        }
    }

    public void sendToAll (Packet<?>... packets) {
        if (packets.length < 1) return;

        for (final Player online : Bukkit.getOnlinePlayers()) {
            if (online.getWorld().equals(reflection_world)) {
                final CraftPlayer ply = (CraftPlayer) player;

                for (Packet<?> packet : packets) {
                    ply.getHandle().playerConnection.sendPacket(packet);
                }
            }
        }
    }
}
