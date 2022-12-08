package me.fullpotato.badlandscaves.SupernaturalPowers.Spells;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.CustomItem;
import me.fullpotato.badlandscaves.NMS.Possession.PossessionNMS;
import me.fullpotato.badlandscaves.Util.ParticleShapes;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import me.fullpotato.badlandscaves.Util.TargetEntity;

/*
====================
ARTIFACT Domino
====================

Chain up to 5 enemies together. Any damage dealt to one is dealt to all.
Mana cost is applied during every application of Domino to a new target.
There is no drain, but Mana cannot regenerate when Domino is active.

There should be a maximum distance check and a line of sight check done upon damage propagation.

To make it easier to use, this should not select one by one like Possession.
Instead, allow group selection of all enemies in one area.

Enemies will be represented using a graph (adjacency list)
Damage will be propagated by performing DFS on the graph
Each adjacency list will be stored under the caster's UUID in a HashMap
 */

public class Domino extends UsePowers implements Listener {
    //Constants    
    private static final int MAXIMUM_DISTANCE_SQUARED = 400; //20 blocks between each node
    private static final int MAX_LINKS = 5;
    private static final String DOMINO_CASTER_TAG = "domino_caster";
    private static final double DAMAGE_REDUCTION_FACTOR = 0.75;
    private static final int DELAY = 10;

    private HashMap<UUID, HashMap<LivingEntity, HashSet<LivingEntity>>> linkData;

    //External classes being used
    private final TargetEntity targetManager;
    private final ParticleShapes particleShapes;
    private final PossessionNMS outlineManager;

    //Numeric values
    private final int cost;

    public Domino(BadlandsCaves plugin, ParticleShapes particleShapes) {
        super(plugin, particleShapes);
        this.linkData = new HashMap<>();
        this.targetManager = new TargetEntity();
        this.particleShapes = particleShapes;
        this.cost = plugin.getOptionsConfig().getInt("spell_costs.possess_mana_cost");
        this.outlineManager = plugin.getPossessionNMS();
    }

