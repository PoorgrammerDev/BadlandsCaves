package me.fullpotato.badlandscaves.AlternateDimensions.StructureMechanics;

import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.Lectern;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vindicator;
import org.bukkit.entity.Witch;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootTables;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.CustomItem;
import net.md_5.bungee.api.ChatColor;

public class CastleBossEntry implements Listener {
    private BadlandsCaves plugin;
    private NamespacedKey baseKey;      //used to check if lectern is the Castle Boss Entrance lectern
    private NamespacedKey activeKey;    //used to check if there is already an active fight

    public CastleBossEntry(BadlandsCaves plugin) {
        this.plugin = plugin;
        this.baseKey = new NamespacedKey(plugin, "castle_boss_entrance");
        this.activeKey = new NamespacedKey(plugin, "castle_boss_fight_active");
    }

    @EventHandler
    public void AttemptEnterStage(PlayerInteractEvent event) {
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            final Block block = event.getClickedBlock();
            if (block != null && checkBlock(block, baseKey)) {
                event.setUseInteractedBlock(Event.Result.DENY);

                if (event.getHand() != null && event.getHand().equals(EquipmentSlot.HAND)) {
                    final Player player = event.getPlayer();
                    final World world = block.getWorld();

                    //If fight is not active yet =================
                    if (!checkBlock(block, this.activeKey)) {
                        //Check if all mobs on the outside have been destroyed =============
                        final Collection<Entity> guards = world.getNearbyEntities(block.getLocation(), 20, 20, 20, (Entity entity) -> {
                            return (entity instanceof Vindicator || entity instanceof Witch);
                        });
                    
                        if (guards.size() > 0) {
                            player.sendMessage(ChatColor.RED + "Defeat the remaining guards.");

                            if (guards.size() < 5) {
                                for (Entity guard : guards) {
                                    if (guard instanceof LivingEntity) {
                                        final LivingEntity livingGuard = (LivingEntity) guard;
                                        livingGuard.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 30, 0));
                                    }
                                }
                            }

                            return;
                        }

                        //Summon the boss
                        final Location spawnLoc = block.getLocation().add(0, 3, 38); //This is the offset from the lectern to the throne
                        final Location[] treeLocations = {  // Offsets from lectern to the trees
                            block.getLocation().add(15, 1, 33), 
                            block.getLocation().add(-15, 1, 33),
                            block.getLocation().add(15, 1, 13),
                            block.getLocation().add(-15, 1, 13),
                        };

                        Vindicator boss = spawnBoss(world, spawnLoc, block.getLocation(), treeLocations);
                        boss.setTarget(player);

                        //Mark fight as active
                        final Lectern blockState = (Lectern) block.getState();
                        blockState.getPersistentDataContainer().set(this.activeKey, PersistentDataType.BYTE, (byte) 1);
                        blockState.update();
                    }


                    //Teleport the player into the arena
                    final Location arenaEntrance = block.getLocation().add(0, 0, 4);
                    player.teleport(arenaEntrance, TeleportCause.PLUGIN);

                }
            }
        }
    }

    private Vindicator spawnBoss(World world, Location location, Location lecternLocation, Location[] treeLocations) {
        final Vindicator boss = (Vindicator) world.spawnEntity(location, EntityType.VINDICATOR);

        //Attributes, tags, loot
        boss.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(80.0f);
        boss.getAttribute(Attribute.GENERIC_ARMOR).setBaseValue(25.0f);
        boss.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(25.0f);
        boss.getAttribute(Attribute.GENERIC_ATTACK_KNOCKBACK).setBaseValue(12.5f);
        boss.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.4f);
        boss.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(0.75f);
        boss.getAttribute(Attribute.GENERIC_FOLLOW_RANGE).setBaseValue(999f);
        boss.getPersistentDataContainer().set(new NamespacedKey(plugin, "is_castle_boss"), PersistentDataType.BYTE, (byte) 1);
        boss.setLootTable(Bukkit.getLootTable(LootTables.EMPTY.getKey()));
        boss.setRemoveWhenFarAway(false);
        boss.setPersistent(true);
        boss.setGlowing(true);
        boss.setHealth(boss.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
        boss.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 32767, 0, true, false));
        boss.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 32767, 2, true, false));

        plugin.getSystemConfig().set("castle_boss." + boss.getUniqueId() + ".saved_lectern_location", lecternLocation);

        plugin.getSystemConfig().set("castle_boss." + boss.getUniqueId() + ".saved_tree_location_0", treeLocations[0]);
        plugin.getSystemConfig().set("castle_boss." + boss.getUniqueId() + ".saved_tree_location_1", treeLocations[1]);
        plugin.getSystemConfig().set("castle_boss." + boss.getUniqueId() + ".saved_tree_location_2", treeLocations[2]);
        plugin.getSystemConfig().set("castle_boss." + boss.getUniqueId() + ".saved_tree_location_3", treeLocations[3]);
        plugin.saveSystemConfig();

        //Weapon ---------------
        final ItemStack weapon = plugin.getCustomItemManager().getItem(CustomItem.VOIDMATTER_AXE);
        
        boss.getEquipment().setItemInMainHand(weapon);
        boss.getEquipment().setItemInMainHandDropChance(-999);
        boss.getEquipment().setItemInOffHandDropChance(-999);
        return boss;
    }


    public boolean checkBlock(Block block, NamespacedKey key) {
        if (block.getType().equals(Material.LECTERN)) {
            if (block.getState() instanceof Lectern) {
                final Lectern state = (Lectern) block.getState();
                final PersistentDataContainer container = state.getPersistentDataContainer();

                if (container.has(key, PersistentDataType.BYTE)) {
                    Byte result = container.get(key, PersistentDataType.BYTE);
                    if (result != null) {
                        return result == (byte) 1;
                    }
                }
            }
        }
        return false;
    }


}
