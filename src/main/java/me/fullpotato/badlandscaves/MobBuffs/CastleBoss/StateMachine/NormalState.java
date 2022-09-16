package me.fullpotato.badlandscaves.MobBuffs.CastleBoss.StateMachine;

import java.util.Random;

import org.bukkit.entity.Player;
import org.bukkit.entity.Vindicator;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.MobBuffs.CastleBoss.CastleBoss;

public class NormalState extends CastleBossState {

    private CastleBossState[] hitTransition = {
        new ChainAttackState(plugin, manager, boss, random),
        new ExplosiveState(plugin, manager, boss, random),
    };

    public NormalState(BadlandsCaves plugin, CastleBoss manager, Vindicator boss, Random random) {
        super(plugin, manager, boss, random);
    }

    @Override
    public void Start() {
    }

    @Override
    public void End() {
    }

    @Override
    public void Attack(Player player, double damage) {
        if (random.nextInt(100) < 25) {
            manager.setState(boss, hitTransition[random.nextInt(hitTransition.length)]);
        } 
    }
    
}
