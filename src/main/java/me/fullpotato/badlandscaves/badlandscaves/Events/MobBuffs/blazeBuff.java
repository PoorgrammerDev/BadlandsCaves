package me.fullpotato.badlandscaves.badlandscaves.Events.MobBuffs;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import org.bukkit.entity.Blaze;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class blazeBuff implements Listener {
    private BadlandsCaves plugin;
    public blazeBuff(BadlandsCaves bcav) {
        plugin = bcav;
    }

    @EventHandler
    public void HMBlaze (CreatureSpawnEvent event) {
        if (!(event.getEntity() instanceof Blaze)) return;
        boolean isHardmode = plugin.getConfig().getBoolean("game_values.hardmode");
        if (!isHardmode) return;

        Blaze blaze = (Blaze) event.getEntity();
        blaze.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 9999, 2, true, true));
    }
}
