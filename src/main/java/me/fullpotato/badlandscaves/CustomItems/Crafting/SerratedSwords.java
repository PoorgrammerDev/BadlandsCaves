package me.fullpotato.badlandscaves.CustomItems.Crafting;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.Loot.TreasureGear;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SerratedSwords implements Listener {
    private final BadlandsCaves plugin;
    private final String serrated_lore = "§cSerrated";
    private final Material[] swords = {
            Material.WOODEN_SWORD,
            Material.STONE_SWORD,
            Material.IRON_SWORD,
            Material.GOLDEN_SWORD,
            Material.DIAMOND_SWORD,
            Material.NETHERITE_SWORD,
    };

    public SerratedSwords(BadlandsCaves plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void makeSerrated(PlayerInteractEvent event) {
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            if (event.getClickedBlock() != null && event.getClickedBlock().getType().equals(Material.STONECUTTER)) {
                final Player player = event.getPlayer();
                if ((byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) != 1) {
                    if (event.getItem() != null) {
                        final ItemStack item = event.getItem();
                        if (Arrays.asList(swords).contains(item.getType())) {
                            Voltshock voltshock = new Voltshock(plugin);
                            Corrosive corrosive = new Corrosive(plugin);
                            TreasureGear treasureGear = new TreasureGear();
                            if (!isSerrated(item) && !voltshock.isVoltshock(item) && !corrosive.isCorrosive(item) && !treasureGear.isTreasureGear(item)) {
                                Damageable damageable_meta = (Damageable) item.getItemMeta();
                                int max_durability = item.getType().getMaxDurability();
                                if (damageable_meta != null && damageable_meta.getDamage() < max_durability * 0.75) {
                                    event.setCancelled(true);
                                    damageable_meta.setDamage((int) (damageable_meta.getDamage() + max_durability * 0.25));

                                    ItemMeta meta = (ItemMeta) damageable_meta;
                                    ArrayList<String> lore = new ArrayList<>();
                                    lore.add(serrated_lore);
                                    meta.setLore(lore);
                                    meta.setCustomModelData(124);

                                    item.setItemMeta(meta);
                                    player.playSound(event.getClickedBlock().getLocation(), Sound.BLOCK_GRINDSTONE_USE, SoundCategory.BLOCKS, 1, 1);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public boolean isSerrated(ItemStack item) {
        if (Arrays.asList(swords).contains(item.getType())) {
            if (item.hasItemMeta()) {
                ItemMeta meta = item.getItemMeta();
                if (meta != null && meta.hasLore()) {
                    List<String> lore = meta.getLore();
                    if (lore != null && lore.size() >= 1) {
                        return lore.get(0).equalsIgnoreCase(serrated_lore);
                    }
                }
            }
        }
        return false;
    }
}
