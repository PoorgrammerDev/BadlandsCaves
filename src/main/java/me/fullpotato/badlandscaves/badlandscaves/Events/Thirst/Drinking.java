package me.fullpotato.badlandscaves.badlandscaves.Events.Thirst;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
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

public class Drinking implements Listener {
    private BadlandsCaves plugin;
    public Drinking(BadlandsCaves bcav) {
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

                player.setMetadata("Thirst", new FixedMetadataValue(plugin, Math.min(current_thirst + thirst_add, 100)));

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
                ItemStack purified_water = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.purified_water").getValues(true));
                ItemStack antidote = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.antidote").getValues(true));
                ItemStack mana_potion = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.mana_potion").getValues(true));

                //testing if purified
                if (item.isSimilar(purified_water)) {
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

                            player.setMetadata("Thirst", new FixedMetadataValue(plugin, Math.min(current_thirst + thirst_add, 100)));
                            player.setMetadata("thirst_sys_var", new FixedMetadataValue(plugin, buffer));
                        }
                }
                else if (item.isSimilar(antidote)) {
                    double current_tox = player.getMetadata("Toxicity").get(0).asDouble();
                    double tox_decr;
                    if (isHardmode) {
                        tox_decr = plugin.getConfig().getInt("game_values.hardmode_values.antidote_drink_tox_decr");
                    }
                    else {
                        tox_decr = plugin.getConfig().getInt("game_values.pre_hardmode_values.antidote_drink_tox_decr");
                    }

                    player.setMetadata("Toxicity", new FixedMetadataValue(plugin, Math.max(current_tox - tox_decr, 0)));
                }
                else if (item.isSimilar(mana_potion)) {
                    int has_powers = player.getMetadata("has_supernatural_powers").get(0).asInt();
                    if (has_powers >= 1.0) {
                        int Mana = player.getMetadata("Mana").get(0).asInt();
                        int max_mana = player.getMetadata("max_mana").get(0).asInt();
                        int new_mana = Math.min(Mana + 100, max_mana);
                        player.setMetadata("Mana", new FixedMetadataValue(plugin, new_mana));
                    }
                }
            }
        }
    }
}
