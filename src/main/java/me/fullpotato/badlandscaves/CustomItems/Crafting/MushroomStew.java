package me.fullpotato.badlandscaves.CustomItems.Crafting;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SuspiciousStewMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Random;

public class MushroomStew implements Listener {
    private final Random random;

    public MushroomStew(Random random) {
        this.random = random;
    }

    @EventHandler
    public void randomStewCraft (PrepareItemCraftEvent event) {
        CraftingInventory inventory = event.getInventory();
        if (inventory.getResult() != null && inventory.getResult().getType().equals(Material.MUSHROOM_STEW)) {
            ItemStack stew = new ItemStack(Material.SUSPICIOUS_STEW);

            SuspiciousStewMeta meta = (SuspiciousStewMeta) stew.getItemMeta();
            PotionEffectType[] effectTypes = PotionEffectType.values();
            assert meta != null;
            meta.addCustomEffect(new PotionEffect(effectTypes[random.nextInt(effectTypes.length)], random.nextInt(200), 0), true);
            stew.setItemMeta(meta);

            inventory.setResult(stew);
        }
    }
}
