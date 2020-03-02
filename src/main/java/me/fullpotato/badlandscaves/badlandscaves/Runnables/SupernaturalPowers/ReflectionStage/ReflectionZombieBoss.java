package me.fullpotato.badlandscaves.badlandscaves.Runnables.SupernaturalPowers.ReflectionStage;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.badlandscaves.NMS.ReflectionWorldNMS;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
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

    //TODO read below
    /*
    also if the player has placeable blocks, it can just randomly place it
    give the zombie a medium ranged attack that'll incentivize the player to run away
        */

    @Override
    public void run () {
        final Random random = new Random();
        ArrayList<Zombie> zombie_list = (ArrayList<Zombie>) (world.getEntitiesByClass(Zombie.class));
        if (zombie_list.isEmpty()) return;

        zombie = zombie_list.get(0);
        if (!zombie.getWorld().equals(world)) return;

        //clone spawning and moving
        ArrayList<Player> players = (ArrayList<Player>) (world.getEntitiesByClass(Player.class));
        if (!players.isEmpty()) {
            CloneMechanism(players.get(0));
        }


        int cooldown = zombie.hasMetadata("teleport_cooldown") ? zombie.getMetadata("teleport_cooldown").get(0).asInt() : 0;
        if (zombie.getTarget() == null || !(zombie.getTarget() instanceof Player) || cooldown > 0) {
            if (cooldown > 0) {
                zombie.setMetadata("teleport_cooldown", new FixedMetadataValue(plugin, cooldown - 1));
            }
            return;
        }


        final Player player = (Player) zombie.getTarget();
        final Location player_loc = player.getEyeLocation();

        if (zombie.getLocation().distanceSquared(player_loc) > 25 && random.nextBoolean()) {
            //far away events

            //teleports to player if own health is higher than 30%
            double zombie_health = zombie.getHealth() / zombie.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();

            if (zombie_health > 0.3) {
                Location tele = random.nextBoolean() ? getLocationBehindPlayer(player) : getLocationNearPlayer(player, random, 5);
                if (tele == null) return;

                if (tele.distanceSquared(player_loc) > 0.25) {
                    skipTimeBase(zombie, player, tele, random);

                    if (random.nextInt(100) < 100) {
                        int pick_event = random.nextInt(4);
                        if (pick_event == 0) {
                            playerEat(player, random);
                        }
                        else if (pick_event == 1) {
                            for (int a = 0; a < random.nextInt(15) + 10; a++) {
                                playerMineBlock(player, random);
                            }
                        }
                        else if (pick_event == 2) {
                            player.getInventory().setHeldItemSlot(random.nextInt(9));
                        }
                        else if (pick_event == 3){
                            for (int a = 0; a < random.nextInt(15) + 10; a++) {
                                playerPlaceBlock(player, random);
                            }
                        }
                    }

                    //TODO move this elsewhere
                    if (random.nextInt(100) < 20) {
                        player.damage(random.nextDouble() * 10, zombie);
                    }
                }
            }

            //if less than, it'll run away and heal
            else {
                zombie.setTarget(null);
                Location retreat = getRetreatLocation(player, zombie, 5);

                if (retreat != null) {
                    skipTimeBase(zombie, player, retreat, random);
                    //TODO add more events triggered here
                }
            }

        }
        else if (random.nextBoolean()) {
            //nearby events
        }
    }

    public void CloneMechanism (Player player) {
        ReflectionWorldNMS nms = new ReflectionWorldNMS(player);
        if (player.hasMetadata("reflection_zombie") && player.getMetadata("reflection_zombie").get(0).asBoolean()) {
            nms.move(zombie);
        }
        else {
            nms.disguiseAsPlayer(zombie);
            player.setMetadata("reflection_zombie" , new FixedMetadataValue(plugin, true));
        }
    }

    public void skipTimeBase (final Zombie zombie, final Player player, final Location zombie_warp, final Random random) {
        final Location zombie_orig_loc = zombie.getLocation();
        final Location player_loc = player.getEyeLocation();

        zombie.teleport(zombie_warp);
        zombie.setMetadata("teleport_cooldown", new FixedMetadataValue(plugin, random.nextInt(80) + 20));
        warpPlayer(player, random);

        //effects
        player.spawnParticle(Particle.FLASH, zombie_orig_loc, 1);
        player.spawnParticle(Particle.PORTAL, zombie_orig_loc, 20, 1, 1, 1);
        player.spawnParticle(Particle.SMOKE_NORMAL, player_loc, 20, 1, 1, 1);
        player.playSound(player.getLocation(), Sound.BLOCK_BELL_USE, 1, 0.2F);
    }

    private Location getLocationNearPlayer(final Player player, final Random random, final int range) {
        final int max_tries = 100;
        final Location location = player.getLocation();
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

            test_location = new Location(player.getWorld(), test_x, test_y, test_z);
            if (location.distanceSquared(test_location) >= 0.25 && location.distanceSquared(test_location) <= distance_range) {
                if (locationViable(test_location)) {
                    return test_location;
                }
            }
        }

        return null;
    }

    private boolean locationViable (Location test_location) {
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

    private Location getLocationBehindPlayer (final Player player) {

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

    private boolean hasBlockBelow (final Location location, final int limit) {
        for (int a = location.getBlockY(); a > Math.max(location.getBlockY() - limit, 0); a--) {
            Location test_below = location.clone();
            if (!test_below.getBlock().isPassable()) {
                return true;
            }
        }
        return false;
    }

    public void warpPlayer (final Player player, final Random random) {
        final Location player_loc = player.getEyeLocation();
        BlockIterator iterate = new BlockIterator(world, player_loc.toVector(), player_loc.getDirection(), 0, random.nextInt(3) + 2);

        Block block = iterate.next();
        while (iterate.hasNext()) {
            block = iterate.next();
            if (!block.isPassable()) {
                break;
            }
        }

        Location player_moving = block.getLocation();
        player_moving.setYaw(player_loc.getYaw());
        player_moving.setPitch(player_loc.getPitch());
        player.teleport(player_moving);
    }

    public void playerEat (final Player player, final Random random) {
        if (player.getFoodLevel() < 20) {
            for (int a = 0; a < 9; a++) {
                if (player.getInventory().getContents()[a] == null) continue;
                if (player.getInventory().getContents()[a].getType().isEdible()) {
                    Material material = player.getInventory().getContents()[a].getType();

                    ArrayList<Material> blacklisted = new ArrayList<>();
                    blacklisted.add(Material.ROTTEN_FLESH);
                    blacklisted.add(Material.SPIDER_EYE);
                    blacklisted.add(Material.PUFFERFISH);
                    blacklisted.add(Material.CHORUS_FRUIT);
                    blacklisted.add(Material.SUSPICIOUS_STEW);
                    blacklisted.add(Material.CHICKEN);
                    blacklisted.add(Material.POISONOUS_POTATO);
                    if (blacklisted.contains(material)) continue;

                    //constants
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

                    int food_add = 0;
                    if (food_vals.containsKey(material)) {
                        food_add = food_vals.get(material);
                    }
                    else {
                        food_add = random.nextInt(3);
                    }

                    if (material.equals(Material.ENCHANTED_GOLDEN_APPLE)) {
                        player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 2400, 3));
                        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 400, 1));
                        player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 6000, 0));
                        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 6000, 0));
                    }
                    else if (material.equals(Material.GOLDEN_APPLE)) {
                        player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 2400, 0));
                        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100, 1));
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
                    //adds damage to item
                    ItemStack item = player.getInventory().getItemInMainHand();
                    Damageable meta = (Damageable) item.getItemMeta();
                    meta.setDamage(meta.getDamage() + 1);
                    item.setItemMeta((ItemMeta) meta);

                    block.breakNaturally(item);
                    break;
                }
            }
        }
    }

    private Block findAdequateMiningBlock (final Player player, final Random random, final int range) {
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

    public void playerPlaceBlock (final Player player, final Random random) {
        for (int a = 0; a < 9; a++) {
            if (player.getInventory().getContents()[a] == null) continue;
            if (player.getInventory().getContents()[a].getType().isBlock()) {
                Material material = player.getInventory().getContents()[a].getType();
                final int range = 5;
                final Block block = findAdequatePlacingBlock(player, random, range);

                if (block != null) {
                    player.getInventory().setHeldItemSlot(a);
                    block.setType(material);

                    player.playSound(block.getLocation(), Sound.BLOCK_STONE_PLACE, 1,1);
                    player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount() - 1);
                }

            }
        }
    }

    private Block findAdequatePlacingBlock (final Player player, final Random random, final int range) {
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
        final Location zombie_location = zombie.getLocation();
        final Location player_location = player.getLocation();
        final double distance_range = Math.pow(range - 1, 2);

        double farthest_distance = 0;
        Location farthest_location = null;

        //finding farthest location from player
        for (int x = -range; x <= range; x++) {
            for (int y = -range; y <= range; y++) {
                for (int z = -range; z <= range; z++) {
                    Location test_loc = new Location(zombie.getWorld(), zombie_location.getX() + x, zombie_location.getY() + y, zombie_location.getZ() + z);
                    if (test_loc.distanceSquared(zombie_location) < distance_range) {
                        if (test_loc.distanceSquared(player_location) > farthest_distance) {
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
}
