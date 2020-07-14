package me.fullpotato.badlandscaves.CustomItems.Using;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.Crafting.Starlight.StarlightTools;
import me.fullpotato.badlandscaves.CustomItems.CustomItem;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Random;

public class ShieldBlocking implements Listener {
    private final BadlandsCaves plugin;

    public ShieldBlocking(BadlandsCaves plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void shield(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            double damage = event.getDamage();
            if (player.isBlocking() && damage > 0 && event.getFinalDamage() <= 0) {
                ItemStack playersShield = player.getInventory().getItemInMainHand().getType().equals(Material.SHIELD) ? player.getInventory().getItemInMainHand() : player.getInventory().getItemInOffHand();

                final ItemStack stoneShield = CustomItem.STONE_SHIELD.getItem();
                final ItemStack ironShield = CustomItem.IRON_SHIELD.getItem();
                final ItemStack diamondShield = CustomItem.DIAMOND_SHIELD.getItem();
                final ItemStack netheriteShield = CustomItem.NETHERITE_SHIELD.getItem();
                final Random random = new Random();

                double modifier = 2;
                boolean ignored = false;
                if ((byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) != 1) {
                    if (playersShield.hasItemMeta()) {
                        ItemMeta meta = playersShield.getItemMeta();
                        if (meta.hasDisplayName()) {
                            if (meta.getDisplayName().equals(stoneShield.getItemMeta().getDisplayName()) && damage / 3.0 > 0) {
                                modifier = 3;
                            }
                            else if (meta.getDisplayName().equals(ironShield.getItemMeta().getDisplayName()) && damage / 5.0 > 0) {
                                modifier = 5;
                                ignored = (damage / modifier < 1 && random.nextBoolean());
                            }
                            else if (meta.getDisplayName().equals(diamondShield.getItemMeta().getDisplayName()) && damage / 7.0 > 0) {
                                modifier = 7;
                                ignored = (damage / modifier < 1 || random.nextInt(100) < 25);
                            }
                            else if (meta.getDisplayName().equals(netheriteShield.getItemMeta().getDisplayName()) && damage / 9.0 > 0) {
                                modifier = 9;
                                ignored = (damage / modifier < 1 || random.nextBoolean());
                            }
                            else {
                                StarlightTools starlightTools = new StarlightTools(plugin);
                                if (starlightTools.isStarlightShield(playersShield)) {
                                    modifier = 15;
                                    ignored = (damage / modifier < 1 || random.nextInt(100) < 75);
                                }
                            }
                        }
                    }
                }

                player.setCooldown(Material.SHIELD, Math.max((int) (( damage) * 10 / modifier), player.getCooldown(Material.SHIELD)));
                if (!ignored) {
                    Vector velocity = player.getVelocity();
                    player.damage(damage / modifier);

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            player.setVelocity(velocity);
                        }
                    }.runTaskLater(plugin, 1);
                }
            }
        }
    }
}
