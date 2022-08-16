package me.fullpotato.badlandscaves.SupernaturalPowers.Spells;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.CustomItem;
import me.fullpotato.badlandscaves.SupernaturalPowers.Spells.Runnables.PossessionMobsRunnable;
import me.fullpotato.badlandscaves.Util.ParticleShapes;
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
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

public class Possession extends UsePowers implements Listener {
    private final World backrooms;
    private final int cost = plugin.getOptionsConfig().getInt("spell_costs.possess_mana_cost");
    private final int drain = plugin.getOptionsConfig().getInt("spell_costs.possess_mana_drain");
    private final ParticleShapes particleShapes;
    public Possession(BadlandsCaves bcav, ParticleShapes particleShapes) {
        super(bcav, particleShapes);
        this.particleShapes = particleShapes;
        this.backrooms = plugin.getServer().getWorld(plugin.getBackroomsWorldName());
    }

    @EventHandler
    public void use_possession(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        final boolean has_powers = (byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == 1;
        if (!has_powers) return;

        ItemStack possess = plugin.getCustomItemManager().getItem(CustomItem.POSSESS);
        if (player.getInventory().getItemInOffHand().isSimilar(possess)) {
            Action action = event.getAction();
            if (action.equals(Action.RIGHT_CLICK_BLOCK) || action.equals(Action.RIGHT_CLICK_AIR)) {
                EquipmentSlot e = event.getHand();
                assert e != null;
                if (e.equals(EquipmentSlot.OFF_HAND)) {
                    event.setCancelled(true);
                    if ((byte) PlayerScore.SPELL_COOLDOWN.getScore(plugin, player) == 1) return;
                    if (((int) PlayerScore.SPELLS_SILENCED_TIMER.getScore(plugin, player) > 0)) return;
                    if (attemptSilence(player)) return;
                    if (player.getWorld().equals(backrooms)) return;

                    boolean in_possession = (PlayerScore.IN_POSSESSION.hasScore(plugin, player)) && ((byte) PlayerScore.IN_POSSESSION.getScore(plugin, player) == 1);
                    boolean has_doppelganger = (PlayerScore.DIGGING_DOPPELGANGER_ACTIVE.hasScore(plugin, player)) && ((byte) PlayerScore.DIGGING_DOPPELGANGER_ACTIVE.getScore(plugin, player) == 1);
                    if (in_possession) {
                        PlayerScore.IN_POSSESSION.setScore(plugin, player, 0);
                    }
                    else if (has_doppelganger) {
                        PlayerScore.DIGGING_DOPPELGANGER_ACTIVE.setScore(plugin, player, 0);
                    }
                    else {
                        final double mana = ((double) PlayerScore.MANA.getScore(plugin, player));
                        final double pos_drain_tick = drain / 20.0;
                        if (mana >= (pos_drain_tick + cost)) {
                            preventDoubleClick(player);
                            TargetEntity targetEntity = new TargetEntity();
                            final LivingEntity target = targetEntity.findTargetLivingEntity(player.getEyeLocation(), 15, 0.2, 0.2, false, player);
                            if (target != null && player.hasLineOfSight(target)) {
                                if (canPossess(target)) {
                                    boolean target_already_pos = target.hasMetadata("possessed") && target.getMetadata("possessed").get(0).asBoolean();
                                    if (!target_already_pos) {
                                        enterPossession(player, target);
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

    public void enterPossession (Player player, LivingEntity target) {
        final double mana = ((double) PlayerScore.MANA.getScore(plugin, player));

        //cancel mana regen and keep mana bar active
        PlayerScore.MANA.setScore(plugin, player, mana - cost);
        PlayerScore.MANA_REGEN_DELAY_TIMER.setScore(plugin, player, plugin.getOptionsConfig().getInt("mana_regen_cooldown"));
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

    @EventHandler
    public void forceExit (PlayerSwapHandItemsEvent event) {
        final Player player = event.getPlayer();
        if ((byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == 1) {
            if ((byte) PlayerScore.IN_POSSESSION.getScore(plugin, player) == 1 || (byte) PlayerScore.DIGGING_DOPPELGANGER_ACTIVE.getScore(plugin, player) == 1) {
                final ItemStack item = event.getMainHandItem();
                if (item != null) {
                    for (ActivePowers value : ActivePowers.values()) {
                        if (item.isSimilar(plugin.getCustomItemManager().getItem(value.getItem()))) {
                            event.setCancelled(true);
                            PlayerScore.IN_POSSESSION.setScore(plugin, player, 0);
                            PlayerScore.DIGGING_DOPPELGANGER_ACTIVE.setScore(plugin, player, 0);
                            return;
                        }
                    }
                }
            }
        }
    }

    public boolean canPossess (LivingEntity entity) {
        if (entity instanceof Player || entity instanceof EnderDragon || entity instanceof Wither) return false;

        final PersistentDataContainer container = entity.getPersistentDataContainer();
        final NamespacedKey ascended = new NamespacedKey(plugin, "ascended");
        if (container.has(ascended, PersistentDataType.BYTE)) {
            final Byte result = container.get(ascended, PersistentDataType.BYTE);
            if (result != null) {
                return result == 0;
            }
        }

        return true;
    }
}
