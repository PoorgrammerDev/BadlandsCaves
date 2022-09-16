package me.fullpotato.badlandscaves.MobBuffs.CastleBoss;

import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vindicator;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.CustomItem;
import me.fullpotato.badlandscaves.MobBuffs.CastleBoss.StateMachine.CastleBossState;
import me.fullpotato.badlandscaves.MobBuffs.CastleBoss.StateMachine.NormalState;

public class CastleBoss implements Listener {
    private final BadlandsCaves plugin;
    private final Random random;

    private HashMap<UUID, CastleBossState> stateMap;

    public CastleBoss(BadlandsCaves plugin, Random random) {
        this.plugin = plugin;
        this.random = random;
        this.stateMap = new HashMap<>();
    }

    @EventHandler
    public void BossHitPlayer(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        if (!(event.getDamager() instanceof Vindicator)) return;

        final Player player = (Player) event.getEntity();
        final Vindicator boss = (Vindicator) event.getDamager();

        //Check if vindicator is the boss
        final NamespacedKey bossKey = new NamespacedKey(plugin, "is_castle_boss");
        if (!boss.getPersistentDataContainer().has(bossKey, PersistentDataType.BYTE) ||
            boss.getPersistentDataContainer().get(bossKey, PersistentDataType.BYTE) != (byte) 1) return;

        //Regular effects
        player.setNoDamageTicks(0);

        final CastleBossState state = getState(boss);
        state.Attack(player, event.getDamage());
    }


    @EventHandler
    public void ExplosionImmunity(EntityDamageEvent event) {
        if (event.isCancelled()) return;

        if (!(event.getEntity() instanceof Vindicator)) return;
        final Vindicator boss = (Vindicator) event.getEntity();

        //Check if explosion
        if (event.getCause() != DamageCause.ENTITY_EXPLOSION &&
            event.getCause() != DamageCause.BLOCK_EXPLOSION) return;

        //Check if vindicator is the boss
        final NamespacedKey bossKey = new NamespacedKey(plugin, "is_castle_boss");
        if (!boss.getPersistentDataContainer().has(bossKey, PersistentDataType.BYTE) ||
            boss.getPersistentDataContainer().get(bossKey, PersistentDataType.BYTE) != (byte) 1) return;

        event.setCancelled(true);
    }

    public CastleBossState getState(Vindicator boss) {
        if (this.stateMap.containsKey(boss.getUniqueId())) {
            return this.stateMap.get(boss.getUniqueId());
        }

        final CastleBossState normalState = new NormalState(plugin, this, boss, random);
        this.stateMap.put(boss.getUniqueId(), normalState);
        normalState.Start();

        return normalState;
    }
    
    public void setState(Vindicator boss, CastleBossState state) {
        final CastleBossState oldState = getState(boss);
        if (oldState != null) oldState.End();

        this.stateMap.put(boss.getUniqueId(), state);
        state.Start();
    }

}

// enum CastleBossState {
//     NORMAL,
//     CHAIN_ATTACK_READY,
//     CHAIN_ATTACK,
//     EXPLOSIVE_ATTACK,
//     TRAVELLING_BLADES,
//     HEALING,
// }