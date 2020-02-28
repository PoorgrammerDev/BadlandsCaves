package me.fullpotato.badlandscaves.badlandscaves.Events.Loot.MobDeathLoot;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Player;
import org.bukkit.entity.Spider;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Random;

public class spiderDeathLoot implements Listener {
    private BadlandsCaves plugin;
    public spiderDeathLoot (BadlandsCaves bcav) {
        plugin = bcav;
    }

    @EventHandler
    public void spiderDeath (EntityDeathEvent event) {
        if (event.getEntity() instanceof Spider) {
            final Spider spider = (Spider) event.getEntity();
            final Player player = spider.getKiller();
            final Random random = new Random();

            //can drop soul of decay if player killed
            if (player != null) {
                List<ItemStack> drops = event.getDrops();
                final int soul_chance = plugin.getConfig().getInt("game_values.soul_drop_chance");
                final ItemStack spider_soul = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.spider_soul").getValues(true));
                if (random.nextInt(100) <= soul_chance) {
                    drops.add(spider_soul);
                }
            }

        }
    }
}
