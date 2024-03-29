package me.fullpotato.badlandscaves.MobBuffs.CastleBoss;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.Lectern;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vindicator;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.loot.LootContext;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.CustomItem;
import me.fullpotato.badlandscaves.Loot.DimensionStructureTable;
import me.fullpotato.badlandscaves.MobBuffs.CastleBoss.StateMachine.CastleBossState;
import me.fullpotato.badlandscaves.MobBuffs.CastleBoss.StateMachine.NormalState;
import me.fullpotato.badlandscaves.SupernaturalPowers.Artifacts.Artifact;
import me.fullpotato.badlandscaves.Util.ParticleShapes;
import net.md_5.bungee.api.ChatColor;

public class CastleBoss implements Listener {
    private final BadlandsCaves plugin;
    private final ParticleShapes particleShapes;
    private final Random random;
    private final DimensionStructureTable lootManager;

    private final HashMap<UUID, CastleBossState> stateMap;

    public CastleBoss(BadlandsCaves plugin, ParticleShapes particleShapes, Random random, DimensionStructureTable lootManager) {
        this.plugin = plugin;
        this.particleShapes = particleShapes;
        this.random = random;
        this.stateMap = new HashMap<>();
        this.lootManager = lootManager;
    }

    @EventHandler
    public void BossHitPlayer(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        if (!(event.getDamager() instanceof Vindicator)) return;

        final Player player = (Player) event.getEntity();
        final Vindicator boss = (Vindicator) event.getDamager();

        //Check if vindicator is the boss
        final NamespacedKey bossKey = new NamespacedKey(plugin, "is_castle_boss");
        if (!boss.getPersistentDataContainer().has(bossKey, PersistentDataType.BYTE) ||
            boss.getPersistentDataContainer().get(bossKey, PersistentDataType.BYTE) != (byte) 1) return;

        //Regular effects
        player.setNoDamageTicks(0);

        final CastleBossState state = getState(boss);
        state.Attack(player, event.getDamage());
    }


    @EventHandler
    public void ExplosionImmunity(EntityDamageEvent event) {
        if (event.isCancelled()) return;

        if (!(event.getEntity() instanceof Vindicator)) return;
        final Vindicator boss = (Vindicator) event.getEntity();

        //Check if explosion
        if (event.getCause() != DamageCause.ENTITY_EXPLOSION &&
            event.getCause() != DamageCause.BLOCK_EXPLOSION) return;

        //Check if vindicator is the boss
        final NamespacedKey bossKey = new NamespacedKey(plugin, "is_castle_boss");
        if (!boss.getPersistentDataContainer().has(bossKey, PersistentDataType.BYTE) ||
            boss.getPersistentDataContainer().get(bossKey, PersistentDataType.BYTE) != (byte) 1) return;

        event.setCancelled(true);
    }

    @EventHandler
    public void HalfHealthBuff(EntityDamageEvent event) {
        if (event.isCancelled()) return;

        if (!(event.getEntity() instanceof Vindicator)) return;
        final Vindicator boss = (Vindicator) event.getEntity();

        //Check if vindicator is the boss
        final NamespacedKey bossKey = new NamespacedKey(plugin, "is_castle_boss");
        final NamespacedKey voidKey = new NamespacedKey(plugin, "is_void");
        if (!boss.getPersistentDataContainer().has(bossKey, PersistentDataType.BYTE) ||
            boss.getPersistentDataContainer().get(bossKey, PersistentDataType.BYTE) != (byte) 1) return;

        final boolean isVoid = boss.getPersistentDataContainer().has(voidKey, PersistentDataType.BYTE) &&
            boss.getPersistentDataContainer().get(voidKey, PersistentDataType.BYTE) == (byte) 1;

        //Trigger buff if HP is half
        final double hpRatio = boss.getHealth() / boss.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();
        if (hpRatio > 0.5) return;

        //If already buffed, return
        final NamespacedKey secondPhaseBuff = new NamespacedKey(plugin, "castle_boss_buffed");
        if (boss.getPersistentDataContainer().has(secondPhaseBuff, PersistentDataType.BYTE) &&
            boss.getPersistentDataContainer().get(secondPhaseBuff, PersistentDataType.BYTE) == (byte) 1) return;

        //Stat buffs
        boss.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.6f);
        boss.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(999f);
        boss.getAttribute(Attribute.GENERIC_ATTACK_KNOCKBACK).setBaseValue(25.0f);

