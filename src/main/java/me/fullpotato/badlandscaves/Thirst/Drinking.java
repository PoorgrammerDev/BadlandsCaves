package me.fullpotato.badlandscaves.Thirst;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.CustomItem;
import me.fullpotato.badlandscaves.Effects.PlayerEffects;
import me.fullpotato.badlandscaves.SupernaturalPowers.BackroomsManager;
import me.fullpotato.badlandscaves.Util.ParticleShapes;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Random;

public class Drinking implements Listener {
    private final BadlandsCaves plugin;
    private final PlayerEffects playerEffects;
    private final BackroomsManager backroomsManager;
    public Drinking(BadlandsCaves bcav, PlayerEffects playerEffects, BackroomsManager backroomsManager) {
        plugin = bcav;
        this.playerEffects = playerEffects;
        this.backroomsManager = backroomsManager;
    }
    @EventHandler
    public void drink (PlayerItemConsumeEvent event) {
        ItemStack item = event.getItem();
        if (!item.getType().equals(Material.POTION)) {
            return;
        }

        Player player = event.getPlayer();
        if (item.getItemMeta() != null && item.getItemMeta().getLore() != null) {
            PotionMeta potionMeta = (PotionMeta) item.getItemMeta();

            //toxic water
            if (potionMeta.getBasePotionData().getType().equals(PotionType.WATER)) {
                drinkToxicWater(player);
            }
            //either purified or antidote
            else if (potionMeta.getBasePotionData().getType().equals(PotionType.UNCRAFTABLE)) {
                //testing if purified
                if (item.isSimilar(plugin.getCustomItemManager().getItem(CustomItem.PURIFIED_WATER))) {
                    drinkPurifiedWater(player);
                }
                else if (item.isSimilar(plugin.getCustomItemManager().getItem(CustomItem.ANTIDOTE))) {
                    drinkAntidote(player);
                }
                else if (item.isSimilar(plugin.getCustomItemManager().getItem(CustomItem.MANA_POTION))) {
                    drinkManaPotion(player);
                }
                else if (item.isSimilar(plugin.getCustomItemManager().getItem(CustomItem.RECALL_POTION))) {
                    drinkRecall(player, event);
                }
            }
        }
    }

    public void drinkToxicWater (Player player) {
        final boolean hardmode = plugin.getSystemConfig().getBoolean("hardmode");
        final double current_thirst = (double) PlayerScore.THIRST.getScore(plugin, player);
        double thirst_add = plugin.getOptionsConfig().getInt(hardmode ? "hardmode_values.tox_drink_thirst_incr" : "pre_hardmode_values.tox_drink_thirst_incr");
        PlayerScore.THIRST.setScore(plugin, player, Math.min(current_thirst + thirst_add, 100));

        double current_tox = (double) PlayerScore.TOXICITY.getScore(plugin, player);
        int tox_add = plugin.getOptionsConfig().getInt(hardmode ? "hardmode_values.tox_drink_tox_incr" : "pre_hardmode_values.tox_drink_tox_incr");
        PlayerScore.TOXICITY.setScore(plugin, player, current_tox + tox_add);
        playerEffects.applyEffects(player, true);
    }

    public void drinkPurifiedWater (Player player) {
        final boolean hardmode = plugin.getSystemConfig().getBoolean("hardmode");
        double current_thirst = (double) PlayerScore.THIRST.getScore(plugin, player);
        int thirst_threshold = plugin.getOptionsConfig().getInt(hardmode ? "hardmode_values.threshold_thirst_sys" : "pre_hardmode_values.threshold_thirst_sys");
        double thirst_add = plugin.getOptionsConfig().getInt(hardmode ? "hardmode_values.purified_drink_thirst_incr" : "pre_hardmode_values.purified_drink_thirst_incr");
        int overflow = (int) (Math.max((current_thirst + thirst_add) - 100, 0));
        int buffer = hardmode ? (thirst_threshold * -50) - (50 * overflow) : (thirst_threshold * -100) - (50 * overflow);

        PlayerScore.THIRST.setScore(plugin, player, Math.min(current_thirst + thirst_add, 100));
        PlayerScore.THIRST_SYS_VAR.setScore(plugin, player, Math.min((int) ((double) PlayerScore.THIRST_SYS_VAR.getScore(plugin, player)), buffer));
        playerEffects.applyEffects(player, true);
    }

