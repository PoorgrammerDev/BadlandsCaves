package me.fullpotato.badlandscaves.SupernaturalPowers.Spells;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.CustomItem;
import me.fullpotato.badlandscaves.SupernaturalPowers.Spells.Runnables.PossessionMobsRunnable;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import me.fullpotato.badlandscaves.Util.TargetEntity;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.RayTraceResult;

public class Possession extends UsePowers implements Listener {
    public Possession(BadlandsCaves bcav) {
        super(bcav);
    }

    @EventHandler
    public void use_possession(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        final boolean has_powers = (byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == 1;
        if (!has_powers) return;

        ItemStack possess = CustomItem.POSSESS.getItem();
        if (player.getInventory().getItemInOffHand().isSimilar(possess)) {
            Action action = event.getAction();
            if (action.equals(Action.RIGHT_CLICK_BLOCK) || action.equals(Action.RIGHT_CLICK_AIR)) {
                EquipmentSlot e = event.getHand();
                assert e != null;
                if (e.equals(EquipmentSlot.OFF_HAND)) {
                    event.setCancelled(true);
                    if ((byte) PlayerScore.SPELL_COOLDOWN.getScore(plugin, player) == 1) return;

                    boolean in_possession = (PlayerScore.IN_POSSESSION.hasScore(plugin, player)) && ((byte) PlayerScore.IN_POSSESSION.getScore(plugin, player) == 1);
                    if (in_possession) {
                        PlayerScore.IN_POSSESSION.setScore(plugin, player, 0);
                    } else {
                        double mana = ((double) PlayerScore.MANA.getScore(plugin, player));
                        int possession_mana_cost = plugin.getConfig().getInt("options.spell_costs.possess_mana_cost");
                        int possession_mana_drain = plugin.getConfig().getInt("options.spell_costs.possess_mana_drain");
                        double pos_drain_tick = possession_mana_drain / 20.0;
                        if (mana >= (pos_drain_tick + possession_mana_cost)) {
                            preventDoubleClick(player);
                            TargetEntity targetEntity = new TargetEntity();
                            LivingEntity target = targetEntity.findTargetLivingEntity(player.getEyeLocation(), 15, 0.2, player);
                            if (target != null && player.hasLineOfSight(target)) {
                                if (!(target instanceof Player) && !(target instanceof EnderDragon) && !(target instanceof Wither) && !(target.getPersistentDataContainer().has(new NamespacedKey(plugin, "augmented"), PersistentDataType.BYTE) && target.getPersistentDataContainer().get(new NamespacedKey(plugin, "augmented"), PersistentDataType.BYTE) == (byte) 1)) {
                                    boolean target_already_pos = target.hasMetadata("possessed") && target.getMetadata("possessed").get(0).asBoolean();

                                    if (!target_already_pos) {
                                        //cancel mana regen and keep mana bar active
                                        PlayerScore.MANA.setScore(plugin, player, mana - possession_mana_cost);
                                        PlayerScore.MANA_REGEN_DELAY_TIMER.setScore(plugin, player, 300);
                                        PlayerScore.MANA_BAR_ACTIVE_TIMER.setScore(plugin, player, 60);

                                        //readying the player and the target
                                        target.setMetadata("possessed", new FixedMetadataValue(plugin, true));
                                        PlayerScore.IN_POSSESSION.setScore(plugin, player, 1);
                                        PlayerScore.POSSESS_ORIG_WORLD.setScore(plugin, player, player.getWorld().getName());
                                        PlayerScore.POSSESS_ORIG_X.setScore(plugin, player, player.getLocation().getX());
                                        PlayerScore.POSSESS_ORIG_Y.setScore(plugin, player, player.getLocation().getY());
                                        PlayerScore.POSSESS_ORIG_Z.setScore(plugin, player, player.getLocation().getZ());
                                        target.setAI(false);

                                        //team
                                        ScoreboardManager manager = plugin.getServer().getScoreboardManager();
                                        assert manager != null;
                                        Scoreboard board = manager.getMainScoreboard();
                                        Team team = board.registerNewTeam("POS_" + player.getUniqueId().toString().substring(0, 12));
                                        team.setAllowFriendlyFire(false);
                                        team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.FOR_OTHER_TEAMS);
                                        team.addEntry(player.getUniqueId().toString());
                                        team.addEntry(target.getUniqueId().toString());
                                        player.setScoreboard(board);


                                        if (player.getGameMode().equals(GameMode.SURVIVAL))
                                            player.setGameMode(GameMode.ADVENTURE);

                                        for (Entity entity : player.getNearbyEntities(20, 20, 20)) {
                                            if (entity instanceof Mob) {
                                                final Mob mob = (Mob) entity;
                                                if (mob.getTarget() != null && mob.getTarget().equals(player)) {
                                                    mob.setTarget(null);
                                                }
                                            }
                                        }

                                        for (Entity entity : player.getNearbyEntities(10, 10, 10)) {
                                            if (entity instanceof Player) {
                                                Player powered = (Player) entity;
                                                if (!(powered.equals(player)) && ((byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, powered) == 1) && powered.getWorld().equals(player.getWorld()) && powered.getLocation().distanceSquared(player.getLocation()) < 100) {
                                                    powered.playSound(player.getLocation(), "custom.supernatural.possession.enter", SoundCategory.PLAYERS, 0.3F, 1);
                                                    powered.spawnParticle(Particle.REDSTONE, player.getLocation(), 10, 0.5, 0.5, 0.5, 0, new Particle.DustOptions(Color.GREEN, 1));
                                                }
                                            }
                                        }

                                        player.teleport(target, PlayerTeleportEvent.TeleportCause.PLUGIN);
                                        player.playSound(player.getLocation(), "custom.supernatural.possession.enter", SoundCategory.PLAYERS, 0.5F, 1);


                                        new PossessionMobsRunnable(plugin, player, target, team).runTaskTimer(plugin, 0, 0);
                                    }
                                }
                            }
                        } else {
                            notEnoughMana(player);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void prevent_damage (EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            Player player = (Player) event.getDamager();
            boolean in_possession = (PlayerScore.IN_POSSESSION.hasScore(plugin, player)) && ((byte) PlayerScore.IN_POSSESSION.getScore(plugin, player) == 1);

            if (in_possession) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void target_possesed (EntityTargetLivingEntityEvent event) {
        if (event.getTarget() instanceof Player) {
            Player player = (Player) event.getTarget();
            boolean in_possession = (PlayerScore.IN_POSSESSION.hasScore(plugin, player)) && ((byte) PlayerScore.IN_POSSESSION.getScore(plugin, player) == 1);

            if (in_possession) {
                event.setTarget(null);
                event.setCancelled(true);
                if (event.getEntity() instanceof Mob) {
                    Mob mob = (Mob) event.getEntity();
                    mob.setTarget(null);
                }
            }
        }
    }
}
