package me.fullpotato.badlandscaves.Loot;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.CustomItem;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

public class UseFishingCrate implements Listener {
    private final BadlandsCaves plugin;
    public UseFishingCrate(BadlandsCaves bcav) {
        plugin = bcav;
    }

    @EventHandler
    public void useFishingCrate (PlayerInteractEvent event) {
        Action action = event.getAction();

        if (action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK)) {
            ItemStack item = event.getItem();
            if (item != null) {
                final ItemStack fishing_crate = plugin.getCustomItemManager().getItem(CustomItem.FISHING_CRATE);
                final ItemStack fishing_crate_hm = plugin.getCustomItemManager().getItem(CustomItem.FISHING_CRATE_HARDMODE);

                if (item.isSimilar(fishing_crate) || item.isSimilar(fishing_crate_hm)) {
                    event.setCancelled(true);
                    final Player player = event.getPlayer();
                    final World world = player.getWorld();
                    final Location location = player.getLocation();
                    final Inventory inventory = player.getInventory();
                    final Random random = new Random();
                    final boolean hardmode = item.isSimilar(fishing_crate_hm);

                    item.setAmount(item.getAmount() - 1);

                    final double player_luck = player.getAttribute(Attribute.GENERIC_LUCK) != null ? player.getAttribute(Attribute.GENERIC_LUCK).getValue() : 0;
                    final int chaos = plugin.getSystemConfig().getInt("chaos_level");
                    final int treasure_base = plugin.getOptionsConfig().getInt("fishing_crate_treasure_base");

                    final double treasure_chance = (100.0 * Math.pow(2.0, player_luck - 8.0)) + treasure_base + (chaos / 20.0);

                    if (random.nextInt(100) < treasure_chance) {

                        LootContext.Builder builder = new LootContext.Builder(location);
                        builder.luck((float) player_luck);
                        builder.killer(player);
                        LootContext lootContext = builder.build();


                        FishingCrateTreasureTable treasureTable = new FishingCrateTreasureTable(plugin, player, hardmode);
                        Collection<ItemStack> collection = treasureTable.populateLoot(random, lootContext);
                        ArrayList<ItemStack> treasure_list = (ArrayList<ItemStack>) collection;

                        for (ItemStack treasure : treasure_list) {
                            if (inventory.firstEmpty() == -1) {
                                world.dropItemNaturally(location, treasure);
                            }
                            else {
                                inventory.addItem(treasure);

                            }
                        }
                        player.playSound(location, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.BLOCKS, 1, 0.7F);
                    }
                    else {
                        final int fish = random.nextInt(8) + (int) (player_luck / 2) + 8;

                        final int salmon_count = random.nextInt(fish);
                        final int cod_amount = fish - salmon_count;

                        if (salmon_count > 0) {
                            final ItemStack salmon = new ItemStack(Material.SALMON, salmon_count);
                            if (inventory.firstEmpty() == -1) {
                                world.dropItemNaturally(location, salmon);
                            }
                            else {
                                inventory.addItem(salmon);
                            }

                        }

                        if (cod_amount > 0) {
                            final ItemStack cod = new ItemStack(Material.COD, cod_amount);
                            if (inventory.firstEmpty() == -1) {
                                world.dropItemNaturally(location,cod);
                            }
                            else {
                                inventory.addItem(cod);
                            }
                        }
                    }

                    location.setY(location.getY() + 1);
                    world.playSound(location, Sound.BLOCK_BARREL_OPEN, SoundCategory.BLOCKS, 1, 1);
                    player.spawnParticle(Particle.NAUTILUS, location, 50, 0.5, 0.5, 0.5);
                }
            }
        }
    }
}
