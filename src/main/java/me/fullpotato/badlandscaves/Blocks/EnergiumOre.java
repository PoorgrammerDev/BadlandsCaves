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
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Random;

public class EnergiumOre implements Listener {
    private final BadlandsCaves plugin;
    private final Random random;

    private final HashSet<Material> validTools;

    public EnergiumOre(BadlandsCaves plugin, Random random) {
        this.plugin = plugin;
        this.random = random;

        validTools = new HashSet<>();
        validTools.add(Material.IRON_PICKAXE);
        validTools.add(Material.DIAMOND_PICKAXE);
        validTools.add(Material.NETHERITE_PICKAXE);
    }

    @EventHandler
    public void breakEnergium (BlockBreakEvent event) {
        if (event.isCancelled()) return;
        if (!event.getBlock().getType().equals(Material.DEAD_BRAIN_CORAL_BLOCK)) return;

        final Player player = event.getPlayer();
        if (!player.getGameMode().equals(GameMode.SURVIVAL) && !player.getGameMode().equals(GameMode.ADVENTURE)) return;

        final EntityEquipment equipment = player.getEquipment();

        if (equipment != null) {
            final ItemStack tool = player.getEquipment().getItemInMainHand();

            if (validTools.contains(tool.getType())) {
                //Calculate # of items to drop, taking fortune into account
                final ItemStack energium = plugin.getCustomItemManager().getItem(CustomItem.ENERGIUM);
                final int maxCount = 2 + tool.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS);
                final int count = random.nextInt(maxCount - 1) + 1;

                //Drop items and experience
                event.setDropItems(false);
                energium.setAmount(count);
                event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), energium);
                event.setExpToDrop(count * 16);
                return;
            }
        }

        event.getPlayer().sendMessage("§cYou need a stronger tool.");
        event.setCancelled(true);
    }

    @EventHandler
    public void preventExplode (EntityExplodeEvent event) {
        event.blockList().removeIf(block -> block.getType().equals(Material.DEAD_BRAIN_CORAL_BLOCK));
    }

    @EventHandler
    public void preventPistonExtend (BlockPistonExtendEvent event) {
        for (Block block : event.getBlocks()) {
            if (block.getType().equals(Material.DEAD_BRAIN_CORAL_BLOCK)) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void preventPistonRetract (BlockPistonRetractEvent event) {
        for (Block block : event.getBlocks()) {
            if (block.getType().equals(Material.DEAD_BRAIN_CORAL_BLOCK)) {
                event.setCancelled(true);
                return;
            }
        }
    }
}
