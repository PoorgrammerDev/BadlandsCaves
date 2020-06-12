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
import org.bukkit.inventory.meta.CrossbowMeta;
import org.bukkit.inventory.meta.ItemMeta;
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
                if (item.hasItemMeta()) {
                    if (item.getItemMeta() instanceof CrossbowMeta) {
                        CrossbowMeta meta = (CrossbowMeta) item.getItemMeta();
                        if (meta != null) {
                            if (meta.getChargedProjectiles() == null || meta.getChargedProjectiles().isEmpty()) {
                                ItemStack arrow = new ItemStack(Material.ARROW);
                                ItemMeta arrow_meta = arrow.getItemMeta();
                                if (arrow_meta != null) {
                                    arrow_meta.setDisplayName("§r");
                                    arrow.setItemMeta(arrow_meta);

                                    meta.addChargedProjectile(arrow);
                                    item.setItemMeta(meta);
                                }
                            }
                        }
                    }
                }


                Player player = event.getPlayer();
                int charge = chargeManager.getCharge(item);
                event.setCancelled(true);
                boolean hardmode = plugin.getConfig().getBoolean("system.hardmode");
                if (hardmode && (byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == (byte) 0 && charge > 0) {
                    final int range = 50;
                    int travelled = 0;

                    World world = player.getWorld();
                    Location location = player.getEyeLocation();
                    Collection<Entity> entityList = world.getNearbyEntities(location, 0.2, 0.2, 0.2);

                    BlockIterator iterator = new BlockIterator(location);
                    while (travelled < range && iterator.hasNext()) {
                        Block block = iterator.next();
                        if (block.isPassable()) {
                            location = block.getLocation().add(0.5, 0.5, 0.5);
                            entityList = world.getNearbyEntities(location, 0.2, 0.2, 0.2);
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

                    double shortestDistance = Double.MAX_VALUE;
                    LivingEntity target = null;
                    for (Entity entity : entityList) {
                        if (entity instanceof LivingEntity) {
                            LivingEntity livingEntity = (LivingEntity) entity;
                            double distance = livingEntity.getLocation().distanceSquared(location);
                            if (distance < shortestDistance) {
                                shortestDistance = distance;
                                target = livingEntity;
                            }
                        }
                    }

                    final int damage = 20;
                    if (target != null) {
                        target.damage(damage, player);
                    }


                    if (player.getGameMode().equals(GameMode.SURVIVAL) || player.getGameMode().equals(GameMode.ADVENTURE)) {
                        chargeManager.setCharge(item, charge - ((travelled / 2) + (target != null ? damage / 2 : 0)));
                    }
                }
            }
        }
    }

}
