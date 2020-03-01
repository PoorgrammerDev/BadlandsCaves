package me.fullpotato.badlandscaves.badlandscaves.NMS;

import net.minecraft.server.v1_15_R1.PacketPlayOutEntityMetadata;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class PossessionNMS {
    private Player player;

    public PossessionNMS(Player player) {
        this.player = player;
    }

    public void markTarget (LivingEntity entity) {
        setMark(entity, true, true);
    }

    public void unmarkTarget (LivingEntity entity) {
        setMark(entity, false, true);
    }

    public void setIndicator(LivingEntity entity) {
        setMark(entity, true, false);
    }

    public void setMark(LivingEntity ent, boolean marked, boolean invis) {
        net.minecraft.server.v1_15_R1.Entity entity = ((CraftEntity) ent).getHandle();
        CraftPlayer ply = (CraftPlayer) player;

        if (invis) {
            entity.setInvisible(marked);
        }
        entity.setFlag(6, marked);

        PacketPlayOutEntityMetadata data = new PacketPlayOutEntityMetadata(entity.getId(), entity.getDataWatcher(), false);
        ply.getHandle().playerConnection.sendPacket(data);
    }
}