    /**
     * Listens to clicks on new entities and adds to graph
     */
    @EventHandler
    public void NewTargets(PlayerInteractEvent event) {
        //must be a right click
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        //must be in offhand
        if (event.getHand() != EquipmentSlot.OFF_HAND) return;
        final Player player = event.getPlayer();

        //Ensure they are magic class
        if ((byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == (byte) 0) return;

        //is holding spell in offhand
        final ItemStack spellItem = plugin.getCustomItemManager().getItem(CustomItem.DOMINO);
        if (!player.getInventory().getItemInOffHand().isSimilar(spellItem)) return;

        event.setCancelled(true);

        //get targets - using same values as Possession but allowing multiple targets
        final List<LivingEntity> targets = new ArrayList<>(this.targetManager.findTargetLivingEntities(player.getEyeLocation(), 15, 0.2, 3, false, player));
        if (targets == null || targets.size() == 0) return;

        final UUID playerID = player.getUniqueId();

        //player does not have any existing links
        if (!this.linkData.containsKey(playerID)){
            this.linkData.put(playerID, new HashMap<>());
        }
        
        for (LivingEntity target : targets) {
            //domino cannot affect players
            if (target instanceof Player) continue;

            //enforce size limit
            if (this.linkData.get(playerID).keySet().size() >= MAX_LINKS) return;
            
            //mana check
            final double mana = ((double) PlayerScore.MANA.getScore(plugin, player));
            if (mana < cost) {
                notEnoughMana(player);
                return;
            }

            //add the entity to graph
            if (LinkEntity(playerID, target)) {
                //edit their metadata to hold a reference to the owner/caster
                //we're using metadata here instead of PDC because
                //since we're using a hashmap, the graph is already reset on reload/restart anyway
                //the PDC value would be useless w/o the graph
                target.setMetadata(DOMINO_CASTER_TAG, new FixedMetadataValue(plugin, playerID.toString()));

                //subtract mana cost
                PlayerScore.MANA.setScore(plugin, player, mana - (double) (cost));
                PlayerScore.MANA_BAR_ACTIVE_TIMER.setScore(plugin, player, 60);
                PlayerScore.MANA_REGEN_DELAY_TIMER.setScore(plugin, player, plugin.getOptionsConfig().getInt("mana_regen_cooldown"));
            }
        }

    }

    /**
     * Checks if the player hits F while holding a spell. 
     */
    @EventHandler
    public void DetectDisableRequest(PlayerSwapHandItemsEvent event) {
        final Player player = event.getPlayer();

        //Ensure they are magic class
        if ((byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == (byte) 0) return;

        //Ensure they are using Domino
        if (!IsUsingDomino(player.getUniqueId())) return;

        //They had something in their offhand
        final ItemStack item = event.getMainHandItem();
        if (item == null) return;

        //They had a spell in the offhand
        for (ActivePowers spell : ActivePowers.values()) {
            if (item.isSimilar(plugin.getCustomItemManager().getItem(spell.getItem()))) {
                event.setCancelled(true);
                DisableDomino(player);
                return;
            }
        }
    }

    @EventHandler
    public void EntityDamaged(EntityDamageEvent event) {
        if (event.isCancelled()) return;
        if (event.getDamage() <= 0) return;
        if (event.getCause() == DamageCause.CUSTOM) return; //this makes sure that the propagation damage doesn't cause infinite recursion
        if (!(event.getEntity() instanceof LivingEntity)) return;

        //entity is being domino'ed
        final LivingEntity entity = (LivingEntity) event.getEntity();
        if (!entity.hasMetadata(DOMINO_CASTER_TAG)) return;

        final List<MetadataValue> metadataValues = entity.getMetadata(DOMINO_CASTER_TAG);
        if (metadataValues.size() < 1) return;

        //get the caster's id and caster from the entity
        UUID casterID = null;
        try {
            casterID = UUID.fromString(metadataValues.get(0).asString());
        }
        catch (Exception e) {
            return;
        }

        final Player player = plugin.getServer().getPlayer(casterID);
        if (player == null) return;
        if (!IsUsingDomino(casterID)) return;

        //propagate damage to all entities linked by Domino
        HashSet<LivingEntity> visited = new HashSet<>();
        PropagateDamage(player, entity, event.getDamage(), entity.getNoDamageTicks(), 5, visited);

        //actually visited
        if (visited.size() > 1) {
            //the function above already damages the original entity too, so disable this
            event.setCancelled(true);
        }
        else {
            RemoveEntity(casterID, entity);
        }
        
    }

    @EventHandler
    public void RemoveEntityOnDeath (EntityDeathEvent event) {
        final LivingEntity entity = event.getEntity();

        //TODO: Cleanup; code repetition
        if (!entity.hasMetadata(DOMINO_CASTER_TAG)) return;

        final List<MetadataValue> metadataValues = entity.getMetadata(DOMINO_CASTER_TAG);
        if (metadataValues.size() < 1) return;

        //get the caster's id and caster from the entity
        UUID casterID = null;
        try {
            casterID = UUID.fromString(metadataValues.get(0).asString());
        }
        catch (Exception e) {
            return;
        }

        final Player player = plugin.getServer().getPlayer(casterID);
        if (player == null) return;
        if (!IsUsingDomino(casterID)) return;

        final UUID finalCasterID = casterID;
        new BukkitRunnable() {
            @Override
            public void run() {
                RemoveEntity(finalCasterID, entity);
            }   
        }.runTaskLater(plugin, 5);

    }

    @EventHandler
    public void PlayerDeathReset (PlayerDeathEvent event) {
        final Player player = event.getEntity();

        //Ensure they are magic class and using domino
        if ((byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == (byte) 0) return;
        if (!IsUsingDomino(player.getUniqueId())) return;

        DisableDomino(player);
    }

    @EventHandler
    public void PlayerDimensionChangeReset (PlayerChangedWorldEvent event) {
        final Player player = event.getPlayer();

        //Ensure they are magic class and using domino
        if ((byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == (byte) 0) return;
        if (!IsUsingDomino(player.getUniqueId())) return;

        DisableDomino(player);
    }

    /**
     * Helper function to disable a player's Domino
     */
    public void DisableDomino(Player player) {
        //Remove every entity's metadata
        for (LivingEntity entity : this.linkData.get(player.getUniqueId()).keySet()) {
            if (entity.hasMetadata(DOMINO_CASTER_TAG)) {
                entity.removeMetadata(DOMINO_CASTER_TAG, plugin);
            }
        }

        //Clear the graph
        this.linkData.get(player.getUniqueId()).clear();
    }

    /**
     * Helper function to add entity to the graph
     * @param playerID Domino Caster
     * @param entity Entity to be added
     * @return If the entity was successfuly added to the graph
     */
    private boolean LinkEntity(UUID playerID, LivingEntity entity) {
        //if empty, initialize the list with the first entity
        if (!IsUsingDomino(playerID)) {
            this.linkData.get(playerID).put(entity, new HashSet<>());
            return true;
        }

        //if not, make sure the entity doesn't already exist in the graph
        if (this.linkData.get(playerID).containsKey(entity)) return false;

        //otherwise, make an edge with the closest entity in the graph that is within range and has line of sight
        LivingEntity closest = null;
        double closestDist = Double.MAX_VALUE;

        //find the closest entity that meets conditions
        for (LivingEntity source : this.linkData.get(playerID).keySet()) {
            if (IsCompatible(source, entity)) {
                double distSq = entity.getLocation().distanceSquared(source.getLocation());
                if (distSq < closestDist) {
                    closest = source;
                    closestDist = distSq;
                }
            }
        }

        //make a link to the closest entity
        if (closest != null) {
            //connect the closest to new, then connect new to closest
            this.linkData.get(playerID).get(closest).add(entity);

            final HashSet<LivingEntity> set = new HashSet<>();
            set.add(closest);
            this.linkData.get(playerID).put(entity, set);
            return true;
        }


        return false;
    }

    private void RemoveEntity(UUID playerID, LivingEntity entity) {
        if (!IsUsingDomino(playerID)) return;
        if (!this.linkData.get(playerID).containsKey(entity)) return;

        //for each of its neighbors, remove their edge to this entity
        for (LivingEntity neighbor : this.linkData.get(playerID).get(entity)) {
            //remove connection from neighbor to this entity
            for (LivingEntity neighborsNeighbors : this.linkData.get(playerID).get(neighbor)) {
                if (neighborsNeighbors.getUniqueId().equals(entity.getUniqueId())) {
                    this.linkData.get(playerID).get(neighbor).remove(neighborsNeighbors);
                    break;
                }
            }

            //Recursively disconnect any "stranded" neighbors
            if (this.linkData.get(playerID).get(neighbor).isEmpty()) {
                RemoveEntity(playerID, neighbor);
            }
        }
        
        this.linkData.get(playerID).remove(entity);
        entity.removeMetadata(DOMINO_CASTER_TAG, plugin);
    }

    /**
     * Iterates through graph and propagates damage to every entity using DFS
     * @param playerID The Domino caster's ID
     * @param entity Source Entity to start from
     * @param damage Damage to apply
     * @param delay Initial delay
     * @param visited Empty HashSet
     */
    private void PropagateDamage(Player player, LivingEntity entity, double damage, int iFrames, int delay, HashSet<LivingEntity> visited) {
        //damage entity and mark as visited
        new BukkitRunnable() {
            @Override
            public void run() {
                entity.damage(damage);
                entity.setNoDamageTicks(iFrames);
            }
        }.runTaskLater(plugin, delay);
        visited.add(entity);

        if (!this.linkData.get(player.getUniqueId()).containsKey(entity)) return;

        for (LivingEntity neighbor : this.linkData.get(player.getUniqueId()).get(entity)) {
            if (!visited.contains(neighbor) && IsCompatible(entity, neighbor)) {
                //continue dfs
                PropagateDamage(player, neighbor, damage * DAMAGE_REDUCTION_FACTOR, iFrames, delay + DELAY, visited);
            }
        }
    }

    public void ShowVisuals (Player player) {
        if (!IsUsingDomino(player.getUniqueId())) return;

        HashSet<LivingEntity> visited = new HashSet<>();

        for (LivingEntity entity :  this.linkData.get(player.getUniqueId()).keySet()) {
            ConnectingParticle(player, entity, visited);
        }

    }


    /**
     * Iterates through graph and shows particle on every edge (Entity <-> Entity)
     * @param playerID The Domino caster's ID
     * @param entity Source Entity to start from
     * @param visited Empty HashSet
     */
    private void ConnectingParticle(Player player, LivingEntity entity, HashSet<LivingEntity> visited) {
        if (visited.contains(entity)) return;

        //make entity glow and mark as visited
        this.outlineManager.setIndicator(player, entity);
        visited.add(entity);

        //loop through this entity's neighbors
        for (LivingEntity neighbor : this.linkData.get(player.getUniqueId()).get(entity)) {
            if (IsCompatible(entity, neighbor)) {
                //spawns connecting particle
                particleShapes.line(
                    player,
                    Particle.REDSTONE,
                    entity.getEyeLocation(),
                    neighbor.getEyeLocation(),
                    0,
                    new Particle.DustOptions(Color.fromRGB(255, 196, 0), 1),
                    1
                );

                //continue dfs
                ConnectingParticle(player, neighbor, visited);
            }
        }
    }

    /**
     * Helper function to check if two entities are compatible (that is, within distance and line of sight)
     */
    private boolean IsCompatible(LivingEntity source, LivingEntity target) {
        return source.getLocation().distanceSquared(target.getLocation()) <= MAXIMUM_DISTANCE_SQUARED && source.hasLineOfSight(target);
    }

    public boolean IsUsingDomino(UUID playerID) {
        return this.linkData.containsKey(playerID) && this.linkData.get(playerID) != null && this.linkData.get(playerID).size() > 0;
    }

    public boolean HasCapacity(UUID playerID) {
        return !IsUsingDomino(playerID) || this.linkData.get(playerID).size() < MAX_LINKS;
    }

}
