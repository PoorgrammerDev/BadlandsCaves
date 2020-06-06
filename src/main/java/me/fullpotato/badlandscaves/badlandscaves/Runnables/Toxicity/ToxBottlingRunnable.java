package me.fullpotato.badlandscaves.badlandscaves.Runnables.Toxicity;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.badlandscaves.Events.CustomItems.CustomItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitRunnable;

public class ToxBottlingRunnable extends BukkitRunnable {
    private BadlandsCaves plugin;
    private Player player;

    public ToxBottlingRunnable(BadlandsCaves bcav, Player ply){
        plugin = bcav;
        player = ply;
    }

    @Override
    public void run() {
        for (int a = 0; a < 36; a++) {
            ItemStack item = player.getInventory().getItem(a);

            if (item != null) {
                if (item.getType().equals(Material.POTION)) {
                    PotionMeta potionMeta = (PotionMeta) item.getItemMeta();
                    if (potionMeta.getBasePotionData().getType().equals(PotionType.WATER)) {
                        ItemStack toxic_water = CustomItem.TOXIC_WATER.getItem();
                        player.getInventory().setItem(a, toxic_water);
                    }
                }
            }
        }
    }
}
