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
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.loot.LootTables;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.fullpotato.badlandscaves.BadlandsCaves;
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
                        spawnBoss(world, spawnLoc);

                        //Mark fight as active
                        final PersistentDataContainer lecternPDC = ((Lectern) block.getState()).getPersistentDataContainer();
                        lecternPDC.set(this.activeKey, PersistentDataType.BYTE, (byte) 1);
                    }


                    //Teleport the player into the arena
                    final Location arenaEntrance = block.getLocation().add(0, 0, 4);
                    player.teleport(arenaEntrance, TeleportCause.PLUGIN);

                }
            }
        }
    }

    private void spawnBoss(World world, Location location) {
        final Vindicator boss = (Vindicator) world.spawnEntity(location, EntityType.VINDICATOR);

        //Attributes, tags, loot
        boss.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(80.0f);
        boss.getAttribute(Attribute.GENERIC_ARMOR).setBaseValue(25.0f);
        boss.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(25.0f);
        boss.getAttribute(Attribute.GENERIC_ATTACK_KNOCKBACK).setBaseValue(12.5f);
        boss.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.5f);
        boss.getAttribute(Attribute.GENERIC_FOLLOW_RANGE).setBaseValue(999f);
        boss.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(999f);
        boss.getPersistentDataContainer().set(new NamespacedKey(plugin, "is_castle_boss"), PersistentDataType.BYTE, (byte) 1);
        boss.setLootTable(Bukkit.getLootTable(LootTables.EMPTY.getKey()));
        boss.setRemoveWhenFarAway(false);
        boss.setPersistent(true);
        boss.setGlowing(true);
        boss.setHealth(boss.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
        boss.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 32767, 0, true, false));

        //Weapon ---------------
        final ItemStack weapon = new ItemStack(Material.NETHERITE_AXE);
        final ItemMeta meta = weapon.getItemMeta();
        if (meta != null) {
            meta.setCustomModelData(172); // Model of Void Axe
            meta.addEnchant(Enchantment.DURABILITY, 1, false);
            weapon.setItemMeta(meta);
        }
                        
        boss.getEquipment().setItemInMainHand(weapon);
        boss.getEquipment().setItemInMainHandDropChance(-999);
        boss.getEquipment().setItemInOffHandDropChance(-999);
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
