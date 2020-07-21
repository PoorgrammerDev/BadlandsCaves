package me.fullpotato.badlandscaves.CustomItems.Using.Starlight;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.Crafting.Starlight.StarlightCharge;
import me.fullpotato.badlandscaves.CustomItems.Crafting.Starlight.StarlightTools;
import me.fullpotato.badlandscaves.CustomItems.Using.Starlight.Nebulites.Mechanisms.NebuliteMechanisms;
import me.fullpotato.badlandscaves.CustomItems.Using.Starlight.Nebulites.Nebulite;
import me.fullpotato.badlandscaves.CustomItems.Using.Starlight.Nebulites.NebuliteManager;
import me.fullpotato.badlandscaves.Util.ParticleShapes;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import me.fullpotato.badlandscaves.Util.TargetEntity;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CrossbowMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BlockIterator;

import java.util.*;

public class StarlightBlasterMechanism extends BukkitRunnable implements Listener {
    private final BadlandsCaves plugin;
    private final StarlightCharge chargeManager;
    private final StarlightTools toolManager;
    private final NebuliteManager nebuliteManager;
    private final int damage = 20;

    public StarlightBlasterMechanism(BadlandsCaves plugin) {
        this.plugin = plugin;
        this.chargeManager = new StarlightCharge(plugin);
        this.toolManager = new StarlightTools(plugin);
        this.nebuliteManager = new NebuliteManager(plugin);
    }

