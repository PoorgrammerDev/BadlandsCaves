package me.fullpotato.badlandscaves.CustomItems.Using.Starlight.Nebulites.Mechanisms;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.Crafting.Starlight.StarlightArmor;
import me.fullpotato.badlandscaves.CustomItems.Crafting.Starlight.StarlightCharge;
import me.fullpotato.badlandscaves.CustomItems.Crafting.Starlight.StarlightTools;
import me.fullpotato.badlandscaves.CustomItems.Using.Starlight.Nebulites.Nebulite;
import me.fullpotato.badlandscaves.CustomItems.Using.Starlight.Nebulites.NebuliteManager;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class NebuliteShockAbsorber extends NebuliteMechanisms implements Listener {

    public NebuliteShockAbsorber(BadlandsCaves plugin, Random random, StarlightArmor starlightArmor, StarlightTools starlightTools, StarlightCharge starlightCharge, NebuliteManager nebuliteManager) {
        super(plugin, random, starlightArmor, starlightTools, starlightCharge, nebuliteManager);
    }

    @EventHandler
    public void preventFallDamage (EntityDamageEvent event) {
        if (plugin.getSystemConfig().getBoolean("hardmode")) {
            if (event.getEntity() instanceof Player) {
                final Player player = (Player) event.getEntity();
                if ((byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == (byte) 0) {
                    EntityDamageEvent.DamageCause cause = event.getCause();
                    if (cause.equals(EntityDamageEvent.DamageCause.FALL)) {
                        final EntityEquipment equipment = player.getEquipment();
                        if (equipment != null) {
                            final ItemStack boots = equipment.getBoots();
                            if (boots != null && starlightArmor.isStarlightArmor(boots) && starlightCharge.getCharge(boots) > 0) {
                                final Nebulite[] nebulites = nebuliteManager.getNebulites(boots);
                                for (Nebulite nebulite : nebulites) {
                                    if (nebulite != null && nebulite.equals(Nebulite.SHOCK_ABSORBER)) {
                                        event.setCancelled(true);
                                        starlightCharge.setCharge(boots, (int) (starlightCharge.getCharge(boots) - (event.getFinalDamage() * 3)));
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
