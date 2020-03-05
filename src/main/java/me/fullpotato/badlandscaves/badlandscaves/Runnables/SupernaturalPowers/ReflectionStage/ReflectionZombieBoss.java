package me.fullpotato.badlandscaves.badlandscaves.Runnables.SupernaturalPowers.ReflectionStage;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.badlandscaves.Events.SupernaturalPowers.Reflection.ReflectionBuild;
import me.fullpotato.badlandscaves.badlandscaves.NMS.ReflectionWorldNMS;
import me.fullpotato.badlandscaves.badlandscaves.Util.AddPotionEffect;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.KeyedBossBar;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Silverfish;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BlockIterator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class ReflectionZombieBoss extends BukkitRunnable {
    private BadlandsCaves plugin;
    private Zombie zombie;
    private World world = Bukkit.getWorld("world_reflection");
    public ReflectionZombieBoss(BadlandsCaves bcav) {
        plugin = bcav;
    }

    //TODO basically just test this entire thing

    @Override
    public void run () {
        final Random random = new Random();
        //get zombie
        ArrayList<Zombie> zombie_list = (ArrayList<Zombie>) (world.getEntitiesByClass(Zombie.class));
        if (zombie_list.isEmpty()) return;

        zombie = zombie_list.get(0);
        if (!zombie.getWorld().equals(world)) return;

        //get player
        Player player = null;
        for (Player ply : world.getEntitiesByClass(Player.class)) {
            if (ply.getGameMode().equals(GameMode.SURVIVAL) || ply.getGameMode().equals(GameMode.ADVENTURE)) {
                player = ply;
            }
        }
        if (player == null) return;


        //NOT RELATED TO ABILITY----------------------------------------------------------------------------------------
        double zombie_health = zombie.getHealth() / zombie.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
        //retreats if low health, regardless of ability cooldown
        if (zombie_health < 0.3) {
            if (zombie_health < 0.15) {
                zombie.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20, 4, false, false));
            }
            else {
                zombie.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20, 2, false, false));
            }
            final int retreating_target_timer = zombie.hasMetadata("retreating_target_timer") ? zombie.getMetadata("retreating_target_timer").get(0).asInt() : 0;
            if (retreating_target_timer <= 0) {
                Location retreat_location = getRetreatLocation(player, zombie, 10);
                if (retreat_location == null) {
                    zombie.setTarget(null);
                }
                else {
                    final int lifespan = 10;
                    Silverfish marker = summonMarker(retreat_location, lifespan);
                    zombie.setTarget(marker);
                    zombie.setMetadata("retreating_target_timer", new FixedMetadataValue(plugin, lifespan));
                }
            }
            else {
                zombie.setMetadata("retreating_target_timer", new FixedMetadataValue(plugin, retreating_target_timer - 1));
            }
        }

        //clone spawning and moving, healthbar
        CloneMechanism(player);
        displayHealth(zombie, player);

        //USING ABILITY-------------------------------------------------------------------------------------------------
        /*
        zombie can't use ability if:
         - cooldown not ready
         - under 15% health
         */
        if (zombie_health < 0.15) return;
        int cooldown = zombie.hasMetadata("teleport_cooldown") ? zombie.getMetadata("teleport_cooldown").get(0).asInt() : 0;
        if (cooldown > 0) {
            zombie.setMetadata("teleport_cooldown", new FixedMetadataValue(plugin, cooldown - 1));
            return;
        }


        final Location player_loc = player.getEyeLocation();

        //over 30% health
        if (zombie_health > 0.3) {
            //if zombie is farther away
            if (zombie.getLocation().distanceSquared(player_loc) > 25 && random.nextBoolean()) {
                //warp to player
                Location tele = random.nextBoolean() ? getLocationBehindPlayer(player) : getLocationNearPlayer(player, random, 5);
                if (tele == null) return;
                if (tele.distanceSquared(player_loc) > 0.25) {
                    skipTimeBase(zombie, player, tele, random, false);
                    timeSkipPlayerAction(player, random);

                    //small hit
                    if (random.nextBoolean()) {
                        if (zombie.getLocation().distanceSquared(player_loc) < 4) {
                            player.damage((random.nextDouble() * 1.5), zombie);
                        }
                    }
                }
            }
            //if zombie is near player
            else if (random.nextBoolean()) {
                Location tele = getAdvanceLocation(player, zombie, 5);
                if (tele == null) return;

                skipTimeBase(zombie, player, tele, random, false);
                if (random.nextInt(100) < 75) {
                    int pick_event = random.nextInt(100);
                    if (pick_event < 30) {
                        if (zombie.getLocation().distanceSquared(player_loc) < 4) {
                            player.playSound(player.getLocation(), Sound.ENTITY_BLAZE_HURT, 2, 0.5F);
                            player.playSound(player.getLocation(), Sound.BLOCK_CONDUIT_DEACTIVATE, 1.2F, 0.1F);
                            player.playSound(player.getLocation(), Sound.BLOCK_CONDUIT_DEACTIVATE, 1.2F, 0.5F);
                            player.playSound(player.getLocation(), Sound.BLOCK_CONDUIT_DEACTIVATE, 1.2F, 1);
                            player.playSound(player.getLocation(), Sound.BLOCK_CONDUIT_DEACTIVATE, 1.2F, 1.5F);
                            player.playSound(player.getLocation(), Sound.BLOCK_CONDUIT_DEACTIVATE, 1.2F, 2);
                            player.damage((random.nextDouble() * 5) + 5, zombie);
                            player.setVelocity(player.getVelocity().multiply(5));
                        }
                    }
                    else {
                        OraOraOra(zombie, player, random);
                    }
                }
                else {
                    timeSkipPlayerAction(player, random);
                }
            }
        }
        //under 30% health
        else {
            //retreat and heal
            Location retreat = getRetreatLocation(player, zombie, 10);

            if (retreat != null) {
                skipTimeBase(zombie, player, retreat, random, true);
                zombieHeal(zombie, player, random);

                if (zombie.getTarget() != null) {
                    if (zombie.getTarget() instanceof Silverfish) {
                        zombie.getTarget().remove();
                    }
                }
            }
        }
    }

    public void CloneMechanism (final Player player) {
        ReflectionWorldNMS nms = new ReflectionWorldNMS(player);
        if (player.hasMetadata("reflection_zombie") && player.getMetadata("reflection_zombie").get(0).asBoolean()) {
            nms.move(zombie);
        }
        else {
            nms.disguiseAsPlayer(zombie);
            player.setMetadata("reflection_zombie" , new FixedMetadataValue(plugin, true));
        }
    }

    public void skipTimeBase (final Zombie zombie, final Player player, final Location zombie_warp, final Random random, final boolean running) {
        final Location zombie_orig_loc = zombie.getLocation();
        final Location player_loc = player.getEyeLocation();

        zombie.teleport(zombie_warp);

        int cooldown = running ? random.nextInt(50) + 50 : random.nextInt(100) + 100;


        zombie.setMetadata("teleport_cooldown", new FixedMetadataValue(plugin, cooldown));
        warpPlayer(player, random);

        //effects
        player.spawnParticle(Particle.FLASH, zombie_orig_loc, 1);
        player.spawnParticle(Particle.PORTAL, zombie_orig_loc, 20, 1, 1, 1);
        player.spawnParticle(Particle.SMOKE_NORMAL, player_loc, 20, 1, 1, 1);
        player.playSound(player.getLocation(), Sound.BLOCK_BELL_USE, 1, 0.2F);
    }

    public void timeSkipPlayerAction (final Player player, final Random random) {
        if (random.nextInt(100) < 75) {
            final int pick_event = random.nextInt(4);
            switch (pick_event) {
                case 0:
                    playerEat(player, random);
                    break;
                case 1:
                    for (int a = 0; a < random.nextInt(15) + 10; a++) {
                        playerMineBlock(player, random);
                    }
                    break;
                case 2:
                    player.getInventory().setHeldItemSlot(random.nextInt(9));
                    break;
                case 3:
                    for (int a = 0; a < random.nextInt(15) + 10; a++) {
                        playerPlaceBlock(player, random);
                    }
                    break;
            }
        }
    }

    public void warpPlayer (final Player player, final Random random) {
        final Location player_loc = player.getEyeLocation();
        BlockIterator iterate = new BlockIterator(world, player_loc.toVector(), player.getVelocity(), 0, random.nextInt(3) + 2);

        Block block = iterate.next();
        while (iterate.hasNext()) {
            block = iterate.next();
            if (!block.isPassable()) {
                break;
            }
        }

        Location player_moving = block.getLocation().clone();
        if (!locationViable(player_moving)) {
            int tries = 0;
            do {
                Location test = getNearbyLocation(player_moving, random, 5);
                player_moving = test != null ? test : player_moving;
                tries++;
            } while (!locationViable(player_moving) && tries < 100);

        }

        player_moving.setYaw(player_loc.getYaw());
        player_moving.setPitch(player_loc.getPitch());
        player.teleport(player_moving);
    }

    public void playerEat (final Player player, final Random random) {
        for (int a = 0; a < 9; a++) {
            if (player.getInventory().getContents()[a] == null) continue;
            if (player.getInventory().getContents()[a].getType().isEdible()) {
                Material material = player.getInventory().getContents()[a].getType();

                if ((material.equals(Material.ENCHANTED_GOLDEN_APPLE) || material.equals(Material.GOLDEN_APPLE))) {
                    double health_percent = player.getHealth() / player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
                    if (health_percent <= 0.5) {

                        int random_seconds_passed = random.nextInt(200);
                        if (material.equals(Material.ENCHANTED_GOLDEN_APPLE)) {
                            AddPotionEffect.addPotionEffect(player, new PotionEffect(PotionEffectType.ABSORPTION, 2400 - random_seconds_passed, 3));
                            AddPotionEffect.addPotionEffect(player, new PotionEffect(PotionEffectType.REGENERATION, 400 - random_seconds_passed, 1));
                            AddPotionEffect.addPotionEffect(player, new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 6000 - random_seconds_passed, 0));
                            AddPotionEffect.addPotionEffect(player, new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 6000 - random_seconds_passed, 0));
                        }
                        else {
                            AddPotionEffect.addPotionEffect(player, new PotionEffect(PotionEffectType.ABSORPTION, 2400 - random_seconds_passed, 0));
                            AddPotionEffect.addPotionEffect(player, new PotionEffect(PotionEffectType.REGENERATION, 100 - random_seconds_passed, 1));
                        }
                    }
                }
                else {
                    if (player.getFoodLevel() < 20.0) {

                        //constants
                        ArrayList<Material> blacklisted = new ArrayList<>();
                        blacklisted.add(Material.ROTTEN_FLESH);
                        blacklisted.add(Material.SPIDER_EYE);
                        blacklisted.add(Material.PUFFERFISH);
                        blacklisted.add(Material.CHORUS_FRUIT);
                        blacklisted.add(Material.SUSPICIOUS_STEW);
                        blacklisted.add(Material.CHICKEN);
                        blacklisted.add(Material.POISONOUS_POTATO);
                        if (blacklisted.contains(material)) continue;

                        HashMap<Material, Integer> food_vals = new HashMap<>();
                        food_vals.put(Material.ENCHANTED_GOLDEN_APPLE, 4);
                        food_vals.put(Material.GOLDEN_APPLE, 4);
                        food_vals.put(Material.GOLDEN_CARROT, 6);
                        food_vals.put(Material.COOKED_MUTTON, 6);
                        food_vals.put(Material.COOKED_PORKCHOP, 8);
                        food_vals.put(Material.COOKED_SALMON, 6);
                        food_vals.put(Material.COOKED_BEEF, 8);
                        food_vals.put(Material.BAKED_POTATO, 5);
                        food_vals.put(Material.BREAD, 5);
                        food_vals.put(Material.COOKED_CHICKEN, 6);
                        food_vals.put(Material.COOKED_COD, 5);
                        food_vals.put(Material.COOKED_RABBIT, 5);
                        food_vals.put(Material.MUSHROOM_STEW, 6);
                        food_vals.put(Material.RABBIT_STEW, 10);
                        food_vals.put(Material.PUMPKIN_PIE, 8);

                        int food_add;
                        if (food_vals.containsKey(material)) {
                            food_add = food_vals.get(material);
                        }
                        else {
                            food_add = random.nextInt(3);
                        }

                        player.getInventory().setHeldItemSlot(a);
                        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_BURP, 1, 1);
                        player.setSaturation(player.getSaturation() + random.nextInt(20));
                        player.setFoodLevel(player.getFoodLevel() + food_add);
                        player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount() - 1);
                        return;
                    }
                }
            }
        }
    }

    public void playerMineBlock (final Player player, final Random random) {
        ArrayList<Material> pickaxes = new ArrayList<>();
        pickaxes.add(Material.WOODEN_PICKAXE);
        pickaxes.add(Material.STONE_PICKAXE);
        pickaxes.add(Material.IRON_PICKAXE);
        pickaxes.add(Material.DIAMOND_PICKAXE);
        pickaxes.add(Material.GOLDEN_PICKAXE);

        for (int a = 0; a < 9; a++) {
            if (player.getInventory().getContents()[a] == null) continue;
            if (pickaxes.contains(player.getInventory().getContents()[a].getType())) {
                final int range = 5;
                Block block = findAdequateMiningBlock(player, random, range);
                if (block != null) {
                    player.getInventory().setHeldItemSlot(a);

                    ItemStack item = player.getInventory().getItemInMainHand();
                    /*
                    //adds damage to item
                    Damageable meta = (Damageable) item.getItemMeta();
                    meta.setDamage(meta.getDamage() + 1);
                    item.setItemMeta((ItemMeta) meta);
                    */

                    block.breakNaturally(item);
                    break;
                }
            }
        }
    }

    public void playerPlaceBlock (final Player player, final Random random) {
        for (int a = 0; a < 9; a++) {
            if (player.getInventory().getContents()[a] == null) continue;
            if (player.getInventory().getContents()[a].getType().isBlock()) {
                Material material = player.getInventory().getContents()[a].getType();

                ArrayList<Material> blacklisted = new ArrayList<>();
                blacklisted.add(Material.BEDROCK);
                blacklisted.add(Material.BARRIER);
                blacklisted.add(Material.COMMAND_BLOCK);
                blacklisted.add(Material.STRUCTURE_BLOCK);
                blacklisted.add(Material.TNT);
                blacklisted.add(Material.CHEST);
                blacklisted.add(Material.SHULKER_BOX);
                blacklisted.add(Material.BLACK_SHULKER_BOX);
                blacklisted.add(Material.BLUE_SHULKER_BOX);
                blacklisted.add(Material.BROWN_SHULKER_BOX);
                blacklisted.add(Material.CYAN_SHULKER_BOX);
                blacklisted.add(Material.GRAY_SHULKER_BOX);
                blacklisted.add(Material.GREEN_SHULKER_BOX);
                blacklisted.add(Material.LIGHT_BLUE_SHULKER_BOX);
                blacklisted.add(Material.LIGHT_GRAY_SHULKER_BOX);
                blacklisted.add(Material.LIME_SHULKER_BOX);
                blacklisted.add(Material.MAGENTA_SHULKER_BOX);
                blacklisted.add(Material.ORANGE_SHULKER_BOX);
                blacklisted.add(Material.PINK_SHULKER_BOX);
                blacklisted.add(Material.PURPLE_SHULKER_BOX);
                blacklisted.add(Material.RED_SHULKER_BOX);
                blacklisted.add(Material.WHITE_SHULKER_BOX);
                blacklisted.add(Material.YELLOW_SHULKER_BOX);
                blacklisted.add(Material.ENDER_CHEST);
                blacklisted.add(Material.BARREL);
                blacklisted.add(Material.FURNACE);
                blacklisted.add(Material.BLAST_FURNACE);
                blacklisted.add(Material.DISPENSER);
                blacklisted.add(Material.DROPPER);
                blacklisted.add(Material.TRAPPED_CHEST);
                //TODO add more here
                if (blacklisted.contains(material)) continue;

                final int range = 5;
                final Block block = findAdequatePlacingBlock(player, random, range);

                if (block != null) {
                    ReflectionBuild build = new ReflectionBuild(plugin);
                    final Material existing_material = block.getType();
                    player.getInventory().setHeldItemSlot(a);
                    block.setType(material);
                    build.undoEdit(block.getLocation(), existing_material, material, random.nextInt(8) + 2, true);

                    player.playSound(block.getLocation(), Sound.BLOCK_STONE_PLACE, 1,1);
                    //player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount() - 1);
                }

            }
        }
    }

    public void zombieHeal (final Zombie zombie, final Player player, final Random random) {
        if (zombie.getLocation().distanceSquared(player.getLocation()) > 225) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (zombie.hasLineOfSight(player)) {
                        this.cancel();
                    }
                    else {
                        if (random.nextBoolean()) {
                            zombie.setHealth(Math.min(zombie.getHealth() + 1, zombie.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()));
                        }
                    }
                }
            }.runTaskTimerAsynchronously(plugin, 60, 20);
        }
    }

    public Location getLocationNearPlayer(final Player player, final Random random, final int range) {
        return getNearbyLocation(player.getLocation(), random, range);
    }

    public Location getNearbyLocation (final Location location, final Random random, final int range) {
        final int max_tries = 100;
        final int x = location.getBlockX();
        final int y = location.getBlockY();
        final int z = location.getBlockZ();
        final int random_bound_range = (range * 2) + 1;
        final double distance_range = Math.pow(range - 1, 2);

        int test_x, test_y, test_z;
        Location test_location;

        for (int a = 0; a < max_tries; a++) {
            test_x = random.nextInt(random_bound_range) + (x - range);
            test_y = random.nextInt(random_bound_range) + (y - range);
            test_z = random.nextInt(random_bound_range) + (z - range);

            test_location = new Location(location.getWorld(), test_x, test_y, test_z);
            if (location.distanceSquared(test_location) >= 0.25 && location.distanceSquared(test_location) <= distance_range) {
                if (locationViable(test_location)) {
                    return test_location;
                }
            }
        }
        return null;
    }

    public boolean locationViable (final Location test_location) {
        if (test_location.getBlock().isPassable()) {
            Location test_above = test_location.clone();
            test_above.add(0, 1, 0);
            if (test_above.getBlock().isPassable()) {
                Location test_below = test_location.clone();
                test_below.subtract(0, 1, 0);
                if (hasBlockBelow(test_below, 20)) {
                    return true;
                }
            }
        }
        return false;
    }

    public Location getLocationBehindPlayer (final Player player) {

        Location player_loc = player.getEyeLocation();
        BlockIterator iterator = new BlockIterator(world, player_loc.toVector(), player_loc.getDirection().multiply(-1), 0, 2);
        Block block = iterator.next();
        while (iterator.hasNext()) {
            block = iterator.next();
            if (!block.isPassable()) {
                break;
            }
        }

        if (block.isPassable()) {
            final Location tele = block.getLocation();
            if (locationViable(tele)) {
                return tele;
            }
        }
        return null;
    }

    public boolean hasBlockBelow (final Location location, final int limit) {
        for (int a = location.getBlockY(); a > Math.max(location.getBlockY() - limit, 0); a--) {
            Location test_below = location.clone();
            if (!test_below.getBlock().isPassable()) {
                return true;
            }
        }
        return false;
    }

    public Block findAdequateMiningBlock (final Player player, final Random random, final int range) {
        final double distance_range = Math.pow(range - 1, 2);
        final Location location = player.getLocation();
        final int max_tries = 100;
        final int x = location.getBlockX();
        final int y = location.getBlockY();
        final int z = location.getBlockZ();
        final int random_bound_range = (range * 2) + 1;

        int test_x, test_y, test_z;
        Location test_loc;

        for (int a = 0; a < max_tries; a++) {
            test_x = random.nextInt(random_bound_range) + (x - range);
            test_y = random.nextInt(random_bound_range) + (y - range);
            test_z = random.nextInt(random_bound_range) + (z - range);

            test_loc = new Location(player.getWorld(), test_x, test_y, test_z);
            Block test_block = test_loc.getBlock();
            if (test_block.getType().isSolid()) {
                if (player.getLocation().distanceSquared(test_loc) < distance_range) {
                    return test_block;
                }
            }
        }
        return null;
    }

    public Block findAdequatePlacingBlock (final Player player, final Random random, final int range) {
        final double distance_range = Math.pow(range - 1, 2);
        final Location location = player.getLocation();
        final int max_tries = 100;
        final int x = location.getBlockX();
        final int y = location.getBlockY();
        final int z = location.getBlockZ();
        final int random_bound_range = (range * 2) + 1;

        int test_x, test_y, test_z;
        Location test_loc;
        for (int a = 0; a < max_tries; a++) {
            test_x = random.nextInt(random_bound_range) + (x - range);
            test_y = random.nextInt(random_bound_range) + (y - range);
            test_z = random.nextInt(random_bound_range) + (z - range);

            test_loc = new Location(player.getWorld(), test_x, test_y, test_z);
            Block test_block = test_loc.getBlock();
            if (test_block.getType().isSolid()) {
                if (player.getLocation().distanceSquared(test_loc) < distance_range) {
                    ArrayList<Location> testing_adjacents = new ArrayList<>();

                    Location test_north = test_loc.clone();
                    test_north.subtract(0, 0, 1);
                    testing_adjacents.add(test_north);

                    Location test_east = test_loc.clone();
                    test_east.add(1, 0, 0);
                    testing_adjacents.add(test_east);

                    Location test_south = test_loc.clone();
                    test_south.add(0, 0, 1);
                    testing_adjacents.add(test_south);

                    Location test_west = test_loc.clone();
                    test_west.subtract(1, 0, 0);
                    testing_adjacents.add(test_west);

                    Location test_up = test_loc.clone();
                    test_up.add(0, 1, 0);
                    testing_adjacents.add(test_up);

                    Location test_down = test_loc.clone();
                    test_down.subtract(0, 1, 0);
                    testing_adjacents.add(test_down);

                    for (Location test : testing_adjacents) {
                        if (test.getBlock().getType().isAir()) {
                            return test.getBlock();
                        }
                    }
                }
            }
        }
        return null;
    }

    public Location getRetreatLocation (final Player player, final Zombie zombie, final int range) {
        return getFarthestLocation(player, zombie, range, false);
    }

    public Location getAdvanceLocation (final Player player, final Zombie zombie, final int range) {
        return getFarthestLocation(player, zombie, range, true);
    }

    public Location getFarthestLocation (final Player player, final Zombie zombie, final int range, final boolean reverse) {
        final Location zombie_location = zombie.getLocation();
        final Location player_location = player.getLocation();
        final double distance_range = Math.pow(range - 1, 2);

        double farthest_distance = reverse ? Integer.MAX_VALUE : 0;
        Location farthest_location = null;

        //finding farthest location from player
        for (int x = -range; x <= range; x++) {
            for (int y = -range; y <= range; y++) {
                for (int z = -range; z <= range; z++) {
                    Location test_loc = new Location(zombie.getWorld(), zombie_location.getX() + x, zombie_location.getY() + y, zombie_location.getZ() + z);
                    if (test_loc.distanceSquared(zombie_location) < distance_range) {
                        if ((test_loc.distanceSquared(player_location) < farthest_distance && reverse) || (test_loc.distanceSquared(player_location) > farthest_distance && !reverse)) {
                            if (locationViable(test_loc)) {
                                farthest_distance = test_loc.distanceSquared(player_location);
                                farthest_location = test_loc.clone();
                            }
                        }
                    }
                }
            }
        }

        return farthest_location;
    }

    public void displayHealth (final Zombie zombie, final Player player) {
        final NamespacedKey key = new NamespacedKey(plugin, "reflection_world_boss_health");
        KeyedBossBar health_bar = Bukkit.getBossBar(key);
        if (health_bar == null) {
            health_bar = Bukkit.createBossBar(key,(player.getDisplayName()), BarColor.BLUE, BarStyle.SEGMENTED_10);
        }

        if (!health_bar.getPlayers().contains(player)) health_bar.addPlayer(player);

        health_bar.setVisible(true);
        final double zombie_health = Math.max(Math.min(zombie.getHealth() / zombie.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue(), 1), 0);
        health_bar.setProgress(zombie_health);
    }

    public Silverfish summonMarker (Location location, int delay) {
        location.add(0, 1, 0);
        Silverfish marker = (Silverfish) world.spawnEntity(location, EntityType.SILVERFISH);
        marker.setInvulnerable(true);
        marker.setSilent(true);
        marker.setGravity(false);
        marker.setAI(false);
        marker.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 99999, 0, false, false));
        marker.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 99999, 5, false, false));

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!marker.isDead()) {
                    marker.remove();
                }
            }
        }.runTaskLaterAsynchronously(plugin, delay);

        return marker;
    }

    public void OraOraOra (final Zombie zombie, final Player player, final Random random) {
        if (player.getLocation().distanceSquared(zombie.getLocation()) < 4) {
            final int hits = random.nextInt(10);
            if (hits > 0) {
                final int[] hit = {0};
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (player.getLocation().distanceSquared(zombie.getLocation()) < 9) {
                            if (hit[0] >= hits) {
                                this.cancel();
                            }
                            else {
                                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1, 1);
                                player.spawnParticle(Particle.SWEEP_ATTACK, player.getLocation(), 20, 1, 1, 1, 0);
                                player.damage((random.nextDouble() / 2) + 0.5, zombie);
                                hit[0]++;
                            }
                        }
                        else {
                            this.cancel();
                        }
                    }
                }.runTaskTimer(plugin, 0, 5);
            }
        }
    }
}
