package me.fullpotato.badlandscaves.CustomItems.Using.Starlight.Nebulites.Mechanisms;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.Crafting.Starlight.StarlightArmor;
import me.fullpotato.badlandscaves.CustomItems.Crafting.Starlight.StarlightCharge;
import me.fullpotato.badlandscaves.CustomItems.Crafting.Starlight.StarlightTools;
import me.fullpotato.badlandscaves.CustomItems.Using.Starlight.Nebulites.Nebulite;
import me.fullpotato.badlandscaves.CustomItems.Using.Starlight.Nebulites.NebuliteManager;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class NebuliteCounterattack extends BukkitRunnable implements Listener {
    private final BadlandsCaves plugin;
    private final Random random = new Random();
    private final StarlightArmor starlightArmor;
    private final StarlightTools starlightTools;
    private final StarlightCharge starlightCharge;
    private final NebuliteManager nebuliteManager;

    public NebuliteCounterattack(BadlandsCaves plugin, StarlightArmor starlightArmor, StarlightTools starlightTools, StarlightCharge starlightCharge, NebuliteManager nebuliteManager) {
        this.plugin = plugin;
        this.starlightArmor = starlightArmor;
        this.starlightTools = starlightTools;
        this.starlightCharge = starlightCharge;
        this.nebuliteManager = nebuliteManager;
    }


    @Override
    public void run() {
        if (plugin.getSystemConfig().getBoolean("hardmode")) {
            plugin.getServer().getOnlinePlayers().forEach(player -> {
               if ((byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == (byte) 0) {
                   final ItemStack mainHand = player.getEquipment().getItemInMainHand();
                   final ItemStack offHand = player.getEquipment().getItemInOffHand();

                   ItemStack shield = null;
                   if (starlightTools.isStarlightShield(mainHand)) shield = mainHand;
                   else if (starlightTools.isStarlightShield(offHand)) shield = offHand;

                   if (shield != null && starlightCharge.getCharge(shield) > 0) {
                       final Nebulite[] nebulites = nebuliteManager.getNebulites(shield);
                       for (Nebulite nebulite : nebulites) {
                           if (nebulite != null && nebulite.equals(Nebulite.COUNTERATTACK)) {
                               byte tracker = (byte) PlayerScore.COUNTERATTACK_TRACKER.getScore(plugin, player);

                               if (player.isBlocking() && tracker == (byte) 0) {
                                   starlightCharge.setCharge(shield, starlightCharge.getCharge(shield) - 5);
                                   tracker = 5;
                               }
                               else {
                                   if (tracker > 0) {
                                       if (tracker == 1) {
                                           tracker = -50;
                                       }
                                       else {
                                           tracker--;
                                       }
                                   }
                                   else if (!player.isBlocking() && tracker < 0) {
                                       if (tracker == -1) {
                                           player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 1, 1);
                                       }
                                       tracker++;
                                   }
                               }

                               PlayerScore.COUNTERATTACK_TRACKER.setScore(plugin, player, tracker);
                               return;
                           }
                       }

                   }
               }
            });
        }
    }

    @EventHandler
    public void shieldCounter (EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            double damage = event.getDamage();
            if (player.isBlocking() && damage > 0 && event.getFinalDamage() <= 0) {
                final byte tracker = (byte) PlayerScore.COUNTERATTACK_TRACKER.getScore(plugin, player);
                if (tracker > 0) {
                    event.setCancelled(true);
                    player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20, (int) (0.2 * tracker * damage), true, true));
                    player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, SoundCategory.PLAYERS, 1, 0.5F);
                    player.sendTitle("", ChatColor.RESET.toString() + ChatColor.of("#db2121") + ChatColor.BOLD + "COUNTER!", 0, 15, 5);
                }
            }
        }
    }
}
