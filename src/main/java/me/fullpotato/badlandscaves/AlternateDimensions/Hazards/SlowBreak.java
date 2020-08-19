package me.fullpotato.badlandscaves.AlternateDimensions.Hazards;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.Effects.PlayerEffects;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class SlowBreak implements Listener {
    private final BadlandsCaves plugin;
    private final EnvironmentalHazards dims;
    private final PlayerEffects playerEffects;

    public SlowBreak(BadlandsCaves plugin) {
        this.plugin = plugin;
        this.dims = new EnvironmentalHazards(plugin);
        playerEffects = new PlayerEffects(plugin);
    }

    @EventHandler
    public void changeWorld (PlayerChangedWorldEvent event) {
        final Player player = event.getPlayer();
        final World exitingWorld = event.getFrom();
        final World enteringWorld = player.getWorld();

        if (dims.isDimension(enteringWorld)) {
            if (dims.hasHazard(enteringWorld, EnvironmentalHazards.Hazard.SLOW_BREAK)) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, Integer.MAX_VALUE, 1, false, false));
            }
        }
        else if (dims.isDimension(exitingWorld)) {
            if (dims.hasHazard(exitingWorld, EnvironmentalHazards.Hazard.SLOW_BREAK)) {
                final PotionEffect potionEffect = player.getPotionEffect(PotionEffectType.SLOW_DIGGING);
                if (potionEffect != null && potionEffect.getDuration() > 999999) {
                    player.removePotionEffect(PotionEffectType.SLOW_DIGGING);
                    playerEffects.applyEffects(player, true);
                }
            }
        }
    }
}
