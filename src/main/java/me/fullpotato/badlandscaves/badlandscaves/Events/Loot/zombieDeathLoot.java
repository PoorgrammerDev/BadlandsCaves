package me.fullpotato.badlandscaves.badlandscaves.Events.Loot;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

public class zombieDeathLoot implements Listener {
    private BadlandsCaves plugin;
    public zombieDeathLoot(BadlandsCaves bcav) {
        plugin = bcav;
    }

    @EventHandler
    public void zombieDeath (EntityDeathEvent event) {
        if (!event.getEntityType().equals(EntityType.ZOMBIE) &&
            !event.getEntityType().equals(EntityType.ZOMBIE_VILLAGER) &&
            !event.getEntityType().equals(EntityType.DROWNED)) {
            return;
        }
        if (event.getEntity().getKiller() == null) {
            return;
        }
        LivingEntity entity = event.getEntity();
        Location location = entity.getLocation();
        Player player = entity.getKiller();
        int looting_mod = player.getInventory().getItemInMainHand().getEnchantmentLevel(Enchantment.LOOT_BONUS_MOBS);
        double luck_mod = player.getAttribute(Attribute.GENERIC_LUCK).getValue();

        LootContext.Builder builder = new LootContext.Builder(location);
        builder.lootedEntity(event.getEntity());
        builder.lootingModifier(looting_mod);
        builder.luck((float) luck_mod);
        builder.killer(player);
        LootContext lootContext = builder.build();

        zombieLootTable zombieLoot = new zombieLootTable();
        Collection<ItemStack> drops = zombieLoot.populateLoot(new Random(), lootContext);
        ArrayList<ItemStack> items = (ArrayList<ItemStack>) drops;

        event.getDrops().clear();

        for (int a = 0; a < items.size(); a++) {
            if (items.get(a).getAmount() > 0) {
                location.getWorld().dropItemNaturally(location, items.get(a));
            }
        }
    }
}
