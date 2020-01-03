package me.fullpotato.badlandscaves.badlandscaves.events.Mob_Buffs;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.plugin.Plugin;

public class prHMZombie implements Listener {
    private Plugin plugin = BadlandsCaves.getPlugin(BadlandsCaves.class);

    @EventHandler
    public void zombSpawn (CreatureSpawnEvent event) {
        Entity entity = event.getEntity();

        if (entity instanceof Zombie) {
            Bukkit.dispatchCommand(entity, "/replaceitem entity @s weapon.mainhand minecraft:iron_sword");
        }
    }
}
