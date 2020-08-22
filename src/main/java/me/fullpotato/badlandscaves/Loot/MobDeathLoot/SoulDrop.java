package me.fullpotato.badlandscaves.Loot.MobDeathLoot;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.CustomItem;
import me.fullpotato.badlandscaves.CustomItems.CustomItemManager;
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
    private final Random random;
    private final HashMap<EntityType, ItemStack> souls = new HashMap<>();

    public SoulDrop(BadlandsCaves bcav, Random random) {
        plugin = bcav;
        this.random = random;
        final CustomItemManager customItemManager = plugin.getCustomItemManager();

        souls.put(EntityType.ZOMBIE, customItemManager.getItem(CustomItem.ZOMBIE_SOUL));
        souls.put(EntityType.ZOMBIE_VILLAGER, customItemManager.getItem(CustomItem.ZOMBIE_SOUL));
        souls.put(EntityType.DROWNED, customItemManager.getItem(CustomItem.ZOMBIE_SOUL));
        souls.put(EntityType.HUSK, customItemManager.getItem(CustomItem.ZOMBIE_SOUL));
        souls.put(EntityType.CREEPER, customItemManager.getItem(CustomItem.CREEPER_SOUL));
        souls.put(EntityType.SKELETON, customItemManager.getItem(CustomItem.SKELETON_SOUL));
        souls.put(EntityType.STRAY, customItemManager.getItem(CustomItem.SKELETON_SOUL));
        souls.put(EntityType.WITHER_SKELETON, customItemManager.getItem(CustomItem.SKELETON_SOUL));
        souls.put(EntityType.SPIDER, customItemManager.getItem(CustomItem.SPIDER_SOUL));
        souls.put(EntityType.CAVE_SPIDER, customItemManager.getItem(CustomItem.SPIDER_SOUL));
        souls.put(EntityType.ZOMBIFIED_PIGLIN, customItemManager.getItem(CustomItem.PIGZOMBIE_SOUL));
        souls.put(EntityType.GHAST, customItemManager.getItem(CustomItem.GHAST_SOUL));
        souls.put(EntityType.SILVERFISH, customItemManager.getItem(CustomItem.SILVERFISH_SOUL));
        souls.put(EntityType.WITCH, customItemManager.getItem(CustomItem.WITCH_SOUL));
        souls.put(EntityType.PHANTOM, customItemManager.getItem(CustomItem.PHANTOM_SOUL));
    }

    @EventHandler
    public void dropSoul (EntityDeathEvent event) {
        if (event.getEntity().getWorld().equals(plugin.getServer().getWorld(plugin.getReflectionWorldName()))) return;
        if (event.getEntity().getWorld().equals(plugin.getServer().getWorld(plugin.getChambersWorldName()))) return;

        if (souls.containsKey(event.getEntity().getType())) {
            final LivingEntity entity = event.getEntity();
            final Player player = entity.getKiller();

            //can drop soul if player killed
            if (player != null) {
                List<ItemStack> drops = event.getDrops();
                final int soul_chance = plugin.getOptionsConfig().getInt("soul_drop_chance");
                final ItemStack soul = souls.get(entity.getType());
                if (random.nextInt(100) <= soul_chance) {
                    drops.add(soul);
                }
            }

        }
    }
}