    @EventHandler
    public void shootBlaster(PlayerInteractEvent event) {
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || event.getAction().equals(Action.RIGHT_CLICK_AIR)) {
            ItemStack item = event.getItem();
            if (item != null && toolManager.isStarlightBlaster(item)) {
                if (item.hasItemMeta()) {
                    if (item.getItemMeta() instanceof CrossbowMeta) {
                        CrossbowMeta meta = (CrossbowMeta) item.getItemMeta();
                        if (meta != null) {
                            if (meta.getChargedProjectiles() == null || meta.getChargedProjectiles().isEmpty()) {
                                ItemStack arrow = new ItemStack(Material.ARROW);
                                ItemMeta arrow_meta = arrow.getItemMeta();
                                if (arrow_meta != null) {
                                    arrow_meta.setDisplayName("§r");
                                    arrow.setItemMeta(arrow_meta);

                                    meta.addChargedProjectile(arrow);
                                    item.setItemMeta(meta);
                                }
                            }
                        }
                    }
                }


                Player player = event.getPlayer();
                int charge = chargeManager.getCharge(item);
                short cooldown = getCooldown(item);
                event.setCancelled(true);
                boolean hardmode = plugin.getSystemConfig().getBoolean("hardmode");
                List<Nebulite> nebuliteList = Arrays.asList(nebuliteManager.getNebulites(item));
                if (hardmode && (byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == (byte) 0 && charge > 0 && (cooldown <= 0 || nebuliteList.contains(Nebulite.RAPID_FIRE))) {
                    Location location = player.getEyeLocation();
                    TargetEntity targetEntity = new TargetEntity();
                    Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(255, nebuliteList.contains(Nebulite.SUPERHEATING_LASER) ? 125 : 200, 1), 1);

                    Location targetLocation;
                    LivingEntity defaultTarget = null;
                    Collection<LivingEntity> scatteredTargets = null;
                    Set<LivingEntity> penetratingBeamTargets = null;
                    if (nebuliteList.contains(Nebulite.SCATTERING_LIGHTS)) {
                        scatteredTargets = targetEntity.findTargetLivingEntities(location, 50, 0.2, 1.5, player);

                        scatteredTargets.forEach(entity -> {
                            if (entity != null) {
                                if (nebuliteList.contains(Nebulite.SUPERHEATING_LASER)) entity.setFireTicks(999999);
                                entity.damage(damage, player);
                                entity.setNoDamageTicks(0);
                            }
                        });
                        targetLocation = targetEntity.getTargetLocation();
                        player.getWorld().spawnParticle(Particle.REDSTONE, targetEntity.getTargetLocation(), 20, 1, 1, 1, 0, dustOptions);
                    }
                    else if (nebuliteList.contains(Nebulite.PENETRATING_BEAM)) {
                        penetratingBeamTargets = findLinedTargets(location, 50, 0.2, player);

                        penetratingBeamTargets.forEach(entity -> {
                            if (entity != null) {
                                if (nebuliteList.contains(Nebulite.SUPERHEATING_LASER)) entity.setFireTicks(999999);
                                entity.damage(damage, player);
                                entity.setNoDamageTicks(0);
                            }
                        });

                        targetEntity.setTargetLocation(location, 50);
                        targetLocation = targetEntity.getTargetLocation();
                    }
                    else {
                        defaultTarget = targetEntity.findTargetLivingEntity(location, 50, 0.2, 0.2, player);

                        if (defaultTarget != null) {
                            if (nebuliteList.contains(Nebulite.SUPERHEATING_LASER)) defaultTarget.setFireTicks(999999);
                            defaultTarget.damage(damage, player);
                            defaultTarget.setNoDamageTicks(0);
                        }
                        targetLocation = targetEntity.getTargetLocation();
                    }

                    if (nebuliteList.contains(Nebulite.SUPERHEATING_LASER)) {
                        int heating_range = nebuliteList.contains(Nebulite.SCATTERING_LIGHTS) ? 5 : 2;
                        for (int x = -heating_range; x < heating_range; x++) {
                            for (int y = -heating_range; y < heating_range; y++) {
                                for (int z = -heating_range; z < heating_range; z++) {
                                    Location heatingLoc = targetLocation.clone().add(x, y, z);
                                    if (heatingLoc.distanceSquared(targetLocation) < heating_range * heating_range) {
                                        Block block = heatingLoc.getBlock();
                                        if (block.getType().isSolid()) {
                                            if (block.getType().equals(Material.MAGMA_BLOCK)) block.setType(Material.LAVA);
                                            else if (block.getType().equals(Material.BLUE_ICE)) block.setType(Material.AIR);
                                            else if (block.getType().equals(Material.PACKED_ICE)) block.setType(Material.AIR);
                                            else if (block.getType().equals(Material.ICE)) block.setType(Material.AIR);
                                            else if (block.getType().equals(Material.SNOW_BLOCK)) block.setType(Material.AIR);
                                            else if (block.getType().isFlammable()) block.setType(Material.COAL_BLOCK);
                                            else if (block.getType().getBlastResistance() < Material.BEDROCK.getBlastResistance()) block.setType(Material.MAGMA_BLOCK);
                                        }
                                        else if (block.getType().equals(Material.WATER)) block.setType(Material.AIR);
                                        else if (block.getType().equals(Material.SNOW)) block.setType(Material.AIR);
                                        block.getState().update(true);
                                    }
                                }
                            }
                        }

                        targetLocation.getWorld().spawnParticle(Particle.SMOKE_LARGE, targetLocation, 20, heating_range / 2.0, heating_range / 2.0, heating_range / 2.0, 0);
                        targetLocation.getWorld().playSound(targetLocation, Sound.BLOCK_LAVA_EXTINGUISH, SoundCategory.BLOCKS, 1, 1);
                    }


                    player.getWorld().playSound(location, "custom.starlight_blaster", 1, 1);
                    ParticleShapes.particleLine(null, Particle.REDSTONE, player.getEyeLocation(), targetLocation, 0, dustOptions, 1);

                    if (player.getGameMode().equals(GameMode.SURVIVAL) || player.getGameMode().equals(GameMode.ADVENTURE)) {
                        int cost = 25;

                        //ENTITIES HIT COST FACTOR
                        if (nebuliteList.contains(Nebulite.SCATTERING_LIGHTS)) {
                            if (scatteredTargets != null) cost += scatteredTargets.size() * damage / 2;
                        }
                        else if (nebuliteList.contains(Nebulite.PENETRATING_BEAM)) {
                            if (penetratingBeamTargets != null) cost += penetratingBeamTargets.size() * damage / 2;
                        }
                        else {
                            if (defaultTarget != null) cost += damage / 2;
                        }

                        //RAPID FIRE COST FACTOR
                        if (nebuliteList.contains(Nebulite.RAPID_FIRE) && cooldown > 0) cost *= (cooldown / 10.0);

                        chargeManager.setCharge(item, charge - cost);
                    }

                    setCooldown(item, (short) 20);
                }
            }
        }
    }

    @Override
    public void run() {
        boolean hardmode = plugin.getSystemConfig().getBoolean("hardmode");
        if (hardmode) {
            for (Player player : plugin.getServer().getOnlinePlayers()) {
                if ((byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == (byte) 0) {
                    for (ItemStack item : player.getInventory()) {
                        if (item != null) {
                            if (toolManager.isStarlightBlaster(item)) {
                                short cooldown = getCooldown(item);
                                if (cooldown > 0) {
                                    if (cooldown == 1) {
                                        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, .5F, 1.2F);
                                    }
                                    cooldown--;
                                    setCooldown(item, cooldown);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void setCooldown(ItemStack item, short time) {
        if (toolManager.isStarlightBlaster(item)) {
            if (item.hasItemMeta()) {
                ItemMeta meta = item.getItemMeta();
                if (meta != null) {
                    meta.getPersistentDataContainer().set(new NamespacedKey(plugin, "blaster_cooldown"), PersistentDataType.SHORT, time);
                    item.setItemMeta(meta);
                }
            }
        }
    }

    public short getCooldown(ItemStack item) {
        if (toolManager.isStarlightBlaster(item)) {
            if (item.hasItemMeta()) {
                ItemMeta meta = item.getItemMeta();
                if (meta != null) {
                    Short cooldown = meta.getPersistentDataContainer().get(new NamespacedKey(plugin, "blaster_cooldown"), PersistentDataType.SHORT);
                    if (cooldown != null) return cooldown;
                }
            }
        }
        return -1;
    }

    public int getDamage() {
        return damage;
    }

    public Set<LivingEntity> findLinedTargets (Location location, int range, double radius, Entity... excludeEntities) {
        Location clone = location.clone();
        final World world = clone.getWorld();
        final BlockIterator iterator = new BlockIterator(clone);
        final List<Entity> exclude = Arrays.asList(excludeEntities);
        int travelled = 0;


        assert world != null;
        Set<Entity> targets = new HashSet<>();
        while (travelled < range && iterator.hasNext()) {
            Block block = iterator.next();
            if (block.isPassable()) {
                clone = block.getLocation().add(0.5, 0.5, 0.5);
                targets.addAll(world.getNearbyEntities(clone, radius, radius, radius));
                travelled++;
            }
            else break;
        }

        targets.removeIf(entity -> {
            if (exclude.contains(entity)) return true;
            if (entity instanceof Player) {
                Player target = (Player) entity;
                return target.getGameMode().equals(GameMode.SPECTATOR);
            }

            return false;
        });

        Set<LivingEntity> livingTargets = new HashSet<>();
        targets.forEach(entity -> {
            if (entity instanceof LivingEntity) {
                livingTargets.add((LivingEntity) entity);
            }
        });

        return livingTargets;
    }
}
