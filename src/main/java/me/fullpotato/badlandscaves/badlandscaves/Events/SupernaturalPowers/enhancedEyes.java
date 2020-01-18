package me.fullpotato.badlandscaves.badlandscaves.Events.SupernaturalPowers;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class enhancedEyes implements Listener {
    private BadlandsCaves plugin;
    public enhancedEyes (BadlandsCaves bcav) {
        plugin = bcav;
    }

    @EventHandler
    public void use_ee (PlayerInteractEvent event) {
        Player player = event.getPlayer();
        int has_powers = player.getMetadata("has_supernatural_powers").get(0).asInt();
        if (has_powers < 1.0) return;

        ItemStack eyes = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.enhanced_eyes").getValues(true));
        if (player.getInventory().getItemInOffHand().isSimilar(eyes)) {
            Action action = event.getAction();
            if (action.equals(Action.RIGHT_CLICK_BLOCK) || action.equals(Action.RIGHT_CLICK_AIR)) {
                EquipmentSlot e = event.getHand();
                assert e != null;
                if (e.equals(EquipmentSlot.OFF_HAND)) {
                    ProtocolManager manager = ProtocolLibrary.getProtocolManager();
                    //PacketContainer container = manager.createPacket(PacketType.Play.Client.
                }
            }
        }
    }
}
