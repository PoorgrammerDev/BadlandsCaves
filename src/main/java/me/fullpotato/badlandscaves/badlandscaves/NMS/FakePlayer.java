package me.fullpotato.badlandscaves.badlandscaves.NMS;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.v1_15_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_15_R1.CraftServer;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class FakePlayer {
    private World world;

    public FakePlayer(World world) {
        this.world = world;
    }

    public Player summonFakePlayer(Location location, Player player, Player sendTo, String name) {
        MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
        WorldServer world = ((CraftWorld) this.world).getHandle();

        EntityPlayer clone = new EntityPlayer(server, world, new GameProfile(player.getUniqueId(), name == null ? player.getName() : name), new PlayerInteractManager(world));
        clone.setPositionRotation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());

        clone.setNoGravity(true);
        clone.setFlag(6, true);

        PacketPlayOutPlayerInfo info = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, clone);
        PacketPlayOutNamedEntitySpawn spawn = new PacketPlayOutNamedEntitySpawn(clone);
        PacketPlayOutScoreboardTeam team_packet = null;

        if (name != null && name.equals("§r")) {
            Scoreboard scoreboard = new Scoreboard();
            ScoreboardTeam team = new ScoreboardTeam(scoreboard, "NO_TAG");
            team.setNameTagVisibility(ScoreboardTeamBase.EnumNameTagVisibility.NEVER);
            scoreboard.addPlayerToTeam(clone.getName(), team);
            team_packet = new PacketPlayOutScoreboardTeam(team, 0);
        }

        if (sendTo == null) {
            sendToAll(info, spawn, team_packet);
        }
        else {
            ((CraftPlayer) sendTo).getHandle().playerConnection.sendPacket(info);
            ((CraftPlayer) sendTo).getHandle().playerConnection.sendPacket(spawn);
            ((CraftPlayer) sendTo).getHandle().playerConnection.sendPacket(team_packet);
        }

        return clone.getBukkitEntity().getPlayer();
    }

    public void move (Location location, Player player, Player sendTo, boolean rotation) {
        if (player != null) {

            EntityPlayer clone = ((CraftPlayer) player).getHandle();
            clone.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
            clone.setHeadRotation(location.getYaw());

            PacketPlayOutEntityTeleport tp = new PacketPlayOutEntityTeleport(clone);
            if (rotation) {
                PacketPlayOutEntityHeadRotation rot = new PacketPlayOutEntityHeadRotation(clone, (byte) ((location.getYaw() * 256.0F) / 360.0F));

                if (sendTo == null) {
                    sendToAll(tp, rot);
                }
                else {
                    ((CraftPlayer) sendTo).getHandle().playerConnection.sendPacket(tp);
                    ((CraftPlayer) sendTo).getHandle().playerConnection.sendPacket(rot);
                }
            }
            else {
                if (sendTo == null) {
                    sendToAll(tp);
                }
                else {
                    ((CraftPlayer) sendTo).getHandle().playerConnection.sendPacket(tp);
                }
            }
        }
    }

    public void remove (Player player) {
        if (player != null) {
            PacketPlayOutEntityDestroy destroy = new PacketPlayOutEntityDestroy(((CraftPlayer) player).getHandle().getId());
            sendToAll(destroy);
        }
    }

    public void sendToAll (Packet<?>... packets) {
        if (packets.length < 1) return;

        for (final Player online : Bukkit.getOnlinePlayers()) {
            if (online.getWorld().equals(world)) {
                final CraftPlayer ply = (CraftPlayer) online;

                for (Packet<?> packet : packets) {
                    ply.getHandle().playerConnection.sendPacket(packet);
                }
            }
        }
    }
}
