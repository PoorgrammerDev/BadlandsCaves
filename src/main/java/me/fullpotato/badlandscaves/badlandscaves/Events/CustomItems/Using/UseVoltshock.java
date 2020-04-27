package me.fullpotato.badlandscaves.badlandscaves.Events.CustomItems.Using;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.badlandscaves.Events.CustomItems.Crafting.Voltshock;
import me.fullpotato.badlandscaves.badlandscaves.Util.PositionManager;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MainHand;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class UseVoltshock implements Listener {
    private BadlandsCaves plugin;

    public UseVoltshock(BadlandsCaves plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void voltshockHit (EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && event.getEntity() instanceof LivingEntity) {
            Player player = (Player) event.getDamager();
            LivingEntity entity = (LivingEntity) event.getEntity();
            if (!player.getMetadata("has_supernatural_powers").get(0).asBoolean()) {
                if (event.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK)) {
                    Voltshock voltshock = new Voltshock(plugin);
                    ItemStack item = player.getInventory().getItemInMainHand();
                    if (voltshock.isVoltshock(item)) {
                        if (!voltshock.getOnCooldown(item) && voltshock.getCharge(item) > 0) {
                            double player_dmg = player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).getValue();
                            if (event.getDamage() >= player_dmg) {
                                Random random = new Random();
                                boolean critical = event.getDamage() > player_dmg;

                                if ((critical && random.nextInt(100) < 80) || (!critical && random.nextInt(100) < 40)) {
                                    if (player.getGameMode().equals(GameMode.SURVIVAL) || player.getGameMode().equals(GameMode.ADVENTURE)) {
                                        voltshock.setCharge(item, voltshock.getCharge(item) - 1);
                                    }
                                    voltshock.setOnCooldown(item, true);

                                    int metal = metallicArmor(entity);
                                    event.setDamage(event.getDamage() * ((metal / 8.0) + 1.5));

                                    int duration = 20 + (10 * (metal / 4));
                                    entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, duration, 99, false, false));

                                    int[] ran = {0};
                                    new BukkitRunnable() {
                                        @Override
                                        public void run() {
                                            if (entity.isDead() || ran[0] > (duration / 5)) {
                                                this.cancel();
                                            }
                                            else {
                                                entity.getWorld().spawnParticle(Particle.CRIT_MAGIC, entity.getEyeLocation(), 20, 0.5, 0.5, 0.5, 0);
                                                ran[0]++;
                                            }
                                        }
                                    }.runTaskTimer(plugin, 0, 5);

                                    new BukkitRunnable() {
                                        @Override
                                        public void run() {
                                            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, .5F, 1.2F);
                                            voltshock.setOnCooldown(item, false);
                                        }
                                    }.runTaskLaterAsynchronously(plugin, 60);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public int metallicArmor (LivingEntity entity) {
        EntityEquipment equipment = entity.getEquipment();
        int output = 0;

        if (equipment != null) {
            if (equipment.getHelmet() != null) {
                if (equipment.getHelmet().getType().equals(Material.IRON_HELMET)) output++;
                else if (equipment.getHelmet().getType().equals(Material.GOLDEN_HELMET)) output++;
                else if (equipment.getHelmet().getType().equals(Material.CHAINMAIL_HELMET)) output++;
            }

            if (equipment.getChestplate() != null) {
                if (equipment.getChestplate().getType().equals(Material.IRON_CHESTPLATE)) output++;
                else if (equipment.getChestplate().getType().equals(Material.GOLDEN_CHESTPLATE)) output++;
                else if (equipment.getChestplate().getType().equals(Material.CHAINMAIL_CHESTPLATE)) output++;
            }

            if (equipment.getLeggings() != null) {
                if (equipment.getLeggings().getType().equals(Material.IRON_LEGGINGS)) output++;
                else if (equipment.getLeggings().getType().equals(Material.GOLDEN_LEGGINGS)) output++;
                else if (equipment.getLeggings().getType().equals(Material.CHAINMAIL_LEGGINGS)) output++;
            }

            if (equipment.getBoots() != null) {
                if (equipment.getBoots().getType().equals(Material.IRON_BOOTS)) output++;
                else if (equipment.getBoots().getType().equals(Material.GOLDEN_BOOTS)) output++;
                else if (equipment.getBoots().getType().equals(Material.CHAINMAIL_BOOTS)) output++;
            }
        }

        return output;
    }
}
