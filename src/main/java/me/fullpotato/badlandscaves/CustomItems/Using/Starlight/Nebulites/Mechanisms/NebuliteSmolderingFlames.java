package me.fullpotato.badlandscaves.CustomItems.Using.Starlight.Nebulites.Mechanisms;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.Crafting.Starlight.StarlightArmor;
import me.fullpotato.badlandscaves.CustomItems.Crafting.Starlight.StarlightCharge;
import me.fullpotato.badlandscaves.CustomItems.Crafting.Starlight.StarlightTools;
import me.fullpotato.badlandscaves.CustomItems.Using.Starlight.Nebulites.Nebulite;
import me.fullpotato.badlandscaves.CustomItems.Using.Starlight.Nebulites.NebuliteManager;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Random;

public class NebuliteSmolderingFlames extends NebuliteMechanisms implements Listener {
    public NebuliteSmolderingFlames(BadlandsCaves plugin, Random random, StarlightArmor starlightArmor, StarlightTools starlightTools, StarlightCharge starlightCharge, NebuliteManager nebuliteManager) {
        super(plugin, random, starlightArmor, starlightTools, starlightCharge, nebuliteManager);
    }

    @EventHandler
    public void playerDamageToFire (EntityDamageEvent event) {
        if (plugin.getSystemConfig().getBoolean("hardmode")) {
            if (event.getEntity() instanceof Player) {
                final Player player = (Player) event.getEntity();
                if ((byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == (byte) 0) {
                    EntityDamageEvent.DamageCause cause = event.getCause();
                    if (cause.equals(EntityDamageEvent.DamageCause.LAVA) ||
                        cause.equals(EntityDamageEvent.DamageCause.FIRE) ||
                        cause.equals(EntityDamageEvent.DamageCause.FIRE_TICK)) {

                        final EntityEquipment equipment = player.getEquipment();
                        if (equipment != null) {
                            for (ItemStack item : equipment.getArmorContents()) {
                                if (item != null && starlightArmor.isStarlightArmor(item) && starlightCharge.getCharge(item) > 0) {
                                    final Nebulite[] nebulites = nebuliteManager.getNebulites(item);
                                    for (Nebulite nebulite : nebulites) {
                                        if (nebulite != null && nebulite.equals(Nebulite.SMOLDERING_FLAMES)) {
                                            player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 600, 0, true, true));
                                            player.setFireTicks(0);
                                            event.setCancelled(true);
                                            starlightCharge.setCharge(item, starlightCharge.getCharge(item) - 10);
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
}
