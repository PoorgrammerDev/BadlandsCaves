package me.fullpotato.badlandscaves.CustomItems.Crafting;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.CustomItem;
import me.fullpotato.badlandscaves.SupernaturalPowers.Artifacts.Artifact;
import me.fullpotato.badlandscaves.SupernaturalPowers.Artifacts.ArtifactManager;
import me.fullpotato.badlandscaves.Util.PlayerScore;

public class AscendedOrb implements Listener {
    private final BadlandsCaves plugin;
    private final ArtifactManager artifactManager;
    
    public AscendedOrb(BadlandsCaves plugin, ArtifactManager artifactManager) {
        this.plugin = plugin;
        this.artifactManager = artifactManager;
    }

    /*
     * This crafting recipe is not to craft the orb - they are not craftable.
     * The main function of the Orb is to be crafted with an Artifact to convert the Artifact to whatever type the Orb is.
     */
    public void ascendedOrbCrafting() {
        final ItemStack placeholder = plugin.getCustomItemManager().getItem(CustomItem.ASCENDED_ORB_PLACEHOLDER);

        ShapelessRecipe recipe = new ShapelessRecipe(new NamespacedKey(plugin, "ascended_orb_use"), placeholder);
        recipe.addIngredient(Material.COMMAND_BLOCK); // orb of ascension
        recipe.addIngredient(Material.KNOWLEDGE_BOOK); // artifact

        plugin.getServer().addRecipe(recipe);
    }

    @EventHandler
    public void validateCrafting(PrepareItemCraftEvent event) {
        if (event.getRecipe() == null || event.getRecipe().getResult() == null) return;

        //Make sure recipe is for artifact
        final ItemStack result = event.getRecipe().getResult();
        final ItemStack placeholder = plugin.getCustomItemManager().getItem(CustomItem.ASCENDED_ORB_PLACEHOLDER);
        if (!result.isSimilar(placeholder)) return;

        if (event.getViewers().isEmpty() || event.getViewers().size() < 1) {
            event.getInventory().setResult(null);
            return;
        }

        //Magic class only
        if (event.getViewers().get(0) instanceof Player) {
            Player player = (Player) event.getViewers().get(0);
            if ((byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == (byte) 0) {
                event.getInventory().setResult(null);
                return;
            }
        }

        final ItemStack[] matrix = event.getInventory().getMatrix();

            //Find the artifact and the orb used
            ItemStack artifact = null;
            ItemStack orb = null;
            for (ItemStack ingredient : matrix) {
                if (ingredient != null) {
                    if (ingredient.getType().equals(Material.COMMAND_BLOCK)) {
                        orb = ingredient;
                    }
                    else {
                        artifact = ingredient;
                    }
                }
            }

            //Make sure the "artifact" is actually an artifact
            if (artifact != null && orb != null && artifactManager.isArtifact(artifact)) {

                final ItemMeta orbMeta = orb.getItemMeta();
                final NamespacedKey orbKey = new NamespacedKey(plugin, "is_ascension_orb");
                final NamespacedKey orbKey2 = new NamespacedKey(plugin, "orb_artifact");

                //Make sure the "orb" is actually an orb
                if (orbMeta != null &&
                    orbMeta.getPersistentDataContainer().has(orbKey, PersistentDataType.BYTE) &&
                    orbMeta.getPersistentDataContainer().get(orbKey, PersistentDataType.BYTE) == (byte) 1) {
                        
                    if (orbMeta.getPersistentDataContainer().has(orbKey2, PersistentDataType.STRING)) {
                        //Interpret artifact name and set output
                        try {
                            final Artifact desiredType = Artifact.valueOf(orbMeta.getPersistentDataContainer().get(orbKey2, PersistentDataType.STRING));
                            event.getInventory().setResult(plugin.getCustomItemManager().getItem(desiredType.getArtifactItem()));
                            return;
                        }
                        catch (Exception ignored) {
                        }

                    }

                }

            }
        event.getInventory().setResult(null);
    }


    
}
