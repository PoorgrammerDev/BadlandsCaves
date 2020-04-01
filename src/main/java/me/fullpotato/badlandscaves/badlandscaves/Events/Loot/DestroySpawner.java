package me.fullpotato.badlandscaves.badlandscaves.Events.Loot;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Random;

public class DestroySpawner implements Listener {
    private BadlandsCaves plugin;

    public DestroySpawner(BadlandsCaves plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void breakSpawner (BlockBreakEvent event) {
        final Block block = event.getBlock();
        if (block.getType().equals(Material.SPAWNER)) {
            final Player player = event.getPlayer();

            ArrayList<Material> pickaxes = new ArrayList<>();
            pickaxes.add(Material.WOODEN_PICKAXE);
            pickaxes.add(Material.STONE_PICKAXE);
            pickaxes.add(Material.IRON_PICKAXE);
            pickaxes.add(Material.GOLDEN_PICKAXE);
            pickaxes.add(Material.DIAMOND_PICKAXE);


            if (pickaxes.contains(player.getInventory().getItemInMainHand().getType())) {
                Random random = new Random();
                final ItemStack pickaxe = player.getInventory().getItemInMainHand();
                final ItemStack rune = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.rune").getValues(true));
                final int fortune = pickaxe.hasItemMeta() && pickaxe.getItemMeta().hasEnchant(Enchantment.LOOT_BONUS_BLOCKS) ? pickaxe.getItemMeta().getEnchantLevel(Enchantment.LOOT_BONUS_BLOCKS) : 0;
                final double chance = 100 * (Math.pow(fortune / 5.0, 1.25) + 0.2);
                final int rolls = random.nextInt(fortune + 1) + 1;

                for (int i = 0; i < rolls; i++) {
                 if (random.nextInt(100) < chance) {
                     player.getWorld().dropItemNaturally(block.getLocation(), rune);
                 }
                }
            }
        }
    }
}
