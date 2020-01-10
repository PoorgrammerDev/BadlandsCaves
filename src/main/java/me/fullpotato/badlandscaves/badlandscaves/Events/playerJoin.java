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

public class playerJoin implements Listener {

    private BadlandsCaves plugin;
    private World world;
    private String[] values;
    public playerJoin(BadlandsCaves bcav, World wrld, String[] ply_vals) {
        plugin = bcav;
        world = wrld;
        values = ply_vals;
    }

    @EventHandler
    public void player_join (PlayerJoinEvent event) {
        //give items
        Player player = event.getPlayer();

        //NEW PLAYER--------------------------------
        if (!player.hasPlayedBefore()) {

            ItemStack starter_sapling = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.starter_sapling").getValues(true));
            ItemStack starter_bone_meal = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.starter_bone_meal").getValues(true));
            player.getInventory().addItem(starter_sapling);
            player.getInventory().addItem(starter_bone_meal);

            for (String meta: values) {
                String filtered;
                String dot_meta;
                double default_val;
                boolean to_int;

                if (meta.contains("#")) {
                    filtered = meta.substring(1);
                    default_val = 0.0;
                    to_int = false;
                }
                else if (meta.contains("*")) {
                    filtered = meta.substring(1);
                    default_val = 100.0;
                    to_int = false;
                }
                else {
                    filtered = meta;
                    default_val = 0;
                    to_int = true;
                }

                dot_meta = "." + filtered;

                if (to_int) plugin.getConfig().set("Scores.users." + player.getUniqueId() + dot_meta , (int) default_val);
                else plugin.getConfig().set("Scores.users." + player.getUniqueId() + dot_meta , default_val);
                plugin.saveConfig();
            }
        }

        //EVERYONE---------------------------------------
        //load config back into metadata

        for (String meta: values) {
            String filtered;
            String dot_meta;
            if (meta.contains("#") || meta.contains("*")) {
                filtered = meta.substring(1);
            }
            else {
                filtered = meta;
            }
            dot_meta = "." + filtered;
            player.setMetadata(filtered, new FixedMetadataValue(plugin, plugin.getConfig().get("Scores.users." + player.getUniqueId() + dot_meta)));
        }
        player.teleport(world.getSpawnLocation());
    }
}