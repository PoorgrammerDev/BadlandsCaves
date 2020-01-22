package me.fullpotato.badlandscaves.badlandscaves.Events.MobBuffs;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Skeleton;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Random;

public class skeleBuff implements Listener {
    private BadlandsCaves plugin;
    public skeleBuff(BadlandsCaves bcav) {
        plugin = bcav;
    }

    @EventHandler
    public void HMskele (CreatureSpawnEvent event) {
        if (!event.getEntity().getType().equals(EntityType.SKELETON)) return;

        boolean isHardmode = plugin.getConfig().getBoolean("game_values.hardmode");
        if (!isHardmode) return;

        Skeleton skeleton = (Skeleton) event.getEntity();
        Location location = skeleton.getLocation();
        World world = location.getWorld();

        Random random = new Random();
        ItemStack HMbow = new ItemStack(Material.BOW, 1);
        ItemMeta bow_meta = HMbow.getItemMeta();
        bow_meta.addEnchant(Enchantment.ARROW_DAMAGE, random.nextInt(5), false);
        bow_meta.addEnchant(Enchantment.ARROW_KNOCKBACK, random.nextInt(2), false);
        bow_meta.addEnchant(Enchantment.ARROW_FIRE, 1, false);
        bow_meta.setDisplayName(ChatColor.GRAY + "Skeleton's Bow");
        HMbow.setItemMeta(bow_meta);

        skeleton.getEquipment().setItemInMainHand(HMbow);
        skeleton.getEquipment().setItemInMainHandDropChance(0);


        skeleton.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 9999, random.nextInt(3), true, true));

        if (random.nextBoolean()) {
            skeleton.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 9999, 0, true, true));
        }

        Location one_up = location;
        one_up.setY(one_up.getBlockY() + 2);

        if (!one_up.getBlock().getType().equals(Material.AIR)) {
            one_up.getBlock().setType(Material.AIR);
        }

        location.setY(location.getBlockY() - 2);

        world.spawnEntity(location, EntityType.WITHER_SKELETON);

    }

}
