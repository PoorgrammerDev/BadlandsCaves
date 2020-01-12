package me.fullpotato.badlandscaves.badlandscaves.Events.MobBuffs;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Random;

public class zombieBuff implements Listener {
    private BadlandsCaves plugin;
    public zombieBuff (BadlandsCaves bcav) {
        plugin = bcav;
    }

    @EventHandler
    public void HM_zombie (CreatureSpawnEvent event) {
        if (!(event.getEntity() instanceof Zombie)) return;
        if (event.getEntityType().equals(EntityType.PIG_ZOMBIE)) return;

        boolean isHardmode = plugin.getConfig().getBoolean("game_values.hardmode");
        if (!isHardmode) return;

        LivingEntity zombie = (LivingEntity) event.getEntity();
        Location location = zombie.getLocation();
        //World world = location.getWorld();

        Random random = new Random();
        ItemStack sword = new ItemStack(Material.DIAMOND_SWORD, 1);
        ItemMeta sword_meta = sword.getItemMeta();
        sword_meta.addEnchant(Enchantment.DAMAGE_ALL, random.nextInt(5), false);
        sword_meta.addEnchant(Enchantment.FIRE_ASPECT, random.nextInt(2), false);
        sword_meta.setDisplayName(ChatColor.DARK_GREEN + "Zombie's Sword");
        sword.setItemMeta(sword_meta);

        ItemStack[] armor = {
                new ItemStack(Material.IRON_BOOTS),
                new ItemStack(Material.IRON_LEGGINGS),
                new ItemStack(Material.DIAMOND_CHESTPLATE),
                new ItemStack(Material.IRON_HELMET)
        };

        zombie.getEquipment().setItemInMainHand(sword);
        zombie.getEquipment().setArmorContents(armor);
        zombie.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(999);

        location.setY(location.getBlockY() - 2);
    }
}
