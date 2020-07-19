package me.fullpotato.badlandscaves.CustomItems.Using.Starlight;

import me.fullpotato.badlandscaves.BadlandsCaves;
import me.fullpotato.badlandscaves.CustomItems.Crafting.Starlight.StarlightCharge;
import me.fullpotato.badlandscaves.CustomItems.Crafting.Starlight.StarlightTools;
import me.fullpotato.badlandscaves.CustomItems.Using.Starlight.Nebulites.Nebulite;
import me.fullpotato.badlandscaves.CustomItems.Using.Starlight.Nebulites.NebuliteManager;
import me.fullpotato.badlandscaves.CustomItems.Using.UseCorrosive;
import me.fullpotato.badlandscaves.CustomItems.Using.UseSerrated;
import me.fullpotato.badlandscaves.CustomItems.Using.UseVoltshock;
import me.fullpotato.badlandscaves.Util.PlayerScore;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class StarlightSaberMechanism implements Listener {
    private final BadlandsCaves plugin;
    private final StarlightTools starlightTools;
    private final StarlightCharge chargeManager;
    private final NebuliteManager nebuliteManager;
    private final UseVoltshock useVoltshock;
    private final UseCorrosive useCorrosive;
    private final UseSerrated useSerrated;
    private final Random random = new Random();

    public StarlightSaberMechanism(BadlandsCaves plugin) {
        this.plugin = plugin;
        this.starlightTools = new StarlightTools(plugin);
        this.chargeManager = new StarlightCharge(plugin);
        this.nebuliteManager = new NebuliteManager(plugin);
        this.useVoltshock = new UseVoltshock(plugin);
        this.useCorrosive = new UseCorrosive(plugin);
        this.useSerrated = new UseSerrated(plugin);
    }

    @EventHandler
    public void saberHitElemental (EntityDamageByEntityEvent event) {
        if (plugin.getSystemConfig().getBoolean("hardmode")) {
            if (event.getDamager() instanceof Player && event.getEntity() instanceof LivingEntity) {
                final Player player = (Player) event.getDamager();

                double player_dmg = player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).getValue();
                if (event.getDamage() >= player_dmg) {
                    if ((byte) PlayerScore.HAS_SUPERNATURAL_POWERS.getScore(plugin, player) == (byte) 0) {
                        final LivingEntity entity = (LivingEntity) event.getEntity();
                        final ItemStack item = player.getInventory().getItemInMainHand();
                        if (starlightTools.isStarlightSaber(item) && chargeManager.getCharge(item) > 0 && !getOnCooldown(item)) {
                            setOnCooldown(item, true);
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, .5F, 1.2F);
                                    setOnCooldown(item, false);
                                }
                            }.runTaskLater(plugin, 60);

                            useVoltshock.applyShock(entity);
                            final Nebulite[] nebulites = nebuliteManager.getNebulites(item);
                            for (Nebulite nebulite : nebulites) {
                                if (nebulite.equals(Nebulite.CORRODING_LIGHTS)) {
                                    int count = random.nextInt(10) + 5;
                                    if (useCorrosive.applyCorrosion(entity, random, count, false)) chargeManager.setCharge(item, chargeManager.getCharge(item) - count);
                                }
                                else if (nebulite.equals(Nebulite.JAGGED_LIGHTS)) {
                                    int count = random.nextInt(3) + 3;
                                    if (useSerrated.applyBleeding(player, entity, event.getDamage(), count, false)) chargeManager.setCharge(item, chargeManager.getCharge(item) - count);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void setOnCooldown(ItemStack item, boolean onCooldown) {
        if (starlightTools.isStarlightSaber(item)) {
            final ItemMeta meta = item.getItemMeta();
            meta.getPersistentDataContainer().set(new NamespacedKey(plugin, "saber_cooldown"), PersistentDataType.BYTE, onCooldown ? (byte) 1 : (byte) 0);
            item.setItemMeta(meta);
        }
    }

    public boolean getOnCooldown(ItemStack item) {
        if (starlightTools.isStarlightSaber(item)) {
            Byte result = item.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(plugin, "saber_cooldown"), PersistentDataType.BYTE);
            if (result != null) return result == (byte) 1;
        }
        return true;
    }
}
