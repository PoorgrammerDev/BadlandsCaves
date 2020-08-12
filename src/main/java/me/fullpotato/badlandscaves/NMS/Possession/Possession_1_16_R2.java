package me.fullpotato.badlandscaves.NMS.Possession;

import net.minecraft.server.v1_16_R2.Entity;
import net.minecraft.server.v1_16_R2.PacketPlayOutEntityMetadata;
import org.bukkit.craftbukkit.v1_16_R2.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_16_R2.entity.CraftPlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class Possession_1_16_R2 implements PossessionNMS {

    public void markTarget (Player player, LivingEntity entity) {
        setMark(player, entity, true, true);
    }

    public void unmarkTarget (Player player, LivingEntity entity) {
        setMark(player, entity, false, true);
    }

    public void setIndicator(Player player, LivingEntity entity) {
        setMark(player, entity, true, false);
    }

    public void setMark(Player player, LivingEntity ent, boolean marked, boolean invis) {
        Entity entity = ((CraftEntity) ent).getHandle();
        CraftPlayer ply = (CraftPlayer) player;

        if (invis) {
            entity.setInvisible(marked);
        }
        entity.setFlag(6, marked);

        PacketPlayOutEntityMetadata data = new PacketPlayOutEntityMetadata(entity.getId(), entity.getDataWatcher(), false);
        ply.getHandle().playerConnection.sendPacket(data);
    }
}
