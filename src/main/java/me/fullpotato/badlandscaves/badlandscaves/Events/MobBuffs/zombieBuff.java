package me.fullpotato.badlandscaves.badlandscaves.Events.MobBuffs;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import org.bukkit.ChatColor;
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

        LivingEntity zombie = event.getEntity();
        //World world = location.getWorld();

        Random random = new Random();
        ItemStack sword = new ItemStack(Material.DIAMOND_SWORD, 1);
        ItemMeta sword_meta = sword.getItemMeta();
        sword_meta.addEnchant(Enchantment.DAMAGE_ALL, random.nextInt(5) + 3, true);
        sword_meta.addEnchant(Enchantment.FIRE_ASPECT, random.nextInt(2), false);
        sword_meta.setDisplayName(ChatColor.DARK_GREEN + "Zombie's Sword");
        sword.setItemMeta(sword_meta);

        boolean dia_upg = random.nextBoolean();
        ItemStack boots = dia_upg ? new ItemStack(Material.DIAMOND_BOOTS) : new ItemStack(Material.IRON_BOOTS);
        ItemMeta boots_meta = boots.getItemMeta();
        boots_meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, random.nextInt(3), true);
        boots.setItemMeta(boots_meta);

        dia_upg = random.nextBoolean();
        ItemStack leggings = dia_upg ? new ItemStack(Material.DIAMOND_LEGGINGS) : new ItemStack(Material.IRON_LEGGINGS);
        ItemMeta leggings_meta = leggings.getItemMeta();
        leggings_meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, random.nextInt(3), true);
        leggings.setItemMeta(leggings_meta);

        dia_upg = random.nextBoolean();
        ItemStack chestplate = dia_upg ? new ItemStack(Material.DIAMOND_CHESTPLATE) : new ItemStack(Material.IRON_CHESTPLATE);
        ItemMeta chestplate_meta = chestplate.getItemMeta();
        chestplate_meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, random.nextInt(3), true);
        chestplate.setItemMeta(chestplate_meta);

        dia_upg = random.nextBoolean();
        ItemStack helmet = dia_upg ? new ItemStack(Material.DIAMOND_HELMET) : new ItemStack(Material.IRON_HELMET);
        ItemMeta helmet_meta = helmet.getItemMeta();
        helmet_meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, random.nextInt(3), true);
        helmet.setItemMeta(helmet_meta);

        ItemStack[] armor = {
                boots,
                leggings,
                chestplate,
                helmet,
        };

        zombie.getEquipment().setItemInMainHand(sword);
        zombie.getEquipment().setArmorContents(armor);
        zombie.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(999);
    }
}