    public void drinkAntidote (Player player) {
        final boolean hardmode = plugin.getSystemConfig().getBoolean("hardmode");
        double current_tox = (double) PlayerScore.TOXICITY.getScore(plugin, player);
        double tox_decr = plugin.getOptionsConfig().getInt(hardmode ? "hardmode_values.antidote_drink_tox_decr" : "pre_hardmode_values.antidote_drink_tox_decr");
        PlayerScore.TOXICITY.setScore(plugin, player, Math.max(current_tox - tox_decr, 0));

        final PotionEffectType[] negative = {
                PotionEffectType.POISON,
                PotionEffectType.WITHER,
                PotionEffectType.BLINDNESS,
                PotionEffectType.HUNGER,
                PotionEffectType.CONFUSION,
        };

        for (PotionEffectType type : negative) {
            player.removePotionEffect(type);
        }

        player.getPersistentDataContainer().set(new NamespacedKey(plugin, "corrosive_debuff"), PersistentDataType.BYTE, (byte) 0);
        playerEffects.applyEffects(player, true);
    }

    public void drinkManaPotion (Player player) {
        final boolean has_powers = (byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == 1;
        if (has_powers) {
            double Mana = ((double) PlayerScore.MANA.getScore(plugin, player));
            double max_mana = ((double) PlayerScore.MAX_MANA.getScore(plugin, player));
            double new_mana = Math.min(Mana + 100, max_mana);
            PlayerScore.MANA.setScore(plugin, player, new_mana);
            PlayerScore.MANA_BAR_ACTIVE_TIMER.setScore(plugin, player, 60);
        }
    }

    public void drinkRecall (Player player, Cancellable event) {
        Location location = player.getLocation();
        ArrayList<World> blacklisted = new ArrayList<>();
        blacklisted.add(plugin.getServer().getWorld(plugin.getReflectionWorldName()));
        blacklisted.add(plugin.getServer().getWorld(plugin.getBackroomsWorldName()));
        blacklisted.add(plugin.getServer().getWorld(plugin.getDescensionWorldName()));
        blacklisted.add(plugin.getServer().getWorld(plugin.getChambersWorldName()));
        blacklisted.add(plugin.getServer().getWorld(plugin.getWithdrawWorldName()));

        if (location.getWorld() != null && blacklisted.contains(location.getWorld())) {
            player.sendMessage("§cYou cannot drink that right now.");
            event.setCancelled(true);
            return;
        }

        Random random = new Random();
        double[] tracker = {0.0};
        new BukkitRunnable() {
            @Override
            public void run() {
                if (tracker[0] > player.getHeight()) {
                    this.cancel();
                    Location updated = player.getLocation();
                    Location warp = player.getBedSpawnLocation() == null ? plugin.getServer().getWorld(plugin.getMainWorldName()).getSpawnLocation() : player.getBedSpawnLocation();
                    warp.setYaw(updated.getYaw());
                    warp.setPitch(updated.getPitch());

                    updated.getWorld().spawnParticle(Particle.REDSTONE, updated, 100, 1, 1, 1, 1, new Particle.DustOptions(Color.YELLOW, 0.5F));
                    updated.getWorld().playSound(updated, "custom.supernatural.displace.warp", SoundCategory.PLAYERS, 1, 1);

                    player.teleport(warp, PlayerTeleportEvent.TeleportCause.PLUGIN);

                    warp.getWorld().spawnParticle(Particle.REDSTONE, warp, 100, 1, 1, 1, 1, new Particle.DustOptions(Color.YELLOW, 0.5F));
                    warp.getWorld().playSound(warp, "custom.supernatural.displace.warp", SoundCategory.PLAYERS, 1, 1);

                    if (updated.distanceSquared(location) > 0 && random.nextInt(1000) < updated.distanceSquared(location)) {
                        backroomsManager.enterBackRooms(player, random);
                    }
                }
                else {
                    ParticleShapes.particleCircle(null, Particle.REDSTONE, location.clone().add(0, tracker[0], 0), 1, 0, new Particle.DustOptions(Color.YELLOW, 0.5F));
                    tracker[0] += 0.1;
                }
            }
        }.runTaskTimer(plugin, 0, 1);
    }


}
