package me.fullpotato.badlandscaves.badlandscaves.Events;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;

public class player_join implements Listener {

    private BadlandsCaves plugin;
    private World world;
    public player_join(BadlandsCaves bcav, World wrld) {
        plugin = bcav;
        world = wrld;
    }

    @EventHandler
    public void player_join (PlayerJoinEvent event) {
        //give items
        Player player = event.getPlayer();

        //NEW PLAYER--------------------------------
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

            //default config set for new players
            plugin.getConfig().set("Scores.users." + player.getUniqueId() + ".Deaths", 0);
            plugin.getConfig().set("Scores.users." + player.getUniqueId() + ".Thirst", 100.0);
            plugin.getConfig().set("Scores.users." + player.getUniqueId() + ".Toxicity", 0.0);
            plugin.getConfig().set("Scores.users." + player.getUniqueId() + ".thirst_sys_var", 0.0);
            plugin.getConfig().set("Scores.users." + player.getUniqueId() + ".tox_nat_decr_var", 0.0);
            plugin.getConfig().set("Scores.users." + player.getUniqueId() + ".tox_slow_incr_var", 0.0);
            plugin.getConfig().set("Scores.users." + player.getUniqueId() + ".deaths_debuff_slowmine_lvl", 0);
            plugin.getConfig().set("Scores.users." + player.getUniqueId() + ".deaths_debuff_slow_lvl", 0);
            plugin.getConfig().set("Scores.users." + player.getUniqueId() + ".deaths_debuff_hunger_lvl", 0);
            plugin.getConfig().set("Scores.users." + player.getUniqueId() + ".deaths_debuff_poison_lvl", 0);
            plugin.getConfig().set("Scores.users." + player.getUniqueId() + ".tox_debuff_slowmine_lvl", 0);
            plugin.getConfig().set("Scores.users." + player.getUniqueId() + ".tox_debuff_slow_lvl", 0);
            plugin.getConfig().set("Scores.users." + player.getUniqueId() + ".tox_debuff_hunger_lvl", 0);
            plugin.getConfig().set("Scores.users." + player.getUniqueId() + ".tox_debuff_poison_lvl", 0);
            plugin.getConfig().set("Scores.users." + player.getUniqueId() + ".thirst_debuff_slowmine_lvl", 0);
            plugin.getConfig().set("Scores.users." + player.getUniqueId() + ".thirst_debuff_slow_lvl", 0);
            plugin.getConfig().set("Scores.users." + player.getUniqueId() + ".thirst_debuff_hunger_lvl", 0);
            plugin.getConfig().set("Scores.users." + player.getUniqueId() + ".thirst_debuff_poison_lvl", 0);

            plugin.saveConfig();
        }

        //EVERYONE---------------------------------------
        //load config back into metadata
        player.setMetadata("Deaths", new FixedMetadataValue(plugin, plugin.getConfig().get("Scores.users." + player.getUniqueId() + ".Deaths")));
        player.setMetadata("Thirst", new FixedMetadataValue(plugin, plugin.getConfig().get("Scores.users." + player.getUniqueId() + ".Thirst")));
        player.setMetadata("Toxicity", new FixedMetadataValue(plugin, plugin.getConfig().get("Scores.users." + player.getUniqueId() + ".Toxicity")));
        player.setMetadata("thirst_sys_var", new FixedMetadataValue(plugin, plugin.getConfig().get("Scores.users." + player.getUniqueId() + ".thirst_sys_var")));
        player.setMetadata("tox_nat_decr_var", new FixedMetadataValue(plugin, plugin.getConfig().get("Scores.users." + player.getUniqueId() + ".tox_nat_decr_var")));
        player.setMetadata("tox_slow_incr_var", new FixedMetadataValue(plugin, plugin.getConfig().get("Scores.users." + player.getUniqueId() + ".tox_slow_incr_var")));
        player.setMetadata("deaths_debuff_slowmine_lvl", new FixedMetadataValue(plugin, plugin.getConfig().get("Scores.users." + player.getUniqueId() + ".deaths_debuff_slowmine_lvl")));
        player.setMetadata("deaths_debuff_slow_lvl", new FixedMetadataValue(plugin, plugin.getConfig().get("Scores.users." + player.getUniqueId() + ".deaths_debuff_slow_lvl")));
        player.setMetadata("deaths_debuff_hunger_lvl", new FixedMetadataValue(plugin, plugin.getConfig().get("Scores.users." + player.getUniqueId() + ".deaths_debuff_hunger_lvl")));
        player.setMetadata("deaths_debuff_poison_lvl", new FixedMetadataValue(plugin, plugin.getConfig().get("Scores.users." + player.getUniqueId() + ".deaths_debuff_poison_lvl")));
        player.setMetadata("tox_debuff_slowmine_lvl", new FixedMetadataValue(plugin, plugin.getConfig().get("Scores.users." + player.getUniqueId() + ".tox_debuff_slowmine_lvl")));
        player.setMetadata("tox_debuff_slow_lvl", new FixedMetadataValue(plugin, plugin.getConfig().get("Scores.users." + player.getUniqueId() + ".tox_debuff_slow_lvl")));
        player.setMetadata("tox_debuff_hunger_lvl", new FixedMetadataValue(plugin, plugin.getConfig().get("Scores.users." + player.getUniqueId() + ".tox_debuff_hunger_lvl")));
        player.setMetadata("tox_debuff_poison_lvl", new FixedMetadataValue(plugin, plugin.getConfig().get("Scores.users." + player.getUniqueId() + ".tox_debuff_poison_lvl")));
        player.setMetadata("thirst_debuff_slowmine_lvl", new FixedMetadataValue(plugin, plugin.getConfig().get("Scores.users." + player.getUniqueId() + ".thirst_debuff_slowmine_lvl")));
        player.setMetadata("thirst_debuff_slow_lvl", new FixedMetadataValue(plugin, plugin.getConfig().get("Scores.users." + player.getUniqueId() + ".thirst_debuff_slow_lvl")));
        player.setMetadata("thirst_debuff_hunger_lvl", new FixedMetadataValue(plugin, plugin.getConfig().get("Scores.users." + player.getUniqueId() + ".thirst_debuff_hunger_lvl")));
        player.setMetadata("thirst_debuff_poison_lvl", new FixedMetadataValue(plugin, plugin.getConfig().get("Scores.users." + player.getUniqueId() + ".thirst_debuff_poison_lvl")));

        player.teleport(world.getSpawnLocation());
    }
}
