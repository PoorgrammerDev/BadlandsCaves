package me.fullpotato.badlandscaves.MobBuffs;

import me.fullpotato.badlandscaves.BadlandsCaves;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Silverfish;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Random;

public class SilverfishBuff implements Listener {
    private final BadlandsCaves plugin;
    private final Random random;
    public SilverfishBuff(BadlandsCaves bcav, Random random) {
        plugin = bcav;
        this.random = random;
    }

    @EventHandler
    public void HMsilverfish (CreatureSpawnEvent event) {
        if (!(event.getEntity() instanceof Silverfish)) return;
        boolean isHardmode = plugin.getSystemConfig().getBoolean("hardmode");
        if (!isHardmode) return;

        Silverfish silverfish = (Silverfish) event.getEntity();

        silverfish.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 9999, random.nextInt(3) + 1, true, true));
        silverfish.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 9999, random.nextInt(3) + 1, true, true));

        final int chaos = plugin.getSystemConfig().getInt("chaos_level");
        final double chance = Math.pow(1.045, chaos) - 1;
        if (!event.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.CUSTOM)) {
            if (random.nextInt(100) < chance) {
                silverfish.getWorld().spawnEntity(silverfish.getLocation(), EntityType.SILVERFISH);
            }
        }
    }
}
