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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class BlessedAppleEat implements Listener {
    private final BadlandsCaves plugin;
    private final PlayerEffects playerEffects;
    public BlessedAppleEat(BadlandsCaves bcav, PlayerEffects playerEffects) {
        plugin = bcav;
        this.playerEffects = playerEffects;
    }

    @EventHandler
    public void player_eat (PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        int death_count = (int) PlayerScore.DEATHS.getScore(plugin, player);

        final ItemStack blessed_apple = plugin.getCustomItemManager().getItem(CustomItem.BLESSED_APPLE);
        final ItemStack enchanted_blessed_apple = plugin.getCustomItemManager().getItem(CustomItem.ENCHANTED_BLESSED_APPLE);

        if (item.isSimilar(blessed_apple) || item.isSimilar(enchanted_blessed_apple)) {
            //added buffs

            final int saturationLevel, regenerationLevel, absorptionLevel;
            if (item.isSimilar(blessed_apple)) {
                saturationLevel = 0;
                regenerationLevel = 4;
                absorptionLevel = 4;
            }
            else {
                saturationLevel = 1;
                regenerationLevel = 9;
                absorptionLevel = 9;
            }

            new BukkitRunnable() {
                @Override
                public void run() {
                    player.removePotionEffect(PotionEffectType.ABSORPTION);
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 5, saturationLevel, true, true));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 10, regenerationLevel, true, true));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 300, absorptionLevel, true, true));
                }
            }.runTaskLater(plugin, 1);


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
