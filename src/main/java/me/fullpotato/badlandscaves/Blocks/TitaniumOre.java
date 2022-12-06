package me.fullpotato.badlandscaves.Blocks;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.CustomItem;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class TitaniumOre implements Listener {
    private final BadlandsCaves plugin;
    private final Random random;

    public TitaniumOre(BadlandsCaves plugin, Random random) {
        this.plugin = plugin;
        this.random = random;
    }

    @EventHandler
    public void breakTitanium (BlockBreakEvent event) {
        if (event.getBlock().getType().equals(Material.DEAD_TUBE_CORAL_BLOCK)) {
            Player player = event.getPlayer();
            if (player.getGameMode().equals(GameMode.SURVIVAL) || player.getGameMode().equals(GameMode.ADVENTURE)) {
                boolean hardmode = plugin.getSystemConfig().getBoolean("hardmode");
                if (hardmode) {
                    ItemStack tool = player.getEquipment().getItemInMainHand();

                    if (tool.getType().equals(Material.DIAMOND_PICKAXE) ||
                            tool.getType().equals(Material.NETHERITE_PICKAXE)) {
                        final int fortune = tool.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS);
                        int count = 1;

                        if (fortune > 0) {
                            for (int i = 0; i < fortune; i++) {
                                if (random.nextBoolean()) count++;
                                else break;
                            }
                        }
                        event.setDropItems(false);

                        ItemStack titanium = plugin.getCustomItemManager().getItem(CustomItem.TITANIUM_INGOT);
                        titanium.setAmount(count);
                        event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), titanium);
                        event.setExpToDrop(count * 16);
                    } else {
                        event.getPlayer().sendMessage("§cYou need a stronger tool.");
                        event.setCancelled(true);
                    }
                } else {
                    event.getPlayer().sendMessage("§cYou cannot break this block yet.");
                    event.setCancelled(true);
                }
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
