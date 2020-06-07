package me.fullpotato.badlandscaves.Deaths;

import me.fullpotato.badlandscaves.CustomItems.CustomItem;
import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.Util.InventorySerialize;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;

public class DeathHandler implements Listener {

    private BadlandsCaves plugin;
    private World world;
    public DeathHandler(BadlandsCaves bcav) {
        plugin = bcav;
        world = plugin.getServer().getWorld(plugin.mainWorldName);
    }

    @EventHandler
    public void death_handler(PlayerDeathEvent event) {
        Player player = event.getEntity();
        double toxicity = (double) PlayerScore.TOXICITY.getScore(plugin, player);
        double thirst = (double) PlayerScore.THIRST.getScore(plugin, player);

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
        int in_descension = (int) PlayerScore.IN_DESCENSION.getScore(plugin, player);
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

        ItemStack starter_sapling = CustomItem.STARTER_SAPLING.getItem();
        ItemStack starter_bone_meal = CustomItem.STARTER_BONE_MEAL.getItem();

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
        int death_count = (int) PlayerScore.DEATHS.getScore(plugin, player);

        //resetting thirst/tox values on death
        player.setMetadata("Thirst", new FixedMetadataValue(plugin, 100.0));
        player.setMetadata("Toxicity", new FixedMetadataValue(plugin, 0.0));
        player.setMetadata("thirst_sys_var", new FixedMetadataValue(plugin, 0.0));
        player.setMetadata("tox_nat_decr_var", new FixedMetadataValue(plugin, 0.0));
        player.setMetadata("tox_slow_incr_var", new FixedMetadataValue(plugin, 0.0));

        boolean has_powers = (byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == 1;
        if (has_powers) {
            player.setMetadata("Mana", new FixedMetadataValue(plugin, (int) PlayerScore.MAX_MANA.getScore(plugin, player)));
            player.setMetadata("swap_slot", new FixedMetadataValue(plugin, -1));
            player.setMetadata("in_possession", new FixedMetadataValue(plugin, false));
            player.setMetadata("possess_orig_world", new FixedMetadataValue(plugin, "__REMOVED__"));
            player.setMetadata("agility_jump_id", new FixedMetadataValue(plugin, 0));
            player.setMetadata("agility_jump_timer", new FixedMetadataValue(plugin, 0));
            player.setMetadata("withdraw_timer", new FixedMetadataValue(plugin, -255));
        }

        final int in_descension = (int) PlayerScore.IN_DESCENSION.getScore(plugin, player);
        final boolean in_reflection = player.hasMetadata("in_reflection") && (byte) PlayerScore.IN_REFLECTION.getScore(plugin, player) == 1;
        if (in_descension >= 1 && in_descension <= 3) {
            if (in_descension == 2) {
                int towers_capped = player.hasMetadata("descension_shrines_capped") ? (int) PlayerScore.DESCENSION_SHRINES_CAPPED.getScore(plugin, player) : 0;
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
            if (bed_spawn != null) player.teleport(bed_spawn, PlayerTeleportEvent.TeleportCause.PLUGIN);
            else player.teleport(world.getSpawnLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN);
        }

    }
}
