package me.fullpotato.badlandscaves.CustomItems.Using.Starlight.Nebulites;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.Crafting.Starlight.StarlightArmor;
import me.fullpotato.badlandscaves.CustomItems.Crafting.Starlight.StarlightCharge;
import me.fullpotato.badlandscaves.CustomItems.Crafting.Starlight.StarlightTools;
import me.fullpotato.badlandscaves.CustomItems.CustomItem;
import me.fullpotato.badlandscaves.CustomItems.CustomItemManager;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class NebuliteManager {
    private final BadlandsCaves plugin;
    private final CustomItemManager customItemManager;
    private final StarlightCharge chargeManager;
    private final StarlightArmor starlightArmor;
    private final StarlightTools starlightTools;
    private final NamespacedKey key;

    public NebuliteManager(BadlandsCaves plugin, StarlightCharge chargeManager, StarlightArmor starlightArmor, StarlightTools starlightTools) {
        this.plugin = plugin;
        this.key = new NamespacedKey(plugin, "nebulites");
        customItemManager = plugin.getCustomItemManager();
        this.chargeManager = chargeManager;
        this.starlightArmor = starlightArmor;
        this.starlightTools = starlightTools;
    }

    public boolean isSuitable (ItemStack starlight, Nebulite nebulite) {
        for (CustomItem baseItem : nebulite.getBaseItems()) {
            if (baseItem.equals(CustomItem.STARLIGHT_HELMET) && starlight.getType().equals(plugin.getCustomItemManager().getItem(CustomItem.STARLIGHT_HELMET).getType()) && starlightArmor.isStarlightArmor(starlight)) return true;
            if (baseItem.equals(CustomItem.STARLIGHT_CHESTPLATE) && starlight.getType().equals(plugin.getCustomItemManager().getItem(CustomItem.STARLIGHT_CHESTPLATE).getType()) && starlightArmor.isStarlightArmor(starlight)) return true;
            if (baseItem.equals(CustomItem.STARLIGHT_LEGGINGS) && starlight.getType().equals(plugin.getCustomItemManager().getItem(CustomItem.STARLIGHT_LEGGINGS).getType()) && starlightArmor.isStarlightArmor(starlight)) return true;
            if (baseItem.equals(CustomItem.STARLIGHT_BOOTS) && starlight.getType().equals(plugin.getCustomItemManager().getItem(CustomItem.STARLIGHT_BOOTS).getType()) && starlightArmor.isStarlightArmor(starlight)) return true;
            if (baseItem.equals(CustomItem.STARLIGHT_SABER) && starlightTools.isStarlightSaber(starlight)) return true;
            if (baseItem.equals(CustomItem.STARLIGHT_BLASTER) && starlightTools.isStarlightBlaster(starlight)) return true;
            if (baseItem.equals(CustomItem.STARLIGHT_SHIELD) && starlightTools.isStarlightShield(starlight)) return true;
            if (baseItem.equals(CustomItem.STARLIGHT_SENTRY) && starlightTools.isStarlightSentry(starlight)) return true;
            if (baseItem.equals(CustomItem.STARLIGHT_PAXEL) && starlightTools.isStarlightPaxel(starlight)) return true;
        }
        return false;
    }

    public boolean isNebulite (ItemStack item) {
        if (item != null) {
            if (item.hasItemMeta()) {
                final ItemMeta meta = item.getItemMeta();
                if (meta != null) {
                    if (meta.getPersistentDataContainer().has(new NamespacedKey(plugin, "is_nebulite"), PersistentDataType.BYTE)) {
                        Byte result = meta.getPersistentDataContainer().get(new NamespacedKey(plugin, "is_nebulite"), PersistentDataType.BYTE);
                        if (result != null) {
                            return result == (byte) 1;
                        }
                    }
                }
            }
        }
        return false;
    }

    public boolean hasNebulite(ItemStack starlight, Nebulite check) {
        for (Nebulite nebulite : getNebulites(starlight)) {
            if (nebulite.equals(check)) return true;
        }
        return false;
    }

    public boolean isConflicting (ItemStack starlight, Nebulite check) {
        if (hasNebulite(starlight, check)) return true;

        for (Nebulite nebulite : getNebulites(starlight)) {
            final CustomItem[] conflictingItems = nebulite.getMutuallyExplicit();
            if (conflictingItems != null) {
                for (CustomItem conflicting : conflictingItems) {
                    if (check.getNebuliteItem().equals(conflicting)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }



    /** Not to be confused with getNebulites(ItemStack item).
     * This one is intended to get a Nebulite Object from a Nebulite Item.
     * @param item Nebulite Item
     * */
    public Nebulite getNebulite (ItemStack item) {
        if (item != null) {
            if (isNebulite(item)) {
                for (Nebulite nebulite : Nebulite.values()) {
                    if (item.isSimilar(customItemManager.getItem(nebulite.getNebuliteItem()))) {
                        return nebulite;
                    }
                }
            }
        }
        return null;
    }

    /** Not to be confused with getNebulite(ItemStack item).
     * This one is intended to get all applied Nebulites from a Starlight Item.
     * @param item Starlight Item
     * */
    public Nebulite[] getNebulites (ItemStack item) {
        if (item != null) {
            if (chargeManager.isStarlight(item)) {
                final String str = item.getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING);
                if (str != null) {
                    return getNebulitesFromString(str);
                }
            }
        }
        return null;
    }

    public void setNebulites (ItemStack item, Nebulite... nebulites) {
        if (item != null) {
            if (chargeManager.isStarlight(item)) {
                final String str = getStringFromNebulites(nebulites);
                final ItemMeta meta = item.getItemMeta();
                if (meta != null) {
                    //set the inner PDC
                    meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, str);

                    //manage item description ------

                    //???
                    List<String> lore = meta.getLore();
                    if (lore == null) lore = new ArrayList<>();
                    for (int i = 5; i > 0; i--) {
                        if (lore.size() > i) {
                            lore.remove(i);
                        }
                    }

                    ArrayList<String> nebList = new ArrayList<>();
                    if (nebulites != null && nebulites.length > 0) {
                        //display all nebulite strings into nebList
                        for (int i = 0; i < nebulites.length; i++) {
                            if (nebulites[i] != null) {
                                final String name = customItemManager.getItem(nebulites[i].getNebuliteItem()).getItemMeta().getDisplayName();
                                final int index = name.indexOf(ChatColor.stripColor(name));
                                final String desc = ChatColor.RESET + name.substring(0, index) + name.substring(index);

                                nebList.add(desc);
                            }
                        }

                        //add header and "commit" nebList into description
                        if (nebList.size() > 0) {
                            //add header
                            lore.add("");
                            lore.add(ChatColor.RESET.toString() + ChatColor.BOLD + ChatColor.of("#0081fa") + "--- Nebulites ---");

                            lore.addAll(nebList);
                        }

                    }

                    meta.setLore(lore);
                    item.setItemMeta(meta);
                }
            }
        }
    }

    public String getStringFromNebulites (Nebulite... nebulites) {
        StringBuilder builder = new StringBuilder();
        for (Nebulite nebulite : nebulites) {
            if (nebulite != null) {
                builder.append(nebulite.name()).append(";");
            }
        }
        return builder.toString();
    }

    public Nebulite[] getNebulitesFromString (String string) {
        final String[] entries = string.split(";");
        final ArrayList<Nebulite> nebulites = new ArrayList<>();

        for (String entry : entries) {
            try {
                nebulites.add(Nebulite.valueOf(entry));
            }
            catch (IllegalArgumentException ignored) {
            }
        }

        final Nebulite[] output = new Nebulite[nebulites.size()];
        for (int i = 0; i < output.length; i++) {
            output[i] = nebulites.get(i);
        }

        return output;
    }
}
