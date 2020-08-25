package me.fullpotato.badlandscaves.Loot.MobDeathLoot;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.CustomItem;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public class AscendedDrops implements Listener {
    private final BadlandsCaves plugin;
    private final NamespacedKey key;
    private final ItemStack ascendedOrb;

    public AscendedDrops(BadlandsCaves plugin) {
        this.plugin = plugin;
        key = new NamespacedKey(plugin, "ascended");
        ascendedOrb = plugin.getCustomItemManager().getItem(CustomItem.ASCENDED_ORB);
    }

    @EventHandler
    public void killMob (EntityDeathEvent event) {
        final LivingEntity entity = event.getEntity();
        if (entity.getPersistentDataContainer().has(key, PersistentDataType.BYTE)) {
            final Byte result = entity.getPersistentDataContainer().get(key, PersistentDataType.BYTE);
            if (result != null && result == (byte) 1) {
                final int multiplier = plugin.getOptionsConfig().getInt("hardmode_values.ascended_exp_multiplier");
                event.setDroppedExp(event.getDroppedExp() * multiplier);
                event.getDrops().add(ascendedOrb);
            }
        }
    }
}
