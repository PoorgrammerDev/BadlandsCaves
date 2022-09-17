package me.fullpotato.badlandscaves.MobBuffs.CastleBoss.StateMachine;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vindicator;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.CustomItem;
import me.fullpotato.badlandscaves.MobBuffs.CastleBoss.CastleBoss;

public class ChainAttackState extends CastleBossState {
    private boolean beganAttack;

    public ChainAttackState(BadlandsCaves plugin, CastleBoss manager, Vindicator boss, Random random) {
        super(plugin, manager, boss, random);
        this.beganAttack = false;
    }

    @Override
    public void Start() {
        final ItemStack displace = plugin.getCustomItemManager().getItem(CustomItem.DISPLACE);
        boss.getEquipment().setItemInOffHand(displace);
        boss.getEquipment().setItemInOffHandDropChance(-999);

    }

    @Override
    public void End() {
        boss.getEquipment().setItemInOffHand(null);
    }

    @Override
    public void Attack(Player player, double damage) {
        if (this.beganAttack) return;
        this.beganAttack = true;

        final Location origin = boss.getLocation();
        final int hits = isBuffed() ? 8 : 4;
        final int delay = isBuffed() ? 3 : 5;

        int[] count = {0};
        new BukkitRunnable() {
            @Override
            public void run() {
                if (count[0] > hits || boss.getLocation().distanceSquared(player.getLocation()) > 9) {
                    //Teleport back to original location
                    boss.teleport(origin);
                    boss.getWorld().playSound(origin, "custom.supernatural.displace.warp", SoundCategory.HOSTILE, 0.3F, 1);
                    boss.getWorld().spawnParticle(Particle.SPELL_WITCH, origin, 5, 0.1, 0.1, 0.1, 1);

                    //Change to normal state
                    manager.setState(boss, new NormalState(plugin, manager, boss, random));
                    this.cancel();
                    return;
                }

                //Teleport to the player directly after attacking
                final Location location = player.getLocation();
                location.add((random.nextDouble() * 2) - 1, 0, (random.nextDouble() * 2) - 1);

                boss.teleport(location);
                boss.getWorld().playSound(location, "custom.supernatural.displace.warp", SoundCategory.HOSTILE, 0.3F, 1);
                boss.getWorld().spawnParticle(Particle.SPELL_WITCH, location, 5, 0.1, 0.1, 0.1, 1);

                player.damage(damage / 2.0f, boss);
                player.setNoDamageTicks(0);
                
                count[0]++;
            }

        }.runTaskTimer(plugin, 0, delay);
    }
    
}