        //Absorb trees and heal
        final Location[] treeLocations = {
            plugin.getSystemConfig().getLocation("castle_boss." + boss.getUniqueId() + ".saved_tree_location_0"),
            plugin.getSystemConfig().getLocation("castle_boss." + boss.getUniqueId() + ".saved_tree_location_1"),
            plugin.getSystemConfig().getLocation("castle_boss." + boss.getUniqueId() + ".saved_tree_location_2"),
            plugin.getSystemConfig().getLocation("castle_boss." + boss.getUniqueId() + ".saved_tree_location_3"),
        };

        //Sound effects
        boss.getWorld().playSound(boss.getLocation(), Sound.ENTITY_WITHER_AMBIENT, 5, 0.75f);

        for (Location treeLocation : treeLocations) {
            AbsorbTree(treeLocation, boss);
        }

        Particle flame = isVoid ? Particle.SOUL_FIRE_FLAME : Particle.FLAME;
        Color color = isVoid ? Color.AQUA : Color.ORANGE;

        //New particle effect when moving
        final World world = boss.getWorld();
        new BukkitRunnable() {
            @Override
            public void run() {
                if (boss == null || boss.isDead() || !boss.isValid()) {
                    this.cancel();
                    return;
                }

                world.spawnParticle(Particle.REDSTONE, boss.getLocation().add(0, 1, 0), 10, 0.25f, 0.5f, 0.25f, 0, new Particle.DustOptions(color, 1));
                world.spawnParticle(flame, boss.getLocation().add(0, 1, 0), 10, 0.25f, 0.5f, 0.25f, 0);
            }            
        }.runTaskTimer(plugin, 0, 1);
        
