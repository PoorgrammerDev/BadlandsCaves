package me.fullpotato.badlandscaves.CustomItems.Crafting.Starlight;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.CustomItem;
import me.fullpotato.badlandscaves.Util.EnchantmentStorage;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class StarlightCharge implements Listener {
    private final BadlandsCaves plugin;
    private final StarlightArmor armor;
    private final StarlightTools tools;
    private final EnchantmentStorage enchantmentStorage;
    private final EnergyCore coreChecker;

    public StarlightCharge(BadlandsCaves plugin) {
        this.plugin = plugin;
        this.armor = new StarlightArmor(plugin);
        this.tools = new StarlightTools(plugin);
        enchantmentStorage = new EnchantmentStorage(plugin);
        coreChecker = new EnergyCore(plugin);
    }

    public void chargeRecipe() {
        final ItemStack item = plugin.getCustomItemManager().getItem(CustomItem.STARLIGHT_CHARGE_PLACEHOLDER);
        ShapelessRecipe recipe = new ShapelessRecipe(new NamespacedKey(plugin, "starlight_helmet_charge"), item);
        recipe.addIngredient(new RecipeChoice.MaterialChoice(Material.NETHERITE_HELMET, Material.NETHERITE_CHESTPLATE, Material.NETHERITE_LEGGINGS, Material.NETHERITE_BOOTS, Material.NETHERITE_SWORD, Material.NETHERITE_SHOVEL, Material.NETHERITE_PICKAXE, Material.NETHERITE_AXE, Material.CROSSBOW, Material.SHIELD, Material.FISHING_ROD));
        recipe.addIngredient(Material.KNOWLEDGE_BOOK);

        plugin.getServer().addRecipe(recipe);
    }

    @EventHandler
    public void checkChargingRecipes (PrepareItemCraftEvent event) {
        if (event.getRecipe() == null || event.getRecipe().getResult() == null) return;

        final ItemStack result = event.getRecipe().getResult();
        final ItemStack placeholder = plugin.getCustomItemManager().getItem(CustomItem.STARLIGHT_CHARGE_PLACEHOLDER);
        if (!result.isSimilar(placeholder)) return;

        if (event.getViewers().get(0) instanceof Player) {
            Player player = (Player) event.getViewers().get(0);
            if ((byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == (byte) 1) {
                event.getInventory().setResult(null);
                return;
            }
        }

        final ItemStack[] matrix = event.getInventory().getMatrix();

        ItemStack starlight = null;
        ItemStack energyCore = null;
        for (ItemStack item : matrix) {
            if (item != null) {
                if (coreChecker.isEnergyCore(item)) {
                    energyCore = item.clone();
                }
                else if (isStarlight(item)) {
                    starlight = item.clone();
                }
            }
        }

        if (starlight != null && energyCore != null) {
            int newCharge = coreChecker.getCharge(energyCore);

            setCharge(starlight, getCharge(starlight) + newCharge);
            event.getInventory().setResult(starlight);
            return;
        }
        event.getInventory().setResult(null);
    }

    @EventHandler
    public void depleteCharge (PlayerItemDamageEvent event) {
        ItemStack item = event.getItem();
        if (isStarlight(item)) {
            event.setCancelled(true);
            setCharge(item, getCharge(item) - event.getDamage());
        }
    }

    public boolean isStarlight(ItemStack item) {
        return armor.isStarlightArmor(item) || tools.isStarlightSaber(item) ||
                tools.isStarlightShield(item) || tools.isStarlightBlaster(item) ||
                tools.isStarlightPaxel(item) || tools.isStarlightSentry(item);
    }

    public int getCharge(ItemStack item) {
        if (isStarlight(item)) {
            if (item.hasItemMeta()) {
                ItemMeta meta = item.getItemMeta();
                if (meta != null) {
                    Integer charge = meta.getPersistentDataContainer().get(new NamespacedKey(plugin, "starlight_charge"), PersistentDataType.INTEGER);
                    if (charge != null) {
                        return charge;
                    }
                }
            }
        }
        return -1;
    }

    public int getMaxCharge(ItemStack item) {
        if (isStarlight(item)) {
            if (item.hasItemMeta()) {
                ItemMeta meta = item.getItemMeta();
                if (meta != null) {
                    Integer charge = meta.getPersistentDataContainer().get(new NamespacedKey(plugin, "starlight_max_charge"), PersistentDataType.INTEGER);
                    if (charge != null) {
                        return charge;
                    }
                }
            }
        }
        return -1;
    }

    public void setCharge (ItemStack item, int charge) {
        if (isStarlight(item)) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                List<String> lore = meta.getLore();

                if (lore != null && !lore.isEmpty()) {
                    int maxCharge = getMaxCharge(item);
                    int newCharge = Math.max(Math.min(charge, maxCharge), 0);
                    //change stat
                    meta.getPersistentDataContainer().set(new NamespacedKey(plugin, "starlight_charge"), PersistentDataType.INTEGER, newCharge);

                    //change lore
                    String[] chargeDesc = lore.get(0).split(" ");
                    chargeDesc[1] = newCharge + "";
                    lore.set(0, chargeDesc[0] + " " + chargeDesc[1] + " " + chargeDesc[2] + " " + chargeDesc[3]);
                    meta.setLore(lore);

                    //change "durability" bar
                    Damageable durabilityMeta = (Damageable) meta;

                    final int max = item.getType().getMaxDurability();
                    durabilityMeta.setDamage(Math.max(Math.min(max - (int) ((1.0 * newCharge / maxCharge) * max), max - 1), 1));

                    item.setItemMeta((ItemMeta) durabilityMeta);

                    //Update enchantments
                    if (newCharge <= 0) {
                        enchantmentStorage.disenchantItem(item);
                    }
                    else if (!meta.hasEnchants()) {
                        enchantmentStorage.loadEnchantments(item);
                    }
                }
            }
        }
    }

    public void setMaxCharge (ItemStack item, int charge) {
        if (isStarlight(item)) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                List<String> lore = meta.getLore();

                if (lore != null && !lore.isEmpty()) {
                    meta.getPersistentDataContainer().set(new NamespacedKey(plugin, "starlight_max_charge"), PersistentDataType.INTEGER, charge);

                    String[] chargeDesc = lore.get(0).split(" ");
                    chargeDesc[3] = charge + "";

                    lore.set(0, chargeDesc[0] + " " + chargeDesc[1] + " " + chargeDesc[2] + " " + chargeDesc[3]);
                    meta.setLore(lore);

                    //change "durability" bar
                    Damageable durabilityMeta = (Damageable) meta;

                    final int max = item.getType().getMaxDurability();
                    durabilityMeta.setDamage(Math.max(Math.min(max - (int) ((1.0 * getCharge(item) / charge) * max), max - 1), 1));

                    item.setItemMeta((ItemMeta) durabilityMeta);
                }
            }
        }
    }
}
