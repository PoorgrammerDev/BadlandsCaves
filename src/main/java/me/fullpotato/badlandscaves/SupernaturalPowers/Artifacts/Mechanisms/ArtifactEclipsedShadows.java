package me.fullpotato.badlandscaves.SupernaturalPowers.Artifacts.Mechanisms;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.Crafting.Voidmatter;
import me.fullpotato.badlandscaves.CustomItems.CustomItem;
import me.fullpotato.badlandscaves.NMS.EclipsedShadows.EclipsedShadowsNMS;
import me.fullpotato.badlandscaves.SupernaturalPowers.Artifacts.Artifact;
import me.fullpotato.badlandscaves.SupernaturalPowers.Artifacts.ArtifactManager;
import me.fullpotato.badlandscaves.SupernaturalPowers.SoulCampfire;
import me.fullpotato.badlandscaves.SupernaturalPowers.Spells.Runnables.ManaBarManager;
import me.fullpotato.badlandscaves.SupernaturalPowers.Spells.SwapPowers;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class ArtifactEclipsedShadows extends ArtifactMechanisms implements Listener {
    private final SoulCampfire soulCampfire;
    private final ManaBarManager manaBarManager;
    private final SwapPowers swapPowers;
    private final Random random;
    private final EclipsedShadowsNMS nms;
    final double cost = plugin.getOptionsConfig().getDouble("hardmode_values.artifact_costs.eclipsed_shadows");
    public ArtifactEclipsedShadows(BadlandsCaves plugin, Voidmatter voidmatter, ArtifactManager artifactManager, SoulCampfire soulCampfire, ManaBarManager manaBarManager, SwapPowers swapPowers, Random random) {
        super(plugin, voidmatter, artifactManager);
        nms = plugin.getEclipsedShadowsNMS();
        this.soulCampfire = soulCampfire;
        this.manaBarManager = manaBarManager;
        this.swapPowers = swapPowers;
        this.random = random;
    }

    @EventHandler
    public void activateSneak (PlayerToggleSneakEvent event) {
        final Player player = event.getPlayer();
        if (plugin.getSystemConfig().getBoolean("hardmode")) {
            if (player.isSneaking()) {
                final ItemStack offhand = player.getInventory().getItemInOffHand();
                if (offhand.isSimilar(plugin.getCustomItemManager().getItem(CustomItem.ECLIPSED_SHADOWS))) {
                    player.getInventory().setItemInOffHand(null);
                    swapPowers.AttemptDynamicSwap(player, false);
                    swapPowers.AttemptDynamicSwap(player, true);
                }

                if ((byte) PlayerScore.ECLIPSED_SHADOWS_ACTIVE.getScore(plugin, player) == 1) {
                    disable(player);
                }
            }
            else {
                if (player.getGameMode().equals(GameMode.SURVIVAL) || player.getGameMode().equals(GameMode.ADVENTURE)) {
                    if ((byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == 1 && (byte) PlayerScore.SWAP_WINDOW.getScore(plugin, player) == 1) {
                        final double mana = (double) PlayerScore.MANA.getScore(plugin, player);
                        if (mana >= cost) {
                            final EntityEquipment equipment = player.getEquipment();
                            if (equipment != null) {
                                if (soulCampfire.isSpellItem(equipment.getItemInOffHand())) {
                                    for (ItemStack armor : equipment.getArmorContents()) {
                                        if (armor != null && voidmatter.isVoidmatterArmor(armor)) {
                                            if (artifactManager.hasArtifact(player, Artifact.ECLIPSED_SHADOWS)) {
                                                manaBarManager.displayMessage(player, ChatColor.of("#5109eb") + "Eclipsed Shadows", 2, true);
                                                equipment.setItemInOffHand(plugin.getCustomItemManager().getItem(CustomItem.ECLIPSED_SHADOWS));
                                            }
                                            return;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void useItem (PlayerInteractEvent event) {
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || event.getAction().equals(Action.RIGHT_CLICK_AIR)) {
            final Player player = event.getPlayer();
            if ((byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == 1) {
                final ItemStack offhand = player.getInventory().getItemInOffHand();
                if (offhand.isSimilar(plugin.getCustomItemManager().getItem(CustomItem.ECLIPSED_SHADOWS))) {
                    final double mana = (double) PlayerScore.MANA.getScore(plugin, player);
                    if (mana >= cost) {
                        PlayerScore.MANA.setScore(plugin, player, mana - cost);
                        PlayerScore.MANA_BAR_ACTIVE_TIMER.setScore(plugin, player, 60);
                        PlayerScore.MANA_REGEN_DELAY_TIMER.setScore(plugin, player, plugin.getOptionsConfig().getInt("mana_regen_cooldown"));
                        PlayerScore.SPELL_COOLDOWN.setScore(plugin, player, 1);

                        player.getInventory().setItemInOffHand(null);
                        swapPowers.AttemptDynamicSwap(player, false);
                        swapPowers.AttemptDynamicSwap(player, true);

                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                enable(player);
                            }
                        }.runTaskLater(plugin, 1);

                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                PlayerScore.SPELL_COOLDOWN.setScore(plugin, player, 0);
                            }
                        }.runTaskLater(plugin, 2);
                    }
                }
            }
        }
    }

    @EventHandler
    public void preventTarget (EntityTargetLivingEntityEvent event) {
        if (event.getTarget() instanceof Player) {
            final Player player = (Player) event.getTarget();
            if (plugin.getSystemConfig().getBoolean("hardmode")) {
                if ((byte) PlayerScore.ECLIPSED_SHADOWS_ACTIVE.getScore(plugin, player) == 1) {
                    int chance = 100;

                    final double distance = player.getLocation().distanceSquared(event.getEntity().getLocation());
                    if (distance <= 0) chance = 0;
                    else {
                        chance -= (25.0 / distance);

                        final ItemStack main = player.getInventory().getItemInMainHand();
                        if (!main.getType().equals(Material.AIR)) chance /= 2;

                        final ItemStack off = player.getInventory().getItemInOffHand();
                        if (!off.getType().equals(Material.AIR)) chance /= 2;
                    }

                    if (chance > 0 && random.nextInt(100) < chance) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void damageCancel(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            final Player player = (Player) event.getEntity();
            if (plugin.getSystemConfig().getBoolean("hardmode")) {
                if ((byte) PlayerScore.ECLIPSED_SHADOWS_ACTIVE.getScore(plugin, player) == 1) {
                    disable(player);
                }
            }
        }
    }

    @EventHandler
    public void dieCancel (PlayerDeathEvent event) {
        final Player player = event.getEntity();
        if (plugin.getSystemConfig().getBoolean("hardmode")) {
            if ((byte) PlayerScore.ECLIPSED_SHADOWS_ACTIVE.getScore(plugin, player) == 1) {
                disable(player);
            }
        }
    }

    @EventHandler
    public void leaveCancel (PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        if (plugin.getSystemConfig().getBoolean("hardmode")) {
            if ((byte) PlayerScore.ECLIPSED_SHADOWS_ACTIVE.getScore(plugin, player) == 1) {
                disable(player);
            }
        }
    }


    public void enable (Player player) {
        PlayerScore.ECLIPSED_SHADOWS_ACTIVE.setScore(plugin, player, 1);
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 999999, 0, false, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 999999, 13, false, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 999999, 10, false, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 999999, 10, false, false));
        nms.setArmorVisible(player, false);
    }

    public void disable (Player player) {
        player.removePotionEffect(PotionEffectType.INVISIBILITY);
        player.removePotionEffect(PotionEffectType.SPEED);
        player.removePotionEffect(PotionEffectType.WEAKNESS);
        player.removePotionEffect(PotionEffectType.SLOW_DIGGING);
        nms.setArmorVisible(player, true);
        PlayerScore.ECLIPSED_SHADOWS_ACTIVE.setScore(plugin, player, 0);
    }
}
