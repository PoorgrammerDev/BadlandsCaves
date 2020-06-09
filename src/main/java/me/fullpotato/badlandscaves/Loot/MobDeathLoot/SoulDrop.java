package me.fullpotato.badlandscaves.Loot.MobDeathLoot;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.CustomItem;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class SoulDrop implements Listener {
    private final BadlandsCaves plugin;
    private final HashMap<EntityType, ItemStack> souls = new HashMap<>();

    public SoulDrop (BadlandsCaves bcav) {
        plugin = bcav;

        souls.put(EntityType.ZOMBIE, CustomItem.ZOMBIE_SOUL.getItem());
        souls.put(EntityType.ZOMBIE_VILLAGER, CustomItem.ZOMBIE_SOUL.getItem());
        souls.put(EntityType.DROWNED, CustomItem.ZOMBIE_SOUL.getItem());
        souls.put(EntityType.HUSK, CustomItem.ZOMBIE_SOUL.getItem());
        souls.put(EntityType.CREEPER, CustomItem.CREEPER_SOUL.getItem());
        souls.put(EntityType.SKELETON, CustomItem.SKELETON_SOUL.getItem());
        souls.put(EntityType.STRAY, CustomItem.SKELETON_SOUL.getItem());
        souls.put(EntityType.WITHER_SKELETON, CustomItem.SKELETON_SOUL.getItem());
        souls.put(EntityType.SPIDER, CustomItem.SPIDER_SOUL.getItem());
        souls.put(EntityType.CAVE_SPIDER, CustomItem.SPIDER_SOUL.getItem());
        souls.put(EntityType.PIG_ZOMBIE, CustomItem.PIGZOMBIE_SOUL.getItem());
        souls.put(EntityType.GHAST, CustomItem.GHAST_SOUL.getItem());
        souls.put(EntityType.SILVERFISH, CustomItem.SILVERFISH_SOUL.getItem());
        souls.put(EntityType.WITCH, CustomItem.WITCH_SOUL.getItem());
        souls.put(EntityType.PHANTOM, CustomItem.PHANTOM_SOUL.getItem());
    }

    @EventHandler
    public void dropSoul (EntityDeathEvent event) {
        if (event.getEntity().getWorld().equals(plugin.getServer().getWorld(plugin.reflectionWorldName))) return;
        if (event.getEntity().getWorld().equals(plugin.getServer().getWorld(plugin.chambersWorldName))) return;

        if (souls.containsKey(event.getEntity().getType())) {
            final LivingEntity entity = event.getEntity();
            final Player player = entity.getKiller();
            final Random random = new Random();

            //can drop soul if player killed
            if (player != null) {
                List<ItemStack> drops = event.getDrops();
                final int soul_chance = plugin.getConfig().getInt("options.soul_drop_chance");
                final ItemStack soul = souls.get(entity.getType());
                if (random.nextInt(100) <= soul_chance) {
                    drops.add(soul);
                }
            }

        }
    }
}
