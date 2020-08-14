package me.fullpotato.badlandscaves.Other;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.CustomItem;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionType;

public class FishWater implements Listener {
    private final BadlandsCaves plugin;

    public FishWater(BadlandsCaves plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void transformWater (PlayerFishEvent event) {
        if (event.getState().equals(PlayerFishEvent.State.CAUGHT_FISH)) {
            if (event.getCaught() instanceof Item) {
                final Item itemEntity = (Item) event.getCaught();
                final ItemStack item = itemEntity.getItemStack();

                if (item.getType().equals(Material.POTION)) {
                    if (item.getItemMeta() instanceof PotionMeta) {
                        final PotionMeta potionMeta = (PotionMeta) item.getItemMeta();
                        if (potionMeta.getBasePotionData().getType().equals(PotionType.WATER)) {
                            itemEntity.setItemStack(plugin.getCustomItemManager().getItem(CustomItem.TOXIC_WATER));
                        }
                    }
                }
            }
        }
    }
}
