package me.fullpotato.badlandscaves.SupernaturalPowers.Spells;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.Blocks.SilencerBlock;
import me.fullpotato.badlandscaves.NMS.EnhancedEyes.EnhancedEyesNMS;
import me.fullpotato.badlandscaves.SupernaturalPowers.Spells.Runnables.ManaBarManager;
import me.fullpotato.badlandscaves.Util.ParticleShapes;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public abstract class UsePowers {
    protected BadlandsCaves plugin;

    protected UsePowers(BadlandsCaves plugin) {
        this.plugin = plugin;
    }

    public void notEnoughMana(Player player) {
        ManaBarManager manabar = new ManaBarManager(plugin);
        manabar.displayMessage(player, ChatColor.RED + "Not enough Mana!", 2, false);
    }

    public void preventDoubleClick (Player player) {
        PlayerScore.SPELL_COOLDOWN.setScore(plugin, player, 1);
        new BukkitRunnable() {
            @Override
            public void run() {
                PlayerScore.SPELL_COOLDOWN.setScore(plugin, player, 0);
            }
        }.runTaskLaterAsynchronously(plugin, 2);
    }

    public boolean attemptSilence(Player player) {
        final ManaBarManager manaBar = new ManaBarManager(plugin);
        SilencerBlock silencerBlock = new SilencerBlock(plugin);
        Location silencer = silencerBlock.getNearbyActiveSilencer(player.getLocation());
        if (silencer != null) {
            silencerBlock.useSilencer(silencer);
            PlayerScore.SPELLS_SILENCED_TIMER.setScore(plugin, player, 100);
            manaBar.changeColor(player, BarColor.PURPLE, 5, true);
            manaBar.displayMessage(player, ChatColor.DARK_PURPLE.toString() + ChatColor.BOLD + "SILENCED", 5, true);


            //PARTICLES
            ParticleShapes.particleLine(null, Particle.REDSTONE, player.getLocation().add(0, 0.5, 0), silencer.clone().add(0.5, 0.5, 0.5), 0, new Particle.DustOptions(Color.PURPLE, 1), 0.25);
            for (double i = 0; i < player.getHeight(); i+= 0.1) {
                ParticleShapes.particleCircle(null, Particle.REDSTONE, player.getLocation().add(0, i, 0), 1, 0, new Particle.DustOptions(Color.PURPLE, 1));
            }

            //SOUND
            player.playSound(player.getLocation(), Sound.BLOCK_CONDUIT_DEACTIVATE, SoundCategory.BLOCKS, 1, 0.5F);
            player.playSound(player.getLocation(), Sound.PARTICLE_SOUL_ESCAPE, SoundCategory.BLOCKS, 10, 1);
            player.playSound(player.getLocation(), "custom.darkrooms_whispers", SoundCategory.BLOCKS, 1, 1);

            //HIGHLIGHT
            EnhancedEyesNMS nms = plugin.getEnhancedEyesNMS();
            int id = nms.spawnIndicator(player, silencer, ChatColor.DARK_PURPLE);
            new BukkitRunnable() {
                @Override
                public void run() {
                    nms.removeIndicator(player, id);
                }
            }.runTaskLaterAsynchronously(plugin, 100);

            return true;
        }
        return false;
    }
}
