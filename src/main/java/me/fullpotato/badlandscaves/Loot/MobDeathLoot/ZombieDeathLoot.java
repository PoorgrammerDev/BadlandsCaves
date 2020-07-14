package me.fullpotato.badlandscaves.Loot.MobDeathLoot;

import me.fullpotato.badlandscaves.BadlandsCaves;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Random;

public class ZombieDeathLoot implements Listener {

    @EventHandler
    public void zombieDeath (EntityDeathEvent event) {
        if (event.getEntity() instanceof Zombie) {
            final Zombie zombie = (Zombie) event.getEntity();
            final Player player = zombie.getKiller();
            final Random random = new Random();

            List<ItemStack> drops = event.getDrops();
            final int rotten_flesh_count = getRottenFleshAmount(drops);
            final int leather_chance = getLeatherChance(player);

            //for each rotten flesh, there's a chance for it to also spawn leather
            int leather_count = 0;
            for (int a = 0; a < rotten_flesh_count; a++) {
                if (random.nextInt(100) <= leather_chance) {
                    leather_count++;
                }
            }

            if (leather_count > 0) {
                ItemStack leather = new ItemStack(Material.LEATHER, leather_count);
                drops.add(leather);
            }

        }
    }

    //gets how many rotten flesh is dropped
    public int getRottenFleshAmount (List<ItemStack> drops) {
        for (ItemStack drop : drops) {
            if (drop.getType().equals(Material.ROTTEN_FLESH)) {
                return drop.getAmount();
            }
        }
        return 0;
    }

    //if natural death (no player killed), 10% chance by default.
    //if player kill, takes into account looting and luck. max 50%
    public int getLeatherChance (Player player) {
        if (player == null) return 10;

        final int looting_mod = Math.min(player.getInventory().getItemInMainHand().getEnchantmentLevel(Enchantment.LOOT_BONUS_MOBS), 3);
        final double luck_mod = Math.min(player.getAttribute(Attribute.GENERIC_LUCK).getValue(), 5);
        return Math.max(Math.min(((int) (10 + ((10 * looting_mod) + (Math.pow(luck_mod, 3) / 10)))), 50), 0);
    }
}
