package me.fullpotato.badlandscaves.CustomItems.Using.Starlight.Nebulites.Mechanisms;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.Crafting.Starlight.StarlightArmor;
import me.fullpotato.badlandscaves.CustomItems.Crafting.Starlight.StarlightCharge;
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

public class NebuliteSmolderingFlames implements Listener {
    private final BadlandsCaves plugin;
    private final StarlightArmor starlightArmor;
    private final StarlightCharge starlightCharge;
    private final NebuliteManager nebuliteManager;

    public NebuliteSmolderingFlames(BadlandsCaves plugin) {
        this.plugin = plugin;
        this.starlightArmor = new StarlightArmor(plugin);
        this.starlightCharge = new StarlightCharge(plugin);
        this.nebuliteManager = new NebuliteManager(plugin);
    }

    @EventHandler
    public void playerDamageToFire (EntityDamageEvent event) {
        if (plugin.getSystemConfig().getBoolean("hardmode")) {
            if (event.getEntity() instanceof Player) {
                final Player player = (Player) event.getEntity();
                if ((byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == (byte) 0) {
                    EntityDamageEvent.DamageCause cause = event.getCause();
                    if (cause.equals(EntityDamageEvent.DamageCause.LAVA) || ((cause.equals(EntityDamageEvent.DamageCause.FIRE) || cause.equals(EntityDamageEvent.DamageCause.FIRE_TICK)) && player.getHealth() < player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() / 4.0)) {
                        final EntityEquipment equipment = player.getEquipment();
                        if (equipment != null) {
                            for (ItemStack item : equipment.getArmorContents()) {
                                if (item != null && starlightArmor.isStarlightArmor(item) && starlightCharge.getCharge(item) > 0) {
                                    final Nebulite[] nebulites = nebuliteManager.getNebulites(item);
                                    for (Nebulite nebulite : nebulites) {
                                        if (nebulite != null && nebulite.equals(Nebulite.SMOLDERING_FLAMES)) {
                                            player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 100, 0, true, true));
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
