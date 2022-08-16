package me.fullpotato.badlandscaves.SupernaturalPowers.Artifacts.Mechanisms;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.Crafting.Voidmatter;
import me.fullpotato.badlandscaves.NMS.EnhancedEyes.EnhancedEyesNMS;
import me.fullpotato.badlandscaves.NMS.LineOfSight.LineOfSightNMS;
import me.fullpotato.badlandscaves.SupernaturalPowers.Artifacts.Artifact;
import me.fullpotato.badlandscaves.SupernaturalPowers.Artifacts.ArtifactManager;
import me.fullpotato.badlandscaves.SupernaturalPowers.ReflectionStage.ZombieBossBehavior;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootTables;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;
import java.util.Random;

public class ArtifactFleetingSpirits extends ArtifactMechanisms implements Listener {
    private final ZombieBossBehavior zombieBossBehavior;
    private final LineOfSightNMS lineOfSightNMS;
    private final EnhancedEyesNMS enhancedEyesNMS;
    private final Random random;
    final int cost = plugin.getOptionsConfig().getInt("spell_costs.displace_mana_cost") * 4;
    private final EntityDamageEvent.DamageCause[] blacklistedCauses = {
            EntityDamageEvent.DamageCause.FIRE_TICK,
            EntityDamageEvent.DamageCause.POISON,
            EntityDamageEvent.DamageCause.WITHER,
            EntityDamageEvent.DamageCause.STARVATION,
            EntityDamageEvent.DamageCause.SUICIDE,
            EntityDamageEvent.DamageCause.VOID,
    };

    public ArtifactFleetingSpirits(BadlandsCaves plugin, Voidmatter voidmatter, ArtifactManager artifactManager, Random random) {
        super(plugin, voidmatter, artifactManager);
        this.random = random;
        zombieBossBehavior = new ZombieBossBehavior(plugin, random);
        lineOfSightNMS = plugin.getLineOfSightNMS();
        enhancedEyesNMS = plugin.getEnhancedEyesNMS();
    }

