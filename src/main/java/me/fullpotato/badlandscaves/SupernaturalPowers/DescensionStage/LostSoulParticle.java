package me.fullpotato.badlandscaves.SupernaturalPowers.DescensionStage;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;
import java.util.List;

public class LostSoulParticle extends BukkitRunnable {
    private final BadlandsCaves plugin;
    private final World world;

    public LostSoulParticle(BadlandsCaves plugin) {
        this.plugin = plugin;
        world = plugin.getServer().getWorld(plugin.getDescensionWorldName());
    }

    @Override
    public void run() {
        final Collection<? extends Player> players = world.getEntitiesByClass(Player.class);
        if (players.isEmpty()) {
            this.cancel();
            return;
        }

        for (Player player : players) {
            int in_descension = ((int) PlayerScore.IN_DESCENSION.getScore(plugin, player));
            if (in_descension == 2) {
                final List<Entity> entities = player.getNearbyEntities(20, 20, 20);
                for (Entity entity : entities) {
                    if (entity instanceof Zombie) {
                        player.spawnParticle(Particle.REDSTONE, ((Zombie) entity).getEyeLocation(), 2, new Particle.DustOptions(Color.ORANGE, 2));
                    }
                }
            }
        }
    }
}
