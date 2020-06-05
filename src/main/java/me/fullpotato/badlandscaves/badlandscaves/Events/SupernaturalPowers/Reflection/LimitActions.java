package me.fullpotato.badlandscaves.badlandscaves.Events.SupernaturalPowers.Reflection;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.badlandscaves.Events.CustomItems.Using.UseIncompleteSoulCrystal;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.List;

public class LimitActions extends BukkitRunnable implements Listener {
    private final BadlandsCaves plugin;
    private final Material[] armor = {
            Material.LEATHER_HELMET,
            Material.LEATHER_CHESTPLATE,
            Material.LEATHER_LEGGINGS,
            Material.LEATHER_BOOTS,
            Material.CHAINMAIL_HELMET,
            Material.CHAINMAIL_CHESTPLATE,
            Material.CHAINMAIL_LEGGINGS,
            Material.CHAINMAIL_BOOTS,
            Material.IRON_HELMET,
            Material.IRON_CHESTPLATE,
            Material.IRON_LEGGINGS,
            Material.IRON_BOOTS,
            Material.GOLDEN_HELMET,
            Material.GOLDEN_CHESTPLATE,
            Material.GOLDEN_LEGGINGS,
            Material.GOLDEN_BOOTS,
            Material.DIAMOND_HELMET,
            Material.DIAMOND_CHESTPLATE,
            Material.DIAMOND_LEGGINGS,
            Material.DIAMOND_BOOTS,
    };

    private final World reflection_world;

    public LimitActions(BadlandsCaves plugin) {
        this.plugin = plugin;
        reflection_world = plugin.getServer().getWorld(plugin.reflectionWorldName);
    }

    /**
     * Disallows placing of fluids (water, lava)
     * */
    @EventHandler
    public void noFluids (PlayerBucketEmptyEvent event) {
        final Player player = event.getPlayer();
        if (player.getWorld().equals(reflection_world)) {
            event.setCancelled(true);
        }
    }

    /**
     * Disallows placing of end crystals
     * */
    @EventHandler
    public void noCrystalCombat (PlayerInteractEvent event) {
        if (event.getPlayer().getWorld().equals(reflection_world)) {
            if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || event.getAction().equals(Action.RIGHT_CLICK_AIR)) {
                if (event.getItem() != null && event.getItem().getType().equals(Material.END_CRYSTAL)) {
                    event.setCancelled(true);
                }
            }
        }
    }

    /**
     * Disallows exploding
     * */
    @EventHandler
    public void noExplode (EntityExplodeEvent event) {
        if (event.getLocation().getWorld().equals(reflection_world)) {
            event.setCancelled(true);
        }
    }

    /**
     * Disallows any entities to spawn for any reason other than the zombie's "custom" spawn.
     * */
    @EventHandler
    public void preventEntitySpawn (CreatureSpawnEvent event) {
        if (event.getEntity().getWorld().equals(reflection_world)) {
            if (!event.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.CUSTOM)) {
                event.setCancelled(true);
            }
        }
    }

    /**
     * Prevents armor change in inventory.
     * */
    @EventHandler
    public void preventArmorChange (InventoryClickEvent event) {
        if (event.getWhoClicked().getWorld().equals(reflection_world)) {
            //un-equip
            final int slot = event.getSlot();
            if (slot >= 36 && slot <= 39) {
                event.setCancelled(true);
                return;
            }

            //equip
            final List<Material> armor = Arrays.asList(this.armor);

            if (event.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY) && event.getCurrentItem() != null && armor.contains(event.getCurrentItem().getType())) {
                event.setCancelled(true);
            }
        }
    }

    /**
     * Prevents armor change by right clicking armor
     * */
    @EventHandler
    public void preventArmorEquip (PlayerInteractEvent event) {
        if (event.getPlayer().getWorld().equals(reflection_world)) {
            if (event.getItem() == null) return;

            final List<Material> armor = Arrays.asList(this.armor);
            if (armor.contains(event.getItem().getType())) {
                event.setCancelled(true);
            }
        }
    }

    /**
     * In case they bypass the system somehow and equip armor anyway, it'll test every 10 seconds and revert their armor back to what it was before.
     * */
    @Override
    public void run() {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            if (player.getWorld().equals(reflection_world)) {
                if (player.getGameMode().equals(GameMode.SURVIVAL) || player.getGameMode().equals(GameMode.ADVENTURE)) {
                    final EntityEquipment eq = player.getEquipment();
                    if (eq != null) {
                        final ItemStack[] current_armor = {
                                eq.getBoots(),
                                eq.getLeggings(),
                                eq.getChestplate(),
                                eq.getHelmet(),
                        };

                        final ItemStack[] saved_armor = {
                                plugin.getConfig().getConfigurationSection( "Scores.users." + player.getUniqueId() + ".saved_inventories.reflection_inv.36") != null ? ItemStack.deserialize(plugin.getConfig().getConfigurationSection( "Scores.users." + player.getUniqueId() + ".saved_inventories.reflection_inv.36").getValues(true)) : null,
                                plugin.getConfig().getConfigurationSection( "Scores.users." + player.getUniqueId() + ".saved_inventories.reflection_inv.37") != null ? ItemStack.deserialize(plugin.getConfig().getConfigurationSection( "Scores.users." + player.getUniqueId() + ".saved_inventories.reflection_inv.37").getValues(true)) : null,
                                plugin.getConfig().getConfigurationSection( "Scores.users." + player.getUniqueId() + ".saved_inventories.reflection_inv.38") != null ? ItemStack.deserialize(plugin.getConfig().getConfigurationSection( "Scores.users." + player.getUniqueId() + ".saved_inventories.reflection_inv.38").getValues(true)) : null,
                                plugin.getConfig().getConfigurationSection( "Scores.users." + player.getUniqueId() + ".saved_inventories.reflection_inv.39") != null ? ItemStack.deserialize(plugin.getConfig().getConfigurationSection( "Scores.users." + player.getUniqueId() + ".saved_inventories.reflection_inv.39").getValues(true)) : null,
                        };

                        UseIncompleteSoulCrystal crystal = new UseIncompleteSoulCrystal(plugin);
                        crystal.disenchantItems(saved_armor);

                        for (int i = 0; i < 4; i++) {
                            if (current_armor[i] != null) {
                                if (!(current_armor[i].isSimilar(saved_armor[i]))) {
                                    player.getInventory().addItem(current_armor[i]);
                                    player.getInventory().setItem(36 + i, saved_armor[i]);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
