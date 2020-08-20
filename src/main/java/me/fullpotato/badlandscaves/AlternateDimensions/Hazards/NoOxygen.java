package me.fullpotato.badlandscaves.AlternateDimensions.Hazards;

import me.fullpotato.badlandscaves.BadlandsCaves;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityAirChangeEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class NoOxygen extends BukkitRunnable implements Listener {
    private final BadlandsCaves plugin;
    private final EnvironmentalHazards environmentalHazards;

    public NoOxygen(BadlandsCaves plugin, EnvironmentalHazards environmentalHazards) {
        this.plugin = plugin;
        this.environmentalHazards = environmentalHazards;
    }

    @EventHandler
    public void cancelAirRegen(EntityAirChangeEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            World world = player.getWorld();
            if (environmentalHazards.isDimension(world) && environmentalHazards.hasHazard(world, EnvironmentalHazards.Hazard.NO_OXYGEN)) {
                if (player.getGameMode().equals(GameMode.SURVIVAL) || player.getGameMode().equals(GameMode.ADVENTURE)) {
                    if (event.getAmount() > player.getRemainingAir()) {
                        if (event.getAmount() < 0 && event.getAmount() % 10 == 0) {
                            player.setHealth(Math.max(player.getHealth() - 0.75, 0));
                            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_HURT_DROWN, SoundCategory.PLAYERS, 1, 1);
                            player.setNoDamageTicks(0);
                        }
                        else {
                            final EntityEquipment equipment = player.getEquipment();
                            if (equipment != null) {
                                final ItemStack helmet = equipment.getHelmet();
                                if (helmet != null && helmet.containsEnchantment(Enchantment.OXYGEN)) {
                                    final Random random = new Random();
                                    final int level = helmet.getEnchantmentLevel(Enchantment.OXYGEN);

                                    if (random.nextInt(100) < (100.0 * level / (level + 1))) {
                                        event.setCancelled(true);
                                        return;
                                    }
                                }
                            }

                        }
                        event.setAmount(player.getRemainingAir() - 1);
                    }
                }
            }
        }
    }

    @Override
    public void run() {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            World world = player.getWorld();
            if (environmentalHazards.isDimension(world) && environmentalHazards.hasHazard(world, EnvironmentalHazards.Hazard.NO_OXYGEN)) {
                if (player.getGameMode().equals(GameMode.SURVIVAL) || player.getGameMode().equals(GameMode.ADVENTURE)) {
                    if (player.getRemainingAir() == 300) {
                        player.setRemainingAir(299);
                    }
                }
            }
        }
    }
}
