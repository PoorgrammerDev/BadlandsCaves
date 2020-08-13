package me.fullpotato.badlandscaves.CustomItems.Crafting.Starlight;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.Crafting.MatchCrafting;
import me.fullpotato.badlandscaves.CustomItems.CustomItem;
import me.fullpotato.badlandscaves.CustomItems.CustomItemManager;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

public class StarlightComponents extends MatchCrafting implements Listener {
    private final BadlandsCaves plugin;
    private final CustomItemManager customItemManager;

    public StarlightComponents(BadlandsCaves plugin) {
        this.plugin = plugin;
        customItemManager = plugin.getCustomItemManager();
    }

    public void craftBinding () {
        ItemStack binding = customItemManager.getItem(CustomItem.BINDING);
        binding.setAmount(8);

        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, "raw_binding"), binding);
        recipe.shape("#@", "@#");

        recipe.setIngredient('@', Material.GRAVEL);
        recipe.setIngredient('#', new RecipeChoice.MaterialChoice(Material.SAND, Material.RED_SAND));

        plugin.getServer().addRecipe(recipe);
    }

    public void craftGoldCable () {
        ItemStack cable = customItemManager.getItem(CustomItem.GOLDEN_CABLE);
        cable.setAmount(6);

        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, "golden_cable"), cable);
        recipe.shape("###", "@@@", "###");
        recipe.setIngredient('#', Material.COMMAND_BLOCK);
        recipe.setIngredient('@', Material.GOLD_INGOT);

        plugin.getServer().addRecipe(recipe);
    }

    public void craftNetherStarFragment() {
        ItemStack fragment = customItemManager.getItem(CustomItem.NETHER_STAR_FRAGMENT);
        fragment.setAmount(4);

        ShapelessRecipe recipe = new ShapelessRecipe(new NamespacedKey(plugin, "nether_star_fragment"), fragment);
        recipe.addIngredient(Material.NETHER_STAR);

        plugin.getServer().addRecipe(recipe);
    }

    public void craftStarlightCircuit () {
        ItemStack circuit = customItemManager.getItem(CustomItem.STARLIGHT_CIRCUIT);

        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, "starlight_circuit"), circuit);
        recipe.shape("---", "#@#", "---");

        recipe.setIngredient('-', Material.COMMAND_BLOCK);
        recipe.setIngredient('#', Material.REDSTONE);
        recipe.setIngredient('@', Material.COMMAND_BLOCK);

        plugin.getServer().addRecipe(recipe);
    }

    public void craftStarlightBattery() {
        ItemStack battery = customItemManager.getItem(CustomItem.STARLIGHT_BATTERY);

        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, "starlight_battery"), battery);
        recipe.shape(" % ", "#@#", "#@#");

        recipe.setIngredient('%', Material.COMMAND_BLOCK);
        recipe.setIngredient('#', Material.COMMAND_BLOCK);
        recipe.setIngredient('@', Material.COMMAND_BLOCK);

        plugin.getServer().addRecipe(recipe);
    }

    public void craftStarlightModule() {
        ItemStack module = customItemManager.getItem(CustomItem.STARLIGHT_MODULE);

        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, "starlight_module"), module);
        recipe.shape("###", "#%#", "#@#");
        recipe.setIngredient('#', Material.COMMAND_BLOCK);
        recipe.setIngredient('@', Material.STRUCTURE_BLOCK);
        recipe.setIngredient('%', Material.STRUCTURE_BLOCK);

        plugin.getServer().addRecipe(recipe);
    }

    public void craftEnergium() {
        ItemStack energium = customItemManager.getItem(CustomItem.ENERGIUM);
        energium.setAmount(4);

        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, "energium"), energium);
        recipe.shape("#@", "@#");

        recipe.setIngredient('#', Material.REDSTONE);
        recipe.setIngredient('@', Material.DIAMOND);

        plugin.getServer().addRecipe(recipe);
    }

    public void craftPhotonEmitter() {
        ItemStack photonEmitter = customItemManager.getItem(CustomItem.PHOTON_EMITTER);

        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, "photon_emitter"), photonEmitter);
        recipe.shape("#-#", "#@#", "#*#");
        recipe.setIngredient('#', Material.COMMAND_BLOCK);
        recipe.setIngredient('-', Material.ENDER_PEARL);
        recipe.setIngredient('@', Material.COMMAND_BLOCK);
        recipe.setIngredient('*', Material.STRUCTURE_BLOCK);

        plugin.getServer().addRecipe(recipe);
    }

    @EventHandler
    public void craftCable (PrepareItemCraftEvent event) {
        if (event.getRecipe() == null || event.getRecipe().getResult() == null) return;

        final ItemStack result = event.getRecipe().getResult();
        final ItemStack golden_cable = customItemManager.getItem(CustomItem.GOLDEN_CABLE);
        if (!result.isSimilar(golden_cable)) return;

        if (event.getViewers().get(0) instanceof Player) {
            Player player = (Player) event.getViewers().get(0);
            if ((byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == (byte) 1) {
                event.getInventory().setResult(null);
                return;
            }
        }

        final ItemStack[] matrix = event.getInventory().getMatrix();
        final ItemStack binding = customItemManager.getItem(CustomItem.BINDING);
        if (!isMatching(matrix, binding)) event.getInventory().setResult(null);
    }

    @EventHandler
    public void craftCircuit (PrepareItemCraftEvent event) {
        if (event.getRecipe() == null || event.getRecipe().getResult() == null) return;

        final ItemStack result = event.getRecipe().getResult();
        final ItemStack starlight_circuit = customItemManager.getItem(CustomItem.STARLIGHT_CIRCUIT);
        if (!result.isSimilar(starlight_circuit)) return;

        if (event.getViewers().get(0) instanceof Player) {
            Player player = (Player) event.getViewers().get(0);
            if ((byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == (byte) 1) {
                event.getInventory().setResult(null);
                return;
            }
        }

        final ItemStack[] matrix = event.getInventory().getMatrix();
        final ItemStack golden_cable = customItemManager.getItem(CustomItem.GOLDEN_CABLE);
        final ItemStack nether_star_fragment = customItemManager.getItem(CustomItem.NETHER_STAR_FRAGMENT);

        boolean matches = true;
        final int[] slots = {0, 1, 2, 6, 7, 8};
        for (int slot : slots) {
            if (!matrix[slot].isSimilar(golden_cable)) {
                matches = false;
            }
        }

        if (matches) {
            if (!isMatching(matrix, nether_star_fragment, 4)) {
                matches = false;
            }
        }

        if (!matches) {
            event.getInventory().setResult(null);
        }
    }

    @EventHandler
    public void craftBattery (PrepareItemCraftEvent event) {
        if (event.getRecipe() == null || event.getRecipe().getResult() == null) return;

        final ItemStack result = event.getRecipe().getResult();
        final ItemStack starlight_battery = customItemManager.getItem(CustomItem.STARLIGHT_BATTERY);
        if (!result.isSimilar(starlight_battery)) return;

        if (event.getViewers().get(0) instanceof Player) {
            Player player = (Player) event.getViewers().get(0);
            if ((byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == (byte) 1) {
                event.getInventory().setResult(null);
                return;
            }
        }

        final ItemStack[] matrix = event.getInventory().getMatrix();
        final ItemStack titanium_ingot = customItemManager.getItem(CustomItem.TITANIUM_INGOT);
        final ItemStack nether_star_fragment = customItemManager.getItem(CustomItem.NETHER_STAR_FRAGMENT);
        final ItemStack golden_cable = customItemManager.getItem(CustomItem.GOLDEN_CABLE);

        if (isMatching(matrix, titanium_ingot, 3, 5, 6, 8)) {
            if (isMatching(matrix, nether_star_fragment, 4, 7)) {
                if (isMatching(matrix, golden_cable, 1)) {
                    return;
                }
            }
        }

        event.getInventory().setResult(null);
    }

    @EventHandler
    public void craftModule (PrepareItemCraftEvent event) {
        if (event.getRecipe() == null || event.getRecipe().getResult() == null) return;

        final ItemStack result = event.getRecipe().getResult();
        final ItemStack starlight_module = customItemManager.getItem(CustomItem.STARLIGHT_MODULE);
        if (!result.isSimilar(starlight_module)) return;

        if (event.getViewers().get(0) instanceof Player) {
            Player player = (Player) event.getViewers().get(0);
            if ((byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == (byte) 1) {
                event.getInventory().setResult(null);
                return;
            }
        }

        final ItemStack[] matrix = event.getInventory().getMatrix();
        final ItemStack titanium_ingot = customItemManager.getItem(CustomItem.TITANIUM_INGOT);
        final ItemStack starlight_battery = customItemManager.getItem(CustomItem.STARLIGHT_BATTERY);
        final ItemStack starlight_circuit = customItemManager.getItem(CustomItem.STARLIGHT_CIRCUIT);

        if (isMatching(matrix, titanium_ingot)) {
            if (isMatching(matrix, starlight_circuit, 4)) {
                if (isMatching(matrix, starlight_battery, 7)) {
                    return;
                }
            }
        }
        event.getInventory().setResult(null);
    }

    @EventHandler
    public void craftPhotonEmitter (PrepareItemCraftEvent event) {
        if (event.getRecipe() == null || event.getRecipe().getResult() == null) return;

        final ItemStack result = event.getRecipe().getResult();
        final ItemStack photonEmitter = customItemManager.getItem(CustomItem.PHOTON_EMITTER);
        if (!result.isSimilar(photonEmitter)) return;

        if (event.getViewers().get(0) instanceof Player) {
            Player player = (Player) event.getViewers().get(0);
            if ((byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == (byte) 1) {
                event.getInventory().setResult(null);
                return;
            }
        }

        final ItemStack[] matrix = event.getInventory().getMatrix();
        final ItemStack titanium_ingot = customItemManager.getItem(CustomItem.TITANIUM_INGOT);
        final ItemStack starlight_module = customItemManager.getItem(CustomItem.STARLIGHT_MODULE);
        final ItemStack nether_star_fragment = customItemManager.getItem(CustomItem.NETHER_STAR_FRAGMENT);

        boolean matches = true;
        if (!isMatching(matrix, starlight_module) || !isMatching(matrix, nether_star_fragment, 4)) {
            matches = false;
        }

        if (matches) {
            final int[] titaniumSlots = {0, 2, 3, 5, 6, 8};

            for (int slot : titaniumSlots) {
                if (!matrix[slot].isSimilar(titanium_ingot)) {
                    matches = false;
                }
            }
        }

        if (!matches) {
            event.getInventory().setResult(null);
        }
    }

    @EventHandler
    public void preventHereticCraft (PrepareItemCraftEvent event) {
        if (event.getRecipe() == null || event.getRecipe().getResult() == null) return;

        final ItemStack result = event.getRecipe().getResult();
        final ItemStack binding = customItemManager.getItem(CustomItem.BINDING);
        final ItemStack energium = customItemManager.getItem(CustomItem.ENERGIUM);
        if (!result.isSimilar(binding) && !result.isSimilar(energium)) return;

        if (event.getViewers().get(0) instanceof Player) {
            Player player = (Player) event.getViewers().get(0);
            if ((byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == (byte) 1) {
                event.getInventory().setResult(null);
            }
        }
    }
}
