package me.fullpotato.badlandscaves.badlandscaves.Events.CustomItems.Using;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.Random;

public class useFishingCrate implements Listener {
    private BadlandsCaves plugin;
    public useFishingCrate (BadlandsCaves bcav) {
        plugin = bcav;
    }

    @EventHandler
    public void useFishingCrate (PlayerInteractEvent event) {
        Action action = event.getAction();

        if (action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK)) {
            ItemStack item = event.getItem();
            if (item != null) {
                ItemStack fishing_crate = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.fishing_crate").getValues(true));
                if (item.isSimilar(fishing_crate)) {
                    event.setCancelled(true);
                    Player player = event.getPlayer();
                    Location location = player.getLocation();
                    Inventory inventory = player.getInventory();

                    item.setAmount(item.getAmount() - 1);

                    int default_bound = plugin.getConfig().getInt("game_values.fish_or_treasure_base");

                    int luck_effect = 0;
                    if (player.hasPotionEffect(PotionEffectType.LUCK)) {
                        luck_effect = player.getPotionEffect(PotionEffectType.LUCK).getAmplifier();
                    }

                    int fish_bound = plugin.getConfig().getInt("game_values.fish_bound_base");
                    int fish_random = new Random().nextInt(fish_bound);

                    int luck_mod = 0;
                    if (luck_effect > 0) {
                        luck_mod = new Random().nextInt(3 * luck_effect);
                    }
                    int fish = fish_random + luck_mod;

                    for (int a = 0; a <= fish; a++) {
                        boolean isSalmon = new Random().nextBoolean();
                        boolean isCooked = new Random().nextBoolean();

                        if (isSalmon) {
                            if (isCooked) {
                                inventory.addItem(new ItemStack(Material.COOKED_SALMON));
                            }
                            else {
                                inventory.addItem(new ItemStack(Material.SALMON));
                            }
                        }
                        else {
                            if (isCooked) {
                                inventory.addItem(new ItemStack(Material.COOKED_COD));
                            }
                            else {
                                inventory.addItem(new ItemStack(Material.COD));
                            }
                        }
                    }

                    location.setY(location.getY() + 1);
                    player.playSound(location, Sound.BLOCK_BARREL_OPEN, 1, 1);
                    player.spawnParticle(Particle.NAUTILUS, location, 50, 0.5, 0.5, 0.5);
                }
            }
        }
    }
}