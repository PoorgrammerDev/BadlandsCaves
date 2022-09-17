package me.fullpotato.badlandscaves.MobBuffs.CastleBoss.StateMachine;

import java.util.Random;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vindicator;
import org.bukkit.persistence.PersistentDataType;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.MobBuffs.CastleBoss.CastleBoss;

public abstract class CastleBossState {
    protected BadlandsCaves plugin;
    protected CastleBoss manager;
    protected Vindicator boss;
    protected Random random;

    public CastleBossState(BadlandsCaves plugin, CastleBoss manager, Vindicator boss, Random random) {
        this.plugin = plugin;
        this.manager = manager;
        this.boss = boss;
        this.random = random;
    }

    public abstract void Start();
    public abstract void End();
    public abstract void Attack(Player player, double damage);

    protected boolean isBuffed() {
        final NamespacedKey secondPhaseBuff = new NamespacedKey(plugin, "castle_boss_buffed");
        return (boss.getPersistentDataContainer().has(secondPhaseBuff, PersistentDataType.BYTE) &&
            boss.getPersistentDataContainer().get(secondPhaseBuff, PersistentDataType.BYTE) == (byte) 1);
    }
}
