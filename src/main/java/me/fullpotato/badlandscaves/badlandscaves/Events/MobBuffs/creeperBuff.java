package me.fullpotato.badlandscaves.badlandscaves.Events.MobBuffs;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import org.bukkit.entity.Creeper;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class creeperBuff implements Listener {
    private BadlandsCaves plugin;
    public creeperBuff(BadlandsCaves bcav) {
        plugin = bcav;
    }

    @EventHandler
    public void HMcreeper (CreatureSpawnEvent event) {
        if (!(event.getEntity() instanceof Creeper)) return;
        boolean isHardmode = plugin.getConfig().getBoolean("game_values.hardmode");
        if (!isHardmode) return;

        Creeper creeper = (Creeper) event.getEntity();

        int radius = plugin.getConfig().getInt("game_values.hardmode_values.creeper_radius");
        creeper.setExplosionRadius(radius);

        creeper.setSilent(true);
    }
}
