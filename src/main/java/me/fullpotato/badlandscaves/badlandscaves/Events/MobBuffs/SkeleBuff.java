package me.fullpotato.badlandscaves.badlandscaves.Events.MobBuffs;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.WitherSkeleton;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Random;

public class SkeleBuff implements Listener {
    private BadlandsCaves plugin;
    public SkeleBuff(BadlandsCaves bcav) {
        plugin = bcav;
    }

    @EventHandler
    public void HMskele (CreatureSpawnEvent event) {
        if (!(event.getEntity() instanceof Skeleton)) return;

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
                final int augment = plugin.getConfig().getInt("game_values.hardmode_values.augmented_spawn_chance");
                if (random.nextInt(100) > 100) { //TODO change after testing

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
            skeleton.getAttribute(Attribute.GENERIC_ARMOR).setBaseValue(random.nextInt(random.nextInt(100) < chance ? 17 : 12));
            skeleton.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 9999, 0, true, true));

            Location one_up = location;
            one_up.setY(one_up.getBlockY() + 2);

            if (!one_up.getBlock().getType().equals(Material.AIR)) {
                one_up.getBlock().setType(Material.AIR);
            }

            location.setY(location.getBlockY() - 2);


            //witherskeleton
            world.spawnEntity(location, EntityType.WITHER_SKELETON);
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

}
