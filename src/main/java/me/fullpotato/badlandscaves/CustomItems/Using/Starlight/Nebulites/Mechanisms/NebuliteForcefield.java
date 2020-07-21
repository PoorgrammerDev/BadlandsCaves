package me.fullpotato.badlandscaves.CustomItems.Using.Starlight.Nebulites.Mechanisms;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.Using.Starlight.Nebulites.Nebulite;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;


public class NebuliteForcefield extends NebuliteMechanisms implements Listener {
    public NebuliteForcefield(BadlandsCaves plugin) {
        super(plugin);
    }

    @EventHandler
    public void preventDamage(EntityDamageEvent event) {
        if (event.isCancelled()) return;

        if (plugin.getSystemConfig().getBoolean("hardmode")) {
            if (event.getEntity() instanceof Player) {
                final Player player = (Player) event.getEntity();
                if ((byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == (byte) 0) {
                    final EntityEquipment equipment = player.getEquipment();
                    if (equipment != null) {
                        for (ItemStack item : equipment.getArmorContents()) {
                            if (item != null && starlightArmor.isStarlightArmor(item) && starlightCharge.getCharge(item) > 0) {
                                final Nebulite[] nebulites = nebuliteManager.getNebulites(item);

                                boolean hasForcefield = false;
                                boolean hasGuardianAngel = false;
                                for (Nebulite nebulite : nebulites) {
                                    if (nebulite != null) {
                                        if (nebulite.equals(Nebulite.FORCEFIELD)) {
                                            hasForcefield = true;
                                        }
                                        else if (nebulite.equals(Nebulite.GUARDIAN_ANGEL)) {
                                            hasGuardianAngel = true;
                                        }
                                    }
                                }

                                if (hasForcefield) {
                                    if (random.nextInt(100) < 25) {
                                        event.setCancelled(true);
                                        starlightCharge.setCharge(item,  (int) (starlightCharge.getCharge(item) - (event.getFinalDamage() * 2)));
                                    }
                                    else if (event.getFinalDamage() >= player.getHealth() && hasGuardianAngel) {
                                        event.setCancelled(true);
                                        starlightCharge.setCharge(item,  (int) (starlightCharge.getCharge(item) - (event.getFinalDamage() * 20)));
                                    }
                                    return;
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
