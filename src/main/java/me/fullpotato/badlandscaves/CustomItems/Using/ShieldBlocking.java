package me.fullpotato.badlandscaves.CustomItems.Using;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.Crafting.Starlight.StarlightCharge;
import me.fullpotato.badlandscaves.CustomItems.Crafting.Starlight.StarlightTools;
import me.fullpotato.badlandscaves.CustomItems.CustomItem;
import me.fullpotato.badlandscaves.CustomItems.CustomItemManager;
import me.fullpotato.badlandscaves.CustomItems.Using.Starlight.Nebulites.Nebulite;
import me.fullpotato.badlandscaves.CustomItems.Using.Starlight.Nebulites.NebuliteManager;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import org.bukkit.*;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
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
    private final CustomItemManager customItemManager;
    private final StarlightTools starlightTools;
    private final StarlightCharge starlightCharge;
    private final NebuliteManager nebuliteManager;

    public ShieldBlocking(BadlandsCaves plugin, StarlightTools starlightTools, StarlightCharge starlightCharge, NebuliteManager nebuliteManager) {
        this.plugin = plugin;
        customItemManager = plugin.getCustomItemManager();
        this.starlightTools = starlightTools;
        this.starlightCharge = starlightCharge;
        this.nebuliteManager = nebuliteManager;
    }

    @EventHandler
    public void shield(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            double damage = event.getDamage();
            if (player.isBlocking() && damage > 0 && event.getFinalDamage() <= 0) {
                ItemStack playersShield = player.getInventory().getItemInMainHand().getType().equals(Material.SHIELD) ? player.getInventory().getItemInMainHand() : player.getInventory().getItemInOffHand();

                final ItemStack stoneShield = customItemManager.getItem(CustomItem.STONE_SHIELD);
                final ItemStack ironShield = customItemManager.getItem(CustomItem.IRON_SHIELD);
                final ItemStack diamondShield = customItemManager.getItem(CustomItem.DIAMOND_SHIELD);
                final ItemStack netheriteShield = customItemManager.getItem(CustomItem.NETHERITE_SHIELD);
                final Random random = new Random();

                double modifier = 2;
                boolean damageIgnored = false;
                if ((byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) != 1) {
                    if (playersShield.hasItemMeta()) {
                        ItemMeta meta = playersShield.getItemMeta();
                        if (meta.hasDisplayName()) {
                            if (meta.getDisplayName().equals(stoneShield.getItemMeta().getDisplayName()) && damage / 3.0 > 0) {
                                modifier = 3;
                            }
                            else if (meta.getDisplayName().equals(ironShield.getItemMeta().getDisplayName()) && damage / 5.0 > 0) {
                                modifier = 5;
                                damageIgnored = (damage / modifier < 1 && random.nextBoolean());
                            }
                            else if (meta.getDisplayName().equals(diamondShield.getItemMeta().getDisplayName()) && damage / 7.0 > 0) {
                                modifier = 7;
                                damageIgnored = (damage / modifier < 1 || random.nextInt(100) < 25);
                            }
                            else if (meta.getDisplayName().equals(netheriteShield.getItemMeta().getDisplayName()) && damage / 9.0 > 0) {
                                modifier = 9;
                                damageIgnored = (damage / modifier < 1 || random.nextBoolean());
                            }
                            else {
                                if (starlightTools.isStarlightShield(playersShield)) {
                                    final Nebulite[] nebulites = nebuliteManager.getNebulites(playersShield);

                                    for (Nebulite nebulite : nebulites) {
                                        if (nebulite.equals(Nebulite.HARDENED_DEFENSE)) damageIgnored = true;
                                        else if (nebulite.equals(Nebulite.ENERGY_CONVERTER)) {
                                            if (event.getDamager() instanceof Projectile) {
                                                Projectile projectile = (Projectile) event.getDamager();
                                                absorbProjectile(playersShield, projectile);
                                            }
                                        }
                                        else if (nebulite.equals(Nebulite.REFLECTOR)) {
                                            if (event.getDamager() instanceof Projectile) {
                                                Projectile projectile = (Projectile) event.getDamager();
                                                deflectProjectile(player, projectile);
                                            }
                                        }
                                    }

                                    modifier = 15;
                                    if (!damageIgnored) damageIgnored = (damage / modifier < 1 || random.nextInt(100) < 75);
                                }
                            }
                        }
                    }
                }

                final Vector velocity = player.getVelocity().clone();
                if (!damageIgnored) {
                    player.damage(damage / modifier);
                    player.setCooldown(Material.SHIELD, Math.max((int) (( damage) * 10 / modifier), player.getCooldown(Material.SHIELD)));
                }

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        player.setVelocity(velocity);
                    }
                }.runTaskLater(plugin, 1);
            }
        }
    }

    public void deflectProjectile (Player player, Projectile projectile) {
        projectile.remove();

        final World world = player.getWorld();
        final Location location = projectile.getLocation();
        if (location.getWorld() == null || !location.getWorld().equals(world)) return;

        final Vector vector = player.getEyeLocation().getDirection().multiply(2);
        final Projectile clone = (Projectile) world.spawnEntity(location, projectile.getType());
        clone.setShooter(player);
        clone.setVelocity(vector);


        new BukkitRunnable() {
            @Override
            public void run() {
                if (clone.isDead() || clone.isOnGround() || player.isDead() || !player.isOnline() || !clone.getWorld().equals(player.getWorld()) || clone.getLocation().distanceSquared(player.getLocation()) > 625) {
                    clone.remove();
                    this.cancel();
                    return;
                }

                if (clone instanceof AbstractArrow) {
                    AbstractArrow abstractArrow = (AbstractArrow) clone;
                    if (abstractArrow.isInBlock()) {
                        abstractArrow.remove();
                        this.cancel();
                        return;
                    }
                }

                world.spawnParticle(Particle.REDSTONE, clone.getLocation(), 1, new Particle.DustOptions(Color.fromRGB(176, 240, 71), 1));
            }
        }.runTaskTimer(plugin, 0, 0);
    }

    public void absorbProjectile (ItemStack shield, Projectile projectile) {
        final int length = (int) (projectile.getVelocity().lengthSquared() * 1.5);
        projectile.remove();

        if (length > 0 && starlightTools.isStarlightShield(shield)) starlightCharge.setCharge(shield, starlightCharge.getCharge(shield) + length);
    }
}
