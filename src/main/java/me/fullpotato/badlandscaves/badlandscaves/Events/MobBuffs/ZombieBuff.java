package me.fullpotato.badlandscaves.badlandscaves.Events.MobBuffs;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import org.bukkit.*;
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

public class ZombieBuff implements Listener {
    private BadlandsCaves plugin;
    private World descension_world = Bukkit.getWorld("world_descension");
    private World reflection_world = Bukkit.getWorld("world_reflection");
    public ZombieBuff(BadlandsCaves bcav) {
        plugin = bcav;
    }

    @EventHandler
    public void HM_zombie (CreatureSpawnEvent event) {
        if (!(event.getEntity() instanceof Zombie)) return;
        if (event.getEntityType().equals(EntityType.PIG_ZOMBIE)) return;

        boolean isHardmode = plugin.getConfig().getBoolean("game_values.hardmode");
        if (!isHardmode) return;

        //make sure zombie is not a descension "Lost Soul"

        World world = event.getLocation().getWorld();
        if (world == null) return;
        if (world.equals(descension_world)) return;
        if (world.equals(reflection_world)) return;


        //giving armor
        Zombie zombie = (Zombie) event.getEntity();
        Random random = new Random();

        //SWORD-------------------------------------------------------------------
        ItemStack sword = new ItemStack(Material.DIAMOND_SWORD, 1);
        ItemMeta sword_meta = sword.getItemMeta();
        sword_meta.addEnchant(Enchantment.DAMAGE_ALL, random.nextInt(5) + 3, true);
        final int fire = random.nextInt(2);
        if (fire > 0) {
            sword_meta.addEnchant(Enchantment.FIRE_ASPECT, fire, false);
        }
        sword_meta.setDisplayName(ChatColor.DARK_GREEN + "Zombie's Sword");
        sword.setItemMeta(sword_meta);

        //ARMOR----------------------------------------------------------------
        int[] armor_protections = random.ints(4, 0, 3).toArray();

        //BOOTS-----------------------------
        boolean dia_upg = random.nextBoolean();
        ItemStack boots = dia_upg ? new ItemStack(Material.DIAMOND_BOOTS) : new ItemStack(Material.IRON_BOOTS);
        if (armor_protections[0] > 0) {
            ItemMeta boots_meta = boots.getItemMeta();
            boots_meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, armor_protections[0], true);
            boots.setItemMeta(boots_meta);
        }

        //LEGGINGS--------------------------
        dia_upg = random.nextBoolean();
        ItemStack leggings = dia_upg ? new ItemStack(Material.DIAMOND_LEGGINGS) : new ItemStack(Material.IRON_LEGGINGS);
        if (armor_protections[1] > 0){
            ItemMeta leggings_meta = leggings.getItemMeta();
            leggings_meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, armor_protections[1], true);
            leggings.setItemMeta(leggings_meta);
        }

        //CHESTPLATE------------------------
        dia_upg = random.nextBoolean();
        ItemStack chestplate = dia_upg ? new ItemStack(Material.DIAMOND_CHESTPLATE) : new ItemStack(Material.IRON_CHESTPLATE);
        if (armor_protections[2] > 0){
            ItemMeta chestplate_meta = chestplate.getItemMeta();
            chestplate_meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, armor_protections[2], true);
            chestplate.setItemMeta(chestplate_meta);
        }

        //HELMET-----------------------------
        dia_upg = random.nextBoolean();
        ItemStack helmet = dia_upg ? new ItemStack(Material.DIAMOND_HELMET) : new ItemStack(Material.IRON_HELMET);
        if (armor_protections[3] > 0){
            ItemMeta helmet_meta = helmet.getItemMeta();
            helmet_meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, armor_protections[3], true);
            helmet.setItemMeta(helmet_meta);
        }
        //------------------------------------------------------------------------

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
