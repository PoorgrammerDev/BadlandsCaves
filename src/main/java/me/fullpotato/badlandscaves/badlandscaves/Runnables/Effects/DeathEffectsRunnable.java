package me.fullpotato.badlandscaves.badlandscaves.Runnables.Effects;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.badlandscaves.Util.AddPotionEffect;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class DeathEffectsRunnable extends BukkitRunnable {

    private BadlandsCaves plugin;
    public DeathEffectsRunnable(BadlandsCaves bcav ) {
        plugin = bcav;
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!player.getGameMode().equals(GameMode.SURVIVAL) && !player.getGameMode().equals(GameMode.ADVENTURE)) continue;

            final int deaths = player.getMetadata("Deaths").get(0).asInt();
            final boolean supernatural = (player.hasMetadata("has_supernatural_powers") ? player.getMetadata("has_supernatural_powers").get(0).asInt() : 0) >= 1;
            final int in_descension = player.hasMetadata("in_descension") ? player.getMetadata("in_descension").get(0).asInt() : 0;
            final boolean in_reflection = player.hasMetadata("in_reflection") && player.getMetadata("in_reflection").get(0).asBoolean();

            //passing into main player effects
            int poison_lvl = 0;
            int hunger_lvl = 0;
            int slow_lvl = 0;
            int slowmine_lvl = 0;
            int speed_lvl = 0;

            //keeping local
            int weakness = 0;
            int unluck = 0;
            boolean blindness = false;
            double health = 20.0;

            if (in_descension != 1 && in_descension != 2 && !in_reflection) {

                //100+ DEATHS---------------------------------------------
                if (deaths >= 100) {
                    poison_lvl = 10;
                    hunger_lvl = 10;
                    slow_lvl = 10;
                    slowmine_lvl = 10;
                    weakness = 10;
                    unluck = supernatural ? 99 : 10;
                    blindness = true;

                    health = 1.0;
                }

                //80+ DEATHS----------------------------------------------
                else if (deaths >= 80) {
                    poison_lvl = supernatural ? 8 : 10;
                    hunger_lvl = supernatural ? 8 : 10;
                    slow_lvl = supernatural ? 8 : 10;
                    slowmine_lvl = supernatural ? 8 : 10;
                    weakness = supernatural ? 8 : 10;
                    unluck = supernatural ? 50 : 10;
                    blindness = true;

                    health = 1.0;
                }

                //60+ DEATHS----------------------------------------------
                else if (deaths >= 60) {
                    poison_lvl = supernatural ? 0 : 5;
                    hunger_lvl = supernatural ? 5 : 6;
                    slow_lvl = supernatural ? 5 : 6;
                    slowmine_lvl = supernatural ? 4 : 5;
                    weakness = supernatural ? 4 : 5;
                    unluck = supernatural ? 25 : 6;
                    blindness = true;

                    health = supernatural ? 4.0 : 1.0;
                }

                //50+ DEATHS----------------------------------------------
                else if (deaths >= 50) {
                    poison_lvl = supernatural ? 0 : 2;
                    hunger_lvl = supernatural ? 2 : 4;
                    slow_lvl = supernatural ? 3 : 4;
                    slowmine_lvl = supernatural ? 2 : 3;
                    weakness = supernatural ? 2 : 3;
                    unluck = supernatural ? 10 : 4;
                    blindness = true;

                    health = supernatural ? 10.0 : 6.0;
                }

                //30+ DEATHS----------------------------------------------
                else if (deaths >= 30) {
                    hunger_lvl = supernatural ? 1 : 2;
                    slow_lvl = supernatural ? 2 : 3;
                    slowmine_lvl = supernatural ? 1 : 2;
                    weakness = supernatural ? 1 : 2;
                    unluck = supernatural ? 10 : 3;
                    blindness = !supernatural;

                    health = supernatural ? 13.0 : 10.0;
                }

                //20+ DEATHS----------------------------------------------
                else if (deaths >= 20) {
                    slow_lvl = supernatural ? 1 : 2;
                    slowmine_lvl = supernatural ? 0 : 1;
                    weakness = supernatural ? 1 : 1;
                    unluck = supernatural ? 10 : 2;

                    health = 16.0;
                }

                //10+ DEATHS----------------------------------------------
                else if (deaths >= 10) {
                    slow_lvl = supernatural ? 1 : 2;
                    unluck = supernatural ? 10 : 2;
                    weakness = supernatural ? 0 : 1;
                }

                //6+ DEATHS-----------------------------------------------
                else if (deaths >= 6) {
                    if (supernatural) {
                        unluck = 10;
                    }
                    else {
                        slow_lvl = 1;
                        unluck = 1;
                    }
                }

                //5+ DEATHS-----------------------------------------------
                else if (deaths == 5) {
                    if (supernatural) {
                        unluck = 10;
                        speed_lvl = 1;
                    }
                }

                //1+ DEATHS-----------------------------------------------
                else if (deaths >= 1) {
                    if (supernatural) {
                        unluck = 10;
                        speed_lvl = 1;
                        AddPotionEffect.addPotionEffect(player, new PotionEffect(PotionEffectType.FAST_DIGGING, 90, 0, true, false));
                    }
                    else {
                        speed_lvl = 1;
                        AddPotionEffect.addPotionEffect(player, new PotionEffect(PotionEffectType.FAST_DIGGING, 90, 0, true, false));
                        AddPotionEffect.addPotionEffect(player, new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 90, 0, true, false));
                    }
                }

                //NO DEATHS-----------------------------------------------
                else if (deaths == 0) {
                    if (supernatural) {
                        speed_lvl = 1;
                        unluck = 10;
                        AddPotionEffect.addPotionEffect(player, new PotionEffect(PotionEffectType.FAST_DIGGING, 90, 0, true, false));
                        AddPotionEffect.addPotionEffect(player, new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 90, 0, true, false));
                    }
                    else {
                        speed_lvl = 2;
                        AddPotionEffect.addPotionEffect(player, new PotionEffect(PotionEffectType.FAST_DIGGING, 90, 1, true, false));
                        AddPotionEffect.addPotionEffect(player, new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 90, 0, true, false));
                        AddPotionEffect.addPotionEffect(player, new PotionEffect(PotionEffectType.JUMP, 90, 1, true, false));
                        AddPotionEffect.addPotionEffect(player, new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 90, 1, true, false));
                        AddPotionEffect.addPotionEffect(player, new PotionEffect(PotionEffectType.LUCK, 90, 4, true, false));
                    }

                    health = 40.0;
                }
            }


            //passes this into main player effects
            player.setMetadata("deaths_debuff_slowmine_lvl", new FixedMetadataValue(plugin, slowmine_lvl));
            player.setMetadata("deaths_debuff_slow_lvl", new FixedMetadataValue(plugin, slow_lvl));
            player.setMetadata("deaths_debuff_hunger_lvl", new FixedMetadataValue(plugin, hunger_lvl));
            player.setMetadata("deaths_debuff_poison_lvl", new FixedMetadataValue(plugin, poison_lvl));
            player.setMetadata("deaths_buff_speed_lvl", new FixedMetadataValue(plugin, speed_lvl));

            //only death-related effects
            if (weakness > 0) {
                AddPotionEffect.addPotionEffect(player, new PotionEffect(PotionEffectType.WEAKNESS, 90, weakness - 1, true, false));
            }

            if (unluck > 0) {
                AddPotionEffect.addPotionEffect(player, new PotionEffect(PotionEffectType.UNLUCK, 90, unluck - 1, true, false));
            }

            if (blindness) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 90, 0, true, false));
            }

            if (player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue() != health) {
                player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(health);
            }



        }
    }
}
