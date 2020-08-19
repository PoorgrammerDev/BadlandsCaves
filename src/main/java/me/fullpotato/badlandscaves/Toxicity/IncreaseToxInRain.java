package me.fullpotato.badlandscaves.Toxicity;

import me.fullpotato.badlandscaves.AlternateDimensions.Hazards.EnvironmentalHazards;
import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.Crafting.Starlight.StarlightArmor;
import me.fullpotato.badlandscaves.CustomItems.Crafting.Starlight.StarlightCharge;
import me.fullpotato.badlandscaves.CustomItems.Using.Starlight.Nebulites.Nebulite;
import me.fullpotato.badlandscaves.CustomItems.Using.Starlight.Nebulites.NebuliteManager;
import me.fullpotato.badlandscaves.Effects.PlayerEffects;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.Random;

public class IncreaseToxInRain implements Listener {
    private final BadlandsCaves plugin;
    private final Random random = new Random();
    private final StarlightArmor starlightArmor;
    private final StarlightCharge starlightCharge;
    private final NebuliteManager nebuliteManager;
    private final PlayerEffects playerEffects;

    public IncreaseToxInRain(BadlandsCaves plugin) {
        this.plugin = plugin;
        this.starlightArmor = new StarlightArmor(plugin);
        this.starlightCharge = new StarlightCharge(plugin);
        this.nebuliteManager = new NebuliteManager(plugin);
        playerEffects = new PlayerEffects(plugin);
    }

    @EventHandler
    public void increase_tox_in_rain (PlayerMoveEvent event) {
        Player player = event.getPlayer();
        World world = player.getWorld();
        if (!world.hasStorm()) return;

        EnvironmentalHazards dims = new EnvironmentalHazards(plugin);
        if (dims.isDimension(world) && !dims.hasHazard(world, EnvironmentalHazards.Hazard.ACID_RAIN)) return;

        Location location = player.getLocation();
        double temp = world.getTemperature(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        if (temp > 0.95) return;
        if (player.getGameMode() != GameMode.SURVIVAL && player.getGameMode() != GameMode.ADVENTURE) return;

        for (int y = location.getBlockY(); y < world.getMaxHeight(); y++) {
            location.setY(y);
            if (!location.getBlock().isPassable()) return;
        }

        boolean hardmode = plugin.getSystemConfig().getBoolean("hardmode");

        if (hardmode) {
            if ((byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == (byte) 0) {
                final EntityEquipment equipment = player.getEquipment();
                if (equipment != null) {
                    for (ItemStack armor : equipment.getArmorContents()) {
                        if (armor != null && starlightArmor.isStarlightArmor(armor) && starlightCharge.getCharge(armor) > 0) {
                            final Nebulite[] nebulites = nebuliteManager.getNebulites(armor);
                            for (Nebulite nebulite : nebulites) {
                                if (nebulite != null && nebulite.equals(Nebulite.TOXIN_EXPELLER)) {
                                    if (random.nextInt(1000) < 5) starlightCharge.setCharge(armor, starlightCharge.getCharge(armor) - 1);
                                    return;
                                }
                            }
                        }
                    }
                }
            }
        }

        boolean waterBreathing = (player.hasPotionEffect(PotionEffectType.WATER_BREATHING) || player.hasPotionEffect(PotionEffectType.CONDUIT_POWER));

        final int toxic_sys_var = ((int) PlayerScore.TOX_SLOW_INCR_VAR.getScore(plugin, player));
        final Random random = new Random();

        int base = 50;
        if (hardmode) base += 50;
        if (waterBreathing) base -= 25;

        final int rand = random.nextInt(base);
        PlayerScore.TOX_SLOW_INCR_VAR.setScore(plugin, player, toxic_sys_var + rand);


        //overflowing tox_slow to tox
        final double current_tox = (double) PlayerScore.TOXICITY.getScore(plugin, player);
        int current_tox_slow = ((int) PlayerScore.TOX_SLOW_INCR_VAR.getScore(plugin, player));
        if (current_tox_slow >= 100) {
            PlayerScore.TOX_SLOW_INCR_VAR.setScore(plugin, player, 0);
            PlayerScore.TOXICITY.setScore(plugin, player, current_tox + 0.1);
            playerEffects.applyEffects(player, true);
        }


    }
}
