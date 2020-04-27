package me.fullpotato.badlandscaves.badlandscaves.Events.Deaths;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

public class GappleEat implements Listener {
    private BadlandsCaves plugin;
    public GappleEat(BadlandsCaves bcav) {
        plugin = bcav;
    }

    @EventHandler
    public void player_eat (PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        int death_count = player.getMetadata("Deaths").get(0).asInt();

        boolean gapple = item.getType().equals(Material.GOLDEN_APPLE);
        boolean ench_gapple = item.getType().equals(Material.ENCHANTED_GOLDEN_APPLE);

        if (gapple || ench_gapple) {
            int decr_by;
            boolean isHardmode = plugin.getConfig().getBoolean("game_values.hardmode");
            if (gapple) {
                if (isHardmode) {
                    decr_by = plugin.getConfig().getInt("game_values.hardmode_values.death_reverse_gapple");
                }
                else {
                    decr_by = plugin.getConfig().getInt("game_values.pre_hardmode_values.death_reverse_gapple");
                }
            }
            else {
                if (isHardmode) {
                    decr_by = plugin.getConfig().getInt("game_values.hardmode_values.death_reverse_ench_gapple");
                }
                else {
                    decr_by = plugin.getConfig().getInt("game_values.pre_hardmode_values.death_reverse_ench_gapple");
                }
            }

            if (death_count != 0 && decr_by > 0) {
                player.spawnParticle(Particle.VILLAGER_HAPPY, player.getLocation().add(0, 0.5, 0), 50, 1, 1, 1, 0);
                player.playSound(player.getLocation(), "custom.reverse_deaths", SoundCategory.PLAYERS, 0.5F, 1);
                player.setMetadata("Deaths", new FixedMetadataValue(plugin, remove_deaths(death_count, decr_by)));
            }
        }
    }

    public int remove_deaths(int current_deaths, int decrease_by) {
        return Math.max(current_deaths - decrease_by, 0);
    }
}
