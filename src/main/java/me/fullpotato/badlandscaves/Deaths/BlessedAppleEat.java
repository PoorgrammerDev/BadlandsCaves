package me.fullpotato.badlandscaves.Deaths;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.CustomItem;
import me.fullpotato.badlandscaves.Effects.PlayerEffects;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

public class BlessedAppleEat implements Listener {
    private final BadlandsCaves plugin;
    private final PlayerEffects playerEffects;
    public BlessedAppleEat(BadlandsCaves bcav) {
        plugin = bcav;
        playerEffects = new PlayerEffects(plugin);
    }

    @EventHandler
    public void player_eat (PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        int death_count = (int) PlayerScore.DEATHS.getScore(plugin, player);

        final ItemStack blessed_apple = plugin.getCustomItemManager().getItem(CustomItem.BLESSED_APPLE);
        final ItemStack enchanted_blessed_apple = plugin.getCustomItemManager().getItem(CustomItem.ENCHANTED_BLESSED_APPLE);

        if (item.isSimilar(blessed_apple) || item.isSimilar(enchanted_blessed_apple)) {
            if (player.getWorld().equals(plugin.getServer().getWorld(plugin.getReflectionWorldName()))) {
                int cooldown = 100;
                player.setCooldown(Material.GOLDEN_APPLE, cooldown);
                player.setCooldown(Material.ENCHANTED_GOLDEN_APPLE, cooldown);
                return;
            }

            int decr_by;
            boolean isHardmode = plugin.getSystemConfig().getBoolean("hardmode");
            if (item.isSimilar(blessed_apple)) {
                if (isHardmode) {
                    decr_by = plugin.getOptionsConfig().getInt("hardmode_values.death_reverse_gapple");
                }
                else {
                    decr_by = plugin.getOptionsConfig().getInt("pre_hardmode_values.death_reverse_gapple");
                }
            }
            else {
                if (isHardmode) {
                    decr_by = plugin.getOptionsConfig().getInt("hardmode_values.death_reverse_ench_gapple");
                }
                else {
                    decr_by = plugin.getOptionsConfig().getInt("pre_hardmode_values.death_reverse_ench_gapple");
                }
            }

            if (death_count != 0 && decr_by > 0) {
                player.spawnParticle(Particle.VILLAGER_HAPPY, player.getLocation().add(0, 0.5, 0), 50, 1, 1, 1, 0);
                player.playSound(player.getLocation(), "custom.reverse_deaths", SoundCategory.PLAYERS, 0.5F, 1);
                PlayerScore.DEATHS.setScore(plugin, player, remove_deaths(death_count, decr_by));

                playerEffects.applyEffects(player, true);
            }
        }
    }

    public int remove_deaths(int current_deaths, int decrease_by) {
        return Math.max(current_deaths - decrease_by, 0);
    }
}
