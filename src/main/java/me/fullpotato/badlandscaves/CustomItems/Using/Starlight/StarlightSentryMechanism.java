package me.fullpotato.badlandscaves.CustomItems.Using.Starlight;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.Crafting.Starlight.StarlightCharge;
import me.fullpotato.badlandscaves.CustomItems.Crafting.Starlight.StarlightTools;
import me.fullpotato.badlandscaves.CustomItems.CustomItem;
import me.fullpotato.badlandscaves.Util.ParticleShapes;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class StarlightSentryMechanism implements Listener {
    private final BadlandsCaves plugin;

    public StarlightSentryMechanism(BadlandsCaves plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void placeSentry(PlayerInteractEvent event) {
        final ItemStack item = event.getItem();
        if (item != null) {
            final StarlightTools toolManager = new StarlightTools(plugin);
            if (toolManager.isStarlightSentry(item)) {
                if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                    final Player player = event.getPlayer();
                    final World world = player.getWorld();
                    final Block clickedBlock = event.getClickedBlock();
                    event.setCancelled(true);

                    if (clickedBlock != null) {
                        final Block block = clickedBlock.getRelative(event.getBlockFace());
                        final Location location = block.getLocation().add(0.5, 0, 0.5);

                        if ((byte) PlayerScore.HAS_STARLIGHT_SENTRY.getScore(plugin, player) == (byte) 0) {
                            final StarlightCharge chargeManager = new StarlightCharge(plugin);
                            final int charge = chargeManager.getCharge(item);
                            if (charge > 0) {
                                Slime slime = (Slime) world.spawnEntity(location.clone().add(0, 1, 0), EntityType.SLIME);
                                slime.setAI(false);
                                slime.setGravity(false);
                                slime.setSize(4);
                                slime.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 999999, 0, false, false));
                                slime.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 999999, 99, false, false));
                                slime.getPersistentDataContainer().set(new NamespacedKey(plugin, "is_starlight_sentry_hit_detector"), PersistentDataType.BYTE, (byte) 1);

                                ArmorStand armorStand = (ArmorStand) world.spawnEntity(location, EntityType.ARMOR_STAND);
                                armorStand.setCustomName(player.getDisplayName() + "'s Starlight Sentry");
                                armorStand.setMarker(true);
                                armorStand.setVisible(false);
                                armorStand.setInvulnerable(true);
                                armorStand.setGravity(false);
                                armorStand.getEquipment().setHelmet(new ItemStack(Material.DISPENSER));

                                armorStand.getPersistentDataContainer().set(new NamespacedKey(plugin, "is_starlight_sentry"), PersistentDataType.BYTE, (byte) 1);
                                armorStand.getPersistentDataContainer().set(new NamespacedKey(plugin, "charge"), PersistentDataType.INTEGER, charge);
                                armorStand.getPersistentDataContainer().set(new NamespacedKey(plugin, "owner"), PersistentDataType.STRING, player.getUniqueId().toString());
                                armorStand.getPersistentDataContainer().set(new NamespacedKey(plugin, "hitreg_id"), PersistentDataType.STRING, slime.getUniqueId().toString());

                                slime.getPersistentDataContainer().set(new NamespacedKey(plugin, "sentry_id"), PersistentDataType.STRING, armorStand.getUniqueId().toString());

                                PlayerScore.HAS_STARLIGHT_SENTRY.setScore(plugin, player, (byte) 1);
                                PlayerScore.STARLIGHT_SENTRY_UUID.setScore(plugin, player, armorStand.getUniqueId().toString());
                                item.setAmount(item.getAmount() - 1);

                                armorStandSys(armorStand, slime);
                            }
                        }
                    }
                } else if (event.getAction().equals(Action.RIGHT_CLICK_AIR)) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void slimeDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Slime) {
            Slime slime = (Slime) event.getEntity();
            Byte result = slime.getPersistentDataContainer().get(new NamespacedKey(plugin, "is_starlight_sentry_hit_detector"), PersistentDataType.BYTE);
            if (result != null && result == (byte) 1) {
                slime.setFireTicks(0);
                event.setCancelled(true);
                if (event.getCause().equals(EntityDamageEvent.DamageCause.SUFFOCATION)) return;
                String sentryResult = slime.getPersistentDataContainer().get(new NamespacedKey(plugin, "sentry_id"), PersistentDataType.STRING);
                if (sentryResult != null) {
                    final UUID uuid = UUID.fromString(sentryResult);
                    Entity entity = plugin.getServer().getEntity(uuid);
                    if (entity instanceof ArmorStand) {
                        ArmorStand armorStand = (ArmorStand) entity;
                        if (isSentry(armorStand)) {
                            int charge = (int) (getCharge(armorStand) - (event.getDamage() * 5));
                            setCharge(armorStand, charge);

                            if (charge <= 0) {
                                destroySentry(armorStand, slime);
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void ownerPickupSentry (PlayerInteractAtEntityEvent event) {
        if (event.getHand().equals(EquipmentSlot.HAND)) {
            if (event.getRightClicked() instanceof ArmorStand) {
                ArmorStand armorStand = (ArmorStand) event.getRightClicked();
                if (isSentry(armorStand)) {
                    final Player owner = getOwner(armorStand);
                    if (owner != null && owner.equals(event.getPlayer())) {
                        destroySentry(armorStand);
                    }
                }
            }
            else if (event.getRightClicked() instanceof Slime) {
                Slime slime = (Slime) event.getRightClicked();
                String sentryResult = slime.getPersistentDataContainer().get(new NamespacedKey(plugin, "sentry_id"), PersistentDataType.STRING);
                if (sentryResult != null) {
                    final UUID uuid = UUID.fromString(sentryResult);
                    Entity entity = plugin.getServer().getEntity(uuid);
                    if (entity instanceof ArmorStand) {
                        ArmorStand armorStand = (ArmorStand) entity;
                        if (isSentry(armorStand)) {
                            destroySentry(armorStand, slime);
                        }
                    }
                }
            }
        }
    }

    public void destroySentry (ArmorStand armorStand) {
        if (isSentry(armorStand)) {
            String result = armorStand.getPersistentDataContainer().get(new NamespacedKey(plugin, "sentry_id"), PersistentDataType.STRING);
            if (result != null) {
                Entity entity = plugin.getServer().getEntity(UUID.fromString(result));
                if (entity instanceof Slime) {
                    Slime slime = (Slime) entity;
                    destroySentry(armorStand, slime);
                }

            }
        }
    }

    public void destroySentry(ArmorStand armorStand, Slime slime) {
        if (isSentry(armorStand)) {
            int charge = getCharge(armorStand);
            final Player player = getOwner(armorStand);
            if (player != null) {
                final ItemStack item = CustomItem.STARLIGHT_SENTRY.getItem();
                StarlightCharge chargeManager = new StarlightCharge(plugin);
                chargeManager.setCharge(item, charge);

                armorStand.remove();
                slime.remove();

                PlayerScore.HAS_STARLIGHT_SENTRY.setScore(plugin, player, (byte) 0);
                PlayerScore.STARLIGHT_SENTRY_UUID.setScore(plugin, player, "");

                player.sendMessage(ChatColor.YELLOW + "Your Starlight Sentry has been returned to your inventory.");
                if (player.getInventory().firstEmpty() != -1) {
                    player.getInventory().addItem(item);
                } else {
                    player.getWorld().dropItemNaturally(player.getLocation(), item);
                }
            }
        }
    }

    public boolean isSentry (ArmorStand armorStand) {
        Byte armorStandResult = armorStand.getPersistentDataContainer().get(new NamespacedKey(plugin, "is_starlight_sentry"), PersistentDataType.BYTE);
        if (armorStandResult != null) {
            return armorStandResult == (byte) 1;
        }
        return false;
    }

    public int getCharge (ArmorStand armorStand) {
        if (isSentry(armorStand)) {
            Integer result = armorStand.getPersistentDataContainer().get(new NamespacedKey(plugin, "charge"), PersistentDataType.INTEGER);
            if (result != null) {
                return result;
            }
        }
        return -1;
    }

    public @Nullable Player getOwner (ArmorStand armorStand) {
        if (isSentry(armorStand)) {
            String result = armorStand.getPersistentDataContainer().get(new NamespacedKey(plugin, "owner"), PersistentDataType.STRING);
            if (result != null) {
                return plugin.getServer().getPlayer(UUID.fromString(result));
            }
        }
        return null;
    }

    public void setCharge (ArmorStand armorStand, int charge) {
        if (isSentry(armorStand)) {
            armorStand.getPersistentDataContainer().set(new NamespacedKey(plugin, "charge"), PersistentDataType.INTEGER, charge);
        }
    }

    public List<LivingEntity> getHostileMobs (List<Entity> entities) {
        List<LivingEntity> hostileMobs = new ArrayList<>();

        for (Entity entity : entities) {
            if (entity instanceof Monster) {
                if (entity instanceof PigZombie) {
                    if (((PigZombie) entity).isAngry()) {
                        hostileMobs.add((PigZombie) entity);
                    }
                } else {
                    hostileMobs.add((Monster) entity);
                }
            }
            else if (entity instanceof Ghast) {
                hostileMobs.add((Ghast) entity);
            }
            else if (entity instanceof Phantom) {
                hostileMobs.add((Phantom) entity);
            }
        }
        return hostileMobs;
    }


    public void armorStandSys (ArmorStand armorStand, Slime slime) {
        final StarlightBlasterMechanism blasterMechanism = new StarlightBlasterMechanism(plugin);
        final Location location = armorStand.getLocation();
        final int range = 20;
        final int damage = blasterMechanism.getDamage();

        new BukkitRunnable() {
            @Override
            public void run() {
                if (armorStand.isDead()) {
                    this.cancel();
                    return;
                }

                List<Entity> nearbyEntities = armorStand.getNearbyEntities(range, range, range);
                List<LivingEntity> hostiles = getHostileMobs(nearbyEntities);

                double closestDistance = Double.MAX_VALUE;
                LivingEntity target = null;
                for (LivingEntity entity : hostiles) {
                    if (armorStand.hasLineOfSight(entity)) {
                        if (entity instanceof Mob) {
                            Mob mob = (Mob) entity;
                            if (mob.getTarget() == null) {
                                mob.setTarget(slime);
                            }
                        }

                        double distance = armorStand.getLocation().distanceSquared(entity.getLocation());
                        if (distance < closestDistance) {
                            closestDistance = distance;
                            target = entity;
                        }
                    }
                }

                if (target != null) {
                    location.setDirection(target.getLocation().subtract(armorStand.getLocation()).toVector());
                    armorStand.teleport(location);

                    if (target instanceof Mob) {
                        Mob mob = (Mob) target;
                        mob.setTarget(slime);
                    }

                    target.damage(damage, armorStand);
                    armorStand.getWorld().playSound(location, "custom.starlight_blaster", 1, 1);
                    ParticleShapes.particleLine(null, Particle.REDSTONE, armorStand.getLocation().add(0, 1.5, 0), target.getEyeLocation(), 0, new Particle.DustOptions(Color.fromRGB(255, 200, 1), 1), 1);

                    int charge = getCharge(armorStand) - 25;
                    setCharge(armorStand, charge);
                    if (charge <= 0) {
                        destroySentry(armorStand, slime);
                    }

                }
            }
        }.runTaskTimer(plugin, 0, 20);
    }

}
