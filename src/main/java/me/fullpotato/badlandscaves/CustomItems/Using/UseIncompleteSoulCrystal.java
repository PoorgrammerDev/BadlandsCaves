package me.fullpotato.badlandscaves.CustomItems.Using;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.CustomItem;
import me.fullpotato.badlandscaves.SupernaturalPowers.ReflectionStage.LimitActions;
import me.fullpotato.badlandscaves.SupernaturalPowers.ReflectionStage.SpawnBoss;
import me.fullpotato.badlandscaves.SupernaturalPowers.ReflectionStage.ZombieBossBehavior;
import me.fullpotato.badlandscaves.Util.InventorySerialize;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import org.bukkit.Location;
import org.bukkit.Material;
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
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;


public class UseIncompleteSoulCrystal extends LimitedUseItems implements Listener {
    private final BadlandsCaves plugin;
    private final InventorySerialize inventorySerialize;
    private final World descension;
    private final World reflection;
    private final Set<Material> blacklisted;
    private final Random random;

    public UseIncompleteSoulCrystal(BadlandsCaves plugin, InventorySerialize inventorySerialize, Random random) {
        this.plugin = plugin;
        descension = plugin.getServer().getWorld(plugin.getDescensionWorldName());
        reflection = plugin.getServer().getWorld(plugin.getReflectionWorldName());
        this.inventorySerialize = inventorySerialize;
        this.random = random;

        blacklisted = new HashSet<>();
        blacklisted.add(Material.CRAFTING_TABLE);
        blacklisted.add(Material.STICK);
        blacklisted.add(Material.TOTEM_OF_UNDYING);
        blacklisted.add(Material.WATER_BUCKET);
        blacklisted.add(Material.LAVA_BUCKET);
        blacklisted.add(Material.TNT);
        blacklisted.add(Material.CHEST);
        blacklisted.add(Material.ENDER_CHEST);
        blacklisted.add(Material.BARREL);
        blacklisted.add(Material.SHULKER_BOX);
        blacklisted.add(Material.REDSTONE);
        blacklisted.add(Material.DISPENSER);
    }

    @EventHandler
    public void use_crystal (PlayerInteractEvent event) {
        final Action action = event.getAction();
        if (!action.equals(Action.RIGHT_CLICK_BLOCK) && !action.equals(Action.RIGHT_CLICK_AIR)) return;
        if (event.getItem() == null) return;

        final ItemStack current = event.getItem();
        final ItemStack soul_crystal_incomplete = plugin.getCustomItemManager().getItem(CustomItem.SOUL_CRYSTAL_INCOMPLETE);
        if (!checkMatchIgnoreUses(current, soul_crystal_incomplete, 2)) return;

        final Player player = event.getPlayer();
        event.setCancelled(true);

        final boolean in_reflection = (PlayerScore.IN_REFLECTION.hasScore(plugin, player)) && ((byte) PlayerScore.IN_REFLECTION.getScore(plugin, player) == 1);
        if (in_reflection) return;

        if (player.getWorld().equals(reflection) || player.getWorld().equals(descension)) return;
        if ((byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == 1) return;

        if (!descension.getEntitiesByClass(Player.class).isEmpty()) return;

        //removes a use
        depleteUse(current, 2);

        //adds a death
        PlayerScore.DEATHS.setScore(plugin, player, (int) PlayerScore.DEATHS.getScore(plugin, player) + 1);

        //save inventory, then disenchant all items
        inventorySerialize.saveInventory(player, "reflection_inv");
        disenchantInventory(player);

        //remove blacklisted items
        player.getInventory().forEach(itemStack -> {
            if (itemStack != null && blacklisted.contains(itemStack.getType())) {
                itemStack.setAmount(0);
            }
        });

        Location worldspawn = reflection.getSpawnLocation();
        worldspawn.setY(255);

        //removing all effects
        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }

        //entering
        PlayerScore.IN_REFLECTION.setScore(plugin, player, 1);
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


        //runnables
        new SpawnBoss(plugin, player).runTaskLater(plugin, 200);
        new ZombieBossBehavior(plugin, random).runTaskTimer(plugin, 0, 0);
        new LimitActions(plugin, this).runTaskTimer(plugin, 0, 20);
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
