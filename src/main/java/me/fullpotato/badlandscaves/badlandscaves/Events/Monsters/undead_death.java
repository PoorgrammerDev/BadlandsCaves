package me.fullpotato.badlandscaves.badlandscaves.Events.Monsters;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootContext;
import org.bukkit.loot.LootTable;
import org.bukkit.loot.Lootable;

public class undead_death implements Listener {
    private BadlandsCaves plugin;
    public undead_death (BadlandsCaves bcav) {
        plugin = bcav;
    }

    @EventHandler
    public void undead_death (EntityDamageByEntityEvent event) {
        if (event.getEntity().getType().equals(EntityType.ZOMBIE) ||
        event.getEntity().getType().equals(EntityType.ZOMBIE_VILLAGER) ||
        event.getEntity().getType().equals(EntityType.PIG_ZOMBIE)) {
            LivingEntity entity = (LivingEntity) event.getEntity();
            LootTable lootTable = ((Lootable) entity).getLootTable();

            LootContext.Builder builder = new LootContext.Builder(entity.getLocation());

            if (event.getDamager() instanceof Player) {
                Player player = (Player) event.getDamager();
                if (player.getInventory().getItemInMainHand().hasItemMeta()) {
                    if (player.getInventory().getItemInMainHand().getItemMeta().getLore() == null) {
                        if (player.getInventory().getItemInMainHand().getItemMeta().getLore().contains(ChatColor.GRAY + "Soulcatcher")) {
                            //probably just delete this class ngl
                        }
                    }
                }
            }
        }
    }
}
