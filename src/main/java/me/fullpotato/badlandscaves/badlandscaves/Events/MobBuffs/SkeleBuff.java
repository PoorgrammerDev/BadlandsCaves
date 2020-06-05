package me.fullpotato.badlandscaves.badlandscaves.Events.MobBuffs;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.badlandscaves.Runnables.SupernaturalPowers.ReflectionStage.ZombieBossBehavior;
import me.fullpotato.badlandscaves.badlandscaves.Util.ParticleShapes;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.loot.LootTables;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Random;

public class SkeleBuff implements Listener {
    private BadlandsCaves plugin;
    private World chambers;
    public SkeleBuff(BadlandsCaves bcav) {
        plugin = bcav;
        chambers = plugin.getServer().getWorld(plugin.chambersWorldName);
    }

    @EventHandler
    public void HMskele (CreatureSpawnEvent event) {
        if (!(event.getEntity() instanceof Skeleton)) return;
        if (event.getEntity().getWorld().equals(chambers)) return;

        boolean hardmode = plugin.getConfig().getBoolean("game_values.hardmode");
        final int chaos = plugin.getConfig().getInt("game_values.chaos_level");
        final double chance = Math.pow(1.045, chaos) - 1;

        Skeleton skeleton = (Skeleton) event.getEntity();
        Location location = skeleton.getLocation();
        World world = location.getWorld();

        Random random = new Random();
        if (!hardmode && random.nextInt(100) >= chance) return;

        if (skeleton.getType().equals(EntityType.SKELETON)) {
            if (hardmode) {
                final int augment = (chaos / 5) + plugin.getConfig().getInt("game_values.hardmode_values.augmented_spawn_chance");
                if (!event.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.CUSTOM) && random.nextInt(100) < augment) {
                    skeleton.getPersistentDataContainer().set(new NamespacedKey(plugin, "augmented"), PersistentDataType.BYTE, (byte) 1);
                    skeleton.setCustomName(ChatColor.DARK_GRAY.toString() + ChatColor.BOLD + "Summoner");

                    ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
                    LeatherArmorMeta boots_meta = (LeatherArmorMeta) boots.getItemMeta();
                    boots_meta.setColor(Color.BLACK);
                    boots.setItemMeta(boots_meta);


                    ItemStack leggings = new ItemStack(Material.LEATHER_LEGGINGS);
                    LeatherArmorMeta leggings_meta = (LeatherArmorMeta) leggings.getItemMeta();
                    leggings_meta.setColor(Color.BLACK);
                    leggings.setItemMeta(leggings_meta);

                    ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
                    LeatherArmorMeta chestplate_meta = (LeatherArmorMeta) chestplate.getItemMeta();
                    chestplate_meta.setColor(Color.BLACK);
                    chestplate.setItemMeta(chestplate_meta);

                    ItemStack helmet = new ItemStack(Material.LEATHER_HELMET);
                    LeatherArmorMeta helmet_meta = (LeatherArmorMeta) helmet.getItemMeta();
                    helmet_meta.setColor(Color.BLACK);
                    helmet.setItemMeta(helmet_meta);

                    ItemStack[] armor = {boots, leggings, chestplate, helmet};

                    skeleton.getEquipment().setArmorContents(armor);
                    skeleton.getAttribute(Attribute.GENERIC_ARMOR).setBaseValue(25.0);
                    skeleton.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(40.0);
                    skeleton.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(999);
                }
            }

            ItemStack bow = new ItemStack(Material.BOW, 1);
            ItemMeta bow_meta = bow.getItemMeta();
            if (hardmode) bow_meta.addEnchant(Enchantment.ARROW_DAMAGE, random.nextInt(random.nextInt(100) < chance ? 8 : 5) + 3, false);
            else {
                int power = random.nextInt(3) + 1;
                bow_meta.addEnchant(Enchantment.ARROW_DAMAGE, power, false);
            }
            if (hardmode) {
                final int knockback = random.nextInt(random.nextInt(100) < chance ? 8 : 2);
                if (knockback > 0) {
                    bow_meta.addEnchant(Enchantment.ARROW_KNOCKBACK, knockback, false);
                }
                bow_meta.addEnchant(Enchantment.ARROW_FIRE, 1, false);
                bow_meta.setDisplayName(ChatColor.GRAY + "Skeleton's Bow");
            }
            bow.setItemMeta(bow_meta);
            skeleton.getEquipment().setItemInMainHand(bow);

            if (!hardmode) return;
            if (!skeleton.getPersistentDataContainer().has(new NamespacedKey(plugin, "augmented"), PersistentDataType.BYTE) || skeleton.getPersistentDataContainer().get(new NamespacedKey(plugin, "augmented"), PersistentDataType.BYTE) != (byte) 1) skeleton.getAttribute(Attribute.GENERIC_ARMOR).setBaseValue(random.nextInt(random.nextInt(100) < chance ? 17 : 12));
            skeleton.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 9999, 0, true, true));

            Location one_up = location;
            one_up.setY(one_up.getBlockY() + 2);

            if (!one_up.getBlock().getType().equals(Material.AIR)) {
                one_up.getBlock().setType(Material.AIR);
            }

            location.setY(location.getBlockY() - 2);


            //witherskeleton
            if (!skeleton.getPersistentDataContainer().has(new NamespacedKey(plugin, "augmented"), PersistentDataType.BYTE) || skeleton.getPersistentDataContainer().get(new NamespacedKey(plugin, "augmented"), PersistentDataType.BYTE) != (byte) 1) {
                world.spawnEntity(location, EntityType.WITHER_SKELETON);
            }
        }
        else if (skeleton instanceof WitherSkeleton && skeleton.getType().equals(EntityType.WITHER_SKELETON)) {
            if (!hardmode) return;
            WitherSkeleton witherskele = (WitherSkeleton) skeleton;
            //witherskelesword
            boolean iron_upg = random.nextBoolean();
            ItemStack sword = iron_upg ? new ItemStack(Material.IRON_SWORD, 1) : new ItemStack(Material.STONE_SWORD, 1);
            ItemMeta sword_meta = sword.getItemMeta();
            sword_meta.addEnchant(Enchantment.DAMAGE_ALL, random.nextInt(random.nextInt(100) < chance ? 8 : 5) + 3, false);
            sword_meta.setDisplayName(ChatColor.DARK_GRAY + "Wither Skeleton's Sword");
            sword.setItemMeta(sword_meta);
            witherskele.getEquipment().setItemInMainHand(sword);

            if (random.nextBoolean()) {
                witherskele.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 9999, random.nextInt(random.nextInt(100) < chance ? 5 : 3), true, true));
            }
        }
    }

    @EventHandler
    public void summonerShoot(ProjectileLaunchEvent event) {
        if (event.getEntity() instanceof Arrow) {
            Arrow arrow = (Arrow) event.getEntity();
            if (arrow.getShooter() instanceof Skeleton) {
                Skeleton skeleton = (Skeleton) arrow.getShooter();
                if (skeleton.getPersistentDataContainer().has(new NamespacedKey(plugin, "augmented"), PersistentDataType.BYTE) && skeleton.getPersistentDataContainer().get(new NamespacedKey(plugin, "augmented"), PersistentDataType.BYTE) == (byte) 1) {
                    Vector velocity = arrow.getVelocity();
                    Location location = arrow.getLocation();

                    final int chaos = plugin.getConfig().getInt("game_values.chaos_level");
                    final int clones = 1 + (chaos / 10);
                    int[] ran = {0};
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (ran[0] > clones) {
                                this.cancel();
                            }
                            else {
                                Arrow trail = (Arrow) arrow.getWorld().spawnEntity(location, EntityType.ARROW);
                                trail.setVelocity(velocity);
                                trail.setShooter(skeleton);
                                trail.setDamage(arrow.getDamage());
                                trail.setCritical(arrow.isCritical());
                                trail.setFireTicks(arrow.getFireTicks());
                                ran[0]++;
                            }
                        }
                    }.runTaskTimer(plugin, 0, 1);
                }
            }
        }
    }

    @EventHandler
    public void summonerHit(ProjectileHitEvent event) {
        if (event.getHitEntity() != null && event.getHitEntity() instanceof Player && event.getEntity() instanceof Arrow) {
            Player player = (Player) event.getHitEntity();
            Arrow arrow = (Arrow) event.getEntity();
            if (arrow.getShooter() instanceof Skeleton) {
                Skeleton skeleton = (Skeleton) arrow.getShooter();
                if (skeleton.getPersistentDataContainer().has(new NamespacedKey(plugin, "augmented"), PersistentDataType.BYTE) && skeleton.getPersistentDataContainer().get(new NamespacedKey(plugin, "augmented"), PersistentDataType.BYTE) == (byte) 1) {
                    player.setNoDamageTicks(0);


                    final Random random = new Random();
                    final int chaos = plugin.getConfig().getInt("game_values.chaos_level");

                    if (random.nextInt(100) < Math.max(chaos, 10)) {
                        ArrayList<Skeleton> nearbySkeles = new ArrayList<>();
                        for (Entity entity : arrow.getNearbyEntities(20, 20, 20)) {
                            if (entity instanceof Skeleton && !entity.equals(skeleton)) {
                                nearbySkeles.add((Skeleton) entity);
                            }
                        }
                        if (nearbySkeles.size() < Math.max(chaos / 5, 5)) {
                            ZombieBossBehavior locationFinder = new ZombieBossBehavior(plugin);
                            Location spawnLoc = locationFinder.getNearbyLocation(arrow.getLocation(), new Random(), 5);

                            if (spawnLoc != null && spawnLoc.distanceSquared(player.getLocation()) > 2) {
                                new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        arrow.getWorld().spawnParticle(Particle.FLASH, spawnLoc.clone().add(0, 0.5, 0), 1, 0, 0, 0, 0);
                                        arrow.getLocation().getWorld().spawnEntity(spawnLoc, EntityType.SKELETON);
                                    }
                                }.runTaskLater(plugin, 20);
                            }
                        }
                        else {
                            boolean upgraded = false;
                            for (Skeleton nearbySkele : nearbySkeles) {
                                ItemStack bow = nearbySkele.getEquipment().getItemInMainHand();
                                if (bow.getType().equals(Material.BOW)) {
                                    if (random.nextBoolean()) {
                                        ItemMeta meta = bow.getItemMeta();
                                        int power = meta.getEnchantLevel(Enchantment.ARROW_DAMAGE);
                                        if (power < 10) {
                                            power++;
                                            upgraded = true;
                                            meta.addEnchant(Enchantment.ARROW_DAMAGE, power, true);
                                            bow.setItemMeta(meta);
                                            nearbySkele.getEquipment().setItemInMainHand(bow);

                                            nearbySkele.getWorld().spawnParticle(Particle.BLOCK_DUST, nearbySkele.getLocation().add(0, 0.5, 0), 20, 0.5, 0.5, 0.5, 0, Material.ANVIL.createBlockData());
                                        }
                                    }
                                }
                            }

                            if (upgraded) {
                                arrow.getWorld().playSound(arrow.getLocation(), Sound.BLOCK_ANVIL_PLACE, SoundCategory.HOSTILE, 0.5F, 1);
                            }
                        }
                    }
                }
            }
        }
        else if (event.getHitEntity() == null && event.getHitBlock() != null  && event.getEntity() instanceof Arrow) {
            Arrow arrow = (Arrow) event.getEntity();
            if (arrow.getPickupStatus().equals(AbstractArrow.PickupStatus.DISALLOWED)) arrow.remove();
        }
    }

    @EventHandler
    public void skeletonTrigger (EntityTargetLivingEntityEvent event) {
        if (plugin.getConfig().getBoolean("game_values.hardmode")) {
            if (event.getEntity() instanceof Skeleton && event.getTarget() instanceof Skeleton) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void skeletonHit (EntityDamageByEntityEvent event) {
        if (plugin.getConfig().getBoolean("game_values.hardmode")) {
            if (event.getEntity() instanceof Skeleton) {
                if (event.getDamager() instanceof Skeleton) {
                    event.setCancelled(true);
                }
                else if (event.getDamager() instanceof Arrow) {
                    Arrow arrow = (Arrow) event.getDamager();
                    if (arrow.getShooter() instanceof Skeleton) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }
}
