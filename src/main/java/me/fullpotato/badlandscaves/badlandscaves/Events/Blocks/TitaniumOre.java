package me.fullpotato.badlandscaves.badlandscaves.Events.Blocks;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Random;

public class TitaniumOre implements Listener {
    private BadlandsCaves plugin;

    public TitaniumOre(BadlandsCaves plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void breakTitanium (BlockBreakEvent event) {
        if (event.getBlock().getType().equals(Material.DEAD_TUBE_CORAL_BLOCK)) {
            boolean hardmode = plugin.getConfig().getBoolean("game_values.hardmode");
            if (hardmode) {
                Player player = event.getPlayer();
                ItemStack tool = player.getEquipment().getItemInMainHand();

                if (player.getGameMode().equals(GameMode.SURVIVAL) || player.getGameMode().equals(GameMode.ADVENTURE)) {
                    if (tool.getType().equals(Material.DIAMOND_PICKAXE)) {
                        final int fortune = tool.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS);
                        int count = 1;

                        final Random random = new Random();
                        if (fortune > 0) {
                            for (int i = 0; i < fortune; i++) {
                                if (random.nextBoolean()) count++;
                            }
                        }
                        event.setDropItems(false);

                        ItemStack titanium = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.titanium_fragment").getValues(true));
                        titanium.setAmount(count);
                        event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), titanium);
                        event.setExpToDrop(count * 16);
                    }
                    else {
                        event.getPlayer().sendMessage("§cYou need a stronger tool.");
                        event.setCancelled(true);
                    }
                }
            }
            else {
                event.getPlayer().sendMessage("§cYou cannot break this block yet.");
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void preventExplode (EntityExplodeEvent event) {
        event.blockList().removeIf(block -> block.getType().equals(Material.DEAD_TUBE_CORAL_BLOCK));
    }

    @EventHandler
    public void preventPistonExtend (BlockPistonExtendEvent event) {
        for (Block block : event.getBlocks()) {
            if (block.getType().equals(Material.DEAD_TUBE_CORAL_BLOCK)) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void preventPistonRetract (BlockPistonRetractEvent event) {
        for (Block block : event.getBlocks()) {
            if (block.getType().equals(Material.DEAD_TUBE_CORAL_BLOCK)) {
                event.setCancelled(true);
                return;
            }
        }
    }
}
