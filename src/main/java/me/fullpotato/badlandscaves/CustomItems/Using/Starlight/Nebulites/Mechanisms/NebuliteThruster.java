package me.fullpotato.badlandscaves.CustomItems.Using.Starlight.Nebulites.Mechanisms;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.Using.Starlight.Nebulites.Nebulite;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

public class NebuliteThruster extends NebuliteMechanisms implements Listener {

    public NebuliteThruster(BadlandsCaves plugin) {
        super(plugin);
    }

    @EventHandler
    public void moveChargeCost (PlayerMoveEvent event) {
        if (plugin.getSystemConfig().getBoolean("hardmode")) {
            final Player player = event.getPlayer();
            if ((byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == (byte) 0) {
                EntityEquipment equipment = player.getEquipment();
                if (equipment != null) {
                    for (ItemStack armor : equipment.getArmorContents()) {
                        if (armor != null && starlightArmor.isStarlightArmor(armor) && starlightCharge.getCharge(armor) > 0) {
                            for (Nebulite nebulite : nebuliteManager.getNebulites(armor)) {
                                if (nebulite != null && nebulite.equals(Nebulite.THRUSTER)) {
                                    if (random.nextInt(1000) < 1) starlightCharge.setCharge(armor, starlightCharge.getCharge(armor) - 1);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
