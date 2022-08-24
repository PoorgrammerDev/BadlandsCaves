package me.fullpotato.badlandscaves.MobBuffs;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.SupernaturalPowers.ReflectionStage.ZombieBossBehavior;
import me.fullpotato.badlandscaves.Util.NameTagHide;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.SoundCategory;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Biome;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.loot.LootTables;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class WitchBuff implements Listener {
    private final BadlandsCaves plugin;
    private final Random random;

    public WitchBuff(BadlandsCaves plugin, Random random) {
        this.plugin = plugin;
        this.random = random;
    }

    @EventHandler
    public void witchStatBuff (CreatureSpawnEvent event) {
        boolean hardmode = plugin.getSystemConfig().getBoolean("hardmode");
        if (!hardmode) return;
        if (!(event.getEntity() instanceof Witch)) return;

        final Witch witch = (Witch) event.getEntity();

        //Change behaviour if the monster is spawned in The Void
        if (event.getLocation().getBlock().getBiome() == Biome.THE_VOID) {
            witch.setCustomName("Void Witch");
            witch.getPersistentDataContainer().set(new NamespacedKey(plugin, "voidMonster"), PersistentDataType.BYTE, (byte) 1);

            witch.getAttribute(Attribute.GENERIC_ARMOR).setBaseValue(15.0);
            NameTagHide.getInstance().Hide(witch);
            return;
        }

        witch.getAttribute(Attribute.GENERIC_ARMOR).setBaseValue(9.5);
    }

    @EventHandler
    public void witchTargetKeepDistance (EntityTargetLivingEntityEvent event) {
        boolean hardmode = plugin.getSystemConfig().getBoolean("hardmode");
        if (!hardmode) return;

        if (event.getEntity() instanceof Witch) {
            final Witch witch = (Witch) event.getEntity();
            final LivingEntity target = event.getTarget();
            if (target == null) return;
            if (witch.hasMetadata("runnable") && witch.getMetadata("runnable").get(0).asBoolean()) return;

            final int chaos = plugin.getSystemConfig().getInt("chaos_level");
            final double chance = Math.pow(1.045, chaos) - 1;


            if (!witch.hasMetadata("teleport_cooldown")) witch.setMetadata("teleport_cooldown", new FixedMetadataValue(plugin, 0));
            witch.setMetadata("runnable", new FixedMetadataValue(plugin, true));
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (witch.isDead() || target.isDead() || witch.getTarget() == null || !witch.getTarget().equals(target)) {
                        witch.setMetadata("runnable", new FixedMetadataValue(plugin, false));
                        this.cancel();
                    }
                    else {
                        int cooldown = witch.getMetadata("teleport_cooldown").get(0).asInt();

                        if (cooldown > 0) {
                            witch.setMetadata("teleport_cooldown", new FixedMetadataValue(plugin, cooldown - 1));
                        }
                        else {
                            double square_distance = target.getLocation().distanceSquared(witch.getLocation());
                            if (square_distance < 9) {
                                if (random.nextInt(100) < chance) spawnUnderworldFighter(target, witch.getLocation(), 10, witch);
                                witch.teleport(findLocation(target.getLocation(), random, 9, 100, 8), PlayerTeleportEvent.TeleportCause.PLUGIN);

                                for (Player powered : plugin.getServer().getOnlinePlayers()) {
                                    if ((byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, powered) == 1) {
                                        powered.playSound(witch.getLocation(), "custom.supernatural.displace.warp", SoundCategory.HOSTILE, 0.3F, 1);
                                        powered.spawnParticle(Particle.SPELL_WITCH, witch.getLocation(), 5, 0.1, 0.1, 0.1, 1);
                                    }
                                }


                                witch.setMetadata("teleport_cooldown", new FixedMetadataValue(plugin, random.nextInt(5) + 10));
                            }
                        }
                    }
                }
            }.runTaskTimer(plugin, 0, 20);
        }
    }

    public Location findLocation (Location targetLocation, Random random, int lower_bound, int higher_bound, int range) {
        ZombieBossBehavior locationFinder = new ZombieBossBehavior(plugin, random);
        Location test;
        for (int i = 0; i < 100; i++) {
            test = locationFinder.getNearbyLocation(targetLocation, random, range);
            if (test != null && test.distanceSquared(targetLocation) > lower_bound && test.distanceSquared(targetLocation) < higher_bound) {
                return test;
            }
        }
        return targetLocation;
    }

    public void spawnUnderworldFighter (LivingEntity target, Location location, int lifespan, Witch source) {
        location.getWorld().spawnParticle(Particle.FLASH, location, 1);
        Vindicator vindicator = (Vindicator) location.getWorld().spawnEntity(location, EntityType.VINDICATOR);
        vindicator.getEquipment().setItemInMainHandDropChance(0);
        vindicator.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 9999, 1, true));
        vindicator.getAttribute(Attribute.GENERIC_ARMOR).setBaseValue(15);
        vindicator.setLootTable(LootTables.EMPTY.getLootTable());
        vindicator.setCanJoinRaid(false);
        vindicator.setPatrolLeader(false);
        vindicator.setTarget(target);

        final NamespacedKey voidKey = new NamespacedKey(plugin, "voidMonster");
        if (source.getPersistentDataContainer().has(voidKey, PersistentDataType.BYTE) && source.getPersistentDataContainer().get(voidKey, PersistentDataType.BYTE) == 1) {
            vindicator.setCustomName("Void Vindicator");
            NameTagHide.getInstance().Hide(vindicator);
        }

        int[] timeElapsed = {0};
        new BukkitRunnable() {
            @Override
            public void run() {
                if (timeElapsed[0] > lifespan) {
                    vindicator.getLocation().getWorld().spawnParticle(Particle.FLASH, vindicator.getLocation(), 1);
                    vindicator.remove();
                    this.cancel();
                }

                if (vindicator.isDead() || target.isDead()) {
                    this.cancel();
                }
                else {
                    if (vindicator.getLocation().distanceSquared(target.getLocation()) > 25) {
                        ZombieBossBehavior teleporter = new ZombieBossBehavior(plugin, random);
                        Location nearbyLocation = teleporter.getNearbyLocation(target.getLocation(), random, 2);
                        if (nearbyLocation != null) vindicator.teleport(nearbyLocation, PlayerTeleportEvent.TeleportCause.PLUGIN);
                    }
                }
                timeElapsed[0]++;
            }
        }.runTaskTimer(plugin, 0, 20);
    }
}
