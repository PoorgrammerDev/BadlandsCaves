package me.fullpotato.badlandscaves.badlandscaves.Events.Loot.MobDeathLoot;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Random;


public class creeperDeathLoot implements Listener {
    private BadlandsCaves plugin;
    public creeperDeathLoot (BadlandsCaves bcav) {
        plugin = bcav;
    }

    //TODO maybe for all the ones that dont have any custom drops except for souls, could be put in one class and be a lot more efficient?
    @EventHandler
    public void creeperDeath (EntityDeathEvent event) {
        if (event.getEntity() instanceof Creeper) {
            final Creeper creeper = (Creeper) event.getEntity();
            final Player player = creeper.getKiller();
            final Random random = new Random();

            //can drop soul of decay if player killed
            if (player != null) {
                List<ItemStack> drops = event.getDrops();
                final int soul_chance = plugin.getConfig().getInt("game_values.soul_drop_chance");
                final ItemStack creeper_soul = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.creeper_soul").getValues(true));
                if (random.nextInt(100) <= soul_chance) {
                    drops.add(creeper_soul);
                }
            }

        }
    }
}
