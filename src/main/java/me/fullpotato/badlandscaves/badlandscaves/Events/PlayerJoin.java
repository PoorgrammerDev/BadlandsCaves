package me.fullpotato.badlandscaves.badlandscaves.Events;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.badlandscaves.Util.PlayerConfigLoadSave;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

public class PlayerJoin implements Listener {

    private BadlandsCaves plugin;
    private String[] values;
    public PlayerJoin(BadlandsCaves bcav, String[] ply_vals) {
        plugin = bcav;
        values = ply_vals;
    }

    @EventHandler
    public void player_join (PlayerJoinEvent event) {
        //give items
        Player player = event.getPlayer();

        PlayerConfigLoadSave loader = new PlayerConfigLoadSave(plugin, values);
        //NEW PLAYER--------------------------------
        if (!player.hasPlayedBefore()) {

            ItemStack starter_sapling = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.starter_sapling").getValues(true));
            ItemStack starter_bone_meal = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.starter_bone_meal").getValues(true));
            player.getInventory().addItem(starter_sapling);
            player.getInventory().addItem(starter_bone_meal);

            //default values
            loader.saveDefault(player);
        }

        //EVERYONE---------------------------------------
        //load config back into metadata
        loader.loadPlayer(player);

        //REGARDING SUPERNATURAL POWERS----------------------------------

        //if they log off in the withdraw pocket dimension, it sends them back to the real world when they log back in
        int has_powers = player.getMetadata("has_supernatural_powers").get(0).asInt();
        if (has_powers < 1.0) return;

        if (player.getWorld().equals(Bukkit.getWorld("world_empty"))) {

            if (player.getMetadata("withdraw_timer").get(0).asInt() <= 0) {
                String origworldname = plugin.getConfig().getString("Scores.users." + player.getUniqueId() + ".withdraw_orig_world");
                assert origworldname != null;
                World origworld = Bukkit.getWorld(origworldname);

                Location location = player.getLocation();
                location.setWorld(origworld);

                player.teleport(location);
            }
        }

        //reset agility jump timer
        player.setMetadata("agility_jump_id", new FixedMetadataValue(plugin, 0));
        player.setMetadata("agility_jump_timer", new FixedMetadataValue(plugin, 0));
    }
}
