package me.fullpotato.badlandscaves.NMS.Possession;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public interface PossessionNMS {
    void markTarget(Player player, LivingEntity entity);
    void unmarkTarget(Player player, LivingEntity entity);
    void setIndicator(Player player, LivingEntity entity);
    void setMark(Player player, LivingEntity ent, boolean marked, boolean invis);
}
