package me.fullpotato.badlandscaves.CustomItems.Using.Starlight.Nebulites.Mechanisms;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.Using.Starlight.Nebulites.Nebulite;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class NebuliteShieldThruster extends NebuliteMechanisms implements Listener {
    public NebuliteShieldThruster(BadlandsCaves plugin) {
        super(plugin);
    }

    @EventHandler
    public void playerMove (PlayerMoveEvent event) {
        if (plugin.getSystemConfig().getBoolean("hardmode")) {
            final Player player = event.getPlayer();
            if ((byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == (byte) 0 &&
                    event.getTo() != null && event.getFrom().distanceSquared(event.getTo()) > 0) {

                final ItemStack mainHand = player.getEquipment().getItemInMainHand();
                final ItemStack offHand = player.getEquipment().getItemInOffHand();

                ItemStack shield = null;
                if (starlightTools.isStarlightShield(mainHand)) shield = mainHand;
                else if (starlightTools.isStarlightShield(offHand)) shield = offHand;

                if (shield != null && starlightCharge.getCharge(shield) > 0) {
                    final Nebulite[] nebulites = nebuliteManager.getNebulites(shield);
                    for (Nebulite nebulite : nebulites) {
                        if (nebulite != null && nebulite.equals(Nebulite.SHIELD_THRUSTER)) {
                            if (player.isBlocking()) {
                                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1, 30, true, true));
                                if (random.nextInt(100) < 1) starlightCharge.setCharge(shield, starlightCharge.getCharge(shield) - 1);
                            }
                            else {
                                if (random.nextInt(1000) < 1) starlightCharge.setCharge(shield, starlightCharge.getCharge(shield) - 1);
                            }
                            return;
                        }
                    }
                }
            }
        }
    }
}
