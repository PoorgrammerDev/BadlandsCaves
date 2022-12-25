package me.fullpotato.badlandscaves.Other;

import me.fullpotato.badlandscaves.BadlandsCaves;
import net.md_5.bungee.api.ChatColor;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.AbstractVillager;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.VillagerAcquireTradeEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerItemMendEvent;
import org.bukkit.event.world.LootGenerateEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class NoMending implements Listener {
    private final BadlandsCaves plugin;

    public NoMending(BadlandsCaves plugin) {
        this.plugin = plugin;
    }

    /**
     * Forces anvil interactions with Mending or Unbreaking 5 books to produce an Unbreaking 5 result
     */
    @EventHandler
    public void AnvilFix(PrepareAnvilEvent event) {
        if (event.getResult() == null || event.getResult().getType() == Material.AIR) return;

        //Checks if any of the ingredients contians Mending or Unbreaking 5
        for (final ItemStack item : event.getInventory()) {
            if (item.hasItemMeta()) {
                final ItemMeta meta = item.getItemMeta();
                boolean modifyResult = false;

                //If the item is an enchanted book -- check its stored enchantment
                if (meta instanceof EnchantmentStorageMeta) {
                    final EnchantmentStorageMeta bookMeta = (EnchantmentStorageMeta) meta;
                    if (bookMeta != null) {
                        modifyResult = (
                            bookMeta.hasStoredEnchant(Enchantment.MENDING) ||
                            (
                                bookMeta.hasStoredEnchant(Enchantment.DURABILITY) &&
                                bookMeta.getStoredEnchantLevel(Enchantment.DURABILITY) == 5
                            )
                        );
                    }
                }

                //If the item is not a book -- check its actual enchantment
                else if (meta != null) {
                    modifyResult = (
                        meta.hasEnchant(Enchantment.MENDING) ||
                        (
                            meta.hasEnchant(Enchantment.DURABILITY) &&
                            meta.getEnchantLevel(Enchantment.DURABILITY) == 5
                        )
                    );
                }

                //If it meets either condition, modify the resulting output
                if (modifyResult) {
                    final ItemStack result = event.getResult();
                    final ItemMeta resultMeta = result.getItemMeta();
                    
                    //if the result is another enchanting book for whatever reason, then change stored enchantment instaed of enchantment
                    if (resultMeta instanceof EnchantmentStorageMeta) {
                        final EnchantmentStorageMeta bookMeta = (EnchantmentStorageMeta) resultMeta;
                        if (bookMeta != null) {
                            bookMeta.removeStoredEnchant(Enchantment.MENDING);
                            bookMeta.addStoredEnchant(Enchantment.DURABILITY, 5, true);
                    
                            final ArrayList<String> lore = new ArrayList<>();
                            lore.add(ChatColor.DARK_AQUA + "Replaces Mending Enchantment");
                            bookMeta.setLore(lore);
                    
                            result.setItemMeta(bookMeta);
                        }
                    }

                    //removes mending and substitutes unb5 -- this fixes the unb3 cap bug and makes mending autoconvert to unb5 in the anvil
                    else if (resultMeta != null) {
                        resultMeta.removeEnchant(Enchantment.MENDING);
                        resultMeta.addEnchant(Enchantment.DURABILITY, 5, true);
                        result.setItemMeta(resultMeta);
                    }

                    event.setResult(result);
                    return;
                }
            }
        }
            
    }

    @EventHandler
    public void ConvertTrade(VillagerAcquireTradeEvent event) {
        MerchantRecipe original = event.getRecipe();
        ItemStack result = original.getResult();

        //if mending, replace item. if not, return
        if (!ReplaceMending(result)) return;

        //copies over the original recipe but with the converted result
        final MerchantRecipe recipe = new MerchantRecipe(result, original.getUses(), original.getMaxUses(), original.hasExperienceReward(), original.getVillagerExperience(), original.getPriceMultiplier());
        recipe.setIngredients(original.getIngredients());
        event.setRecipe(recipe);
    }

    @EventHandler
    public void ConvertFishing(PlayerFishEvent event) {
        if (event.getState().equals(PlayerFishEvent.State.CAUGHT_FISH)) {
            if (event.getCaught() instanceof Item) {
                final Item item = (Item) event.getCaught();
                final ItemStack itemStack = item.getItemStack();
                ReplaceMending(itemStack);
                item.setItemStack(itemStack);
            }
        }
    }

    /**
     * Replaces Mending when an entity dies and was holding it
     */
    @EventHandler
    public void ConvertDeathDrops(EntityDeathEvent event) {
        event.getDrops().forEach(this::ReplaceMending);
    }

    /**
     * Replaces Mending on loot generation (dungeon chest, etc.)
     */
    @EventHandler
    public void ConvertLootGen(LootGenerateEvent event) {
        final List<ItemStack> loot = event.getLoot();
        loot.forEach(this::ReplaceMending);
        new BukkitRunnable() {
            @Override
            public void run() {
                event.setLoot(loot);
            }
        }.runTaskLater(plugin, 1);
    }

    /**
     * Replaces Mending upon pickup
     */
    @EventHandler
    public void ConvertOnPickup (EntityPickupItemEvent event) {
        if (event.getEntity() instanceof Player) {
            final Item itemEntity = event.getItem();
            final ItemStack item = itemEntity.getItemStack();
            ReplaceMending(item);
            itemEntity.setItemStack(item);
        }
    }

    /**
     * Disables the actual item mend event, rendering Mending useless even if the player gets it
     */
    @EventHandler
    public void CancelMendingRepair(PlayerItemMendEvent event) {
        event.setCancelled(true);
    }

    /**
     * Checks if an item has Mending and replaces it
     */
    public boolean ReplaceMending(ItemStack item) {
        final ItemMeta meta = item.getItemMeta();
        if (item.getType().equals(Material.ENCHANTED_BOOK)) {
            if (meta instanceof EnchantmentStorageMeta) {
                final EnchantmentStorageMeta enchantmentStorageMeta = (EnchantmentStorageMeta) item.getItemMeta();
                if (enchantmentStorageMeta.hasStoredEnchant(Enchantment.MENDING)) {
                    enchantmentStorageMeta.removeStoredEnchant(Enchantment.MENDING);
                    enchantmentStorageMeta.addStoredEnchant(Enchantment.DURABILITY, 5, true);
                    
                    //Adds description to explain unbrekaing 5 
                    final ArrayList<String> lore = new ArrayList<>();
                    lore.add(ChatColor.DARK_AQUA + "Replaces Mending Enchantment");
                    enchantmentStorageMeta.setLore(lore);

                    item.setItemMeta(enchantmentStorageMeta);
                    return true;
                }
            }
        }
        else {
            if (meta != null && meta.hasEnchant(Enchantment.MENDING)) {
                meta.removeEnchant(Enchantment.MENDING);
                meta.addEnchant(Enchantment.DURABILITY, 5, true);
                item.setItemMeta(meta);
                return true;
            }
        }
        return false;
    }
}