    @EventHandler
    public void playerDamage (EntityDamageEvent event) {
        if (plugin.getSystemConfig().getBoolean("hardmode")) {
            if (event.getEntity() instanceof Player) {
                final Player player = (Player) event.getEntity();
                final EntityDamageEvent.DamageCause cause = event.getCause();
                for (EntityDamageEvent.DamageCause blacklistedCause : blacklistedCauses) {
                    if (cause.equals(blacklistedCause)) return;
                }

                final double finalDamage = event.getFinalDamage();
                if (finalDamage > player.getHealth() || finalDamage / player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() > 0.5D) {
                    final int level = (int) PlayerScore.DISPLACE_LEVEL.getScore(plugin, player);
                    if ((byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == 1 && level > 0) {
                        final EntityEquipment equipment = player.getEquipment();
                        if (equipment != null) {
                            for (ItemStack armor : equipment.getArmorContents()) {
                                if (armor != null && voidmatter.isVoidmatterArmor(armor)) {
                                    if (artifactManager.hasArtifact(player, Artifact.FLEETING_SPIRITS)) {
                                        final Location location = player.getLocation();
                                        Location source = null;
                                        Entity damager = null;
                                        if (event instanceof EntityDamageByEntityEvent) {
                                            EntityDamageByEntityEvent byEntityEvent = (EntityDamageByEntityEvent) event;
                                            damager = byEntityEvent.getDamager();
                                            source = damager.getLocation();

                                            if (damager instanceof Projectile) {
                                                Projectile projectile = (Projectile) damager;
                                                ProjectileSource shooter = projectile.getShooter();
                                                if (shooter instanceof Entity) {
                                                    damager = (Entity) shooter;
                                                }
                                            }

                                        }
                                        else if (event instanceof EntityDamageByBlockEvent) {
                                            EntityDamageByBlockEvent byBlockEvent = (EntityDamageByBlockEvent) event;
                                            Block block = byBlockEvent.getDamager();
                                            if (block != null) {
                                                source = block.getLocation();
                                            }
                                        }
                                        if (attemptWarp(player, source)) {
                                            event.setCancelled(true);

                                            if (damager instanceof LivingEntity) {
                                                spawnVindicator(player, (LivingEntity) damager, location, true);
                                            }
                                        }
                                    }
                                    return;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public boolean attemptWarp (Player player, @Nullable Location source) {
        final int level = (int) PlayerScore.DISPLACE_LEVEL.getScore(plugin, player);
        final double mana = (double) PlayerScore.MANA.getScore(plugin, player);

        if (mana >= cost) {
            final Location location = player.getLocation();
            int warpRange = level == 1 ? 15 : 30;
            for (int i = 0; i < 100; i++) {
                final Location warp;
                if (source != null) {
                    warp = zombieBossBehavior.getFarthestLocation(player, source, warpRange, false);
                }
                else {
                    warp = zombieBossBehavior.getNearbyLocation(location, random, warpRange);
                }

                if (warp != null && lineOfSightNMS.hasLineOfSight(player, warp) && isViable(warp)) {
                    PlayerScore.MANA.setScore(plugin, player, mana - cost);
                    PlayerScore.MANA_REGEN_DELAY_TIMER.setScore(plugin, player, plugin.getOptionsConfig().getInt("mana_regen_cooldown"));
                    PlayerScore.MANA_BAR_ACTIVE_TIMER.setScore(plugin, player, 60);

                    warp.setYaw(location.getYaw());
                    warp.setPitch(location.getPitch());

                    player.teleport(warp, PlayerTeleportEvent.TeleportCause.PLUGIN);
                    player.setVelocity(new Vector());
                    player.spawnParticle(Particle.SPELL_WITCH, warp.add(0, 1, 0), 20, 0.5, 1, 0.5);
                    player.playSound(warp, "custom.supernatural.displace.warp", SoundCategory.PLAYERS, 1F, 1);

                    for (Entity entity : player.getNearbyEntities(10, 10, 10)) {
                        if (entity instanceof Player) {
                            Player powered = (Player) entity;
                            if (!powered.equals(player) && powered.getWorld().equals(player.getWorld()) && powered.getLocation().distanceSquared(warp) < 100) {
                                if ((byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, powered) == 1) {
                                    powered.playSound(warp, "custom.supernatural.displace.warp", SoundCategory.PLAYERS, 0.3F, 1);
                                    powered.spawnParticle(Particle.SPELL_WITCH, warp, 5, 0.1, 0.1, 0.1, 1);
                                }
                            }
                        }
                    }
                    return true;
                }
                if (warpRange > 0) warpRange--;
            }
        }

        return false;
    }

    public boolean isViable (Location location) {
        final Material type = location.getBlock().getType();
        return !type.equals(Material.WATER) && !type.equals(Material.LAVA);
    }

    public void spawnVindicator (Player player, LivingEntity damager, Location location, boolean allowLeader) {
        final Vindicator vindicator = (Vindicator) player.getWorld().spawnEntity(location, EntityType.VINDICATOR);
        vindicator.setLootTable(LootTables.EMPTY.getLootTable());
        vindicator.getAttribute(Attribute.GENERIC_ARMOR).setBaseValue(20);
        vindicator.getEquipment().setItemInMainHandDropChance(-999.99F);
        vindicator.getEquipment().setItemInMainHand(new ItemStack(Material.NETHERITE_AXE));
        vindicator.getPersistentDataContainer().set(new NamespacedKey(plugin, "minion"), PersistentDataType.BYTE, (byte) 1);
        vindicator.getPersistentDataContainer().set(new NamespacedKey(plugin, "owner"), PersistentDataType.STRING, player.getUniqueId().toString());

        vindicator.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 999999, 3, true, true));
        vindicator.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 999999, 3, true, true));
        vindicator.setCanJoinRaid(false);

        if (allowLeader && random.nextInt(100) < 10) {
            vindicator.setPatrolLeader(true);

            for (int i = 0; i < random.nextInt(6) + 1; i++) {
                spawnVindicator(player, damager, location, false);
            }
        }


        vindicator.setTarget(damager);

        final int lifespan = 300;
        final int[] timer = {0};

        new BukkitRunnable() {
            @Override
            public void run() {
                if (damager.isDead() || vindicator.isDead() || player.isDead() || !player.isOnline() || timer[0] > lifespan) {
                    if (!vindicator.isDead()) {
                        vindicator.getWorld().spawnParticle(Particle.FLASH, vindicator.getLocation().add(0, 1, 0), 1);
                        vindicator.remove();
                    }
                    this.cancel();
                    return;
                }

                enhancedEyesNMS.highlightEntity(player, vindicator, ChatColor.DARK_BLUE);

                final LivingEntity target = vindicator.getTarget();
                if (target == null || !target.equals(damager)) {
                    vindicator.setTarget(damager);
                }

                if (damager.getWorld().equals(vindicator.getWorld()) && damager.getLocation().distanceSquared(vindicator.getLocation()) > 9) {
                    final Location vindicatorWarp = zombieBossBehavior.getNearbyLocation(damager.getLocation(), random, 1);
                    if (vindicatorWarp != null) {
                        vindicator.teleport(vindicator, PlayerTeleportEvent.TeleportCause.PLUGIN);
                    }
                }

                timer[0]++;
            }
        }.runTaskTimer(plugin, 0, 0);
    }
}
