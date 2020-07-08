package me.fullpotato.badlandscaves.Thirst;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.CustomItem;
import me.fullpotato.badlandscaves.SupernaturalPowers.BackroomsManager;
import me.fullpotato.badlandscaves.Util.ParticleShapes;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import org.bukkit.*;
import org.bukkit.entity.Player;
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
    public Drinking(BadlandsCaves bcav) {
        plugin = bcav;
    }
    @EventHandler
    public void drink (PlayerItemConsumeEvent event) {
        ItemStack item = event.getItem();
        if (!item.getType().equals(Material.POTION)) {
            return;
        }

        boolean isHardmode = plugin.getConfig().getBoolean("system.hardmode");
        Player player = event.getPlayer();
        if (item.getItemMeta() != null && item.getItemMeta().getLore() != null) {
            PotionMeta potionMeta = (PotionMeta) item.getItemMeta();

            //toxic water
            if (potionMeta.getBasePotionData().getType().equals(PotionType.WATER)) {
                double current_thirst = (double) PlayerScore.THIRST.getScore(plugin, player);
                double thirst_add;

                if (isHardmode) {
                    thirst_add = plugin.getConfig().getInt("options.hardmode_values.tox_drink_thirst_incr");
                }
                else {
                    thirst_add = plugin.getConfig().getInt("options.pre_hardmode_values.tox_drink_thirst_incr");
                }

                PlayerScore.THIRST.setScore(plugin, player, Math.min(current_thirst + thirst_add, 100));

                double current_tox = (double) PlayerScore.TOXICITY.getScore(plugin, player);
                double tox_add;

                if (isHardmode) {
                    tox_add = plugin.getConfig().getInt("options.hardmode_values.tox_drink_tox_incr");
                }
                else {
                    tox_add = plugin.getConfig().getInt("options.pre_hardmode_values.tox_drink_tox_incr");
                }

                PlayerScore.TOXICITY.setScore(plugin, player, current_tox + tox_add);
            }
            //either purified or antidote
            else if (potionMeta.getBasePotionData().getType().equals(PotionType.UNCRAFTABLE)) {
                ItemStack purified_water = CustomItem.PURIFIED_WATER.getItem();
                ItemStack antidote = CustomItem.ANTIDOTE.getItem();
                ItemStack mana_potion = CustomItem.MANA_POTION.getItem();
                ItemStack recall_potion = CustomItem.RECALL_POTION.getItem();

                //testing if purified
                if (item.isSimilar(purified_water)) {
                    double current_thirst = (double) PlayerScore.THIRST.getScore(plugin, player);
                    int thirst_threshold = isHardmode ? plugin.getConfig().getInt("options.hardmode_values.threshold_thirst_sys") : plugin.getConfig().getInt("options.pre_hardmode_values.threshold_thirst_sys");
                    double thirst_add = isHardmode ? plugin.getConfig().getInt("options.hardmode_values.purified_drink_thirst_incr") : plugin.getConfig().getInt("options.pre_hardmode_values.purified_drink_thirst_incr");
                    int overflow = (int) (Math.max((current_thirst + thirst_add) - 100, 0));
                    int buffer = isHardmode ? (thirst_threshold * -50) - (50 * overflow) : (thirst_threshold * -100) - (50 * overflow);

                    PlayerScore.THIRST.setScore(plugin, player, Math.min(current_thirst + thirst_add, 100));
                    PlayerScore.THIRST_SYS_VAR.setScore(plugin, player, Math.min((int) ((double) PlayerScore.THIRST_SYS_VAR.getScore(plugin, player)), buffer));
                }
                else if (item.isSimilar(antidote)) {
                    double current_tox = (double) PlayerScore.TOXICITY.getScore(plugin, player);
                    double tox_decr = isHardmode ? plugin.getConfig().getInt("options.hardmode_values.antidote_drink_tox_decr") : plugin.getConfig().getInt("options.pre_hardmode_values.antidote_drink_tox_decr");
                    PlayerScore.TOXICITY.setScore(plugin, player, Math.max(current_tox - tox_decr, 0));

                    player.removePotionEffect(PotionEffectType.POISON);
                    player.removePotionEffect(PotionEffectType.WITHER);
                    player.removePotionEffect(PotionEffectType.BLINDNESS);
                    player.removePotionEffect(PotionEffectType.HUNGER);
                    player.removePotionEffect(PotionEffectType.CONFUSION);

                    player.getPersistentDataContainer().set(new NamespacedKey(plugin, "corrosive_debuff"), PersistentDataType.BYTE, (byte) 0);

                }
                else if (item.isSimilar(mana_potion)) {
                    final boolean has_powers = (byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == 1;
                    if (has_powers) {
                        double Mana = ((double) PlayerScore.MANA.getScore(plugin, player));
                        double max_mana = ((double) PlayerScore.MAX_MANA.getScore(plugin, player));
                        double new_mana = Math.min(Mana + 100, max_mana);
                        PlayerScore.MANA.setScore(plugin, player, new_mana);
                    }
                }
                else if (item.isSimilar(recall_potion)) {
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
                                    BackroomsManager backrooms = new BackroomsManager(plugin);
                                    backrooms.enterBackRooms(player, random);
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
        }
    }
}
