package me.fullpotato.badlandscaves.CustomItems.Using.Starlight;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.Crafting.Starlight.StarlightCharge;
import me.fullpotato.badlandscaves.CustomItems.Crafting.Starlight.StarlightTools;
import me.fullpotato.badlandscaves.Util.ParticleShapes;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BlockIterator;

import java.util.Collection;

public class StarlightBlasterMechanism implements Listener {
    private final BadlandsCaves plugin;
    private final StarlightCharge chargeManager;
    private final StarlightTools toolManager;

    public StarlightBlasterMechanism(BadlandsCaves plugin) {
        this.plugin = plugin;
        this.chargeManager = new StarlightCharge(this.plugin);
        this.toolManager = new StarlightTools(this.plugin);
    }

    @EventHandler
    public void shootBlaster(PlayerInteractEvent event) {
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || event.getAction().equals(Action.RIGHT_CLICK_AIR)) {
            ItemStack item = event.getItem();
            if (item != null && toolManager.isStarlightBlaster(item)) {
                Player player = event.getPlayer();
                int charge = chargeManager.getCharge(item);
                event.setCancelled(true);
                boolean hardmode = plugin.getConfig().getBoolean("system.hardmode");
                if (hardmode && (byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == (byte) 0 && charge > 0) {

                    final int range = 50;
                    int travelled = 0;

                    World world = player.getWorld();
                    Location location = player.getEyeLocation();
                    Collection<Entity> entityList;

                    BlockIterator iterator = new BlockIterator(location);
                    while (travelled < range && iterator.hasNext()) {
                        Block block = iterator.next();
                        if (block.isPassable()) {
                            location = block.getLocation().add(0.5, 0.5, 0.5);
                            entityList = world.getNearbyEntities(location, 0.5, 0.5, 0.5);
                            entityList.removeIf(entity -> {
                                if (entity instanceof Player) {
                                    Player target = (Player) entity;
                                    return target.getGameMode().equals(GameMode.SPECTATOR) || target.equals(player);
                                }
                                return false;
                            });

                            if (entityList.isEmpty()) {
                                travelled++;
                            }
                            else break;
                        }
                        else break;
                    }

                    ParticleShapes.particleLine(null, Particle.REDSTONE, player.getEyeLocation(), location, 0, new Particle.DustOptions(Color.fromRGB(255, 200, 1), 1), 1);
                    entityList = world.getNearbyEntities(location, 2, 2, 2);

                    world.spawnParticle(Particle.REDSTONE, location, 20, 1, 1, 1, 0, new Particle.DustOptions(Color.fromRGB(255, 200, 1), 1));
                    int entities = 0;
                    for (Entity entity : entityList) {
                        if (entity instanceof LivingEntity) {
                            LivingEntity livingEntity = (LivingEntity) entity;
                            livingEntity.damage(20, player);
                            entities++;
                        }
                    }

                    if (player.getGameMode().equals(GameMode.SURVIVAL) || player.getGameMode().equals(GameMode.ADVENTURE)) {
                        chargeManager.setCharge(item, charge - (travelled + (entities * 3)));
                    }
                }
            }
        }
    }

}