        // Set buffed tag
        boss.getPersistentDataContainer().set(secondPhaseBuff, PersistentDataType.BYTE, (byte) 1);
    }

    @EventHandler
    public void Death(EntityDeathEvent event) {
        if (!(event.getEntity() instanceof Vindicator)) return;

        final Vindicator boss = (Vindicator) event.getEntity();

        //Check if vindicator is the boss
        final NamespacedKey bossKey = new NamespacedKey(plugin, "is_castle_boss");
        if (!boss.getPersistentDataContainer().has(bossKey, PersistentDataType.BYTE) ||
            boss.getPersistentDataContainer().get(bossKey, PersistentDataType.BYTE) != (byte) 1) return;

        //Remove lectern config entry and then the lectern
        final Location lecternLoc = plugin.getSystemConfig().getLocation("castle_boss." + boss.getUniqueId() + ".saved_lectern_location");
        if (lecternLoc.getBlock().getType() == Material.LECTERN) {
            final Lectern lectern = (Lectern) lecternLoc.getBlock().getState();
            final String uuid = lectern.getPersistentDataContainer().get(new NamespacedKey(plugin, "location_uuid"), PersistentDataType.STRING);
            plugin.getSystemConfig().set("castle_lectern_locations." + uuid, null);
            plugin.saveSystemConfig();
        }
        lecternLoc.getBlock().setType(Material.AIR);

        //Remove concrete wall
        Location corner = lecternLoc.clone().add(-4, 0, 2); //offset from lectern to bottom corner of black concrete wall
        RemoveWall(corner, 9, 8);

        //Remove side concrete walls
        corner = lecternLoc.clone().add(27, 0, 13);
        RemoveWall(corner, 3, 10);

        corner = lecternLoc.clone().add(-29, 0, 13);
        RemoveWall(corner, 3, 10);

        //Remove all barriers from stage area
        //25,13,41 : -25,0,5
        corner = lecternLoc.clone().add(-25, 0, 5);
        for (int x = 0; x < 51; x++) {
            for (int y = 0; y < 14; y++) {
                for (int z = 0; z < 37; z++) {
                    final Block block = corner.clone().add(x, y, z).getBlock();
                    if (block.getType() == Material.BARRIER) {
                        block.setType(Material.AIR);
                    }
                }
            }
        }

        //Drop an Orb of Ascension
        final ItemStack orb = plugin.getCustomItemManager().getItem(CustomItem.ASCENDED_ORB);
        final ItemMeta meta = orb.getItemMeta();
        if (meta != null) {
            List<String> lore = meta.getLore();        
            if (lore == null) lore = new ArrayList<String>();

            lore.add(ChatColor.GRAY + "Traveling Blades");
            meta.setLore(lore);

            meta.getPersistentDataContainer().set(new NamespacedKey(plugin, "orb_artifact"), PersistentDataType.STRING, Artifact.TRAVELING_BLADES.name());
            orb.setItemMeta(meta);
        }
        event.getDrops().clear();
        event.getDrops().add(orb);

        //50x exp rate for killing boss
        event.setDroppedExp(event.getDroppedExp() * 50);

        //Extinguish all flames near place of death to prevent loot from burning
        for (int x = -5; x <= 5; x++) {
            for (int y = -5; y <= 5; y++) {
                for (int z = -5; z <= 5; z++) {
                    Location loc = boss.getLocation().add(x, y, z);
                    if (loc != null) {
                        Material type = loc.getBlock().getType();
                        if (type == Material.FIRE || type == Material.LAVA || type == Material.SOUL_FIRE) {
                            loc.getBlock().setType(Material.AIR);
                        }
                    }
                }
            }
        }

        //Drop loot repeatedly
        final int chaos = plugin.getSystemConfig().getInt("chaos_level");
        final int count = (chaos / 20 > 0) ? random.nextInt(chaos / 20) + 1 : 1;
        int[] ticker = {0};
        new BukkitRunnable() {
            @Override
            public void run() {
                if (ticker[0] >= count) {
                    this.cancel();
                    return;
                }
                
                lootManager.populateLoot(random, new LootContext.Builder(boss.getLocation()).build()).forEach((ItemStack item) -> {
                    boss.getWorld().dropItemNaturally(boss.getLocation(), item);
                });

                ticker[0]++;
            }
            
        }.runTaskTimer(plugin, 0, 10);



        //Remove config entries
        plugin.getSystemConfig().set("castle_boss." + boss.getUniqueId(), null);

    }

    private void AbsorbTree(Location treeLocation, Vindicator boss) {
        int[] y = {0};

        new BukkitRunnable() {
            @Override
            public void run() {
                if (y[0] > 12) {
                    this.cancel();
                    return;
                }

                for (int x = -5; x <= 5; x++) {
                    for (int z = -5; z <= 5; z++) {
                        final Block block = treeLocation.clone().add(x, y[0], z).getBlock();

                        final boolean nonVoidType = block.getType() == Material.NETHER_WART_BLOCK || block.getType() == Material.CRIMSON_STEM || block.getType() == Material.SHROOMLIGHT;
                        final boolean voidType = block.getType() == Material.WARPED_WART_BLOCK || block.getType() == Material.WARPED_STEM;

                        if (nonVoidType || voidType) {
                            block.setType(Material.BASALT);
                            boss.setHealth(Math.min(boss.getHealth() + 0.5f, boss.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue()));
                            particleShapes.lineDelayed(null, nonVoidType ? Particle.FLAME : Particle.SOUL_FIRE_FLAME, block.getLocation(), boss.getLocation(), 0, null, 2, 1);
                        }
                    }
                }

                y[0]++;
            }
        }.runTaskTimer(plugin, 0, 5);
    }

    private void RemoveWall(Location negativeCorner, int xLength, int yLength) {
        for (int x = 0; x < xLength; x++) {
            for (int y = 0; y < yLength; y++) {
                final Block block = negativeCorner.clone().add(x, y, 0).getBlock();
                if (block.getType() == Material.BLACK_CONCRETE) {
                    block.setType(Material.AIR); 
                }
            }
        }
    }

    public CastleBossState getState(Vindicator boss) {
        if (this.stateMap.containsKey(boss.getUniqueId())) {
            return this.stateMap.get(boss.getUniqueId());
        }

        final CastleBossState normalState = new NormalState(plugin, this, boss, random);
        this.stateMap.put(boss.getUniqueId(), normalState);
        normalState.Start();

        return normalState;
    }
    
    public void setState(Vindicator boss, CastleBossState state) {
        final CastleBossState oldState = getState(boss);
        if (oldState != null) oldState.End();

        this.stateMap.put(boss.getUniqueId(), state);
        state.Start();
    }

}
