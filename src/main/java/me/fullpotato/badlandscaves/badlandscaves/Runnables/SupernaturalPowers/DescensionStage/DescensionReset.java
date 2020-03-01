package me.fullpotato.badlandscaves.badlandscaves.Runnables.SupernaturalPowers.DescensionStage;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.BlockIterator;

import java.util.List;
import java.util.Random;

public class DescensionReset extends BukkitRunnable {
    private BadlandsCaves plugin;
    private World world = Bukkit.getWorld("world_descension");
    public DescensionReset(BadlandsCaves bcav) {
        plugin = bcav;
    }

    @Override
    public void run() {
        if (world == null) return;
        if (world.getGameRuleValue(GameRule.DO_WEATHER_CYCLE) == null) return;
        if (world.getGameRuleValue(GameRule.DO_WEATHER_CYCLE)) return;

        Player waiting = null;
        Player running = null;
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getWorld().equals(world) && !player.isDead() && (player.getGameMode().equals(GameMode.ADVENTURE) || player.getGameMode().equals(GameMode.SURVIVAL))) {
                int state = player.getMetadata("in_descension").get(0).asInt();
                //state 1: waiting in box
                if (state == 1) {
                    waiting = player;
                }
                //state 2: in field
                else if (state == 2) {
                    running = player;
                }
            }
        }

        if (waiting != null && running == null) {
            waiting.setMetadata("in_descension", new FixedMetadataValue(plugin, 2));

            //removes entities
            List<Entity> entities = world.getEntities();
            for (Entity entity : entities) {
                if (entity instanceof Item || entity instanceof Zombie || entity instanceof ExperienceOrb || entity instanceof EnderCrystal) {
                    entity.remove();
                }
            }

            //regenerating the world has been disabled - it doesn't seem to do much and causes too much lag
            /*
            //regenerate the world
            new makeDescensionStage(plugin, world).run();
             */

            //instead, we're just regenning the pillars / shrines.
            MakeDescensionStage mkDscStg = new MakeDescensionStage(plugin, world);
            mkDscStg.genDefaultShrines();

            //removing bridges, if any
            removeBridge();

            Team team = getDescensionTeam();
            spawnMobs(team);

            //setting timer
            int time_limit = plugin.getConfig().getInt("game_values.descension_time_limit");
            waiting.setMetadata("descension_timer", new FixedMetadataValue(plugin, time_limit));

            //deploy player in stage
            waiting.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 999999, 0));
            Location deploy = new Location(world, 65, 127, 0, 0, 90);
            waiting.teleport(deploy);
        }
    }

    //default spawnmobs
    public void spawnMobs(Team team) {
        int mob_cap = plugin.getConfig().getInt("game_values.descension_mob_limit");
        spawnMobs(team, mob_cap);
    }

    //spawnmobs w/ custom mobcap
    public void spawnMobs (Team team, int mob_cap) {
        Random random = new Random();
        Location origin = new Location(world, 0, 64, 0);
        int upper_lim = 80;
        int lower_lim = 50;
        int mob_rate = mob_cap / 14;
        int counter = 0;
        for (int x = -upper_lim; x <= upper_lim; x++) {
            for (int z = -upper_lim; z <= upper_lim; z++) {
                if (counter >= mob_cap) break;

                double rand = random.nextInt(1000);
                if (rand <= mob_rate) {
                    Location test = new Location(world, x, 64, z);
                    if (test.distance(origin) > lower_lim && test.distance(origin) < upper_lim) {
                        int y = world.getHighestBlockYAt(x, z);

                        test.setY(y);
                        Zombie zombie = (Zombie) world.spawnEntity(test, EntityType.ZOMBIE);
                        zombie.setCustomName(ChatColor.GRAY + "Lost Soul");
                        zombie.setBaby(false);
                        zombie.setSilent(true);
                        zombie.getEquipment().clear();
                        zombie.getEquipment().getItemInMainHand().setAmount(0);
                        zombie.getEquipment().getItemInOffHand().setAmount(0);
                        zombie.setRemoveWhenFarAway(false);
                        zombie.setInvulnerable(true);
                        zombie.setCollidable(false);
                        team.addEntry(zombie.getUniqueId().toString());
                        zombie.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 999999, 0, false, false), true);

                        counter++;
                    }
                }
            }
        }

        for (Entity entity : world.getEntities()) {
            if (entity instanceof Chicken) {
                entity.remove();
            }
        }
    }

    public Team getDescensionTeam() {
        //team
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        assert manager != null;
        Scoreboard board = manager.getMainScoreboard();
        String title = "DESCENSION_TEAM";
        Team team;
        if (board.getTeam(title) == null) {
            team = board.registerNewTeam(title);
            team.setAllowFriendlyFire(false);
            team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
        }
        else {
            team = board.getTeam(title);
        }

        return team;
    }

    public void removeBridge () {
        Location[] crystal_locations = {
                new Location(world, 46, 80, 46, 135, 0),
                new Location(world, -46, 80, 46, -135, 0),
                new Location(world, -46, 80, -46, -45, 0),
                new Location(world, 46, 80, -46, 45, 0),
        };

        for (Location location : crystal_locations) {
            BlockIterator iterator = new BlockIterator(location, 4, 65);

            Block lastBlock = iterator.next();

            while (iterator.hasNext()) {

                lastBlock = iterator.next();

                if (lastBlock.getType().equals(Material.BARRIER)) {
                    lastBlock.setType(Material.AIR);
                }
            }
        }
    }
}
