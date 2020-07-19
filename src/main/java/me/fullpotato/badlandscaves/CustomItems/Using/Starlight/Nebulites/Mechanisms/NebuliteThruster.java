package me.fullpotato.badlandscaves.CustomItems.Using.Starlight.Nebulites.Mechanisms;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.Crafting.Starlight.StarlightArmor;
import me.fullpotato.badlandscaves.CustomItems.Crafting.Starlight.StarlightCharge;
import me.fullpotato.badlandscaves.CustomItems.Using.Starlight.Nebulites.Nebulite;
import me.fullpotato.badlandscaves.CustomItems.Using.Starlight.Nebulites.NebuliteManager;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class NebuliteThruster implements Listener {
    private final BadlandsCaves plugin;
    private final Random random = new Random();
    private final StarlightArmor starlightArmor;
    private final StarlightCharge starlightCharge;
    private final NebuliteManager nebuliteManager;

    public NebuliteThruster(BadlandsCaves plugin) {
        this.plugin = plugin;
        this.starlightArmor = new StarlightArmor(plugin);
        this.starlightCharge = new StarlightCharge(plugin);
        this.nebuliteManager = new NebuliteManager(plugin);
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
