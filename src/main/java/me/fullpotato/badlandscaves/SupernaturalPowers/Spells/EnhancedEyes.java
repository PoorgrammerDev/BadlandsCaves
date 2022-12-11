package me.fullpotato.badlandscaves.SupernaturalPowers.Spells;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.CustomItem;
import me.fullpotato.badlandscaves.NMS.EnhancedEyes.EnhancedEyesNMS;
import me.fullpotato.badlandscaves.SupernaturalPowers.Artifacts.Artifact;
import me.fullpotato.badlandscaves.SupernaturalPowers.Artifacts.ArtifactManager;
import me.fullpotato.badlandscaves.SupernaturalPowers.Artifacts.Mechanisms.ArtifactDirectionalVision;
import me.fullpotato.badlandscaves.SupernaturalPowers.Spells.Runnables.EyesRunnable;
import me.fullpotato.badlandscaves.Util.ParticleShapes;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;


public class EnhancedEyes extends UsePowers implements Listener {
    private final List<Material> mineralsTier1 = Arrays.asList(Material.COAL_ORE, Material.IRON_ORE, Material.COAL_BLOCK, Material.IRON_BLOCK);
    private final List<Material> mineralsTier2 = Arrays.asList(Material.DIAMOND_ORE, Material.EMERALD_ORE, Material.GOLD_ORE, Material.NETHER_GOLD_ORE, Material.GILDED_BLACKSTONE, Material.LAPIS_ORE, Material.REDSTONE_ORE, Material.NETHER_QUARTZ_ORE, Material.DIAMOND_BLOCK, Material.EMERALD_BLOCK, Material.GOLD_BLOCK, Material.LAPIS_BLOCK, Material.REDSTONE_BLOCK, Material.DEAD_TUBE_CORAL_BLOCK, Material.ANCIENT_DEBRIS);
    private final List<Material> storage = Arrays.asList(Material.CHEST, Material.BARREL, Material.SHULKER_BOX, Material.BLACK_SHULKER_BOX, Material.BLUE_SHULKER_BOX, Material.BROWN_SHULKER_BOX, Material.CYAN_SHULKER_BOX, Material.GRAY_SHULKER_BOX, Material.GREEN_SHULKER_BOX, Material.LIGHT_BLUE_SHULKER_BOX, Material.LIGHT_GRAY_SHULKER_BOX, Material.LIME_SHULKER_BOX, Material.MAGENTA_SHULKER_BOX, Material.ORANGE_SHULKER_BOX, Material.PINK_SHULKER_BOX, Material.PURPLE_SHULKER_BOX, Material.YELLOW_SHULKER_BOX, Material.RED_SHULKER_BOX, Material.WHITE_SHULKER_BOX, Material.SPAWNER);
    private final EnhancedEyesNMS nms;
    private final int initial_mana_cost;
    private final int constant_mana_drain;
    private final Map<Integer, Integer> levelRangeMap;
    private final ArtifactManager artifactManager;
    private final ArtifactDirectionalVision artifactDirectionalVision;
    private final World backrooms;
    private final ParticleShapes particleShapes;

    public EnhancedEyes(BadlandsCaves plugin, ArtifactManager artifactManager, ParticleShapes particleShapes) {
        super(plugin, particleShapes);
        this.backrooms = plugin.getServer().getWorld(plugin.getBackroomsWorldName());
        nms = plugin.getEnhancedEyesNMS();
        this.artifactManager = artifactManager;
        this.particleShapes = particleShapes;

        levelRangeMap = new HashMap<>();
        levelRangeMap.put(1, 5);
        levelRangeMap.put(2, 10);
        initial_mana_cost = plugin.getOptionsConfig().getInt("spell_costs.eyes_mana_cost");
        constant_mana_drain = plugin.getOptionsConfig().getInt("spell_costs.eyes_mana_drain");
        artifactDirectionalVision = new ArtifactDirectionalVision(plugin, this, particleShapes);
    }

