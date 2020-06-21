package me.fullpotato.badlandscaves.SupernaturalPowers.ReflectionStage;

import me.fullpotato.badlandscaves.BadlandsCaves;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;

public class SpawnBoss extends BukkitRunnable {
    private final BadlandsCaves plugin;
    private final Player player;
    private final World reflection_world;
    private final HashMap<Material, Integer> dmg = new HashMap<>();
    public SpawnBoss(BadlandsCaves bcav, Player ply) {
        plugin = bcav;
        player = ply;
        reflection_world = plugin.getServer().getWorld(plugin.reflectionWorldName);

        dmg.put(Material.WOODEN_SWORD, 4);
        dmg.put(Material.GOLDEN_SWORD, 4);
        dmg.put(Material.STONE_SWORD, 5);
        dmg.put(Material.IRON_SWORD, 6);
        dmg.put(Material.DIAMOND_SWORD, 7);
        dmg.put(Material.WOODEN_AXE, 7);
        dmg.put(Material.GOLDEN_AXE, 7);
        dmg.put(Material.STONE_AXE, 9);
        dmg.put(Material.IRON_AXE, 9);
        dmg.put(Material.DIAMOND_AXE, 9);
        dmg.put(Material.TRIDENT, 9);
    }

    @Override
    public void run() {
        removeEntities();

        Location spawn_loc = reflection_world.getSpawnLocation();
        spawn_loc.setY(reflection_world.getHighestBlockYAt(spawn_loc) + 5);
        Zombie boss = spawnBoss(spawn_loc);

        ItemStack empty = new ItemStack(Material.AIR);
        new BukkitRunnable() {
            @Override
            public void run() {
                boss.getEquipment().setItemInMainHand(empty);
                boss.getEquipment().setItemInOffHand(empty);
                boss.getEquipment().setHelmet(empty);
                boss.getEquipment().setChestplate(empty);
                boss.getEquipment().setLeggings(empty);
                boss.getEquipment().setBoots(empty);
            }
        }.runTaskLaterAsynchronously(plugin, 5);


        reflection_world.setTime(18000);

        final Location player_loc = player.getLocation();
        player.playSound(player_loc, Sound.ENTITY_WITHER_SPAWN, SoundCategory.HOSTILE, 1.2F, 1);
        player.playSound(player_loc, Sound.BLOCK_CONDUIT_ACTIVATE, SoundCategory.HOSTILE, 1.2F, 0.5F);
        player.playSound(player_loc, Sound.BLOCK_BEACON_ACTIVATE, SoundCategory.HOSTILE, 1.2F, 0.5F);
    }

    public Zombie spawnBoss (Location spawn_loc) {
        Zombie boss = (Zombie) reflection_world.spawnEntity(spawn_loc, EntityType.ZOMBIE);
        boss.setBaby(false);
        boss.setSilent(true);
        boss.setCustomName(player.getDisplayName());
        boss.setCustomNameVisible(false);
        boss.setCanPickupItems(false);
        boss.setRemoveWhenFarAway(false);
        boss.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(40.0);
        boss.getAttribute(Attribute.GENERIC_FOLLOW_RANGE).setBaseValue(999);
        boss.setHealth(40.0);
        boss.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 99999, 0, false, false));
        boss.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 99999, 0, false, false));
        boss.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 99999, 1, false, false));
        boss.getAttribute(Attribute.GENERIC_ARMOR).setBaseValue(player.getAttribute(Attribute.GENERIC_ARMOR).getValue());
        boss.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS).setBaseValue(player.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS).getValue());
        if (getHighestDamage() != null) {
            boss.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(dmg.get(getHighestDamage()));
        }

        return boss;
    }

    public Material getHighestDamage() {
        int highest_damage = 1;
        Material highest_material = null;

        for (ItemStack item : player.getInventory()) {
            if (item != null) {
                if (dmg.containsKey(item.getType())) {
                    if (dmg.get(item.getType()) > highest_damage) {
                        highest_damage = dmg.get(item.getType());
                        highest_material = item.getType();
                    }
                }
            }
        }
        return highest_material;
    }

    public void removeEntities () {
        if (reflection_world != null) {
            List<Entity> entities = reflection_world.getEntities();
            for (Entity entity : entities) {
                if (!(entity instanceof Player)) {
                    entity.remove();
                }
            }
        }
    }

}
