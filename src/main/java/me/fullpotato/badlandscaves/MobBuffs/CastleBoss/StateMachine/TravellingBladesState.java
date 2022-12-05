package me.fullpotato.badlandscaves.MobBuffs.CastleBoss.StateMachine;

import java.util.Random;

import org.bukkit.entity.Player;
import org.bukkit.entity.Vindicator;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.CustomItem;
import me.fullpotato.badlandscaves.MobBuffs.CastleBoss.CastleBoss;
import me.fullpotato.badlandscaves.SupernaturalPowers.Artifacts.Mechanisms.Runnables.TravellingBladesProjectile;

public class TravellingBladesState extends CastleBossState {
    private boolean beganAttack;

    public TravellingBladesState(BadlandsCaves plugin, CastleBoss manager, Vindicator boss, Random random) {
        super(plugin, manager, boss, random);
        this.beganAttack = false;
    }

    @Override
    public void Start() {
        boss.getEquipment().setItemInMainHand(plugin.getCustomItemManager().getItem(CustomItem.VOIDMATTER_BLADE));
    }

    @Override
    public void End() {
        boss.getEquipment().setItemInMainHand(plugin.getCustomItemManager().getItem(CustomItem.VOIDMATTER_AXE));
    }

    @Override
    public void Attack(Player player, double damage) {
        if (this.beganAttack) return;
        this.beganAttack = true;

        final int count = isBuffed() ? 15 : 10;
        new TravellingBladesProjectile(plugin, random, boss, count, damage, this::ToNormalState).runTaskTimer(plugin, 0, 0);
    }

    //Reverts to normal state after Travelling Blades finishes -- using callback
    private void ToNormalState(int unused) {
        manager.setState(boss, new NormalState(plugin, manager, boss, random));
    }
    
}
