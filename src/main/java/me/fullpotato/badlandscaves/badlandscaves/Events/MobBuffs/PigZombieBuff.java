package me.fullpotato.badlandscaves.badlandscaves.Events.MobBuffs;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.PigZombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PigZombieAngerEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Random;

public class PigZombieBuff implements Listener {
    private BadlandsCaves plugin;
    private World chambers = Bukkit.getWorld("world_chambers");
    public PigZombieBuff(BadlandsCaves bcav) {
        plugin = bcav;
    }

    @EventHandler
    public void HMpigzombie (PigZombieAngerEvent event) {
        boolean isHardmode = plugin.getConfig().getBoolean("game_values.hardmode");
        if (!isHardmode) return;
        if (event.getEntity().getWorld().equals(chambers)) return;

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
        pigZombie.setMetadata("undershirt", new FixedMetadataValue(plugin, false));
    }

    @EventHandler
    public void assignPigUnderShirt (CreatureSpawnEvent event) {
        boolean isHardmode = plugin.getConfig().getBoolean("game_values.hardmode");
        if (!isHardmode) return;
        if (event.getEntity().getWorld().equals(chambers)) return;

        if (event.getEntity() instanceof PigZombie) {
            final Random random = new Random();
            final int chaos = plugin.getConfig().getInt("game_values.chaos_level");
            final double chance = Math.pow(1.045, chaos) - 1;
            if (random.nextInt(100) < chance) {
                event.getEntity().setMetadata("undershirt", new FixedMetadataValue(plugin, true));
            }
        }
    }

    @EventHandler
    public void pigUnderShirt (EntityDamageEvent event) {
        boolean isHardmode = plugin.getConfig().getBoolean("game_values.hardmode");
        if (!isHardmode) return;
        if (event.getEntity().getWorld().equals(chambers)) return;

        if (event.getEntity() instanceof PigZombie) {
            PigZombie entity = (PigZombie) event.getEntity();
            if (entity.hasMetadata("undershirt") && entity.getMetadata("undershirt").get(0).asBoolean()) {
                if (entity.getHealth() - event.getFinalDamage() <= 0) {
                    entity.setMetadata("undershirt", new FixedMetadataValue(plugin, false));
                    event.setDamage(entity.getHealth() - 1);
                    entity.getWorld().playSound(entity.getLocation(), Sound.ITEM_TOTEM_USE, SoundCategory.HOSTILE, 0.3F, 0.9F);
                    entity.getWorld().spawnParticle(Particle.TOTEM, entity.getEyeLocation(), 10, 0.5, 0.5, 0.5, 0);
                }
            }
        }
    }
}
