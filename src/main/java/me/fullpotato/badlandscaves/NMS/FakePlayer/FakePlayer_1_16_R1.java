package me.fullpotato.badlandscaves.NMS.FakePlayer;

import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Pair;
import me.fullpotato.badlandscaves.BadlandsCaves;
import net.minecraft.server.v1_16_R1.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R1.CraftServer;
import org.bukkit.craftbukkit.v1_16_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_16_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class FakePlayer_1_16_R1 implements FakePlayerNMS{
    private final BadlandsCaves plugin;

    public FakePlayer_1_16_R1(BadlandsCaves plugin) {
        this.plugin = plugin;
    }

    public Player summonFakePlayer(Location location, Player player, @Nullable Player sendTo, @Nullable String name) {
        return summonFakePlayer(location, player, sendTo, name, false);
    }

    public Player summonFakePlayer(Location location, Player player, @Nullable Player sendTo, @Nullable String name, boolean copyArmor) {
        MinecraftServer server = ((CraftServer) plugin.getServer()).getServer();
        WorldServer world = ((CraftWorld) location.getWorld()).getHandle();

        EntityPlayer clone = new EntityPlayer(server, world, new GameProfile(player.getUniqueId(), name == null ? player.getName() : name), new PlayerInteractManager(world));
        clone.setPositionRotation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());

        PacketPlayOutEntityEquipment equipmentPacket = null;
        if (copyArmor) {
            EntityEquipment equipment = player.getEquipment();

            List<Pair<EnumItemSlot, net.minecraft.server.v1_16_R1.ItemStack>> list = new ArrayList<>();
            list.add(new Pair<>(EnumItemSlot.HEAD, CraftItemStack.asNMSCopy(equipment.getHelmet())));
            list.add(new Pair<>(EnumItemSlot.CHEST, CraftItemStack.asNMSCopy(equipment.getChestplate())));
            list.add(new Pair<>(EnumItemSlot.LEGS, CraftItemStack.asNMSCopy(equipment.getLeggings())));
            list.add(new Pair<>(EnumItemSlot.FEET, CraftItemStack.asNMSCopy(equipment.getBoots())));

            equipmentPacket = new PacketPlayOutEntityEquipment(clone.getId(), list);
        }


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
            if (copyArmor) sendToAll(equipmentPacket);
        }
        else {
            ((CraftPlayer) sendTo).getHandle().playerConnection.sendPacket(info);
            ((CraftPlayer) sendTo).getHandle().playerConnection.sendPacket(spawn);
            ((CraftPlayer) sendTo).getHandle().playerConnection.sendPacket(team_packet);

            if (copyArmor) {
                ((CraftPlayer) sendTo).getHandle().playerConnection.sendPacket(equipmentPacket);
            }
        }

        return clone.getBukkitEntity().getPlayer();
    }

    public void giveHandItem (Player player, Player sendTo, ItemStack item) {
        EntityPlayer clone = ((CraftPlayer) player).getHandle();

        List<Pair<EnumItemSlot, net.minecraft.server.v1_16_R1.ItemStack>> list = new ArrayList<>();
        list.add(new Pair<>(EnumItemSlot.MAINHAND, CraftItemStack.asNMSCopy(item)));

        PacketPlayOutEntityEquipment handItemPacket = new PacketPlayOutEntityEquipment(clone.getId(), list);

        if (sendTo == null) sendToAll(handItemPacket);
        else ((CraftPlayer) sendTo).getHandle().playerConnection.sendPacket(handItemPacket);
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

    public void damage (Player player, Player sendTo, boolean damaged) {
        EntityPlayer clone = ((CraftPlayer) player).getHandle();
        PacketPlayOutAnimation animation = new PacketPlayOutAnimation(clone, damaged ? 1 : 0);

        if (sendTo == null) sendToAll(animation);
        else ((CraftPlayer) sendTo).getHandle().playerConnection.sendPacket(animation);
    }

    public void remove (Player player) {
        if (player != null) {
            PacketPlayOutEntityDestroy destroy = new PacketPlayOutEntityDestroy(((CraftPlayer) player).getHandle().getId());
            sendToAll(destroy);
        }
    }

    public void sendToAll (Object... packets) {
        if (packets.length < 1) return;

        for (final Player online : plugin.getServer().getOnlinePlayers()) {
            final CraftPlayer ply = (CraftPlayer) online;

            for (Object obj : packets) {
                if (obj instanceof Packet) {
                    Packet<?> packet = (Packet<?>) obj;
                    ply.getHandle().playerConnection.sendPacket(packet);
                }
            }
        }
    }
}
