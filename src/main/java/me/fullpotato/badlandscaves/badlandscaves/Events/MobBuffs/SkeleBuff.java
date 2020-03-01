package me.fullpotato.badlandscaves.badlandscaves.Events.MobBuffs;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
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
        if (!event.getEntity().getType().equals(EntityType.SKELETON)) return;

        boolean isHardmode = plugin.getConfig().getBoolean("game_values.hardmode");
        if (!isHardmode) return;

        Skeleton skeleton = (Skeleton) event.getEntity();
        Location location = skeleton.getLocation();
        World world = location.getWorld();

        Random random = new Random();
        ItemStack HMbow = new ItemStack(Material.BOW, 1);
        ItemMeta bow_meta = HMbow.getItemMeta();
        bow_meta.addEnchant(Enchantment.ARROW_DAMAGE, random.nextInt(5) + 3, false);
        bow_meta.addEnchant(Enchantment.ARROW_KNOCKBACK, random.nextInt(2), false);
        bow_meta.addEnchant(Enchantment.ARROW_FIRE, 1, false);
        bow_meta.setDisplayName(ChatColor.GRAY + "Skeleton's Bow");
        HMbow.setItemMeta(bow_meta);

        skeleton.getEquipment().setItemInMainHand(HMbow);


        skeleton.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 9999, random.nextInt(3), true, true));
        skeleton.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 9999, 0, true, true));

        Location one_up = location;
        one_up.setY(one_up.getBlockY() + 2);

        if (!one_up.getBlock().getType().equals(Material.AIR)) {
            one_up.getBlock().setType(Material.AIR);
        }

        location.setY(location.getBlockY() - 2);


        //witherskeleton
        WitherSkeleton witherskele = (WitherSkeleton) world.spawnEntity(location, EntityType.WITHER_SKELETON);

        //witherskelesword
        boolean iron_upg = random.nextBoolean();
        ItemStack sword = iron_upg ? new ItemStack(Material.IRON_SWORD, 1) : new ItemStack(Material.STONE_SWORD, 1);
        ItemMeta sword_meta = sword.getItemMeta();
        sword_meta.addEnchant(Enchantment.DAMAGE_ALL, random.nextInt(5) + 3, false);
        sword_meta.setDisplayName(ChatColor.DARK_GRAY + "Wither Skeleton's Sword");
        sword.setItemMeta(sword_meta);
        witherskele.getEquipment().setItemInMainHand(sword);

        //potion effects
        witherskele.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 9999, random.nextInt(3), true, true));
        if (random.nextBoolean()) {
            witherskele.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 9999, random.nextInt(3), true, true));
        }

    }

}
