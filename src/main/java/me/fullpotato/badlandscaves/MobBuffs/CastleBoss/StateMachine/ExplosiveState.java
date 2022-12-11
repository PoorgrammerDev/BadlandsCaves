package me.fullpotato.badlandscaves.MobBuffs.CastleBoss.StateMachine;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vindicator;
import org.bukkit.inventory.ItemStack;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.MobBuffs.CastleBoss.CastleBoss;

public class ExplosiveState extends CastleBossState {

    public ExplosiveState(BadlandsCaves plugin, CastleBoss manager, Vindicator boss, Random random) {
        super(plugin, manager, boss, random);
    }

    @Override
    public void Start() {
        boss.getEquipment().setItemInOffHand(new ItemStack(Material.TNT));
        boss.getEquipment().setItemInOffHandDropChance(-999);
    }

    @Override
    public void End() {
        boss.getEquipment().setItemInOffHand(null);
    }

    @Override
    public void Attack(Player player, double damage) {
        final int range = isBuffed() ? 5 : 3;
        final int count = isBuffed() ? 10 : 5;

        //Summons explosions around the target on hit
        for (int i = 0; i < count; ++i) {
            final Location location = player.getLocation().add(random.nextInt(range * 2) - range, random.nextInt(range * 2) - range, random.nextInt(range * 2) - range);
            player.getWorld().createExplosion(location, 5, false, false);
        }

        //50% chance per hit to revert to normal
        if (random.nextBoolean()) {
            manager.setState(boss, new NormalState(plugin, manager, boss, random));
        }

    }
    
}
