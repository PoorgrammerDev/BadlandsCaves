package me.fullpotato.badlandscaves.badlandscaves.Events.SupernaturalPowers;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.badlandscaves.Runnables.SupernaturalPowers.ManaBarManager;
import me.fullpotato.badlandscaves.badlandscaves.Runnables.SupernaturalPowers.PossessionMobsRunnable;
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
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.RayTraceResult;

public class Possession extends UsePowers implements Listener {
    public Possession(BadlandsCaves bcav) {
        super(bcav);
    }

    @EventHandler
    public void use_possession (PlayerInteractEvent event) {
        Player player = event.getPlayer();
        final boolean has_powers = player.getMetadata("has_supernatural_powers").get(0).asBoolean();
        if (!has_powers) return;

        ItemStack possess = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.possess").getValues(true));
        if (player.getInventory().getItemInOffHand().isSimilar(possess)) {
            Action action = event.getAction();
            if (action.equals(Action.RIGHT_CLICK_BLOCK) || action.equals(Action.RIGHT_CLICK_AIR)) {
                EquipmentSlot e = event.getHand();
                assert e != null;
                if (e.equals(EquipmentSlot.OFF_HAND)) {
                    event.setCancelled(true);
                    if (player.getMetadata("spell_cooldown").get(0).asBoolean()) return;

                   boolean in_possession = player.hasMetadata("in_possession") && player.getMetadata("in_possession").get(0).asBoolean();
                    if (in_possession) {
                        player.setMetadata("in_possession", new FixedMetadataValue(plugin, false));
                    }
                    else {
                        double mana = player.getMetadata("Mana").get(0).asDouble();
                        int possession_mana_cost = plugin.getConfig().getInt("game_values.possess_mana_cost");
                        int possession_mana_drain = plugin.getConfig().getInt("game_values.possess_mana_drain");
                        double pos_drain_tick = possession_mana_drain / 20.0;
                        if (mana >= (pos_drain_tick + possession_mana_cost)) {
                            preventDoubleClick(player);
                            World world = player.getWorld();
                            RayTraceResult result = world.rayTraceEntities(player.getEyeLocation().add(0.5,0.5,0.5),player.getLocation().getDirection(),10);
                            if (result != null && result.getHitEntity() != null) {
                                if (result.getHitEntity() instanceof LivingEntity) {
                                    LivingEntity target = (LivingEntity) result.getHitEntity();
                                    if (!(target instanceof Player) && !(target instanceof EnderDragon) && !(target instanceof Wither) && !(target.hasMetadata("augmented") && target.getMetadata("augmented").get(0).asBoolean())){
                                        boolean target_already_pos = target.hasMetadata("possessed") && target.getMetadata("possessed").get(0).asBoolean();

                                        if (!target_already_pos) {
                                            //cancel mana regen and keep mana bar active
                                            player.setMetadata("Mana", new FixedMetadataValue(plugin, mana - possession_mana_cost));
                                            player.setMetadata("mana_regen_delay_timer", new FixedMetadataValue(plugin, 15));
                                            player.setMetadata("mana_bar_active_timer", new FixedMetadataValue(plugin, 60));

                                            //readying the player and the target
                                            target.setMetadata("possessed", new FixedMetadataValue(plugin, true));
                                            player.setMetadata("in_possession", new FixedMetadataValue(plugin, true));
                                            player.setMetadata("possess_orig_world", new FixedMetadataValue(plugin, player.getWorld().getName()));
                                            player.setMetadata("possess_orig_x", new FixedMetadataValue(plugin, player.getLocation().getX()));
                                            player.setMetadata("possess_orig_y", new FixedMetadataValue(plugin, player.getLocation().getY()));
                                            player.setMetadata("possess_orig_z", new FixedMetadataValue(plugin, player.getLocation().getZ()));
                                            target.setAI(false);

                                            //team
                                            ScoreboardManager manager = Bukkit.getScoreboardManager();
                                            assert manager != null;
                                            Scoreboard board = manager.getMainScoreboard();
                                            Team team = board.registerNewTeam("POS_" + player.getUniqueId().toString().substring(0, 12));
                                            team.setAllowFriendlyFire(false);
                                            team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.FOR_OTHER_TEAMS);
                                            team.addEntry(player.getUniqueId().toString());
                                            team.addEntry(target.getUniqueId().toString());
                                            player.setScoreboard(board);




                                            if (player.getGameMode().equals(GameMode.SURVIVAL)) player.setGameMode(GameMode.ADVENTURE);

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
                                                    if (!(powered.equals(player)) && powered.getMetadata("has_supernatural_powers").get(0).asBoolean() && powered.getWorld().equals(player.getWorld()) && powered.getLocation().distanceSquared(player.getLocation()) < 100) {
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
                            }
                        }
                        else {
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
            boolean in_possession = player.hasMetadata("in_possession") && player.getMetadata("in_possession").get(0).asBoolean();

            if (in_possession) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void target_possesed (EntityTargetLivingEntityEvent event) {
        if (event.getTarget() instanceof Player) {
            Player player = (Player) event.getTarget();
            boolean in_possession = player.hasMetadata("in_possession") && player.getMetadata("in_possession").get(0).asBoolean();

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
