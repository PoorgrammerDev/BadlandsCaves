package me.fullpotato.badlandscaves.badlandscaves.Runnables;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class tox_bottling_runnable extends BukkitRunnable {
    private BadlandsCaves plugin;
    private Player player;

    public tox_bottling_runnable (BadlandsCaves bcav, Player ply){
        plugin = bcav;
        player = ply;
    }

    @Override
    public void run() {
        System.out.print(4);

        for (int a = 0; a < 36; a++) {
            ItemStack item = player.getInventory().getItem(a);
            if (item.getType().equals(Material.POTION)) {
                PotionMeta potionMeta = (PotionMeta) item.getItemMeta();
                if (potionMeta.getBasePotionData().getType().equals(PotionType.WATER)) {
                    ItemStack toxic_water = new ItemStack(Material.POTION, 1);
                    ItemMeta tox_water_meta = toxic_water.getItemMeta();
                    tox_water_meta.setDisplayName(ChatColor.DARK_GREEN + "Toxic Water Bottle");
                    tox_water_meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
                    ArrayList<String> tox_wat_lore = new ArrayList<>();
                    tox_wat_lore.add(ChatColor.DARK_GRAY + "Leaves a disgusting taste in your mouth");
                    tox_water_meta.setLore(tox_wat_lore);
                    PotionMeta tox_wat_pot_meta = (PotionMeta) tox_water_meta;
                    tox_wat_pot_meta.setColor(Color.fromRGB(55,117,89));
                    tox_wat_pot_meta.setBasePotionData(new PotionData(PotionType.WATER));

                    toxic_water.setItemMeta(tox_wat_pot_meta);

                    player.getInventory().setItem(a, toxic_water);

                }
            }
        }
    }
}
