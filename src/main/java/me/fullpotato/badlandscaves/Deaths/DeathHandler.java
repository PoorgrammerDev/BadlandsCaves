package me.fullpotato.badlandscaves.Deaths;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.CustomItem;
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

    private final BadlandsCaves plugin;
    private final World world;
    public DeathHandler(BadlandsCaves bcav) {
        plugin = bcav;
        world = plugin.getServer().getWorld(plugin.getMainWorldName());
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

        resetPlayer(player, false, false, true);

    }

    @EventHandler
    public void give_starter_on_spawn (PlayerRespawnEvent event) {
        boolean active = plugin.getOptionsConfig().getBoolean("give_new_starter_on_spawn");

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
        PlayerScore.THIRST.setScore(plugin, player, 100.0);
        PlayerScore.TOXICITY.setScore(plugin, player, 0.0);
        PlayerScore.THIRST_SYS_VAR.setScore(plugin, player, 0.0);
        PlayerScore.TOX_SLOW_INCR_VAR.setScore(plugin, player, 0.0);
        PlayerScore.WITHDRAW_TIMER.setScore(plugin, player, -255);

        boolean has_powers = (byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == 1;
        if (has_powers) {
            PlayerScore.MANA.setScore(plugin, player, PlayerScore.MAX_MANA.getScore(plugin, player));
            PlayerScore.SWAP_SLOT.setScore(plugin, player, -1);
            PlayerScore.IN_POSSESSION.setScore(plugin, player, 0);
            PlayerScore.POSSESS_ORIG_WORLD.setScore(plugin, player, "__REMOVED__");
            player.setMetadata("agility_jump_id", new FixedMetadataValue(plugin, 0));
            player.setMetadata("agility_jump_timer", new FixedMetadataValue(plugin, 0));
        }

        final int in_descension = (int) PlayerScore.IN_DESCENSION.getScore(plugin, player);
        final boolean in_reflection = (PlayerScore.IN_REFLECTION.hasScore(plugin, player)) && (byte) PlayerScore.IN_REFLECTION.getScore(plugin, player) == 1;
        if (in_descension >= 1 && in_descension <= 3) {
            if (in_descension == 2) {
                int towers_capped = (PlayerScore.DESCENSION_SHRINES_CAPPED.hasScore(plugin, player)) ? (int) PlayerScore.DESCENSION_SHRINES_CAPPED.getScore(plugin, player) : 0;
                boolean supernatural = towers_capped == 4;
                int displace = towers_capped == 4 ? 1 : 0;

                //resetting values
                PlayerScore.HAS_SUPERNATURAL_POWERS.setScore(plugin, player, supernatural ? 1 : 0);
                PlayerScore.DISPLACE_LEVEL.setScore(plugin, player, displace);

                PlayerScore.DESCENSION_DETECT.setScore(plugin, player, 0);
                PlayerScore.AGILITY_LEVEL.setScore(plugin, player, 0);
                PlayerScore.POSSESS_LEVEL.setScore(plugin, player, 0);
                PlayerScore.DESCENSION_SHRINES_CAPPED.setScore(plugin, player, 0);
                PlayerScore.MAX_MANA.setScore(plugin, player, 100);

            }
            PlayerScore.IN_DESCENSION.setScore(plugin, player, 0);

            if (player.getGameMode().equals(GameMode.ADVENTURE)) player.setGameMode(GameMode.SURVIVAL);
        }
        else if (in_reflection) {
            PlayerScore.IN_REFLECTION.setScore(plugin, player, 0);
            PlayerScore.REFLECTION_ZOMBIE.setScore(plugin, player, 0);
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
                PlayerScore.DEATHS.setScore(plugin, player, death_count + 1);
            }
        }

        if (sendToSpawn) {
            Location bed_spawn = player.getBedSpawnLocation();
            if (bed_spawn != null) player.teleport(bed_spawn, PlayerTeleportEvent.TeleportCause.PLUGIN);
            else player.teleport(world.getSpawnLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN);
        }

    }
}
