package me.fullpotato.badlandscaves.CustomItems.Using;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.CustomItem;
import me.fullpotato.badlandscaves.SupernaturalPowers.ReflectionStage.SpawnBoss;
import me.fullpotato.badlandscaves.Util.InventorySerialize;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;


public class UseIncompleteSoulCrystal extends LimitedUseItems implements Listener {
    private BadlandsCaves plugin;
    public UseIncompleteSoulCrystal (BadlandsCaves bcav) {
        plugin = bcav;
    }

    @EventHandler
    public void use_crystal (PlayerInteractEvent event) {
        final Action action = event.getAction();
        if (!action.equals(Action.RIGHT_CLICK_BLOCK) && !action.equals(Action.RIGHT_CLICK_AIR)) return;
        if (event.getItem() == null) return;

        final ItemStack current = event.getItem();
        final ItemStack soul_crystal_incomplete = CustomItem.SOUL_CRYSTAL_INCOMPLETE.getItem();
        if (!checkMatchIgnoreUses(current, soul_crystal_incomplete, 2)) return;

        final Player player = event.getPlayer();
        event.setCancelled(true);

        final boolean in_reflection = player.hasMetadata("in_reflection") && player.getMetadata("in_reflection").get(0).asBoolean();
        if (in_reflection) return;

        final World descension = plugin.getServer().getWorld(plugin.descensionWorldName);
        final World reflection = plugin.getServer().getWorld(plugin.reflectionWorldName);
        if (player.getWorld().equals(reflection) || player.getWorld().equals(descension)) return;
        if ((byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == 1) return;

        //removes a use
        depleteUse(current, 2);

        //adds a death
        player.setMetadata("Deaths", new FixedMetadataValue(plugin, (int) PlayerScore.DEATHS.getScore(plugin, player) + 1));


        //save inventory, then disenchant all items
        InventorySerialize saveInv = new InventorySerialize(plugin);
        saveInv.saveInventory(player, "reflection_inv");
        disenchantInventory(player);

        Location worldspawn = reflection.getSpawnLocation();
        worldspawn.setY(255);

        //removing all effects
        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }

        //entering
        player.setMetadata("in_reflection", new FixedMetadataValue(plugin, true));
        player.teleport(worldspawn, PlayerTeleportEvent.TeleportCause.PLUGIN);

        //heal and full hunger
        new BukkitRunnable() {
            @Override
            public void run() {
                player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20);
                player.setHealth(20);
                player.setSaturation(20);
                player.setFoodLevel(20);
            }
        }.runTaskLaterAsynchronously(plugin, 1);


        //spawning the boss, delay of 10 seconds
        new SpawnBoss(plugin, player).runTaskLater(plugin, 200);
    }

    public void disenchantInventory (final Player player) {
        disenchantItems(player.getInventory().getContents());
    }

    public void disenchantItems (ItemStack[] items) {
        for (ItemStack item : items) {
            if (item != null && item.hasItemMeta() && item.getItemMeta().hasEnchants()) {
                ItemMeta item_meta = item.getItemMeta();
                for (Enchantment enchantment : Enchantment.values()) {
                    if (item_meta.hasEnchant(enchantment)) {
                        item_meta.removeEnchant(enchantment);
                    }
                }
                item.setItemMeta(item_meta);
            }
        }
    }
}
