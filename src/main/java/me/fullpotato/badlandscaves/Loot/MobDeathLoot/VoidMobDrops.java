package me.fullpotato.badlandscaves.Loot.MobDeathLoot;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.Crafting.SoulLantern;
import me.fullpotato.badlandscaves.CustomItems.CustomItem;
import me.fullpotato.badlandscaves.CustomItems.CustomItemManager;
import me.fullpotato.badlandscaves.CustomItems.Using.UseSoulLantern;

import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.Random;

public class VoidMobDrops implements Listener {
    private final BadlandsCaves plugin;
    private final Random random;
    private NamespacedKey voidKey;
    private final int dropChance;

    public VoidMobDrops(BadlandsCaves bcav, Random random) {
        this.plugin = bcav;
        this.random = random;
        this.voidKey = new NamespacedKey(plugin, "voidMonster");
        this.dropChance = plugin.getOptionsConfig().getInt("void_monster_drop_chance");
    }

    @EventHandler
    public void dropSoul (EntityDeathEvent event) {
        if (event.getEntity().getWorld().equals(plugin.getServer().getWorld(plugin.getReflectionWorldName()))) return;
        if (event.getEntity().getWorld().equals(plugin.getServer().getWorld(plugin.getChambersWorldName()))) return;

        //If the monster is a Void Monster
        final LivingEntity entity = event.getEntity();
        if (!entity.getPersistentDataContainer().has(this.voidKey, PersistentDataType.BYTE) || 
            entity.getPersistentDataContainer().get(this.voidKey, PersistentDataType.BYTE) != 1) return;

        //can drop voidmatter if player killed
        final Player player = entity.getKiller();
        if (player == null) return;

        final ItemStack voidmatter = plugin.getCustomItemManager().getItem(CustomItem.VOIDMATTER);
        if (random.nextInt(100) <= this.dropChance) {
            //Add to drops
            event.getDrops().add(voidmatter);
        }
    }
}
