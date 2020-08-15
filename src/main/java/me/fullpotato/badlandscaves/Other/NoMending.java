package me.fullpotato.badlandscaves.Other;

import me.fullpotato.badlandscaves.BadlandsCaves;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.VillagerAcquireTradeEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerItemMendEvent;
import org.bukkit.event.world.LootGenerateEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class NoMending implements Listener {
    private final BadlandsCaves plugin;

    public NoMending(BadlandsCaves plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void villagerTrade (VillagerAcquireTradeEvent event) {
        checkForMending(event.getRecipe().getResult());
    }

    @EventHandler
    public void cancelFishing (PlayerFishEvent event) {
        if (event.getState().equals(PlayerFishEvent.State.CAUGHT_FISH)) {
            if (event.getCaught() instanceof Item) {
                final Item item = (Item) event.getCaught();
                final ItemStack itemStack = item.getItemStack();
                checkForMending(itemStack);
                item.setItemStack(itemStack);
            }
        }
    }

    @EventHandler
    public void cancelDeathDrops (EntityDeathEvent event) {
        event.getDrops().forEach(this::checkForMending);
    }

    @EventHandler
    public void cancelLoot (LootGenerateEvent event) {
        final List<ItemStack> loot = event.getLoot();
        loot.forEach(this::checkForMending);
        new BukkitRunnable() {
            @Override
            public void run() {
                event.setLoot(loot);
            }
        }.runTaskLater(plugin, 1);
    }

    @EventHandler
    public void pickupItem (EntityPickupItemEvent event) {
        if (event.getEntity() instanceof Player) {
            final Item itemEntity = event.getItem();
            final ItemStack item = itemEntity.getItemStack();
            checkForMending(item);
            itemEntity.setItemStack(item);
        }
    }

    @EventHandler
    public void cancelMending (PlayerItemMendEvent event) {
        event.setCancelled(true);
    }

    public void checkForMending(ItemStack item) {
        final ItemMeta meta = item.getItemMeta();
        if (item.getType().equals(Material.ENCHANTED_BOOK)) {
            if (meta instanceof EnchantmentStorageMeta) {
                final EnchantmentStorageMeta enchantmentStorageMeta = (EnchantmentStorageMeta) item.getItemMeta();
                if (enchantmentStorageMeta.hasStoredEnchant(Enchantment.MENDING)) {
                    enchantmentStorageMeta.removeStoredEnchant(Enchantment.MENDING);
                    enchantmentStorageMeta.addStoredEnchant(Enchantment.DURABILITY, 5, true);

                    item.setItemMeta(enchantmentStorageMeta);
                }
            }
        }
        else {
            if (meta != null && meta.hasEnchant(Enchantment.MENDING)) {
                meta.removeEnchant(Enchantment.MENDING);
                meta.addEnchant(Enchantment.DURABILITY, 5, true);
                item.setItemMeta(meta);
            }
        }
    }
}
