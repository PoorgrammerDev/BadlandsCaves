package me.fullpotato.badlandscaves.badlandscaves.events;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;

public class NewPlayer implements Listener {

    private BadlandsCaves plugin;
    public NewPlayer(BadlandsCaves bcav) {
        plugin = bcav;
    }

    @EventHandler
    public void newPlayer (PlayerLoginEvent event) {
        //give items
        Player player = event.getPlayer();

        if (!player.hasPlayedBefore()) {
            ItemStack starter_sapling = new ItemStack(Material.OAK_SAPLING, 1);
            ItemMeta sapling_meta = starter_sapling.getItemMeta();

            sapling_meta.setDisplayName(ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + "Starter Sapling" + ChatColor.DARK_GRAY + "]");
            sapling_meta.addEnchant(Enchantment.DURABILITY, 1, false);
            sapling_meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

            starter_sapling.setItemMeta(sapling_meta);


            ItemStack starter_bone_meal = new ItemStack(Material.BONE_MEAL, 3);
            ItemMeta bone_meta = starter_bone_meal.getItemMeta();

            bone_meta.setDisplayName(ChatColor.DARK_GRAY + "[" + ChatColor.WHITE + "Starter Bone Meal" + ChatColor.DARK_GRAY + "]");
            bone_meta.addEnchant(Enchantment.DURABILITY, 1, false);
            bone_meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

            starter_bone_meal.setItemMeta(bone_meta);


            player.getInventory().addItem(starter_sapling);
            player.getInventory().addItem(starter_bone_meal);

            //default values
            player.setMetadata("Deaths", new FixedMetadataValue(plugin, 0.0));
            player.setMetadata("Thirst", new FixedMetadataValue(plugin, 100.0));
            player.setMetadata("Toxicity", new FixedMetadataValue(plugin, 0.0));

            player.setMetadata("thirst_sys_var", new FixedMetadataValue(plugin, 0.0));
            player.setMetadata("tox_nat_decr_var", new FixedMetadataValue(plugin, 0.0));
            player.setMetadata("tox_slow_incr_var", new FixedMetadataValue(plugin, 0.0));

            //stacking debuffs
            player.setMetadata("deaths_debuff_slowmine_lvl", new FixedMetadataValue(plugin, 0));
            player.setMetadata("deaths_debuff_slow_lvl", new FixedMetadataValue(plugin, 0));
            player.setMetadata("deaths_debuff_hunger_lvl", new FixedMetadataValue(plugin, 0));
            player.setMetadata("deaths_debuff_poison_lvl", new FixedMetadataValue(plugin, 0));

            player.setMetadata("tox_debuff_slowmine_lvl", new FixedMetadataValue(plugin, 0));
            player.setMetadata("tox_debuff_slow_lvl", new FixedMetadataValue(plugin, 0));
            player.setMetadata("tox_debuff_hunger_lvl", new FixedMetadataValue(plugin, 0));
            player.setMetadata("tox_debuff_poison_lvl", new FixedMetadataValue(plugin, 0));

            player.setMetadata("thirst_debuff_slowmine_lvl", new FixedMetadataValue(plugin, 0));
            player.setMetadata("thirst_debuff_slow_lvl", new FixedMetadataValue(plugin, 0));
            player.setMetadata("thirst_debuff_hunger_lvl", new FixedMetadataValue(plugin, 0));
            player.setMetadata("thirst_debuff_poison_lvl", new FixedMetadataValue(plugin, 0));
        }
    }

}
