package me.fullpotato.badlandscaves.badlandscaves.Runnables.Effects;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.badlandscaves.Util.AddPotionEffect;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
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

            int deaths = player.getMetadata("Deaths").get(0).asInt();
            boolean supernatural = (player.hasMetadata("has_supernatural_powers") ? player.getMetadata("has_supernatural_powers").get(0).asInt() : 0) >= 1;
            int in_descension = player.hasMetadata("in_descension") ? player.getMetadata("in_descension").get(0).asInt() : 0;

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

            if (in_descension != 1 && in_descension != 2) {

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
                    poison_lvl = supernatural ? 5 : 10;
                    hunger_lvl = supernatural ? 5 : 10;
                    slow_lvl = supernatural ? 3 : 10;
                    slowmine_lvl = supernatural ? 5 : 10;
                    weakness = supernatural ? 5 : 10;
                    unluck = supernatural ? 50 : 10;
                    blindness = true;

                    health = 1.0;
                }

                //60+ DEATHS----------------------------------------------
                else if (deaths >= 60) {
                    poison_lvl = supernatural ? 0 : 5;
                    hunger_lvl = supernatural ? 3 : 6;
                    slow_lvl = supernatural ? 3 : 6;
                    slowmine_lvl = supernatural ? 3 : 5;
                    weakness = supernatural ? 3 : 5;
                    unluck = supernatural ? 20 : 6;
                    blindness = true;

                    health = supernatural ? 6.0 : 1.0;
                }

                //55+ DEATHS (AFFECTS SUPERNATURAL USERS ONLY)------------
                else if (supernatural && deaths >= 55) {
                    hunger_lvl = 1;
                    slow_lvl = 2;
                    slowmine_lvl = 2;
                    weakness = 2;
                    unluck = 10;

                    health = 10.0;

                }

                //51+ DEATHS (AFFECTS SUPERNATURAL USERS ONLY)------------
                else if (supernatural && deaths >= 51) {
                    slow_lvl = 2;
                    slowmine_lvl = 1;
                    weakness = 1;
                    unluck = 10;

                    health = 10.0;

                }

                //50+ DEATHS----------------------------------------------
                else if (deaths >= 50) {
                    poison_lvl = supernatural ? 0 : 2;
                    hunger_lvl = supernatural ? 0 : 4;
                    slow_lvl = supernatural ? 0 : 4;
                    slowmine_lvl = supernatural ? 0 : 3;
                    weakness = supernatural ? 0 : 3;
                    unluck = supernatural ? 10 : 4;
                    blindness = !supernatural;

                    health = supernatural ? 20.0 : 6.0;

                }

                //30+ DEATHS----------------------------------------------
                else if (deaths >= 30) {
                    hunger_lvl = 2;
                    slow_lvl = 3;
                    slowmine_lvl = 2;
                    weakness = 2;
                    unluck = 3;
                    blindness = true;

                    health = 10.0;
                }

                //20+ DEATHS----------------------------------------------
                else if (deaths >= 20) {
                    slow_lvl = 2;
                    slowmine_lvl = 1;
                    weakness = 1;
                    unluck = 2;

                    health = 16.0;
                }

                //10+ DEATHS----------------------------------------------
                else if (deaths >= 10) {
                    slow_lvl = 2;
                    unluck = 2;
                    weakness = 1;
                }

                //6+ DEATHS-----------------------------------------------
                else if (deaths >= 6) {
                    slow_lvl = 1;
                    unluck = 1;
                }

                //5+ DEATHS-----------------------------------------------
                else if (deaths == 5) {
                }

                //1+ DEATHS-----------------------------------------------
                else if (deaths >= 1) {
                    AddPotionEffect.addPotionEffect(player, new PotionEffect(PotionEffectType.FAST_DIGGING, 90, 0, true, false), true);
                    AddPotionEffect.addPotionEffect(player, new PotionEffect(PotionEffectType.SPEED, 90, 0, true, false), true);
                    AddPotionEffect.addPotionEffect(player, new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 90, 0, true, false), true);
                }

                //NO DEATHS-----------------------------------------------
                else if (deaths == 0) {
                    speed_lvl = 2;

                    AddPotionEffect.addPotionEffect(player, new PotionEffect(PotionEffectType.FAST_DIGGING, 90, 1, true, false), true);
                    AddPotionEffect.addPotionEffect(player, new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 90, 0, true, false), true);
                    AddPotionEffect.addPotionEffect(player, new PotionEffect(PotionEffectType.JUMP, 90, 1, true, false), true);
                    AddPotionEffect.addPotionEffect(player, new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 90, 1, true, false), true);
                    AddPotionEffect.addPotionEffect(player, new PotionEffect(PotionEffectType.LUCK, 90, 4, true, false), true);

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
                AddPotionEffect.addPotionEffect(player, new PotionEffect(PotionEffectType.WEAKNESS, 90, weakness - 1, true, false), true);
            }

            if (unluck > 0) {
                AddPotionEffect.addPotionEffect(player, new PotionEffect(PotionEffectType.UNLUCK, 90, unluck - 1, true, false), true);
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
