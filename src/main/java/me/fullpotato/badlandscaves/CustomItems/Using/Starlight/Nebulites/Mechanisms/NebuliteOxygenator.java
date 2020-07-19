package me.fullpotato.badlandscaves.CustomItems.Using.Starlight.Nebulites.Mechanisms;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.Crafting.Starlight.StarlightArmor;
import me.fullpotato.badlandscaves.CustomItems.Crafting.Starlight.StarlightCharge;
import me.fullpotato.badlandscaves.CustomItems.Using.Starlight.Nebulites.Nebulite;
import me.fullpotato.badlandscaves.CustomItems.Using.Starlight.Nebulites.NebuliteManager;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityAirChangeEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class NebuliteOxygenator implements Listener {
    private final BadlandsCaves plugin;
    private final Random random = new Random();
    private final StarlightArmor starlightArmor;
    private final StarlightCharge starlightCharge;
    private final NebuliteManager nebuliteManager;

    public NebuliteOxygenator(BadlandsCaves plugin) {
        this.plugin = plugin;
        this.starlightArmor = new StarlightArmor(plugin);
        this.starlightCharge = new StarlightCharge(plugin);
        this.nebuliteManager = new NebuliteManager(plugin);
    }

    @EventHandler
    public void replenishOxygen (EntityAirChangeEvent event) {
        if (plugin.getSystemConfig().getBoolean("hardmode")) {
            if (event.getEntity() instanceof Player) {
                final Player player = (Player) event.getEntity();

                if ((byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == (byte) 0) {
                    int amount = event.getAmount();
                    if (amount < 0 && amount < player.getRemainingAir()) {
                        final EntityEquipment equipment = player.getEquipment();
                        if (equipment != null) {
                            final ItemStack helmet = equipment.getHelmet();
                            if (helmet != null && starlightArmor.isStarlightArmor(helmet) && starlightCharge.getCharge(helmet) > 0) {
                                final Nebulite[] nebulites = nebuliteManager.getNebulites(helmet);
                                for (Nebulite nebulite : nebulites) {
                                    if (nebulite != null && nebulite.equals(Nebulite.OXYGENATOR)) {
                                        event.setAmount(amount + 30);
                                        starlightCharge.setCharge(helmet, starlightCharge.getCharge(helmet) - 30);

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
