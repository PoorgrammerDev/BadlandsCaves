package me.fullpotato.badlandscaves.badlandscaves.Events.Deaths;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.badlandscaves.Util.InventorySerialize;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;

public class DeathHandler implements Listener {

    private BadlandsCaves plugin;
    private World world = Bukkit.getWorld("world");
    public DeathHandler(BadlandsCaves bcav) {
        plugin = bcav;
    }

    @EventHandler
    public void death_handler(PlayerDeathEvent event) {
        Player player = event.getEntity();
        double toxicity = player.getMetadata("Toxicity").get(0).asDouble();
        double thirst = player.getMetadata("Thirst").get(0).asDouble();

        if (toxicity >= 100 && thirst <= 0) {
            event.setDeathMessage(player.getDisplayName() + " died of multiple causes");
        }
        else if (toxicity >= 100) {
            event.setDeathMessage(player.getDisplayName() + " died of toxicity");
        }
        else if (thirst <= 0) {
            event.setDeathMessage(player.getDisplayName() + " died of dehydration");
        }

        //silent death in descension
        int in_descension = player.getMetadata("in_descension").get(0).asInt();
        if (in_descension == 2) {
            event.setDeathMessage(null);
            if (player.getGameMode().equals(GameMode.ADVENTURE)) player.setGameMode(GameMode.SURVIVAL);
            InventorySerialize invser = new InventorySerialize(plugin);
            invser.loadInventory(player, "descension_inv", true, true);
            return;
        }

        resetPlayer(player, false, false, true);

    }

    @EventHandler
    public void give_starter_on_spawn (PlayerRespawnEvent event) {
        boolean active = plugin.getConfig().getBoolean("game_values.give_new_starter_on_spawn");

        if (!active) return;

        Player player = event.getPlayer();
        Inventory inventory = player.getInventory();

        ItemStack starter_sapling = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.starter_sapling").getValues(true));
        ItemStack starter_bone_meal = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.starter_bone_meal").getValues(true));

        inventory.addItem(starter_sapling);
        inventory.addItem(starter_bone_meal);
    }

    @EventHandler
    public void correctHealth (PlayerRespawnEvent event) {
        final Player player = event.getPlayer();
        new BukkitRunnable() {
            @Override
            public void run() {
                if (player.getAttribute(Attribute.GENERIC_MAX_HEALTH) != null) {
                    player.setHealth(Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue());
                }
                else {
                    player.setHealth(20);
                }
            }
        }.runTaskLaterAsynchronously(plugin, 20);
    }

    public void resetPlayer (Player player) {
        resetPlayer(player, false, false, false);
    }

    public void resetPlayer (Player player, boolean simulateDeath, boolean sendToSpawn, boolean addDeath) {
        int death_count = player.getMetadata("Deaths").get(0).asInt();

        //resetting thirst/tox values on death
        player.setMetadata("Thirst", new FixedMetadataValue(plugin, 100.0));
        player.setMetadata("Toxicity", new FixedMetadataValue(plugin, 0.0));
        player.setMetadata("thirst_sys_var", new FixedMetadataValue(plugin, 0.0));
        player.setMetadata("tox_nat_decr_var", new FixedMetadataValue(plugin, 0.0));
        player.setMetadata("tox_slow_incr_var", new FixedMetadataValue(plugin, 0.0));

        boolean has_powers = player.getMetadata("has_supernatural_powers").get(0).asBoolean();
        if (has_powers) {
            player.setMetadata("Mana", new FixedMetadataValue(plugin, 100));
            player.setMetadata("swap_slot", new FixedMetadataValue(plugin, -1));
            player.setMetadata("in_possession", new FixedMetadataValue(plugin, false));
            player.setMetadata("possess_orig_world", new FixedMetadataValue(plugin, "__REMOVED__"));
            player.setMetadata("agility_jump_id", new FixedMetadataValue(plugin, 0));
            player.setMetadata("agility_jump_timer", new FixedMetadataValue(plugin, 0));
            player.setMetadata("withdraw_timer", new FixedMetadataValue(plugin, -255));
        }

        final int in_descension = player.getMetadata("in_descension").get(0).asInt();
        final boolean in_reflection = player.hasMetadata("in_reflection") && player.getMetadata("in_reflection").get(0).asBoolean();
        if (in_descension >= 1 && in_descension <= 3) {
            if (in_descension == 2) {
                int towers_capped = player.hasMetadata("descension_shrines_capped") ? player.getMetadata("descension_shrines_capped").get(0).asInt() : 0;
                boolean supernatural = towers_capped == 4;
                int displace = towers_capped == 4 ? 1 : 0;

                //resetting values
                player.setMetadata("has_supernatural_powers", new FixedMetadataValue(plugin, supernatural));
                player.setMetadata("displace_level", new FixedMetadataValue(plugin, displace));

                player.setMetadata("descension_detect", new FixedMetadataValue(plugin, 0));
                player.setMetadata("agility_level", new FixedMetadataValue(plugin, 0));
                player.setMetadata("possess_level", new FixedMetadataValue(plugin, 0));
                player.setMetadata("descension_shrines_capped", new FixedMetadataValue(plugin, 0));
                player.setMetadata("max_mana", new FixedMetadataValue(plugin, 100));

            }

            player.setMetadata("in_descension", new FixedMetadataValue(plugin, 0));
        }
        else if (in_reflection) {
            player.setMetadata("in_reflection", new FixedMetadataValue(plugin, false));
            player.setMetadata("reflection_zombie", new FixedMetadataValue(plugin, false));
        }

        //simulate death
        if (simulateDeath) {
            player.getInventory().clear();
            player.setFoodLevel(20);
            if (player.getAttribute(Attribute.GENERIC_MAX_HEALTH) != null) player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
            if (player.getEquipment() != null) player.getEquipment().clear();

            for (PotionEffect effect : player.getActivePotionEffects()) {
                player.removePotionEffect(effect.getType());
            }
        }

        if (addDeath) {
            if (in_descension != 1 && in_descension != 2 && !in_reflection) {
                player.setMetadata("Deaths", new FixedMetadataValue(plugin, death_count + 1));
            }
        }

        if (sendToSpawn) {
            Location bed_spawn = player.getBedSpawnLocation();
            if (bed_spawn != null) player.teleport(bed_spawn);
            else player.teleport(world.getSpawnLocation());
        }

    }
}
