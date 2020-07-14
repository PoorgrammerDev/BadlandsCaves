package me.fullpotato.badlandscaves.CustomItems.Using.Starlight;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.Crafting.Starlight.StarlightCharge;
import me.fullpotato.badlandscaves.CustomItems.Crafting.Starlight.StarlightTools;
import me.fullpotato.badlandscaves.CustomItems.CustomItem;
import me.fullpotato.badlandscaves.Util.ParticleShapes;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.KeyedBossBar;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
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

                    if ((byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == (byte) 0) {
                        if (clickedBlock != null) {
                            final Block block = clickedBlock.getRelative(event.getBlockFace());
                            final Location location = block.getLocation().add(0.5, 0, 0.5);

                            if (block.getRelative(BlockFace.DOWN).getType().isSolid()) {
                                if ((byte) PlayerScore.HAS_STARLIGHT_SENTRY.getScore(plugin, player) == (byte) 0) {
                                    final StarlightCharge chargeManager = new StarlightCharge(plugin);
                                    final int charge = chargeManager.getCharge(item);
                                    final int maxCharge = chargeManager.getMaxCharge(item);
                                    if (charge > 0) {
                                        final Location bottom = location.clone();
                                        bottom.setY(0);

                                        Slime slime = (Slime) world.spawnEntity(bottom, EntityType.SLIME);
                                        slime.setAI(false);
                                        slime.setGravity(false);
                                        slime.setSize(6);
                                        slime.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 999999, 0, false, false));
                                        slime.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 999999, 99, false, false));
                                        slime.getPersistentDataContainer().set(new NamespacedKey(plugin, "is_starlight_sentry_hit_detector"), PersistentDataType.BYTE, (byte) 1);

                                        ArmorStand armorStand = (ArmorStand) world.spawnEntity(bottom, EntityType.ARMOR_STAND);
                                        armorStand.setCustomName(player.getDisplayName() + "'s Starlight Sentry");
                                        armorStand.setMarker(true);
                                        armorStand.setVisible(false);
                                        armorStand.setInvulnerable(true);
                                        armorStand.setGravity(false);

                                        armorStand.getPersistentDataContainer().set(new NamespacedKey(plugin, "is_starlight_sentry"), PersistentDataType.BYTE, (byte) 1);
                                        armorStand.getPersistentDataContainer().set(new NamespacedKey(plugin, "charge"), PersistentDataType.INTEGER, charge);
                                        armorStand.getPersistentDataContainer().set(new NamespacedKey(plugin, "max_charge"), PersistentDataType.INTEGER, maxCharge);
                                        armorStand.getPersistentDataContainer().set(new NamespacedKey(plugin, "owner"), PersistentDataType.STRING, player.getUniqueId().toString());
                                        armorStand.getPersistentDataContainer().set(new NamespacedKey(plugin, "hitreg_id"), PersistentDataType.STRING, slime.getUniqueId().toString());
                                        slime.getPersistentDataContainer().set(new NamespacedKey(plugin, "sentry_id"), PersistentDataType.STRING, armorStand.getUniqueId().toString());

                                        PlayerScore.HAS_STARLIGHT_SENTRY.setScore(plugin, player, (byte) 1);
                                        PlayerScore.STARLIGHT_SENTRY_UUID.setScore(plugin, player, armorStand.getUniqueId().toString());
                                        item.setAmount(item.getAmount() - 1);

                                        int[] current_cmd = {186};
                                        final int end = 190;
                                        new BukkitRunnable() {
                                            @Override
                                            public void run() {
                                                world.playSound(location, Sound.BLOCK_ANVIL_PLACE, SoundCategory.BLOCKS, 2, 0.7F);
                                                slime.teleport(location.clone().add(0, 0.5, 0), PlayerTeleportEvent.TeleportCause.PLUGIN);
                                                armorStand.teleport(location, PlayerTeleportEvent.TeleportCause.PLUGIN);
                                                armorStand.getEquipment().setHelmet(getModel(current_cmd[0]));

                                                new BukkitRunnable() {
                                                    @Override
                                                    public void run() {
                                                        if (armorStand.isDead() || slime.isDead()) {
                                                            this.cancel();
                                                            return;
                                                        }

                                                        if (current_cmd[0] > end) {
                                                            this.cancel();
                                                            armorStand.getEquipment().setHelmet(getModel(185));
                                                            world.playSound(location, Sound.BLOCK_CONDUIT_ACTIVATE, SoundCategory.BLOCKS, 2, 1);
                                                            armorStandSys(armorStand, slime);
                                                            displayChargeBar(player, true);
                                                            setChargeBarValue(player, charge, maxCharge);

                                                            return;
                                                        }

                                                        armorStand.getEquipment().setHelmet(getModel(current_cmd[0]));
                                                        world.playSound(location, Sound.BLOCK_END_PORTAL_FRAME_FILL, SoundCategory.BLOCKS, 2, (float) ((current_cmd[0] - 185) / 3.0));
                                                        current_cmd[0]++;
                                                    }
                                                }.runTaskTimer(plugin, 0, 10);
                                            }
                                        }.runTaskLater(plugin, 2);
                                    }
                                }
                            }
                        }
                    }
                }
                else if (event.getAction().equals(Action.RIGHT_CLICK_AIR)) {
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
                            int charge = (int) (getCharge(armorStand) - (event.getDamage() * 2));
                            setCharge(armorStand, charge);

                            String ownerUUIDResult = armorStand.getPersistentDataContainer().get(new NamespacedKey(plugin, "owner"), PersistentDataType.STRING);
                            if (ownerUUIDResult != null) {
                                UUID ownerUUID = UUID.fromString(ownerUUIDResult);
                                Player owner = plugin.getServer().getPlayer(ownerUUID);
                                if (owner != null) {
                                    Integer maxChargeResult = armorStand.getPersistentDataContainer().get(new NamespacedKey(plugin, "max_charge"), PersistentDataType.INTEGER);
                                    if (maxChargeResult != null) {
                                        setChargeBarValue(owner, charge, maxChargeResult);
                                    }
                                }
                            }

                            World world = armorStand.getWorld();
                            Location location = armorStand.getLocation();

                            world.playSound(location, Sound.BLOCK_STONE_BREAK, SoundCategory.BLOCKS, 1, 1);
                            if (charge <= 0) {
                                world.spawnParticle(Particle.BLOCK_DUST, location.clone().add(0, 2, 0), 75, 1, 3, 1, 0, Material.CRACKED_POLISHED_BLACKSTONE_BRICKS.createBlockData());
                                world.spawnParticle(Particle.BLOCK_DUST, location.clone().add(0, 1, 0), 25, 0.5, 1, 0.5, 0, Material.BLUE_CONCRETE.createBlockData());
                                world.spawnParticle(Particle.BLOCK_DUST, location.clone().add(0, 3, 0), 25, 0.5, 0.5, 0.5, 0, Material.YELLOW_CONCRETE.createBlockData());
                                world.playSound(location, Sound.BLOCK_GLASS_BREAK, SoundCategory.BLOCKS, 5, 1);
                                destroySentry(armorStand, slime, false);
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
                        destroySentry(armorStand, true);
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
                            destroySentry(armorStand, slime, true);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void ownerLogOff (PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        if ((byte) PlayerScore.HAS_STARLIGHT_SENTRY.getScore(plugin, player) == (byte) 1) {
            String result = (String) PlayerScore.STARLIGHT_SENTRY_UUID.getScore(plugin, player);
            if (result != null && !result.isEmpty()) {
                final UUID uuid = UUID.fromString(result);
                Entity entity = plugin.getServer().getEntity(uuid);
                if (entity instanceof ArmorStand) {
                    ArmorStand armorStand = (ArmorStand) entity;
                    if (isSentry(armorStand)) {
                        destroySentry(armorStand, true);
                    }
                }
            }
        }
    }

    public void destroySentry (ArmorStand armorStand, boolean instant) {
        if (isSentry(armorStand)) {
            String result = armorStand.getPersistentDataContainer().get(new NamespacedKey(plugin, "hitreg_id"), PersistentDataType.STRING);
            if (result != null) {
                Entity entity = plugin.getServer().getEntity(UUID.fromString(result));
                if (entity instanceof Slime) {
                    Slime slime = (Slime) entity;
                    destroySentry(armorStand, slime, instant);
                }

            }
        }
    }

    public void destroySentry(ArmorStand armorStand, Slime slime, boolean instant) {
        if (isSentry(armorStand)) {
            int charge = getCharge(armorStand);
            final Player player = getOwner(armorStand);
            if (player != null) {
                final ItemStack item = CustomItem.STARLIGHT_SENTRY.getItem();
                StarlightCharge chargeManager = new StarlightCharge(plugin);
                chargeManager.setCharge(item, charge);

                for (Entity entity : slime.getNearbyEntities(10, 10, 10)) {
                    if (entity instanceof Mob) {
                        Mob mob = (Mob) entity;
                        if (mob.getTarget() != null && mob.getTarget().equals(slime)) {
                            mob.setTarget(null);
                        }
                    }
                }
                armorStand.remove();
                slime.remove();


                PlayerScore.HAS_STARLIGHT_SENTRY.setScore(plugin, player, (byte) 0);
                PlayerScore.STARLIGHT_SENTRY_UUID.setScore(plugin, player, "");

                if (instant) {
                    player.playSound(player.getLocation(), "custom.bell_resonate_instant", SoundCategory.BLOCKS, 1, 1);
                    updateOwnerReturnSentry(player, item);

                }
                else {
                    player.playSound(player.getLocation(), Sound.BLOCK_BELL_RESONATE, SoundCategory.BLOCKS, 1, 1);

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            updateOwnerReturnSentry(player, item);
                        }
                    }.runTaskLater(plugin, 40);
                }

            }
        }
    }

    public void updateOwnerReturnSentry(Player player, ItemStack item) {
        displayChargeBar(player, false);
        player.sendMessage(ChatColor.YELLOW + "Your Starlight Sentry has been returned to your inventory.");
        if (player.getInventory().firstEmpty() != -1) {
            player.getInventory().addItem(item);
        } else {
            player.getWorld().dropItemNaturally(player.getLocation(), item);
        }
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
                if (!armorStand.getLocation().getBlock().getRelative(BlockFace.DOWN).getType().isSolid()) {
                    destroySentry(armorStand, slime, false);
                    return;
                }

                List<Entity> nearbyEntities = armorStand.getNearbyEntities(range, range, range);
                List<LivingEntity> hostiles = getHostileMobs(nearbyEntities);

                double closestDistance = Double.MAX_VALUE;
                LivingEntity target = null;
                for (LivingEntity entity : hostiles) {
                    if (slime.hasLineOfSight(entity)) {
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
                    //location.setDirection(target.getLocation().subtract(armorStand.getLocation()).toVector());
                    //armorStand.teleport(location);

                    if (target instanceof Mob) {
                        Mob mob = (Mob) target;
                        mob.setTarget(slime);
                    }

                    target.damage(damage, armorStand);
                    armorStand.getWorld().playSound(location, "custom.starlight_blaster", 1, 1);
                    ParticleShapes.particleLine(null, Particle.REDSTONE, armorStand.getLocation().add(0, 4.5, 0), target.getEyeLocation(), 0, new Particle.DustOptions(Color.fromRGB(255, 200, 1), 1), 0.25);

                    int charge = getCharge(armorStand) - 25;
                    setCharge(armorStand, charge);
                    if (charge <= 0) {
                        destroySentry(armorStand, slime, false);
                    }

                }
            }
        }.runTaskTimer(plugin, 0, 20);
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

    public ItemStack getModel(int customModelData) {
        ItemStack model = new ItemStack(Material.COMMAND_BLOCK);
        ItemMeta meta = model.getItemMeta();
        meta.setCustomModelData(customModelData);
        model.setItemMeta(meta);
        return model;
    }

    public KeyedBossBar getChargeBar(Player player) {
        final boolean has_sentry = (byte) PlayerScore.HAS_STARLIGHT_SENTRY.getScore(plugin, player) == 1;
        NamespacedKey key = new NamespacedKey(plugin, "sentry_" + player.getUniqueId());
        KeyedBossBar chargeBar = plugin.getServer().getBossBar(key);
        if (chargeBar == null && has_sentry) {
            chargeBar = plugin.getServer().createBossBar(key, ChatColor.YELLOW + "Starlight Sentry Charge", BarColor.YELLOW, BarStyle.SEGMENTED_10);
            chargeBar.addPlayer(player);
        }
        return chargeBar;
    }

    public void displayChargeBar (Player player, boolean display) {
        KeyedBossBar bar = getChargeBar(player);
        if (bar != null) {
            bar.setVisible(display);
        }
    }

    public void setChargeBarValue (Player player, int charge, int maxCharge) {
        final boolean has_sentry = (byte) PlayerScore.HAS_STARLIGHT_SENTRY.getScore(plugin, player) == 1;
        if (has_sentry) {
            KeyedBossBar bar = getChargeBar(player);
            bar.setProgress(Math.max(Math.min((double) charge / maxCharge, 1), 0));
        }
    }



}
