package me.fullpotato.badlandscaves.MobBuffs;

import me.fullpotato.badlandscaves.BadlandsCaves;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Phantom;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Random;

public class PhantomBuff implements Listener {
    private final BadlandsCaves plugin;
    private final Random random;

    public PhantomBuff(BadlandsCaves plugin, Random random) {
        this.plugin = plugin;
        this.random = random;
    }

    @EventHandler
    public void phantomTarget (EntityTargetLivingEntityEvent event) {
        if (event.getEntity() instanceof Phantom) {
            Phantom phantom = (Phantom) event.getEntity();
            if (!phantom.getPassengers().isEmpty()) return;

            boolean hardmode = plugin.getSystemConfig().getBoolean("hardmode");
            if (!hardmode) return;

            if (event.getTarget() instanceof Player) {
                Player player = (Player) event.getTarget();

                final int chaos = plugin.getSystemConfig().getInt("chaos_level");
                final double chance = Math.pow(1.045, chaos) - 1;
                if (random.nextInt(100) < chance) {
                    ArrayList<Monster> nearbyMonsters = new ArrayList<>();
                    for (Entity entity : phantom.getNearbyEntities(20, 20, 20)) {
                        if (entity instanceof Monster && !(entity instanceof Phantom) && entity.getVehicle() == null && entity.getLocation().distanceSquared(player.getLocation()) > 4) {
                            nearbyMonsters.add((Monster) entity);
                        }
                    }

                    if (nearbyMonsters.isEmpty()) return;

                    Monster monster = nearbyMonsters.get(random.nextInt(nearbyMonsters.size()));

                    phantom.setTarget(monster);
                    event.setCancelled(true);

                    int[] run = {0};
                    double followrange = phantom.getAttribute(Attribute.GENERIC_FOLLOW_RANGE).getBaseValue();
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (phantom.isDead() || monster.isDead() || player.isDead()) {
                                this.cancel();
                            }
                            else if (phantom.getLocation().distanceSquared(monster.getLocation()) < 4) {
                                monster.setAI(false);
                                phantom.getAttribute(Attribute.GENERIC_FOLLOW_RANGE).setBaseValue(999);
                                phantom.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 20, 127, false, false));

                                monster.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20, 4, false, false));
                                phantom.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20, 4, false, false));
                                phantom.setTarget(player);
                                phantom.addPassenger(monster);
                                this.cancel();

                                new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        run[0]++;
                                        if (phantom.isDead() || player.isDead()) {
                                            monster.setAI(true);
                                            this.cancel();
                                        }
                                        else if (phantom.getLocation().distanceSquared(player.getLocation()) < 4 || run[0] > 100000) {
                                            phantom.getAttribute(Attribute.GENERIC_FOLLOW_RANGE).setBaseValue(followrange);
                                            monster.setAI(true);
                                            monster.setTarget(player);
                                            phantom.removePassenger(monster);
                                            this.cancel();
                                        }
                                    }
                                }.runTaskTimer(plugin, 0, 5);
                            }
                        }
                    }.runTaskTimer(plugin, 0, 5);
                }
            }
        }
    }
}
