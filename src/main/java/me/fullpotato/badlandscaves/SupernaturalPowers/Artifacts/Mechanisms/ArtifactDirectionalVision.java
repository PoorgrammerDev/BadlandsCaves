package me.fullpotato.badlandscaves.SupernaturalPowers.Artifacts.Mechanisms;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.SupernaturalPowers.Spells.EnhancedEyes;
import me.fullpotato.badlandscaves.Util.ParticleShapes;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public class ArtifactDirectionalVision {
    private final BadlandsCaves plugin;
    private final EnhancedEyes enhancedEyes;

    public ArtifactDirectionalVision(BadlandsCaves plugin, EnhancedEyes enhancedEyes) {
        this.plugin = plugin;
        this.enhancedEyes = enhancedEyes;
    }

    public Set<Block> getDirectionalBlocks (Player player, int range, int radius) {
        final Set<Block> output = new HashSet<>();
        final Location scout = player.getLocation();
        for (int i = 0; i < range; i++) {
            scout.add(scout.getDirection().normalize().multiply(1.5));
            if (i % 2 == 0) ParticleShapes.particleSphere(player, Particle.REDSTONE, scout, radius, 0, new Particle.DustOptions(Color.BLUE, 1));
            output.addAll(enhancedEyes.getNearbyBlocks(scout, radius));
        }
        return output;
    }

    public Set<Entity> getDirectionalEntities (Player player, int range, int radius) {
        final World world = player.getWorld();
        final Set<Entity> output = new HashSet<>();
        final Location scout = player.getLocation();
        for (int i = 0; i < range; i++) {
            scout.add(scout.getDirection().normalize().multiply(1.5));
            output.addAll(world.getNearbyEntities(scout, radius, radius, radius));
        }
        return output;
    }
}
