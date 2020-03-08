package me.fullpotato.badlandscaves.badlandscaves.Runnables.SupernaturalPowers.ReflectionStage;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SpawnBoss extends BukkitRunnable {
    private BadlandsCaves plugin;
    private Player player;
    private World reflection_world = Bukkit.getWorld("world_reflection");
    public SpawnBoss(BadlandsCaves bcav, Player ply) {
        plugin = bcav;
        player = ply;
    }

    @Override
    public void run() {
        removeEntities();

        Location spawn_loc = reflection_world.getSpawnLocation();
        spawn_loc.setY(reflection_world.getHighestBlockYAt(spawn_loc) + 5);
        spawnBoss(spawn_loc);
        reflection_world.setTime(18000);

        final Location player_loc = player.getLocation();
        player.playSound(player_loc, Sound.ENTITY_WITHER_SPAWN, 1.2F, 1);
        player.playSound(player_loc, Sound.BLOCK_CONDUIT_ACTIVATE, 1.2F, 0.5F);
        player.playSound(player_loc, Sound.BLOCK_BEACON_ACTIVATE, 1.2F, 0.5F);
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

        boss.getEquipment().setItemInMainHand(null);
        boss.getEquipment().setItemInOffHand(null);
        boss.getEquipment().setHelmet(null);
        boss.getEquipment().setChestplate(null);
        boss.getEquipment().setLeggings(null);
        boss.getEquipment().setBoots(null);

        boss.getAttribute(Attribute.GENERIC_ARMOR).setBaseValue(player.getAttribute(Attribute.GENERIC_ARMOR).getValue());
        boss.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS).setBaseValue(player.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS).getValue());
        boss.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(getHighestDamage());

        return boss;
    }

    public int getHighestDamage() {
        int highest_damage = 1;
        HashMap<Material, Integer> dmg = new HashMap<>();
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

        for (ItemStack item : player.getInventory()) {
            if (item != null) {
                if (dmg.containsKey(item.getType())) {
                    highest_damage = Math.max(highest_damage, dmg.get(item.getType()));
                }
            }
        }

        return highest_damage;
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
