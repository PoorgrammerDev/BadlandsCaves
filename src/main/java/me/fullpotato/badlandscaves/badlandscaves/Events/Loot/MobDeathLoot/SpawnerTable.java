package me.fullpotato.badlandscaves.badlandscaves.Events.Loot.MobDeathLoot;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootContext;
import org.bukkit.loot.LootTable;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Random;

public class SpawnerTable implements LootTable {
    private BadlandsCaves plugin;
    private NamespacedKey key;
    private Player player;

    public SpawnerTable(BadlandsCaves plugin, Player player) {
        this.plugin = plugin;
        this.player = player;

        key = new NamespacedKey(plugin, "mob_spawner_treasure");
    }


    @Override
    public @NotNull Collection<ItemStack> populateLoot(@NotNull Random random, @NotNull LootContext lootContext) {
        // TODO: 4/5/2020 make this lol
        return null;
    }

    @Override
    public void fillInventory(@NotNull Inventory inventory, @NotNull Random random, @NotNull LootContext lootContext) {
    }

    @Override
    public @NotNull NamespacedKey getKey() {
        return key;
    }
}
