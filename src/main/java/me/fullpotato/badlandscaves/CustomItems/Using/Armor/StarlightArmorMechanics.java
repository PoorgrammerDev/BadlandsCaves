package me.fullpotato.badlandscaves.CustomItems.Using.Armor;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.Crafting.StarlightArmor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;

public class StarlightArmorMechanics extends StarlightArmor implements Listener {
    public StarlightArmorMechanics(BadlandsCaves plugin) {
        super(plugin);
    }

    @EventHandler
    public void depleteCharge (PlayerItemDamageEvent event) {
        ItemStack item = event.getItem();
        if (isStarlightArmor(item)) {
            event.setCancelled(true);
            setCharge(item, getCharge(item) - event.getDamage());
        }
    }



}