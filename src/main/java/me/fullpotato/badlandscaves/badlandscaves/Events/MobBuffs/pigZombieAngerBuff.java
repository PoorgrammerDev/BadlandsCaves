package me.fullpotato.badlandscaves.badlandscaves.Events.MobBuffs;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.PigZombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PigZombieAngerEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Random;

public class pigZombieAngerBuff implements Listener {
    private BadlandsCaves plugin;
    public pigZombieAngerBuff(BadlandsCaves bcav) {
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
        pigZombie.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 9999, random.nextInt(2), false, true));
        pigZombie.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 9999, random.nextInt(4), false, true));
        pigZombie.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 9999, random.nextInt(2), false, true));

        pigZombie.getAttribute(Attribute.GENERIC_FOLLOW_RANGE).setBaseValue(random.nextInt(100));
        pigZombie.setAnger(999);

        ItemStack[] armor = {
                new ItemStack(Material.GOLDEN_BOOTS),
                new ItemStack(Material.GOLDEN_LEGGINGS),
                new ItemStack(Material.GOLDEN_CHESTPLATE),
                new ItemStack(Material.GOLDEN_HELMET)
        };

        pigZombie.getEquipment().setArmorContents(armor);
        world.spawnParticle(Particle.FLASH, location, 10);
        pigZombie.setMetadata("buffed", new FixedMetadataValue(plugin, true));
    }
}
