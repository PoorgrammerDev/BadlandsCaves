package me.fullpotato.badlandscaves.badlandscaves.Events.Loot;

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
    public @NotNull Collection<ItemStack> populateLoot(@NotNull Random random, @NotNull LootContext context) {
        final double luck = context.getLuck();
        final int count = Math.min(Math.max(random.nextInt(Math.max(Math.min((int) Math.floor(Math.pow((luck + 10.0) / 6.0, 1.79) + 3.0), 10), 3)), 1), 10);
        final boolean hardmode = plugin.getConfig().getBoolean("game_values.hardmode");
        final boolean supernatural = player.getMetadata("has_supernatural_powers").get(0).asBoolean();

        if (hardmode) {
            if (supernatural) {
                //HARDMODE SUPERNATURAL------------------------------------------------------------------------
                // TODO: 4/6/2020 ARTIFACTS

            }
            else {
                //HARDMODE NORMAL------------------------------------------------------------------------------
                // TODO: 4/6/2020 AUGMENTS 

            }
        }
        else {
            if (supernatural) {
                //PREHARDMODE SUPERNATURAL---------------------------------------------------------------------
                

            }
            else {
                //PREHARDMODE NORMAL---------------------------------------------------------------------------
                // TODO: 4/6/2020 SWORD MODIFIERS

            }
        }


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
