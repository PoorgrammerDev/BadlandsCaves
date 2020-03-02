package me.fullpotato.badlandscaves.badlandscaves.Runnables.SupernaturalPowers.ReflectionStage;

import me.fullpotato.badlandscaves.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.badlandscaves.NMS.ReflectionWorldNMS;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Repairable;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.LazyMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Random;

public class ReflectionZombieBoss extends BukkitRunnable {
    private BadlandsCaves plugin;
    private Zombie zombie;
    private World world = Bukkit.getWorld("world_reflection");
    public ReflectionZombieBoss(BadlandsCaves bcav) {
        plugin = bcav;
    }

    //TODO read below
    //TODO remove the test print statements
    /*
    also have the player, if they have food, it can just randomly eat it
    also if the player has placeable blocks, it can just randomly place it
    also the zombie will not only tp behind the player but around the player as well
    give the zombie a medium ranged attack that'll incentivize the player to run away
    the zombie, if it gets stuck in a wall, will teleport out of it


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

            System.out.print("oof");
            return;
        }

        System.out.print("asdf");

        final Player player = (Player) zombie.getTarget();
        final Location player_loc = player.getEyeLocation();
        final Location zombie_orig_loc = zombie.getLocation();

        if (zombie.getLocation().distanceSquared(player_loc) > 25 && random.nextBoolean()) {
            System.out.print("yare yare daze");
            //far away events
            Location tele = getLocationBehindPlayer(player);
            if (tele == null) return;

            System.out.print("there is a location");

            if (tele.distanceSquared(player_loc) > 0.25) {

                System.out.print("it happened");
                zombie.teleport(tele);
                zombie.setMetadata("teleport_cooldown" , new FixedMetadataValue(plugin, random.nextInt(80) + 20));
                warpPlayer(player, random);

                //playerEat(player, random);
                playerMineBlock(player, random);

                /*
                if (random.nextBoolean()) {
                    player.getInventory().setHeldItemSlot(random.nextInt(9));
                }

                 */

                if (random.nextInt(100) < 20) {
                    player.damage(random.nextDouble() * 10, zombie);
                }

                //effects
                player.spawnParticle(Particle.FLASH, zombie_orig_loc, 1);
                player.spawnParticle(Particle.PORTAL, zombie_orig_loc, 20, 1, 1, 1);
                player.spawnParticle(Particle.SMOKE_NORMAL, player_loc, 20, 1, 1, 1);
                player.playSound(player.getLocation(), Sound.BLOCK_BELL_USE, 1, 0.2F);

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

    public Location getLocationBehindPlayer (Player player) {
        Location player_loc = player.getEyeLocation();
        BlockIterator iterator = new BlockIterator(world, player_loc.toVector(), player_loc.getDirection().multiply(-1), 0, 2);
        Block block = iterator.next();
        while (iterator.hasNext()) {
            block = iterator.next();
            if (!block.isPassable()) {
                break;
            }
        }

        //make sure both "tele" and "above" are passable, so the zombie doesn't suffocate
        //and make sure there's at least one block from "below" to 10 blocks below that
        if (block.isPassable()) {
            final Location tele = block.getLocation();

            Location above = tele.clone();
            above.add(0, 1, 0);
            final Block block_above = above.getBlock();

            if (block_above.isPassable()) {
                Location below = tele.clone();
                below.subtract(0, 1, 0);
                if (hasBlockBelow(below)) {
                    return tele;
                }
            }
        }
        return null;
    }

    public boolean hasBlockBelow (Location location) {
        for (int a = location.getBlockY(); a > Math.max(location.getBlockY() - 10, 0); a--) {
            Location test_below = location.clone();
            if (!test_below.getBlock().isPassable()) {
                return true;
            }
        }
        return false;
    }

    public void warpPlayer (Player player, Random random) {
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

    public void playerEat (Player player, Random random) {
        if (player.getFoodLevel() < 20) {
            for (int a = 0; a < 9; a++) {
                if (player.getInventory().getContents()[a].getType().isEdible()) {
                    player.getInventory().setHeldItemSlot(a);
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_BURP, 1, 1);
                    player.setSaturation(player.getSaturation() + random.nextInt(5));
                    player.setFoodLevel(player.getFoodLevel() + random.nextInt(10));
                    player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount() - 1);
                    return;
                }
            }
        }
    }

    public void playerMineBlock (Player player, Random random) {
        ArrayList<Material> pickaxes = new ArrayList<>();
        pickaxes.add(Material.WOODEN_PICKAXE);
        pickaxes.add(Material.STONE_PICKAXE);
        pickaxes.add(Material.IRON_PICKAXE);
        pickaxes.add(Material.DIAMOND_PICKAXE);
        pickaxes.add(Material.GOLDEN_PICKAXE);

        for (int a = 0; a < 9; a++) {
            if (pickaxes.contains(player.getInventory().getContents()[a].getType())) {
                final Location player_loc = player.getLocation();
                final int range = 3;

                for (int x = -range; x <= range; x++) {
                    for (int y = -range; y <= range; y++) {
                        for (int z = -range; z <= range; z++) {
                            Location test_loc = new Location(player.getWorld(), player_loc.getBlockX() + x,  player_loc.getBlockY() + y,  player_loc.getBlockZ() + z);
                            Block test_block = test_loc.getBlock();
                            if (test_block.getType().isSolid()) {
                                if (random.nextInt(100) < 1) {
                                    test_block.breakNaturally();

                                    player.sendMessage("broke: " + test_block);
                                    player.getInventory().setHeldItemSlot(a);

                                    //adds damage to item
                                    ItemStack item = player.getInventory().getItemInMainHand();
                                    Damageable meta = (Damageable) item.getItemMeta();
                                    meta.setDamage(meta.getDamage() + 1);
                                    item.setItemMeta((ItemMeta) meta);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
