package me.fullpotato.badlandscaves.CustomItems.Using.Starlight.Nebulites;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.Crafting.Starlight.StarlightArmor;
import me.fullpotato.badlandscaves.CustomItems.Crafting.Starlight.StarlightCharge;
import me.fullpotato.badlandscaves.CustomItems.Crafting.Starlight.StarlightTools;
import me.fullpotato.badlandscaves.CustomItems.CustomItem;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NebuliteManager {
    private final BadlandsCaves plugin;
    private final StarlightCharge chargeManager;
    private final NamespacedKey key;

    public NebuliteManager(BadlandsCaves plugin) {
        this.plugin = plugin;
        this.chargeManager = new StarlightCharge(plugin);
        this.key = new NamespacedKey(plugin, "nebulites");
    }

    public boolean isSuitable (ItemStack starlight, Nebulite nebulite) {
        final StarlightArmor armor = new StarlightArmor(plugin);
        final StarlightTools tools = new StarlightTools(plugin);

        for (CustomItem baseItem : nebulite.getBaseItems()) {
            if (baseItem.equals(CustomItem.STARLIGHT_HELMET) && starlight.getType().equals(CustomItem.STARLIGHT_HELMET.getItem().getType()) && armor.isStarlightArmor(starlight)) return true;
            if (baseItem.equals(CustomItem.STARLIGHT_CHESTPLATE) && starlight.getType().equals(CustomItem.STARLIGHT_CHESTPLATE.getItem().getType()) && armor.isStarlightArmor(starlight)) return true;
            if (baseItem.equals(CustomItem.STARLIGHT_LEGGINGS) && starlight.getType().equals(CustomItem.STARLIGHT_LEGGINGS.getItem().getType()) && armor.isStarlightArmor(starlight)) return true;
            if (baseItem.equals(CustomItem.STARLIGHT_BOOTS) && starlight.getType().equals(CustomItem.STARLIGHT_BOOTS.getItem().getType()) && armor.isStarlightArmor(starlight)) return true;
            if (baseItem.equals(CustomItem.STARLIGHT_SABER) && tools.isStarlightSaber(starlight)) return true;
            if (baseItem.equals(CustomItem.STARLIGHT_BLASTER) && tools.isStarlightBlaster(starlight)) return true;
            if (baseItem.equals(CustomItem.STARLIGHT_SHIELD) && tools.isStarlightShield(starlight)) return true;
            if (baseItem.equals(CustomItem.STARLIGHT_SENTRY) && tools.isStarlightSentry(starlight)) return true;
            if (baseItem.equals(CustomItem.STARLIGHT_PAXEL) && tools.isStarlightPaxel(starlight)) return true;
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
                    if (item.isSimilar(nebulite.getNebuliteItem().getItem())) {
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
                    meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, str);

                    List<String> lore = meta.getLore();
                    if (lore == null) lore = new ArrayList<>();
                    for (int i = 5; i > 0; i--) {
                        if (lore.size() > i) {
                            lore.remove(i);
                        }
                    }

                    for (int i = 0; i < nebulites.length; i++) {
                        if (nebulites[i] != null) {
                            lore.add("");
                            lore.add(ChatColor.RESET.toString() + ChatColor.BOLD + ChatColor.of("#0081fa") + "--- Nebulites ---");
                            for (int k = 0; k < 3; k++) {
                                if (nebulites[k] != null) {
                                    // TODO: 7/17/2020 clean this up? idk how or why this works tbh but it works
                                    final String name = nebulites[k].getNebuliteItem().getItem().getItemMeta().getDisplayName();
                                    final int index = name.indexOf(ChatColor.stripColor(name));
                                    final String desc = ChatColor.RESET + name.substring(0, index) + name.substring(index);

                                    lore.add(desc);
                                }
                            }
                            break;
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
