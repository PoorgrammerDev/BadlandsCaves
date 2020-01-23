package me.fullpotato.badlandscaves.badlandscaves.Events.SupernaturalPowers;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.RayTraceResult;

public class Possession implements Listener {
    private BadlandsCaves plugin;
    public Possession(BadlandsCaves bcav) {
        plugin = bcav;
    }

    @EventHandler
    public void use_possession (PlayerInteractEvent event) {
        Player player = event.getPlayer();
        int has_powers = player.getMetadata("has_supernatural_powers").get(0).asInt();
        if (has_powers < 1.0) return;


        ItemStack possess = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.possess").getValues(true));
        if (player.getInventory().getItemInOffHand().isSimilar(possess)) {
            Action action = event.getAction();
            if (action.equals(Action.RIGHT_CLICK_BLOCK) || action.equals(Action.RIGHT_CLICK_AIR)) {
                EquipmentSlot e = event.getHand();
                assert e != null;
                if (e.equals(EquipmentSlot.OFF_HAND)) {
                    event.setCancelled(true);
                    RayTraceResult result = player.rayTraceBlocks(5);

                    if (result != null && result.getHitEntity() != null) {
                        if (result.getHitEntity() instanceof LivingEntity) {
                            if (result.getHitEntity() instanceof Player) {
                                Player target = (Player) result.getHitEntity();
                                //TODO

                            }
                            else {
                                LivingEntity target = (LivingEntity) result.getHitEntity();
                                //TODO
                            }
                        }
                    }
                }
            }
        }
    }
}
