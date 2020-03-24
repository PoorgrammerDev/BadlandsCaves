package me.fullpotato.badlandscaves.badlandscaves.Events.SupernaturalPowers;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.badlandscaves.Runnables.SupernaturalPowers.PossessionMobsRunnable;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.RayTraceResult;

public class Possession implements Listener {
    private BadlandsCaves plugin;
    public Possession(BadlandsCaves bcav) {
        plugin = bcav;
    }

    private Player player;
    private LivingEntity target;

    @EventHandler
    public void use_possession (PlayerInteractEvent event) {
        player = event.getPlayer();
        int has_powers = player.getMetadata("has_supernatural_powers").get(0).asInt();
        if (has_powers < 1.0) return;

        ItemStack possess = ItemStack.deserialize(plugin.getConfig().getConfigurationSection("items.possess").getValues(true));
        if (player.getInventory().getItemInOffHand().isSimilar(possess)) {
            Action action = event.getAction();
            if (action.equals(Action.RIGHT_CLICK_BLOCK) || action.equals(Action.RIGHT_CLICK_AIR)) {
                EquipmentSlot e = event.getHand();
                assert e != null;
                if (e.equals(EquipmentSlot.OFF_HAND)) {
                    event.setCancelled(true);
                   boolean in_possession = player.hasMetadata("in_possession") && player.getMetadata("in_possession").get(0).asBoolean();
                    if (in_possession) {
                        player.setMetadata("in_possession", new FixedMetadataValue(plugin, false));
                    }
                    else {
                        double mana = player.getMetadata("Mana").get(0).asDouble();
                        int possession_mana_drain = plugin.getConfig().getInt("game_values.possession_mana_drain");
                        double pos_drain_tick = possession_mana_drain / 20.0;
                        if (mana >= pos_drain_tick) {
                            World world = player.getWorld();
                            RayTraceResult result = world.rayTraceEntities(player.getEyeLocation().add(0.5,0.5,0.5),player.getLocation().getDirection(),10);
                            if (result != null && result.getHitEntity() != null) {
                                if (result.getHitEntity() instanceof LivingEntity) {
                                    target = (LivingEntity) result.getHitEntity();
                                    if (!(target instanceof Player) && !(target instanceof EnderDragon) && !(target instanceof Wither) && !(target.hasMetadata("augmented") && target.getMetadata("augmented").get(0).asBoolean())){
                                        boolean target_already_pos = target.hasMetadata("possessed") && target.getMetadata("possessed").get(0).asBoolean();

                                        if (!target_already_pos) {
                                            //cancel mana regen and keep mana bar active
                                            player.setMetadata("mana_regen_delay_timer", new FixedMetadataValue(plugin, 30));
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
                                            player.teleport(target);

                                            new PossessionMobsRunnable(plugin, player, target, team).runTaskTimer(plugin, 0, 0);
                                        }
                                    }
                                }
                            }
                        }
                        else {
                            player.setMetadata("mana_needed_timer", new FixedMetadataValue(plugin, 5));
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
