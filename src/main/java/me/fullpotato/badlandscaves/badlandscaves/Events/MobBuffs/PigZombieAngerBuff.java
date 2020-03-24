package me.fullpotato.badlandscaves.badlandscaves.Events.MobBuffs;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.PigZombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PigZombieAngerEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Random;

public class PigZombieAngerBuff implements Listener {
    private BadlandsCaves plugin;
    public PigZombieAngerBuff(BadlandsCaves bcav) {
        plugin = bcav;
    }

    @EventHandler
    public void HMpigzombie (PigZombieAngerEvent event) {
        boolean isHardmode = plugin.getConfig().getBoolean("game_values.hardmode");
        if (!isHardmode) return;

        PigZombie pigZombie = event.getEntity();

        if (pigZombie.hasMetadata("buffed")) return;

        Location location = pigZombie.getLocation();
        World world = location.getWorld();

        Random random = new Random();

        pigZombie.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 9999, random.nextInt(2), false, true));
        pigZombie.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 9999, random.nextInt(3), false, true));
        pigZombie.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 9999, random.nextInt(4), false, true));
        pigZombie.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 9999, random.nextInt(2), false, true));

        pigZombie.getAttribute(Attribute.GENERIC_FOLLOW_RANGE).setBaseValue(random.nextInt(100));
        pigZombie.setAnger(999);

        if (random.nextBoolean()) {
            world.spawnEntity(location, EntityType.MAGMA_CUBE);
        }

        int[] armor_protections = random.ints(4, 0, 4).toArray();

        ItemStack boots = new ItemStack(Material.GOLDEN_BOOTS);
        if (armor_protections[0] > 0) {
            ItemMeta boots_meta = boots.getItemMeta();
            boots_meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, armor_protections[0], false);
            boots.setItemMeta(boots_meta);
        }

        ItemStack leggings = new ItemStack(Material.GOLDEN_LEGGINGS);
        if (armor_protections[1] > 0) {
            ItemMeta leggings_meta = leggings.getItemMeta();
            leggings_meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, armor_protections[1], false);
            leggings.setItemMeta(leggings_meta);
        }

        ItemStack chestplate = new ItemStack(Material.GOLDEN_CHESTPLATE);
        if (armor_protections[2] > 0) {
            ItemMeta chestplate_meta = chestplate.getItemMeta();
            chestplate_meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, armor_protections[2], false);
            chestplate.setItemMeta(chestplate_meta);
        }

        ItemStack helmet = new ItemStack(Material.GOLDEN_HELMET);
        if (armor_protections[3] > 0) {
            ItemMeta helmet_meta = helmet.getItemMeta();
            helmet_meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, armor_protections[3], false);
            helmet.setItemMeta(helmet_meta);
        }

        ItemStack[] armor = {
                boots,
                leggings,
                chestplate,
                helmet,
        };

        pigZombie.getEquipment().setArmorContents(armor);
        world.spawnParticle(Particle.FLASH, location, 10);
        pigZombie.setMetadata("buffed", new FixedMetadataValue(plugin, true));
    }
}
