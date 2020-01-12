package me.fullpotato.badlandscaves.badlandscaves.Events.MobBuffs;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Spider;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class spiderBuff implements Listener {
    private BadlandsCaves plugin;
    public spiderBuff(BadlandsCaves bcav) {
        plugin = bcav;
    }

    @EventHandler
    public void HMspider (CreatureSpawnEvent event) {
        if (!event.getEntityType().equals(EntityType.SPIDER)) return;
        boolean isHardmode = plugin.getConfig().getBoolean("game_values.hardmode");
        if (!isHardmode) return;

        Spider spider = (Spider) event.getEntity();
        Location location = spider.getLocation();
        World world = location.getWorld();

        for (int a = 0; a < 4; a++) world.spawnEntity(location, EntityType.CAVE_SPIDER);
    }
}
