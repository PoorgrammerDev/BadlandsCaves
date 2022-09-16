package me.fullpotato.badlandscaves.MobBuffs.CastleBoss.StateMachine;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vindicator;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.MobBuffs.CastleBoss.CastleBoss;

public class ExplosiveState extends CastleBossState {

    public ExplosiveState(BadlandsCaves plugin, CastleBoss manager, Vindicator boss, Random random) {
        super(plugin, manager, boss, random);
        //TODO Auto-generated constructor stub
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
        Bukkit.broadcastMessage("Performing Explosive Attack");

        for (int i = 0; i < 5; ++i) {
            final Location location = player.getLocation().add(random.nextInt(6) - 3, random.nextInt(6) - 3, random.nextInt(6) - 3);
            player.getWorld().createExplosion(location, 5, false, false);
        }

        if (random.nextBoolean()) {
            manager.setState(boss, new NormalState(plugin, manager, boss, random));
        }

    }
    
}
