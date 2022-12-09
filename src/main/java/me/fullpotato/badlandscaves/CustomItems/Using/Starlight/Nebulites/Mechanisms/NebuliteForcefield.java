package me.fullpotato.badlandscaves.CustomItems.Using.Starlight.Nebulites.Mechanisms;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.Crafting.Starlight.StarlightArmor;
import me.fullpotato.badlandscaves.CustomItems.Crafting.Starlight.StarlightCharge;
import me.fullpotato.badlandscaves.CustomItems.Crafting.Starlight.StarlightTools;
import me.fullpotato.badlandscaves.CustomItems.Using.Starlight.Nebulites.Nebulite;
import me.fullpotato.badlandscaves.CustomItems.Using.Starlight.Nebulites.NebuliteManager;
import me.fullpotato.badlandscaves.Util.PlayerScore;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

import java.util.Random;


public class NebuliteForcefield extends NebuliteMechanisms implements Listener {
    public NebuliteForcefield(BadlandsCaves plugin, Random random, StarlightArmor starlightArmor, StarlightTools starlightTools, StarlightCharge starlightCharge, NebuliteManager nebuliteManager) {
        super(plugin, random, starlightArmor, starlightTools, starlightCharge, nebuliteManager);
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
                                boolean hasGuardian = false;
                                for (Nebulite nebulite : nebulites) {
                                    if (nebulite != null) {
                                        if (nebulite.equals(Nebulite.FORCEFIELD)) {
                                            hasForcefield = true;
                                        }
                                        else if (nebulite.equals(Nebulite.STARLIGHT_GUARDIAN)) {
                                            hasGuardian = true;
                                        }
                                    }
                                }

                                if (hasForcefield) {
                                    if (random.nextInt(100) < 12.5f) {
                                        event.setCancelled(true);
                                        starlightCharge.setCharge(item,  (int) (starlightCharge.getCharge(item) - (event.getFinalDamage() * 2)));
                                        
                                        //sfx
                                        player.getWorld().playSound(player.getLocation(), Sound.ITEM_SHIELD_BLOCK, 1f, 1);

                                        return;
                                    }
                                    else if (event.getFinalDamage() >= player.getHealth() && hasGuardian) {
                                        event.setCancelled(true);
                                        starlightCharge.setCharge(item,  (int) (starlightCharge.getCharge(item) - (event.getFinalDamage() * 40)));
                                        
                                        //sfx
                                        player.getWorld().playSound(player.getLocation(), Sound.ITEM_SHIELD_BLOCK, 1f, 1);
                                        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, 1f, 1);
                                        
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
}
