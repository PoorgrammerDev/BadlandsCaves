package me.fullpotato.badlandscaves.badlandscaves.Events.Loot.MobDeathLoot;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Spider;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class SoulDrop implements Listener {
    private BadlandsCaves plugin;
    private HashMap<EntityType, ItemStack> souls = new HashMap<>();

    public SoulDrop (BadlandsCaves bcav) {
        plugin = bcav;

        souls.put(EntityType.ZOMBIE, ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.zombie_soul").getValues(true)));
        souls.put(EntityType.ZOMBIE_VILLAGER, ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.zombie_soul").getValues(true)));
        souls.put(EntityType.DROWNED, ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.zombie_soul").getValues(true)));
        souls.put(EntityType.HUSK, ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.zombie_soul").getValues(true)));
        souls.put(EntityType.CREEPER, ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.creeper_soul").getValues(true)));
        souls.put(EntityType.SKELETON, ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.skeleton_soul").getValues(true)));
        souls.put(EntityType.STRAY, ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.skeleton_soul").getValues(true)));
        souls.put(EntityType.WITHER_SKELETON, ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.skeleton_soul").getValues(true)));
        souls.put(EntityType.SPIDER, ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.spider_soul").getValues(true)));
        souls.put(EntityType.CAVE_SPIDER, ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.spider_soul").getValues(true)));
        souls.put(EntityType.PIG_ZOMBIE, ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.pigzombie_soul").getValues(true)));
        souls.put(EntityType.GHAST, ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.ghast_soul").getValues(true)));
        souls.put(EntityType.SILVERFISH, ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.silverfish_soul").getValues(true)));
        souls.put(EntityType.WITCH, ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.witch_soul").getValues(true)));
        souls.put(EntityType.PHANTOM, ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.phantom_soul").getValues(true)));
    }

    @EventHandler
    public void dropSoul (EntityDeathEvent event) {
        if (event.getEntity().getWorld().equals(plugin.getServer().getWorld("world_reflection"))) return;
        if (event.getEntity().getWorld().equals(plugin.getServer().getWorld("world_chambers"))) return;

        if (souls.containsKey(event.getEntity().getType())) {
            final LivingEntity entity = event.getEntity();
            final Player player = entity.getKiller();
            final Random random = new Random();

            //can drop soul if player killed
            if (player != null) {
                List<ItemStack> drops = event.getDrops();
                final int soul_chance = plugin.getConfig().getInt("game_values.soul_drop_chance");
                final ItemStack soul = souls.get(entity.getType());
                if (random.nextInt(100) <= soul_chance) {
                    drops.add(soul);
                }
            }

        }
    }
}
