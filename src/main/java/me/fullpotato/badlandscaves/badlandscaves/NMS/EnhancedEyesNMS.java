package me.fullpotato.badlandscaves.badlandscaves.NMS;

import net.minecraft.server.v1_15_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public class EnhancedEyesNMS {
    private Player player;
    public EnhancedEyesNMS(Player player) {
        this.player = player;
    }

    public int spawnIndicator (Location location) {
        return spawnIndicator(location, null);
    }


    public int spawnIndicator(Location location, ChatColor color) {
        World world = ((CraftWorld)(location.getWorld())).getHandle();
        EntityShulker shulker = new EntityShulker(EntityTypes.SHULKER, world);
        CraftPlayer ply = (CraftPlayer) player;
        shulker.setPosition(location.getBlockX() + 0.5, location.getBlockY(), location.getBlockZ() + 0.5);
        shulker.setFlag(6, true); //glowing
        shulker.setInvisible(true);
        shulker.setInvulnerable(true);
        shulker.setNoAI(true);
        shulker.setNoGravity(true);
        shulker.setSilent(true);

        if (color != null) {
            Scoreboard scoreboard = new Scoreboard();
            ScoreboardTeam team = new ScoreboardTeam(scoreboard, UUID.randomUUID().toString().substring(0, 15));
            team.setNameTagVisibility(ScoreboardTeamBase.EnumNameTagVisibility.NEVER);
            team.setColor((EnumChatFormat.valueOf(color.name())));
            scoreboard.addPlayerToTeam(shulker.getName(), team);

            PacketPlayOutScoreboardTeam team_packet = new PacketPlayOutScoreboardTeam(team, 0);
            ply.getHandle().playerConnection.sendPacket(team_packet);
        }

        PacketPlayOutSpawnEntityLiving spawn = new PacketPlayOutSpawnEntityLiving(shulker);
        ply.getHandle().playerConnection.sendPacket(spawn);

        PacketPlayOutEntityMetadata data = new PacketPlayOutEntityMetadata(shulker.getId(), shulker.getDataWatcher(), false);
        ply.getHandle().playerConnection.sendPacket(data);

        return shulker.getId();
    }

    public void highlightEntity (org.bukkit.entity.Entity entity) {
        highlightEntity(entity, null);
    }

    public void highlightEntity(org.bukkit.entity.Entity ent, ChatColor color) {
        Entity entity = ((CraftEntity) ent).getHandle();
        CraftPlayer ply = (CraftPlayer) player;
        entity.setFlag(6, true);

        if (color != null) {
            Scoreboard scoreboard = new Scoreboard();
            ScoreboardTeam team = new ScoreboardTeam(scoreboard, UUID.randomUUID().toString().substring(0, 15));
            team.setColor((EnumChatFormat.valueOf(color.name())));
            scoreboard.addPlayerToTeam(entity.getName(), team);

            PacketPlayOutScoreboardTeam team_packet = new PacketPlayOutScoreboardTeam(team, 0);
            ply.getHandle().playerConnection.sendPacket(team_packet);
        }


        PacketPlayOutEntityMetadata data = new PacketPlayOutEntityMetadata(entity.getId(), entity.getDataWatcher(), false);
        ply.getHandle().playerConnection.sendPacket(data);
    }

    public void removeIndicator (int id) {
        PacketPlayOutEntityDestroy destroy = new PacketPlayOutEntityDestroy(id);

        CraftPlayer ply = (CraftPlayer) player;
        ply.getHandle().playerConnection.sendPacket(destroy);
    }
}
