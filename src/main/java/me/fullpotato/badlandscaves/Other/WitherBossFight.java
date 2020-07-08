package me.fullpotato.badlandscaves.Other;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.CustomItem;
import me.fullpotato.badlandscaves.Util.ParticleShapes;
import me.fullpotato.badlandscaves.Util.StructureTrack;
import me.fullpotato.badlandscaves.Util.TitleEffects;
import me.fullpotato.badlandscaves.WorldGeneration.HallowedChambersWorld;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.ShulkerBox;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.loot.LootTables;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Random;

public class WitherBossFight implements Listener {
    private final BadlandsCaves plugin;
    private final World world;
    private final BlockFace[] adjacent = {
            BlockFace.NORTH,
            BlockFace.EAST,
            BlockFace.SOUTH,
            BlockFace.WEST,
    };
    private static final ArrayList<Player> players = new ArrayList<>();
    private static final ArrayList<Item> droppedKeys = new ArrayList<>();

    public WitherBossFight(BadlandsCaves plugin) {
        this.plugin = plugin;
        world = plugin.getServer().getWorld(plugin.getChambersWorldName());
    }

    //--------------------------------------------------

    @EventHandler
    public void spawnPortal (CreatureSpawnEvent event) {
        if (event.getEntity() instanceof Wither) {
            if (event.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.BUILD_WITHER)) {
                event.setCancelled(true);
                if (plugin.getOptionsConfig().getBoolean("wither_fight.portal_active")) return;
                if (plugin.getOptionsConfig().getInt("wither_fight.fight_stage") != -1) return;

                Location spawnLocation = event.getLocation();
                if (spawnLocation.getWorld() == null || !spawnLocation.getWorld().getEnvironment().equals(World.Environment.NORMAL)) return;

                plugin.getSystemConfig().set("wither_fight.fight_stage", 0);
                plugin.getSystemConfig().set("wither_fight.portal_active", true);
                plugin.getSystemConfig().set("wither_fight.portal_location", spawnLocation);
                plugin.getSystemConfig().set("wither_fight.portal_timer", (spawnLocation.getBlockY() * 0.25) + 180);

                plugin.saveSystemConfig();

                Random random = new Random();
                spawnTunnel(spawnLocation, random);
                destroyAroundPortal(spawnLocation);
                regenMazesAndKeyHolders();

                for (Player player : plugin.getServer().getOnlinePlayers()) {
                    if (player.getWorld().equals(spawnLocation.getWorld()) && player.getLocation().distanceSquared(spawnLocation) < 36) {
                        player.setVelocity(spawnLocation.clone().subtract(player.getLocation()).toVector().multiply(-1));
                    }
                }
            }
        }
    }

    public void portalDestroyTimer() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (plugin.getOptionsConfig().getBoolean("wither_fight.portal_active")) {
                    int timer = plugin.getOptionsConfig().getInt("wither_fight.portal_timer");
                    if (timer > 0) {
                        plugin.getSystemConfig().set("wither_fight.portal_timer", timer - 1);
                    }
                    else {
                        prepareStage();
                        destroyTunnel(plugin.getSystemConfig().getLocation("wither_fight.portal_location"));
                    }
                }
            }
        }.runTaskTimer(plugin, 0, 20);
    }

    @EventHandler
    public void enterWorld (PlayerMoveEvent event) {
        if (!plugin.getOptionsConfig().getBoolean("wither_fight.portal_active")) return;
        Location portalLocation = plugin.getSystemConfig().getLocation("wither_fight.portal_location");
        if (portalLocation == null) return;
        portalLocation = portalLocation.clone();

        portalLocation.setY(0);

        if (event.getTo() != null && event.getTo().getWorld().equals(portalLocation.getWorld()) && event.getTo().distanceSquared(portalLocation) < 49 && event.getTo().getY() < 1.5) {
            World chambers = plugin.getServer().getWorld(plugin.getChambersWorldName());
            Player player = event.getPlayer();

            player.teleport(chambers.getSpawnLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN);
            player.playSound(player.getLocation(), Sound.BLOCK_PORTAL_TRAVEL, SoundCategory.BLOCKS,0.15F, 1);
            if (player.getGameMode().equals(GameMode.SURVIVAL)) player.setGameMode(GameMode.ADVENTURE);
        }

    }

    public void regenMazesAndKeyHolders () {
        StructureTrack[] structures = {
                new StructureTrack(plugin, new Location(world, -8, 126, 21), 0, 0, 0, -32, 1, 1, "badlandscaves:chambers_magma_maze", BlockFace.UP),
                new StructureTrack(plugin, new Location(world, -46, 134, 16), 0, 0, 0, -31, -7, -31, "badlandscaves:chambers_glowstone_maze", BlockFace.UP),
                new StructureTrack(plugin, new Location(world, -41, 134, -21), 0, 0, 0, 1, -7, -31, "badlandscaves:chambers_soulsand_maze", BlockFace.UP),
                new StructureTrack(plugin, new Location(world, -25, 160, 0), 0, 0, 0, -2, -32, -2, "badlandscaves:chambers_keyholder_platform", BlockFace.UP),
                new StructureTrack(plugin, new Location(world, -5, 159, 1), 0, 0, 0, 0, -31, -2, "badlandscaves:chambers_start_barrier", BlockFace.UP),
        };

        HallowedChambersWorld hallowedChambersWorld = new HallowedChambersWorld(plugin);
        hallowedChambersWorld.spawnInStructure(structures);
    }

    public void destroyAroundPortal(Location location) {
        Location middle = location.clone().add(0, 1, 0);
        for (int x = -5; x <= 5; x++) {
            for (int y = -5; y <= 5; y++) {
                for (int z = -5; z <= 5; z++) {
                    Block block = middle.clone().add(x, y, z).getBlock();
                    if (block.getType().getBlastResistance() < Material.BEDROCK.getBlastResistance() && block.getLocation().distanceSquared(middle) < 25) {
                        block.setType(Material.AIR);
                    }
                }
            }
        }
    }

    public void spawnTunnel(Location location, Random random) {
        Location clone = location.clone();
        clone.setX(clone.getBlockX() + 0.5);
        clone.setY(clone.getBlockY() - 1);
        clone.setZ(clone.getBlockZ() + 0.5);

        int range = 8;
        int[] y = {clone.getBlockY()};
        new BukkitRunnable() {
            @Override
            public void run() {
                if (y[0] < 0) {
                    plugin.getServer().broadcastMessage("§cA portal to the Hallowed Chambers has opened!");
                    for (Player player : plugin.getServer().getOnlinePlayers()) {
                        player.playSound(player.getLocation(), Sound.BLOCK_END_PORTAL_SPAWN, SoundCategory.BLOCKS, 0.5F, 1);
                    }


                    this.cancel();
                }
                else {
                    clone.setY(y[0]);
                    Location test;
                    for (int x = -range; x <= range; x++) {
                        for (int z = -range; z <= range; z++) {
                            test = new Location(clone.getWorld(), clone.getBlockX() + x, y[0], clone.getBlockZ() + z);
                            if (test.getBlockY() < 5 || test.getBlock().getType().getBlastResistance() < Material.BEDROCK.getBlastResistance()) {
                                if (test.distanceSquared(clone) < 25) {
                                    test.getBlock().setType(y[0] == 0 ? Material.END_GATEWAY : Material.AIR);
                                }
                                else if (test.distanceSquared(clone) < 36) {
                                    int rand = random.nextInt(3);
                                    if (rand == 0) test.getBlock().setType(Material.COAL_BLOCK);
                                    else if (rand == 1) test.getBlock().setType(Material.BLACK_CONCRETE);
                                    else test.getBlock().setType(Material.BLACK_WOOL);
                                }
                                else if (test.distanceSquared(clone) < 49) {
                                    if (test.getBlock().getType().equals(Material.LAVA)) test.getBlock().setType(Material.MAGMA_BLOCK);
                                    else if (test.getBlock().getType().equals(Material.FIRE)) test.getBlock().breakNaturally();
                                }
                            }
                        }
                    }
                    y[0]--;
                }
            }
        }.runTaskTimer(plugin, 0, 5);
    }

    public void destroyTunnel (Location location) {
        if (!(plugin.getOptionsConfig().getBoolean("wither_fight.portal_active"))) return;

        Location clone = location.clone();
        plugin.getSystemConfig().set("wither_fight.portal_active", false);
        plugin.getSystemConfig().set("wither_fight.portal_location", null);
        plugin.getSystemConfig().set("wither_fight.portal_timer", 0);
        plugin.saveSystemConfig();


        plugin.getServer().broadcastMessage("§3The portal has closed!");
        Location removeGateway = clone.clone();
        removeGateway.setY(0);
        for (int x = -8; x <= 8; x++) {
            for (int z = -8; z < 8; z++) {
                Location test = removeGateway.clone().add(x, 0, z);
                if (test.getBlock().getType().equals(Material.END_GATEWAY)) test.getBlock().breakNaturally();
            }
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                if (clone.getY() < 0) {
                    this.cancel();
                }
                else {
                    clone.getWorld().createExplosion(clone, 9, true, true);
                    clone.setY(clone.getY() - 5);
                }
            }
        }.runTaskTimer(plugin, 0, 5);
    }

    @EventHandler
    public void surpassBarrier (PlayerInteractEvent event) {
        if (event.getAction().equals(Action.LEFT_CLICK_BLOCK) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            Block block = event.getClickedBlock();
            if (block != null && block.getWorld().equals(world)) {
                if (block.getType().equals(Material.RED_NETHER_BRICK_WALL)) {
                    if (event.getHand() != null && event.getHand().equals(EquipmentSlot.HAND)) {
                        int fight_stage = plugin.getOptionsConfig().getInt("wither_fight.fight_stage");
                        if (fight_stage == 0) {
                            for (Player player : plugin.getServer().getOnlinePlayers()) {
                                if (player.getWorld().equals(world)) {
                                    player.sendMessage("§cTo proceed, click the barrier again. This will close off the portal in the Overworld.");
                                }
                            }
                            plugin.getSystemConfig().set("wither_fight.fight_stage", 1);
                            plugin.saveSystemConfig();
                        }
                        else if (fight_stage == 1) {
                            prepareStage();
                        }
                    }
                }
            }
        }
    }

    public void prepareStage() {
        for (int z = -1; z <= 1; z++) {
            for (int y = 128; y <= 131; y++) {
                Block wall = world.getBlockAt(-5, y, z);
                if (wall.getType().equals(Material.RED_NETHER_BRICK_WALL)) {
                    wall.breakNaturally();
                }
            }
        }


        for (Player player : plugin.getServer().getOnlinePlayers()) {
            player.playSound(player.getLocation(), Sound.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, SoundCategory.BLOCKS, 1, 1);
            if (player.getWorld().equals(world)) {
                players.add(player);
            }
        }

        destroyTunnel(plugin.getSystemConfig().getLocation("wither_fight.portal_location"));
        final boolean hardmode = plugin.getSystemConfig().getBoolean("hardmode");
        final boolean waive = plugin.getOptionsConfig().getBoolean("waive_hardmode_chambers_keys");

        if (hardmode && waive) {
            genMazes(new Random(), false);
            advanceToBoss();
        }
        else {
            genMazes(new Random(), true);
            plugin.getSystemConfig().set("wither_fight.fight_stage", 2);
        }

        plugin.saveSystemConfig();
    }

    public void checkIfEnded() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (plugin.getOptionsConfig().getInt("wither_fight.fight_stage") == -1) return;
                if (plugin.getOptionsConfig().getBoolean("wither_fight.portal_active")) return;


                int active = 0;
                for (Player player : players) {
                    if (player.isOnline() && !player.isDead() && player.getWorld().equals(world)) {
                        active++;
                    }
                }

                if (active <= 0) {
                    for (Player player : players) {
                        if (player.getWorld().equals(world)) {
                            Location respawn = player.getBedSpawnLocation();
                            if (respawn == null) {
                                player.teleport(plugin.getServer().getWorld(plugin.getMainWorldName()).getSpawnLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN);
                            }
                            else {
                                player.teleport(respawn, PlayerTeleportEvent.TeleportCause.PLUGIN);
                            }
                        }
                    }
                    players.clear();

                    plugin.getSystemConfig().set("wither_fight.fight_stage", -1);
                    plugin.saveSystemConfig();
                }
            }
        }.runTaskTimer(plugin, 0, 5);
    }

    //---------------------------------------------------

    public void genMazes (Random random, boolean spawnMobs) {
        //magma maze
        Location magma_lower = new Location(world,-39, 128, 23);
        Location magma_upper = new Location(world, -11, 128, 51);
        genMaze(new Location(world, -25, 128, 23), magma_lower, magma_upper, random);

        //glowstone maze
        Location glowstone_lower = new Location(world, -76, 128, -14);
        Location glowstone_upper = new Location(world, -48, 128, 14);
        genMaze(new Location(world, -48, 128, 0), glowstone_lower, glowstone_upper, random);

        //soulsand maze
        Location soulsand_lower = new Location(world, -39, 128, -51);
        Location soulsand_upper = new Location(world, -11, 128, -23);
        genMaze(new Location(world, -25, 128, -23), soulsand_lower, soulsand_upper, random);

        if (spawnMobs) {
            spawnMazeMobs(EntityType.ZOMBIFIED_PIGLIN, magma_lower, magma_upper, random, 20);
            spawnMazeMobs(EntityType.BLAZE, glowstone_lower, glowstone_upper, random, 20);
            spawnMazeMobs(EntityType.WITHER_SKELETON, soulsand_lower, soulsand_upper, random, 20);
        }

    }

    public void spawnMazeMobs (EntityType type, Location lower_bound, Location upper_bound, Random random, int count) {
        if (type.isSpawnable()) {
            Creature[] entities = new Creature[count];

            for (int i = 0; i < entities.length; i++) {
                Location test;
                do {
                    test = new Location(world, random.nextInt(upper_bound.getBlockX() - lower_bound.getBlockX()) + lower_bound.getBlockX(), 128, random.nextInt(upper_bound.getBlockZ() - lower_bound.getBlockZ()) + lower_bound.getBlockZ());
                } while (!test.getBlock().isPassable());

                test.add(0.5, 0, 0.5);
                entities[i] = (Creature) test.getWorld().spawnEntity(test, type);
                entities[i].setRemoveWhenFarAway(false);

                entities[i].getAttribute(Attribute.GENERIC_ARMOR).setBaseValue(7);
                //entities[i].getAttribute(Attribute.GENERIC_FLYING_SPEED).setBaseValue();
                //entities[i].getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue();
                entities[i].getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(4);

                entities[i].getEquipment().setItemInMainHandDropChance(0);
                entities[i].setLootTable(LootTables.EMPTY.getLootTable());

                if (!(entities[i] instanceof Blaze)) {
                    entities[i].getAttribute(Attribute.GENERIC_FOLLOW_RANGE).setBaseValue(3);
                }

                if (entities[i] instanceof PigZombie) {
                    ((PigZombie) entities[i]).setAnger(Integer.MAX_VALUE);
                    ((PigZombie) entities[i]).setAngry(true);
                    ((PigZombie) entities[i]).setBaby(false);
                }

            }

            Creature chosen = entities[random.nextInt(entities.length)];

            CustomItem customItem = null;
            if (chosen instanceof PigZombie) {
                customItem = CustomItem.CHAMBER_MAGMA_KEY;
            }
            else if (chosen instanceof Blaze) {
                customItem = CustomItem.CHAMBER_GLOWSTONE_KEY;
            }
            else if (chosen instanceof WitherSkeleton) {
                customItem = CustomItem.CHAMBER_SOULSAND_KEY;
            }

            if (customItem != null) {
                chosen.getEquipment().setChestplate(customItem.getItem());
                chosen.getEquipment().setChestplateDropChance(1);
            }
        }
    }

    public void genMaze (Location location, Location lower_limit, Location upper_limit, Random random) {

        Location clone = location.clone();
        ArrayList<Location> cells = new ArrayList<>();
        ArrayList<Location> walls = new ArrayList<>();

        cells.add(clone);
        addRespectBounds(walls, clone.clone().add(1, 0, 0), lower_limit, upper_limit);
        addRespectBounds(walls, clone.clone().add(-1, 0, 0), lower_limit, upper_limit);
        addRespectBounds(walls, clone.clone().add(0, 0, 1), lower_limit, upper_limit);
        addRespectBounds(walls, clone.clone().add(0, 0, -1), lower_limit, upper_limit);

        int limit = 0;
        while (!walls.isEmpty() && limit < 10000) {
            int visited = 0;
            Location test_wall = walls.get(random.nextInt(walls.size()));
            Block unsaved = null;
            for (BlockFace face : adjacent) {
                Block test_if_cell = test_wall.getBlock().getRelative(face);
                if (test_if_cell.getType().isAir()) {
                    if (cells.contains(test_if_cell.getLocation())) {
                        visited++;
                    }
                    else if (inBounds(test_if_cell.getLocation(), lower_limit, upper_limit)) {
                        unsaved = test_if_cell;
                    }
                }
            }

            if (visited == 1) {
                test_wall.getBlock().setType(Material.AIR);
                walls.remove(test_wall);

                if (unsaved != null) {
                    cells.add(unsaved.getLocation());

                    for (BlockFace face : adjacent) {
                        Block test_if_wall = unsaved.getRelative(face);
                        if (!test_if_wall.getType().isAir()) {
                            if (!walls.contains(test_if_wall.getLocation())) addRespectBounds(walls, test_if_wall.getLocation(), lower_limit, upper_limit);
                        }
                    }
                }
            }
            limit++;
        }

        for (int x = lower_limit.getBlockX(); x <= upper_limit.getBlockX(); x++) {
            for (int z = lower_limit.getBlockZ(); z <= upper_limit.getBlockZ(); z++) {
                Block block = new Location(world, x, clone.getBlockY(), z).getBlock();
                if (!block.getType().isAir()) {
                    for (int y = 1; y < 4; y++) {
                        block.getLocation().add(0, y, 0).getBlock().setType(Material.NETHER_BRICKS);
                    }
                }
            }
        }
    }

    public void addRespectBounds(ArrayList<Location> list, Location add, Location lower_bound, Location upper_bound) {
        if (inBounds(add, lower_bound, upper_bound)) list.add(add);
    }

    public boolean inBounds(Location test, Location lower_bound, Location upper_bound) {
        if (test.getBlockX() > lower_bound.getBlockX() && test.getBlockZ() > lower_bound.getBlockZ()) {
            return test.getBlockX() < upper_bound.getBlockX() && test.getBlockZ() < upper_bound.getBlockZ();
        }
        return false;
    }

    @EventHandler
    public void useKeys(PlayerInteractEvent event) {
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            if (event.getClickedBlock() != null && event.getClickedBlock().getType().toString().contains("SHULKER_BOX") && event.getClickedBlock().getLocation().getWorld() != null && event.getClickedBlock().getLocation().getWorld().equals(world)) {
                Player player = event.getPlayer();
                ItemStack item = player.getInventory().getItemInMainHand();
                event.setCancelled(true);
                Block block = event.getClickedBlock();

                CustomItem keyType;
                if (block.getType().equals(Material.RED_SHULKER_BOX)) {
                    keyType = CustomItem.CHAMBER_MAGMA_KEY;
                }
                else if (block.getType().equals(Material.YELLOW_SHULKER_BOX)) {
                    keyType = CustomItem.CHAMBER_GLOWSTONE_KEY;
                }
                else if (block.getType().equals(Material.GRAY_SHULKER_BOX)) {
                    keyType = CustomItem.CHAMBER_SOULSAND_KEY;
                }
                else return;

                final ItemStack key = keyType.getItem();

                if (item.isSimilar(key)) {
                    player.playSound(block.getLocation(), Sound.BLOCK_END_PORTAL_FRAME_FILL, SoundCategory.BLOCKS, 1, 1);
                    ItemStack clone = item.clone();
                    clone.setAmount(1);
                    Item dropped_item = world.dropItem(event.getClickedBlock().getLocation().add(0.5, 1.5, 0.5), clone);
                    dropped_item.setPickupDelay(35565);
                    dropped_item.setInvulnerable(true);
                    dropped_item.setVelocity(new Vector());
                    item.setAmount(item.getAmount() - 1);
                    droppedKeys.add(dropped_item);


                    if (block.getState() instanceof ShulkerBox) {
                        ((ShulkerBox) block.getState()).getInventory().setItem(0, new ItemStack(Material.STRUCTURE_VOID));
                    }

                    checkKeys();
                }
                else {
                    if (keyType.equals("magma")) {
                        player.sendMessage("§cSearch the Dungeons for Keys and place them here.");
                    }
                    else if (keyType.equals("glowstone")) {
                        player.sendMessage("§6Search the Dungeons for Keys and place them here.");
                    }
                    else {
                        player.sendMessage("§8Search the Dungeons for Keys and place them here.");
                    }
                }
            }
        }
    }

    public void checkKeys() {
        Block[] keyHolders = {
                world.getBlockAt(-25, 128, 1),
                world.getBlockAt(-26, 128, 0),
                world.getBlockAt(-25, 128, -1),
        };

        int keys = 0;
        for (Block block : keyHolders) {
            if (block.getState() instanceof ShulkerBox) {
                ItemStack item = ((ShulkerBox) block.getState()).getInventory().getItem(0);

                if (item != null && item.getType().equals(Material.STRUCTURE_VOID)) {
                    keys++;
                }
            }
        }

        if (keys == 3) {

            for (Player player : players) {
                player.playSound(player.getLocation(), Sound.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, SoundCategory.BLOCKS, 1, 1);
            }

            for (Item key : droppedKeys) {
                key.remove();
            }
            droppedKeys.clear();

            advanceToBoss();
            clearEntities(false);
        }
    }

    public void advanceToBoss () {
        plugin.getSystemConfig().set("wither_fight.fight_stage", 3);
        plugin.saveSystemConfig();
        for (int x = -26; x <= -24; x++) {
            for (int z = -1; z <= 1; z++) {
                world.getBlockAt(x, 127, z).breakNaturally();
                world.getBlockAt(x, 128, z).breakNaturally();
            }
        }
    }

    //---------------------------------------------------

    @EventHandler
    public void enterWitherBossStage (PlayerMoveEvent event) {
        if (event.getTo() != null && event.getTo().getWorld() != null && event.getTo().getWorld().equals(world)) {
            Location location = event.getTo();
            Location entrance = new Location(world, -25, 14, 0);

            if (location.distanceSquared(entrance) < 25 && Math.abs(location.getBlockY() - entrance.getBlockY()) < 3) {
                Location warp = new Location(world, 300.5, 250, 0.5);
                Player player = event.getPlayer();

                warp.setYaw(player.getLocation().getYaw());
                warp.setPitch(player.getLocation().getPitch());

                Vector velocity = player.getVelocity();
                event.getPlayer().teleport(warp, PlayerTeleportEvent.TeleportCause.PLUGIN);
                player.setVelocity(velocity);

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        clearEntities(true);
                    }
                }.runTaskLater(plugin, 5);

                checkIfStartViable();
            }
        }
    }

    public void checkIfStartViable () {
        for (Player player : players) {
            if (player.getLocation().getX() < 200) {
                return;
            }
        }
        plugin.getSystemConfig().set("wither_fight.fight_stage", 4);
        plugin.saveSystemConfig();


        int[] time = {5};
        new BukkitRunnable() {
            @Override
            public void run() {
                if (time[0] <= 0) {
                    this.cancel();
                    spawnBoss(new Location(world, 300, 206, 0), new Random(), 45, players.size());
                    TitleEffects titleEffects = new TitleEffects(plugin);
                    players.forEach(player -> {
                        titleEffects.sendDecodingTitle(player, "FIGHT", net.md_5.bungee.api.ChatColor.of("#ff7f00") + ChatColor.BOLD.toString(), "", "", 0, 30, 10, 2, false);
                    });
                }
                else {
                    players.forEach(player -> {
                        player.sendTitle("§c§l" + time[0], null, 5, 10, 5);
                        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.HOSTILE, 0.5F, 1);
                    });
                    time[0]--;
                }
            }
        }.runTaskTimer(plugin, 0, 20);


    }

    @EventHandler
    public void stopMobsTargetEachOther (EntityTargetLivingEntityEvent event) {
        if (event.getEntity().getWorld().equals(world) && event.getTarget() != null && event.getTarget().getWorld().equals(world)) {
            if (event.getEntity() instanceof Monster && event.getTarget() instanceof Monster) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void stopMobsDamageEachOther (EntityDamageByEntityEvent event) {
        if (event.getEntity().getWorld().equals(world) && event.getDamager().getWorld().equals(world)) {
            if (event.getEntity() instanceof Monster && (event.getDamager() instanceof Monster || event.getDamager() instanceof WitherSkull)) {
                event.setCancelled(true);
            }
        }
    }

    public void spawnBoss(Location location, Random random, int range, int playerCount) {
        Wither wither = (Wither) world.spawnEntity(location, EntityType.WITHER);
        wither.getAttribute(Attribute.GENERIC_FOLLOW_RANGE).setBaseValue(999);

        int health = Math.min(450 + (150 * playerCount), 5000);
        wither.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(health);
        wither.setHealth(health);
        wither.setCustomName("§5The Wither");

        wither.setRemoveWhenFarAway(false);

        world.spawnParticle(Particle.FLASH, location.clone().add(0, 0.5, 0), 1);
        world.playSound(location, Sound.ENTITY_WITHER_SPAWN, SoundCategory.HOSTILE, 1, 1);
        spawnMobs(location, random, Math.min(25 + (10 * playerCount), 100), range, false, true);
    }

    @EventHandler
    public void witherHeadsFire (ProjectileLaunchEvent event) {
        if (event.getEntity() instanceof WitherSkull) {
            WitherSkull skull = (WitherSkull) event.getEntity();
            if (skull.getShooter() instanceof Wither) {
                Random random = new Random();
                if (!skull.isCharged()) {
                    if (random.nextInt(100) < 10) {
                        skull.setCharged(true);
                    }
                }

                if (skull.isCharged()) {
                    if (random.nextInt(100) < 25) {
                        Player player = null;

                        for (Entity entity : skull.getNearbyEntities(10, 10, 10)) {
                            if (entity instanceof Player) {
                                player = (Player) entity;
                                break;
                            }
                        }

                        if (player != null) {
                            final Player finalPlayer = player;

                            final int lifespan = 600;
                            int[] time = {0};
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    if (skull.isDead() || finalPlayer.isDead() || time[0] > lifespan) {
                                        this.cancel();
                                    }
                                    else {
                                        boolean close = skull.getLocation().distanceSquared(finalPlayer.getLocation()) < 16;
                                        double mult = close ? 0.5 : 0.05;

                                        skull.setVelocity(finalPlayer.getEyeLocation().subtract(skull.getLocation()).toVector().multiply(mult));
                                        if (close) {
                                            world.spawnParticle(Particle.LAVA, skull.getLocation(), 5, 0, 0, 0, 0);
                                        }
                                        else {
                                            world.spawnParticle(Particle.FLAME, skull.getLocation(), 1, 0, 0, 0, 0);
                                        }

                                        time[0]++;
                                    }
                                }
                            }.runTaskTimerAsynchronously(plugin, 0, 0);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void witherHeadsHit (ProjectileHitEvent event) {
        if (event.getEntity() instanceof WitherSkull) {
            WitherSkull skull = (WitherSkull) event.getEntity();
            Entity hitEntity = event.getHitEntity();

            Random random = new Random();
            if (skull.isCharged()) {
                if (hitEntity != null) {
                    if (random.nextBoolean()) {
                        final int explosions = random.nextInt(5) + 1;

                        int[] power = {explosions};
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                if (power[0] < 0) {
                                    this.cancel();
                                }
                                else {
                                    world.createExplosion(skull.getLocation().add(0, (explosions - power[0]) * 2, 0), power[0], false, false, skull);
                                    power[0]--;
                                }
                            }
                        }.runTaskTimer(plugin, 0, 5);
                    }
                }
                else {
                    if (random.nextBoolean()) {
                        Location location = skull.getLocation();
                        final int lifespan = 100;
                        int[] ticks = {0};

                        if (location.getWorld().equals(world)) {
                            world.playSound(location, Sound.BLOCK_PORTAL_TRIGGER, SoundCategory.HOSTILE, 1, 1);
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    if (ticks[0] > lifespan) {
                                        this.cancel();
                                    }
                                    else {
                                        world.spawnParticle(Particle.REDSTONE, location, 2, 0.1, 0.1, 0.1,0, new Particle.DustOptions(Color.BLACK, 2));

                                        world.spawnParticle(Particle.REDSTONE, location, 10, 2, 1, 1,0, new Particle.DustOptions(Color.RED, 1));
                                        world.spawnParticle(Particle.REDSTONE, location, 10, 1, 2, 1,0, new Particle.DustOptions(Color.RED, 1));
                                        world.spawnParticle(Particle.REDSTONE, location, 10, 1, 1, 2,0, new Particle.DustOptions(Color.RED, 1));

                                        for (Entity entity : world.getNearbyEntities(location, 10, 10, 10)) {
                                            if (entity instanceof Player) {
                                                Player player = (Player) entity;
                                                if (player.getWorld().equals(world) && player.getLocation().distanceSquared(location) > 1 && player.getLocation().distanceSquared(location) < 25) {
                                                    player.setVelocity(location.clone().subtract(player.getLocation()).toVector().multiply(0.3));
                                                }
                                            }
                                        }
                                        ticks[0]++;
                                    }
                                }
                            }.runTaskTimer(plugin, 0, 0);
                        }
                    }
                }

            }
            else {
                if (hitEntity != null) {
                    if (hitEntity instanceof Player) {
                        if (random.nextInt(100) < 15) {
                            spawnMobs(skull.getLocation(), random,random.nextInt(4) + 1, 5, false, true);
                        }
                    }
                }
                else {
                    if (skull.getShooter() instanceof Wither) {
                        if (random.nextInt(100) < 25) {
                            for (Entity entity : skull.getNearbyEntities(10, 10, 10)) {
                                if (entity instanceof Player) {
                                    Player player = (Player) entity;
                                    WitherSkull newSkull = (WitherSkull) world.spawnEntity(skull.getLocation(), EntityType.WITHER_SKULL);
                                    newSkull.setVelocity(player.getEyeLocation().subtract(skull.getLocation()).toVector().multiply(0.2));
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void spawnMobs (Location location, Random random, int count, int range, boolean centerStage, boolean flashParticle) {
        int[] x = null;
        int[] z = null;

        if (!centerStage) {
            x = random.ints(count, location.getBlockX() - range, location.getBlockX() + range).toArray();
            z = random.ints(count, location.getBlockZ() - range, location.getBlockZ() + range).toArray();
        }

        for (int i = 0; i < count; i++) {
            Location minion_spawn = centerStage ? location : new Location(world, x[i], getTopYInCave(world, x[i], z[i], 221), z[i]);
            if (flashParticle) world.spawnParticle(Particle.FLASH, minion_spawn.clone().add(0, 0.5, 0), 1);
            WitherSkeleton skeleton = (WitherSkeleton) world.spawnEntity(minion_spawn, EntityType.WITHER_SKELETON);
            skeleton.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 9999, 1, false, false));
            skeleton.getAttribute(Attribute.GENERIC_ARMOR).setBaseValue(5);
            skeleton.getAttribute(Attribute.GENERIC_FOLLOW_RANGE).setBaseValue(999);
            skeleton.setRemoveWhenFarAway(false);
            skeleton.setLootTable(LootTables.EMPTY.getLootTable());
            skeleton.getEquipment().setItemInMainHandDropChance(0);

            if (random.nextBoolean()) {
                ItemStack ironSword = new ItemStack(Material.IRON_SWORD);
                if (random.nextBoolean()) {
                    ItemMeta meta = ironSword.getItemMeta();
                    meta.addEnchant(Enchantment.DAMAGE_ALL, random.nextInt(3) + 1, true);
                    ironSword.setItemMeta(meta);
                }

                skeleton.getEquipment().setItemInMainHand(ironSword);
            }
            else if (random.nextBoolean()) {
                ItemStack bow = new ItemStack(Material.BOW);
                if (random.nextBoolean()) {
                    ItemMeta meta = bow.getItemMeta();
                    meta.addEnchant(Enchantment.ARROW_DAMAGE, random.nextInt(3) + 1, true);
                    bow.setItemMeta(meta);
                }

                skeleton.getEquipment().setItemInMainHand(bow);
            }
            else {
                ItemStack stoneSword = new ItemStack(Material.STONE_SWORD);
                if (random.nextBoolean()) {
                    ItemMeta meta = stoneSword.getItemMeta();
                    meta.addEnchant(Enchantment.DAMAGE_ALL, random.nextInt(3) + 1, true);
                    stoneSword.setItemMeta(meta);
                }

                skeleton.getEquipment().setItemInMainHand(stoneSword);
            }
        }
    }

    public int getTopYInCave(World world, int x, int z, int y_start) {
        Location location = new Location(world, x, world.getMaxHeight(), z);
        int flag = 0;
        for (int y = y_start; y >= 0; y--) {
            location.setY(y);
            if (flag == 0 && location.getBlock().getType().isSolid()) {
                flag = 1;
            }
            if (flag == 1 && location.getBlock().isPassable()) {
                flag = 2;
            }
            if (flag == 2 && location.getBlock().getType().isSolid()) {
                return location.getBlockY() + 1;
            }
        }
        return -1;
    }

    public void clearEntities(boolean removeWither) {
        for (Entity entity : world.getEntities()) {
            if (entity instanceof Blaze || entity instanceof WitherSkeleton || entity instanceof PigZombie || (removeWither && entity instanceof Wither)) {
                entity.remove();
            }
        }
    }

    @EventHandler
    public void resetToSurvival (PlayerDeathEvent event) {
        if (players.contains(event.getEntity())) {
            if (event.getEntity().getGameMode().equals(GameMode.ADVENTURE)) event.getEntity().setGameMode(GameMode.SURVIVAL);
            players.remove(event.getEntity());
        }
    }

    @EventHandler
    public void winBattle (EntityDeathEvent event) {
        if (event.getEntity() instanceof Wither) {
            Wither wither = (Wither) event.getEntity();
            if (wither.getWorld().equals(world)) {
                boolean hardmode = plugin.getSystemConfig().getBoolean("hardmode");
                clearEntities(false);
                plugin.getServer().broadcastMessage("§dThe Wither has been slain!");

                if (!hardmode) {
                    TitleEffects titleEffects = new TitleEffects(plugin);
                    plugin.getServer().getOnlinePlayers().forEach(player -> {
                        titleEffects.sendDecodingTitle(player, "ENTERING HARDMODE", ChatColor.of("#ff6600") + ChatColor.BOLD.toString(), "Prepare yourself.", ChatColor.of("#ff4000").toString(), 0, 80, 20, 2, false);
                        player.playSound(player.getLocation(), Sound.BLOCK_END_PORTAL_SPAWN, SoundCategory.MASTER, 1, 0.5F);
                        player.playSound(player.getLocation(), Sound.ENTITY_WITHER_SPAWN, SoundCategory.MASTER, 1, 1);
                        player.playSound(player.getLocation(), "custom.darkrooms_whispers", SoundCategory.MASTER, 0.5F, 0.5F);
                    });

                    plugin.getSystemConfig().set("hardmode", true);
                    plugin.getSystemConfig().set("chaos_level", 0);
                    plugin.saveSystemConfig();

                    plugin.getServer().resetRecipes();
                    plugin.loadCraftingRecipes();
                }


                forceOutOfCenter();
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        plugin.getSystemConfig().set("wither_fight.fight_stage", 5);
                        plugin.saveSystemConfig();
                        exitPortalParticles();
                        giveLoot();
                    }
                }.runTaskLater(plugin, 5);
            }
        }
    }

    public void forceOutOfCenter () {
        new BukkitRunnable() {
            @Override
            public void run() {
                Location centerStage = new Location(world, 300.5, 206, 0.5);
                for (Entity entity : world.getNearbyEntities(centerStage, 9, 9, 9)) {
                    Vector vector = centerStage.clone().subtract(entity.getLocation()).toVector();
                    vector.multiply(-10);
                    vector.setY(vector.getY() * 1.5);
                    entity.setVelocity(vector);
                }
            }
        }.runTaskLater(plugin, 1);
    }

    public void giveLoot () {
        ItemStack crate = CustomItem.FISHING_CRATE_HARDMODE.getItem();
        crate.setAmount(new Random().nextInt(16) + 16);

        for (Player player : players) {
            if (player.getInventory().firstEmpty() == -1) {
                world.dropItemNaturally(player.getLocation(), crate);
            }
            else {
                player.getInventory().addItem(crate);
            }
        }
    }

    public void exitPortalParticles () {
        Location centerStage = new Location(world, 300.5, 206, 0.5);
        new BukkitRunnable() {
            @Override
            public void run() {
                if (plugin.getOptionsConfig().getInt("wither_fight.fight_stage") == 5) {
                    ParticleShapes.particleSphere(null, Particle.REDSTONE, centerStage, 9, 0, new Particle.DustOptions(Color.ORANGE, 1));
                }
                else {
                    this.cancel();
                }
            }
        }.runTaskTimerAsynchronously(plugin, 0, 0);
    }

    @EventHandler
    public void exitPortalMechanism (PlayerMoveEvent event) {
        Location centerStage = new Location(world, 300.5, 206, 0.5);
        if (event.getTo() != null && event.getTo().getWorld() != null && event.getTo().getWorld().equals(world)) {
            if (plugin.getOptionsConfig().getInt("wither_fight.fight_stage") == 5) {
                if (event.getTo().distanceSquared(centerStage) < 81) {
                    Player player = event.getPlayer();
                    if (player.getGameMode().equals(GameMode.ADVENTURE)) player.setGameMode(GameMode.SURVIVAL);

                    Location bedSpawn = player.getBedSpawnLocation();
                    if (bedSpawn == null) {
                        player.teleport(plugin.getServer().getWorld(plugin.getMainWorldName()).getSpawnLocation());
                    }
                    else {
                        player.teleport(bedSpawn);
                    }
                }
            }
        }
    }
}