    @EventHandler
    public void use_eyes (PlayerInteractEvent event) {
        Player player = event.getPlayer();

        final boolean has_powers = (byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == 1;
        if (!has_powers) return;

        final ItemStack eyes = plugin.getCustomItemManager().getItem(CustomItem.ENHANCED_EYES);
        if (player.getInventory().getItemInOffHand().isSimilar(eyes)) {
            Action action = event.getAction();
            if (action.equals(Action.RIGHT_CLICK_BLOCK) || action.equals(Action.RIGHT_CLICK_AIR)) {
                EquipmentSlot e = event.getHand();
                assert e != null;
                if (e.equals(EquipmentSlot.OFF_HAND)) {
                    final boolean hardmode = plugin.getSystemConfig().getBoolean("hardmode");
                    event.setCancelled(true);

                    if ((byte) PlayerScore.SPELL_COOLDOWN.getScore(plugin, player) == 1) return;
                    if (((int) PlayerScore.SPELLS_SILENCED_TIMER.getScore(plugin, player) > 0)) return;
                    if (attemptSilence(player)) return;
                    if (player.getWorld().equals(backrooms)) return;

                    final int eyes_level = (PlayerScore.EYES_LEVEL.hasScore(plugin, player)) ? (int) PlayerScore.EYES_LEVEL.getScore(plugin, player) : 0;
                    if (eyes_level < 1) return;

                    final boolean using_eyes = (byte) PlayerScore.USING_EYES.getScore(plugin, player) == 1;

                    preventDoubleClick(player);
                    if (using_eyes) {
                        PlayerScore.USING_EYES.setScore(plugin, player, 0);
                    }
                    else {
                        double mana = ((double) PlayerScore.MANA.getScore(plugin, player));

                        boolean waive = false;
                        if (hardmode) {
                            if (artifactManager.hasArtifact(player, Artifact.CHEAP_SIGHT)) {
                                waive = true;
                            }
                        }


                        if ((waive && mana >= constant_mana_drain) || mana >= (initial_mana_cost + constant_mana_drain)) {
                            final int radius = levelRangeMap.get(eyes_level);

                            if (hardmode) {
                                if (artifactManager.hasArtifact(player, Artifact.DIRECTIONAL_VISION)) {
                                    enableEnhancedEyes(player, artifactDirectionalVision.getDirectionalBlocks(player, radius * 3, radius / 3), artifactDirectionalVision.getDirectionalEntities(player, radius * 3, radius / 3), false);
                                    return;
                                }
                            }

                            enableEnhancedEyes(player, getNearbyBlocks(player.getLocation(), radius), player.getNearbyEntities(radius, radius, radius), true);
                        }
                        else {
                            notEnoughMana(player);
                        }
                    }
                }
            }
        }
    }

    public void enableEnhancedEyes (Player player, Set<Block> blocks, Collection<Entity> entities, boolean particle) {
        final boolean hardmode = plugin.getSystemConfig().getBoolean("hardmode");
        final int eyes_level = (PlayerScore.EYES_LEVEL.hasScore(plugin, player)) ? (int) PlayerScore.EYES_LEVEL.getScore(plugin, player) : 0;
        final double mana = (double) PlayerScore.MANA.getScore(plugin, player);
        final Location location = player.getLocation();

        //EFFECTS-------------------------------------------------------------------
        //Sound Effects
        player.playSound(player.getLocation(), "custom.supernatural.enhanced_eyes.start", SoundCategory.PLAYERS, 0.5F, 1);
        new BukkitRunnable() {
            @Override
            public void run() {
                if (((byte) PlayerScore.USING_EYES.getScore(plugin, player) == 1)) {
                    player.stopSound("custom.supernatural.enhanced_eyes.ambience");
                    player.playSound(player.getLocation(), "custom.supernatural.enhanced_eyes.ambience", 0.4F, 1);
                }
                else {
                    this.cancel();
                }
            }
        }.runTaskTimerAsynchronously(plugin, 5, 330);

        if (particle) {
            //Particle effects
            particleShapes.sphere(player, Particle.REDSTONE, player.getLocation(), levelRangeMap.get(eyes_level) - 1, 0, new Particle.DustOptions(Color.BLUE, 1));
            //Effects for nearby magic players
            for (Entity entity : player.getNearbyEntities(10, 10, 10)) {
                if (entity instanceof Player) {
                    Player powered = (Player) entity;
                    if (!(powered.equals(player)) && (byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, powered) == 1 && powered.getWorld().equals(player.getWorld()) && powered.getLocation().distanceSquared(player.getLocation()) < 100) {
                        powered.playSound(player.getLocation(), "custom.supernatural.enhanced_eyes.start", SoundCategory.PLAYERS, 0.3F, 1);
                        powered.spawnParticle(Particle.REDSTONE, player.getEyeLocation(), 5, 0.05, 0.05, 0.05, 0, new Particle.DustOptions(Color.BLUE, 1));
                    }
                }
            }
        }

        //SPAWN BLOCK INDICATORS------------------------------------------------
        final ArrayList<Integer> ids = new ArrayList<>();
        final StringBuilder builder = new StringBuilder();
        blocks.forEach(block -> {
            final Location blockLoc = block.getLocation();
            int nmsID;

            if (eyes_level > 1) {
                //spawns colored indicator
                final ChatColor color = getColor(block.getType());
                nmsID = nms.spawnIndicator(player, blockLoc, color);
            }
            else {
                //spawns white indicator
                nmsID = nms.spawnIndicator(player, blockLoc);
            }

            //adds ids to list and appends to eyes map
            ids.add(nmsID);
            builder.append(blockLoc.getBlockX()).append(",").append(blockLoc.getBlockY()).append(",").append(blockLoc.getBlockZ()).append(":").append(nmsID).append("_");
        });

        plugin.getSystemConfig().set("player_info." + player.getUniqueId() + ".eyes_map", builder.toString());
        plugin.saveSystemConfig();

        boolean waive = false;
        if (hardmode) {
            if (artifactManager.hasArtifact(player, Artifact.CHEAP_SIGHT)) {
                waive = true;
            }
        }

        PlayerScore.MANA.setScore(plugin, player, mana - ((waive ? 0 : initial_mana_cost) - (constant_mana_drain / 20.0)));
        PlayerScore.MANA_REGEN_DELAY_TIMER.setScore(plugin, player, plugin.getOptionsConfig().getInt("mana_regen_cooldown"));
        PlayerScore.MANA_BAR_ACTIVE_TIMER.setScore(plugin, player, 60);
        PlayerScore.USING_EYES.setScore(plugin, player, 1);

        new EyesRunnable(plugin, player, location, ids, entities).runTaskTimer(plugin, 0, 0);
    }

    public Set<Block> getNearbyBlocks(Location location, int blockRange) {
        final Set<Block> output = new HashSet<>();
        final double distSqRange = Math.pow(blockRange - 1, 2);

        for (int x = -blockRange; x < blockRange; x++) {
            for (int y = -blockRange; y < blockRange; y++) {
                for (int z = -blockRange; z < blockRange; z++) {
                    final Location clone = location.clone().add(x, y, z);
                    if (location.distanceSquared(clone) < distSqRange) {
                        final Block block = clone.getBlock();
                        final Material type = block.getType();
                        if (mineralsTier1.contains(type) || mineralsTier2.contains(type) || storage.contains(type)) {
                            output.add(block);
                        }
                    }
                }
            }
        }
        return output;
    }

    public ChatColor getColor (Material material) {
        if (mineralsTier1.contains(material)) {
            return ChatColor.GRAY;
        }
        else if (mineralsTier2.contains(material)) {
            return ChatColor.BLUE;
        }
        else if (storage.contains(material)) {
            return ChatColor.GREEN;
        }
        return ChatColor.WHITE;
    }

    @EventHandler
    public void breakBlock (BlockBreakEvent event) {
        Player player = event.getPlayer();

        final boolean has_powers = (byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == 1;
        if (!has_powers) return;

        final boolean using_eyes = ((byte) PlayerScore.USING_EYES.getScore(plugin, player) == 1);
        if (!using_eyes) return;

        String string_map = plugin.getSystemConfig().getString("player_info." + player.getUniqueId() + ".eyes_map");

        assert string_map != null;
        HashMap<Location, Integer> blocks_maps = stringToMap(player, string_map);

        Location block_location = event.getBlock().getLocation();
        if (blocks_maps.containsKey(block_location)) {
            EnhancedEyesNMS nms = plugin.getEnhancedEyesNMS();
            nms.removeIndicator(player, blocks_maps.get(block_location));
        }

    }

    @EventHandler
    public void forceExit (PlayerSwapHandItemsEvent event) {
        final Player player = event.getPlayer();
        if ((byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == 1) {
        
            //must not be in swap mode
            if (!PlayerScore.SWAP_WINDOW.hasScore(plugin, player) || (byte) PlayerScore.SWAP_WINDOW.getScore(plugin, player) == 1) return;
        
            if ((byte) PlayerScore.USING_EYES.getScore(plugin, player) == 1) {
                final ItemStack item = event.getMainHandItem();
                if (item != null) {
                    for (ActivePowers value : ActivePowers.values()) {
                        if (item.isSimilar(plugin.getCustomItemManager().getItem(value.getItem()))) {
                            event.setCancelled(true);
                            PlayerScore.USING_EYES.setScore(plugin, player, 0);
                            return;
                        }
                    }
                }
            }
        }
    }


    public HashMap<Location, Integer> stringToMap(Player player, String string_map) {
        World world = player.getWorld();
        ArrayList<Integer> ids = new ArrayList<>();
        ArrayList<Location> locations = new ArrayList<>();
        HashMap<Location, Integer> output = new HashMap<>();

        String[] pairs = string_map.split("_");

        for (String str : pairs) {
            String[] pairs_layer_two = str.split(":");
            ids.add(Integer.parseInt(pairs_layer_two[1]));

            String[] pairs_layer_three = pairs_layer_two[0].split(",");
            ArrayList<Integer> coords = new ArrayList<>();

            for (String str_coord : pairs_layer_three) {
                coords.add(Integer.parseInt(str_coord));
            }

            if (coords.size() == 3) {
                Location location = new Location(world, coords.get(0), coords.get(1), coords.get(2));
                locations.add(location);
            }
        }

        if (ids.size() == locations.size()) {
            for (int a = 0; a < ids.size(); a++) {
                output.put(locations.get(a), ids.get(a));
            }
        }

        return output;
    }

    public boolean isOre (Material material) {
        return mineralsTier1.contains(material) || mineralsTier2.contains(material) || storage.contains(material);
    }

}
