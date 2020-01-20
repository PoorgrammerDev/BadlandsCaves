package me.fullpotato.badlandscaves.badlandscaves.Events.SupernaturalPowers;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;

public class enhancedEyes implements Listener {
    private BadlandsCaves plugin;
    public enhancedEyes (BadlandsCaves bcav) {
        plugin = bcav;
    }

    private int slime_id = Integer.MAX_VALUE;

    @EventHandler
    public void use_eyes (PlayerInteractEvent event) throws InvocationTargetException {
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
                    event.setCancelled(true);

                    Location location = player.getLocation();
                    double x = location.getX();
                    double y = location.getY();
                    double z = location.getZ();

/*
                    WrapperPlayServerSpawnEntityLiving wrapper = new WrapperPlayServerSpawnEntityLiving();
                    wrapper.setType(EntityType.SLIME);
                    wrapper.setEntityID(slime_id);
                    wrapper.setUniqueId(UUID.randomUUID());
                    wrapper.setX(player.getLocation().getX());
                    wrapper.setY(player.getLocation().getY());
                    wrapper.setZ(player.getLocation().getZ());

                    wrapper.sendPacket(player);
*/
                    /*ProtocolManager manager = ProtocolLibrary.getProtocolManager();
                    PacketContainer slime_spawn = manager.createPacket(PacketType.Play.Server.SPAWN_ENTITY_LIVING);
                    slime_spawn.getIntegers().write(0, slime_id).write(1, (int) EntityType.SLIME.getTypeId()); //doesn't spawn a slime
                    slime_spawn.getUUIDs().write(0, UUID.randomUUID());
                    slime_spawn.getDoubles().write(0, x).write(1, y).write(2, z);
                    slime_id--;

                    PacketContainer slime_meta = manager.createPacket(PacketType.Play.Server.ENTITY_METADATA);
                    //what to put here?

                    manager.sendServerPacket(event.getPlayer(), slime_spawn);
                    manager.sendServerPacket(event.getPlayer(), slime_meta);
*/
                }
            }
        }
    }
}
