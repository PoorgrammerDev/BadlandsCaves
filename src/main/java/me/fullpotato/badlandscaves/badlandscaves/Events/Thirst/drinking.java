package me.fullpotato.badlandscaves.badlandscaves.Events.Thirst;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionType;

public class drinking implements Listener {
    private BadlandsCaves plugin;
    public drinking(BadlandsCaves bcav) {
        plugin = bcav;
    }
    @EventHandler
    public void drink (PlayerItemConsumeEvent event) {
        ItemStack item = event.getItem();
        if (!item.getType().equals(Material.POTION)) {
            return;
        }

        boolean isHardmode = plugin.getConfig().getBoolean("game_values.hardmode");
        Player player = event.getPlayer();
        if (item.getItemMeta() != null && item.getItemMeta().getLore() != null) {
            PotionMeta potionMeta = (PotionMeta) item.getItemMeta();

            //toxic water
            if (potionMeta.getBasePotionData().getType().equals(PotionType.WATER)) {
                double current_thirst = player.getMetadata("Thirst").get(0).asDouble();
                double thirst_add;

                if (isHardmode) {
                    thirst_add = plugin.getConfig().getInt("game_values.hardmode_values.tox_drink_thirst_incr");
                }
                else {
                    thirst_add = plugin.getConfig().getInt("game_values.pre_hardmode_values.tox_drink_thirst_incr");
                }

                player.setMetadata("Thirst", new FixedMetadataValue(plugin, addThirst(current_thirst, thirst_add)));

                double current_tox = player.getMetadata("Toxicity").get(0).asDouble();
                double tox_add;

                if (isHardmode) {
                    tox_add = plugin.getConfig().getInt("game_values.hardmode_values.tox_drink_tox_incr");
                }
                else {
                    tox_add = plugin.getConfig().getInt("game_values.pre_hardmode_values.tox_drink_tox_incr");
                }

                player.setMetadata("Toxicity", new FixedMetadataValue(plugin, current_tox + tox_add));
            }
            //either purified or antidote
            else if (potionMeta.getBasePotionData().getType().equals(PotionType.UNCRAFTABLE)) {
                //testing if purified

                if (potionMeta.getDisplayName().equalsIgnoreCase(ChatColor.DARK_AQUA + "Purified Water Bottle")) {
                    if (potionMeta.getLore().contains(ChatColor.GRAY + "Light and refreshing.")) {
                        //TODO change this to use config item deserialization
                        if (potionMeta.getEnchantLevel(Enchantment.DURABILITY) < 50) {
                            double current_thirst = player.getMetadata("Thirst").get(0).asDouble();
                            int thirst_threshold;
                            double thirst_add;
                            int buffer;

                            if (isHardmode) {
                                thirst_add = plugin.getConfig().getInt("game_values.hardmode_values.purified_drink_thirst_incr");
                                thirst_threshold = plugin.getConfig().getInt("game_values.hardmode_values.threshold_thirst_sys");
                                buffer = thirst_threshold * -10;
                            }
                            else {
                                thirst_add = plugin.getConfig().getInt("game_values.pre_hardmode_values.purified_drink_thirst_incr");
                                thirst_threshold = plugin.getConfig().getInt("game_values.pre_hardmode_values.threshold_thirst_sys");
                                buffer = thirst_threshold * -100;
                            }

                            player.setMetadata("Thirst", new FixedMetadataValue(plugin, addThirst(current_thirst, thirst_add)));
                            player.setMetadata("thirst_sys_var", new FixedMetadataValue(plugin, buffer));
                        }
                    }
                }
                else if (potionMeta.getDisplayName().equalsIgnoreCase(ChatColor.LIGHT_PURPLE + "Antidote Bottle")){
                    if (potionMeta.getLore().contains(ChatColor.GRAY + "Purges the toxins from your body.")) {
                        //TODO change this to use config item deserialization
                        if (potionMeta.getEnchantLevel(Enchantment.DURABILITY ) > 50) {
                            double current_tox = player.getMetadata("Toxicity").get(0).asDouble();

                            double tox_decr;
                            if (isHardmode) {
                                tox_decr = plugin.getConfig().getInt("game_values.hardmode_values.antidote_drink_tox_decr");
                            }
                            else {
                                tox_decr = plugin.getConfig().getInt("game_values.pre_hardmode_values.antidote_drink_tox_decr");
                            }

                            player.setMetadata("Toxicity", new FixedMetadataValue(plugin, decrTox(current_tox, tox_decr)));
                        }
                    }
                }
            }
        }
    }

    public double addThirst (double current_thirst, double thirst_add) {
        if (current_thirst + thirst_add <= 100) {
            return current_thirst + thirst_add;
        }
        else {
            return 100;
        }
    }

    public double decrTox (double current_tox, double tox_decr) {
        if (current_tox - tox_decr >= 0) {
            return current_tox - tox_decr;
        }
        else {
            return 0;
        }
    }
}
